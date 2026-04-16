// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.ProductDAO;
import bubblehub.model.CartItem;
import bubblehub.model.Product;
import bubblehub.util.CartRuleUtil;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Servlet cho phép người dùng chọn topping gắn vào một thức uống vừa thêm vào giỏ
@WebServlet(name = "ToppingSelectionController", urlPatterns = {"/ToppingSelectionController"})
public class ToppingSelectionController extends HttpServlet {

    // Phân nhánh xử lý theo phương thức HTTP
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equals(method)) {
            handleGet(req, resp); // Hiển thị trang chọn topping
        } else if ("POST".equals(method)) {
            handlePost(req, resp); // Xử lý khi người dùng chọn topping
        }
    }

    // GET: hiển thị trang chọn topping với danh sách topping có sẵn
    private void handleGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        List<CartItem> cart = session == null ? null : (List<CartItem>) session.getAttribute("cart");
        // Giỏ hàng trống → về trang giỏ hàng
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        CartRuleUtil.normalizeToppingLinks(cart);

        // Lấy thức uống mà người dùng muốn gắn topping vào
        String drinkLineKey = req.getParameter("drinkLineKey");
        CartItem drinkItem = CartRuleUtil.findDrinkByLineKey(cart, drinkLineKey);
        if (drinkItem == null) {
            session.setAttribute("cartError", "Không tìm thấy thức uống để chọn topping.");
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Tính số slot topping còn trống = số lượng drink - topping đã gắn
        int availableSlots = Math.max(0, drinkItem.getQuantity()
                - CartRuleUtil.totalAttachedToppingQty(cart, drinkLineKey));
        // Số lượng gợi ý mặc định (từ tham số URL hoặc giới hạn trong slot trống)
        int suggestedQty = 1;
        try {
            suggestedQty = Integer.parseInt(req.getParameter("suggestedQty"));
        } catch (Exception ignored) {}
        suggestedQty = Math.max(1, Math.min(suggestedQty, Math.max(1, availableSlots)));

        // Truyền dữ liệu vào request để JSP hiển thị
        ProductDAO productDAO = new ProductDAO();
        req.setAttribute("drinkItem", drinkItem);
        req.setAttribute("availableSlots", availableSlots);
        req.setAttribute("suggestedQty", suggestedQty);
        req.setAttribute("toppings", productDAO.getAvailableToppings()); // Danh sách topping đang bán
        req.getRequestDispatcher("jsp/toppingSelection.jsp").forward(req, resp);
    }

    // POST: xử lý khi người dùng submit form chọn topping
    private void handlePost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        CartRuleUtil.ensureLineKeys(cart);
        CartRuleUtil.normalizeToppingLinks(cart);

        // Lấy drink cần gắn topping
        String drinkLineKey = req.getParameter("drinkLineKey");
        CartItem drinkItem = CartRuleUtil.findDrinkByLineKey(cart, drinkLineKey);
        if (drinkItem == null) {
            session.setAttribute("cartError", "Không tìm thấy thức uống để gắn topping.");
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Xác định hành động
        String action = req.getParameter("subAction");
        if (action == null || action.trim().isEmpty()) {
            action = req.getParameter("action");
        }
        if ("skip".equals(action)) {
            // Người dùng bỏ qua → về giỏ hàng
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Kiểm tra slot còn trống
        int availableSlots = Math.max(0, drinkItem.getQuantity()
                - CartRuleUtil.totalAttachedToppingQty(cart, drinkLineKey));
        if (availableSlots <= 0) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Lấy topping được chọn
        int toppingPid;
        try {
            toppingPid = Integer.parseInt(req.getParameter("toppingPid"));
        } catch (NumberFormatException e) {
            session.setAttribute("cartError", "Vui lòng chọn topping hợp lệ.");
            resp.sendRedirect(buildSelectionUrl(req, drinkLineKey));
            return;
        }

        // Kiểm tra topping tồn tại và đang bán
        ProductDAO productDAO = new ProductDAO();
        Product topping = productDAO.getById(toppingPid);
        if (topping == null || !topping.isAvailable() || !topping.isTopping()) {
            session.setAttribute("cartError", "Topping không tồn tại hoặc đã ngừng bán.");
            resp.sendRedirect(buildSelectionUrl(req, drinkLineKey));
            return;
        }

        // Xác định số lượng topping (giới hạn trong slot trống và tối đa 20)
        int quantity = 1;
        try {
            quantity = Integer.parseInt(req.getParameter("quantity"));
        } catch (NumberFormatException ignored) {}
        quantity = Math.max(1, Math.min(quantity, Math.min(availableSlots, 20)));

        CartItem targetDrink = drinkItem;
        String targetDrinkLineKey = drinkLineKey;

        // Nếu số lượng topping < số lượng drink: tách (split) drink thành 2 phần
        // - 1 phần có quantity = số lượng topping (sẽ gắn topping vào)
        // - 1 phần còn lại tiếp tục không có topping
        if (quantity < drinkItem.getQuantity()) {
            CartItem splitDrink = new CartItem();
            splitDrink.setPid(drinkItem.getPid());
            splitDrink.setProductName(drinkItem.getProductName());
            splitDrink.setPrice(drinkItem.getPrice());
            splitDrink.setQuantity(quantity);
            splitDrink.setSugarLevel(drinkItem.getSugarLevel());
            splitDrink.setIceLevel(drinkItem.getIceLevel());
            splitDrink.setCategoryName(drinkItem.getCategoryName());
            splitDrink.setTopping(false);
            splitDrink.setLineKey(UUID.randomUUID().toString()); // Key mới cho phần tách ra

            drinkItem.setQuantity(drinkItem.getQuantity() - quantity); // Giảm drink gốc
            cart.add(splitDrink);
            targetDrink = splitDrink;
            targetDrinkLineKey = splitDrink.getLineKey();
        }

        // Kiểm tra topping này đã có trong giỏ gắn vào cùng drink chưa → merge nếu có
        boolean merged = false;
        for (CartItem item : cart) {
            if (item.isTopping()
                    && item.getPid() == toppingPid
                    && targetDrinkLineKey.equals(item.getAttachedDrinkKey())) {
                int maxQty = Math.max(0, targetDrink.getQuantity() - CartRuleUtil.totalAttachedToppingQtyExcept(
                        cart, targetDrinkLineKey, item.getLineKey()));
                item.setQuantity(Math.min(item.getQuantity() + quantity, maxQty));
                item.setAttachedDrinkName(targetDrink.getProductName());
                merged = true;
                break;
            }
        }

        if (!merged) {
            // Tạo topping item mới và thêm vào giỏ hàng
            CartItem item = new CartItem();
            item.setPid(topping.getPid());
            item.setProductName(topping.getName());
            item.setPrice(topping.getPrice());
            item.setQuantity(quantity);
            item.setSugarLevel(0);    // Topping không có mức đường
            item.setIceLevel("no");   // Topping không có đá
            item.setCategoryName(topping.getCategoryName());
            item.setTopping(true);
            item.setLineKey(UUID.randomUUID().toString());
            item.setAttachedDrinkKey(targetDrinkLineKey);
            item.setAttachedDrinkName(targetDrink.getProductName());
            cart.add(item);
        }

        // Chuẩn hóa lại giỏ hàng sau khi thêm topping
        CartRuleUtil.normalizeToppingLinks(cart);
        resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
    }

    // Xây dựng URL quay lại trang chọn topping
    private String buildSelectionUrl(HttpServletRequest req, String drinkLineKey) {
        String value = drinkLineKey == null ? "" : drinkLineKey;
        return req.getContextPath() + "/MainController?action=ToppingSelection&drinkLineKey=" + value;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "ToppingSelectionController";
    }
}
