// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.ProductDAO;
import bubblehub.model.CartItem;
import bubblehub.model.Product;
import bubblehub.util.CartRuleUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Servlet quản lý giỏ hàng: xem, thêm, cập nhật, xóa sản phẩm
@WebServlet(name = "CartController", urlPatterns = {"/CartController"})
public class CartController extends HttpServlet {

    // Trang JSP hiển thị giỏ hàng
    private static final String SUCCESS = "jsp/cart.jsp";

    // GET: hiển thị giỏ hàng, chuẩn hóa liên kết topping, xóa thông báo lỗi cũ
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = SUCCESS;
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
                // Chuẩn hóa giỏ hàng: sửa liên kết topping bị lỗi
                CartRuleUtil.normalizeToppingLinks(cart);

                // Chuyển lỗi từ session sang request rồi xóa khỏi session
                String cartError = (String) session.getAttribute("cartError");
                if (cartError != null) {
                    request.setAttribute("cartError", cartError);
                    session.removeAttribute("cartError");
                }
                session.removeAttribute("cartInfo"); // Xóa thông báo info cũ
            }
        } catch (Exception e) {
            log("Error at CartController: " + e.toString());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // POST: xử lý các thao tác trên giỏ hàng (add/update/bulk-update/remove/clear)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        // Tạo giỏ hàng mới nếu chưa có trong session
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        CartRuleUtil.ensureLineKeys(cart); // Đảm bảo tất cả item có lineKey

        // Xác định hành động cần thực hiện
        String action = request.getParameter("subAction");
        if (action == null || action.trim().isEmpty()) {
            action = request.getParameter("action");
        }

        try {
            if ("add".equals(action)) {
                handleAddToCart(request, response, session, cart); // Thêm sản phẩm
                return;
            } else if ("update".equals(action)) {
                handleUpdate(request, cart);       // Cập nhật số lượng 1 item
            } else if ("bulk-update".equals(action)) {
                handleBulkUpdate(request, cart);   // Cập nhật nhiều item cùng lúc
            } else if ("remove".equals(action)) {
                handleRemove(request, cart);       // Xóa 1 item
            } else if ("clear".equals(action)) {
                cart.clear();                      // Xóa toàn bộ giỏ hàng
            }
        } catch (Exception e) {
            log("Error at CartController doPost: " + e.toString());
        }

        response.sendRedirect("MainController?action=Cart"); // Về trang giỏ hàng
    }

    // Xử lý thêm sản phẩm vào giỏ hàng
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, List<CartItem> cart) throws IOException {
        int pid = Integer.parseInt(request.getParameter("pid"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        quantity = Math.max(1, Math.min(quantity, 20)); // Giới hạn số lượng 1-20

        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getById(pid);
        // Kiểm tra sản phẩm tồn tại và đang bán
        if (product == null || !product.isAvailable()) {
            session.setAttribute("cartError", "Sản phẩm không tồn tại hoặc đã ngừng bán.");
            response.sendRedirect("MainController?action=Cart");
            return;
        }

        boolean isTopping = product.isTopping();
        // Topping chỉ được thêm khi đã có thức uống trong giỏ
        if (isTopping && !CartRuleUtil.hasDrinkItem(cart)) {
            session.setAttribute("cartError", "Bạn cần có ít nhất 1 thức uống trước khi thêm topping.");
            response.sendRedirect("MainController?action=Cart");
            return;
        }

        // Topping không cần tùy chọn đường/đá, thức uống cần
        int sugarLevel = isTopping ? 0 : Integer.parseInt(request.getParameter("sugarLevel"));
        String iceLevel = isTopping ? "no" : request.getParameter("iceLevel");
        String attachTo = isTopping ? request.getParameter("attachTo") : null; // lineKey của drink được chọn
        CartItem attachedDrink = null;

        if (isTopping) {
            // Tìm thức uống mà topping này sẽ gắn vào
            attachedDrink = CartRuleUtil.findDrinkByLineKey(cart, attachTo);
            if (attachedDrink == null) {
                session.setAttribute("cartError", "Vui lòng chọn thức uống để gắn topping.");
                response.sendRedirect("MainController?action=ProductDetail&pid=" + pid);
                return;
            }
            // Kiểm tra drink còn slot topping không (slot = số lượng drink - topping đã gắn)
            int existingLinkedQty = CartRuleUtil.totalAttachedToppingQty(cart, attachedDrink.getLineKey());
            int availableSlots = attachedDrink.getQuantity() - existingLinkedQty;
            if (availableSlots <= 0) {
                session.setAttribute("cartError", "Thức uống đã đủ topping theo số lượng hiện tại.");
                response.sendRedirect("MainController?action=ProductDetail&pid=" + pid);
                return;
            }
            if (quantity > availableSlots) {
                quantity = availableSlots; // Cắt giảm cho vừa slot còn trống
            }
        }

        // Tìm xem sản phẩm đã có trong giỏ chưa (cùng pid, cùng tùy chọn, cùng drink nếu là topping)
        boolean found = false;
        CartItem targetItem = null;
        int beforeQty = 0;
        for (CartItem item : cart) {
            boolean sameItem = item.getPid() == pid && item.isTopping() == isTopping;
            sameItem = sameItem && (!isTopping || attachedDrink.getLineKey().equals(item.getAttachedDrinkKey()));
            sameItem = sameItem && (isTopping
                    || (item.getSugarLevel() == sugarLevel && item.getIceLevel().equals(iceLevel)));
            if (sameItem) {
                beforeQty = item.getQuantity();
                int maxQty = 20;
                if (isTopping) {
                    // Tính số lượng tối đa cho topping này trong drink đó
                    int otherLinkedQty = CartRuleUtil.totalAttachedToppingQtyExcept(
                            cart, attachedDrink.getLineKey(), item.getLineKey());
                    maxQty = Math.max(0, attachedDrink.getQuantity() - otherLinkedQty);
                }
                // Cộng thêm số lượng (không vượt maxQty)
                item.setQuantity(Math.min(item.getQuantity() + quantity, maxQty));
                if (isTopping) {
                    item.setAttachedDrinkName(attachedDrink.getProductName());
                }
                targetItem = item;
                found = true;
                break;
            }
        }

        if (!found) {
            // Chưa có → tạo CartItem mới và thêm vào giỏ
            CartItem item = new CartItem();
            item.setPid(pid);
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            item.setSugarLevel(sugarLevel);
            item.setIceLevel(iceLevel);
            item.setCategoryName(product.getCategoryName());
            item.setTopping(isTopping);
            item.setLineKey(UUID.randomUUID().toString()); // Sinh lineKey duy nhất
            if (isTopping) {
                item.setAttachedDrinkKey(attachedDrink.getLineKey());
                item.setAttachedDrinkName(attachedDrink.getProductName());
            }
            cart.add(item);
            targetItem = item;
        }

        // Nếu là topping → về trang giỏ hàng ngay
        if (isTopping) {
            response.sendRedirect("MainController?action=Cart");
            return;
        }

        if (targetItem == null) {
            session.setAttribute("cartError", "Không thể thêm sản phẩm vào giỏ hàng.");
            response.sendRedirect("MainController?action=Cart");
            return;
        }

        // Tính số lượng thực sự được thêm vào
        int addedQty = targetItem.getQuantity() - beforeQty;
        if (addedQty <= 0) {
            response.sendRedirect("MainController?action=Cart");
            return;
        }

        // Sau khi thêm thức uống → gợi ý trang chọn topping
        String drinkLineKey = targetItem.getLineKey();
        response.sendRedirect("MainController?action=ToppingSelection&drinkLineKey=" + drinkLineKey + "&suggestedQty=" + addedQty);
    }

    // Cập nhật số lượng 1 item theo index trong danh sách giỏ hàng
    private void handleUpdate(HttpServletRequest request, List<CartItem> cart) {
        int index = Integer.parseInt(request.getParameter("index"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        if (index >= 0 && index < cart.size()) {
            CartItem target = cart.get(index);
            if (quantity <= 0) {
                // Xóa item (và topping liên kết nếu là drink)
                if (!target.isTopping()) {
                    String drinkLineKey = target.getLineKey();
                    cart.remove(index);
                    CartRuleUtil.removeLinkedToppings(cart, drinkLineKey);
                } else {
                    cart.remove(index);
                }
            } else {
                int safeQty = Math.min(quantity, 20);
                if (target.isTopping()) {
                    // Kiểm tra slot còn trống của drink parent trước khi cập nhật
                    CartItem parentDrink = CartRuleUtil.findDrinkByLineKey(cart, target.getAttachedDrinkKey());
                    if (parentDrink == null) {
                        cart.remove(index); // Drink parent không tồn tại → xóa topping
                    } else {
                        int otherLinkedQty = CartRuleUtil.totalAttachedToppingQtyExcept(
                                cart, parentDrink.getLineKey(), target.getLineKey());
                        int maxQty = Math.max(0, parentDrink.getQuantity() - otherLinkedQty);
                        if (maxQty <= 0) {
                            cart.remove(index);
                        } else {
                            target.setQuantity(Math.min(safeQty, maxQty));
                        }
                    }
                } else {
                    target.setQuantity(safeQty);
                    CartRuleUtil.normalizeToppingLinks(cart); // Tái chuẩn hóa sau khi đổi số lượng drink
                }
            }
        }
    }

    // Cập nhật nhiều item cùng lúc (form submit toàn bộ giỏ)
    private void handleBulkUpdate(HttpServletRequest request, List<CartItem> cart) {
        String[] lineKeys = request.getParameterValues("lineKey");
        String[] quantities = request.getParameterValues("quantity");
        if (lineKeys != null && quantities != null) {
            int count = Math.min(lineKeys.length, quantities.length);

            // Cập nhật drink trước (để slot topping được tính lại đúng)
            for (int i = 0; i < count; i++) {
                CartItem target = findByLineKey(cart, lineKeys[i]);
                if (target == null || target.isTopping()) {
                    continue;
                }
                int quantity = parseQuantityOrDefault(quantities[i], target.getQuantity());
                if (quantity <= 0) {
                    String drinkLineKey = target.getLineKey();
                    cart.remove(target);
                    CartRuleUtil.removeLinkedToppings(cart, drinkLineKey);
                } else {
                    target.setQuantity(Math.min(quantity, 20));
                }
            }

            // Cập nhật topping sau
            for (int i = 0; i < count; i++) {
                CartItem target = findByLineKey(cart, lineKeys[i]);
                if (target == null || !target.isTopping()) {
                    continue;
                }
                int quantity = parseQuantityOrDefault(quantities[i], target.getQuantity());
                if (quantity <= 0) {
                    cart.remove(target);
                } else {
                    target.setQuantity(Math.min(quantity, 20));
                }
            }

            // Chuẩn hóa lại toàn bộ sau bulk update
            CartRuleUtil.normalizeToppingLinks(cart);
        }
    }

    // Xóa 1 item khỏi giỏ hàng theo index
    private void handleRemove(HttpServletRequest request, List<CartItem> cart) {
        int index = Integer.parseInt(request.getParameter("index"));
        if (index >= 0 && index < cart.size()) {
            CartItem target = cart.get(index);
            if (!target.isTopping()) {
                // Xóa drink → xóa luôn topping đi kèm
                String drinkLineKey = target.getLineKey();
                cart.remove(index);
                CartRuleUtil.removeLinkedToppings(cart, drinkLineKey);
            } else {
                cart.remove(index);
            }
        }
        CartRuleUtil.normalizeToppingLinks(cart);
    }

    // Tìm CartItem trong giỏ theo lineKey
    private CartItem findByLineKey(List<CartItem> cart, String lineKey) {
        if (lineKey == null || lineKey.trim().isEmpty()) {
            return null;
        }
        for (CartItem item : cart) {
            if (lineKey.equals(item.getLineKey())) {
                return item;
            }
        }
        return null;
    }

    // Parse số lượng từ chuỗi, trả về defaultValue nếu không hợp lệ
    private int parseQuantityOrDefault(String raw, int defaultValue) {
        if (raw == null) {
            return defaultValue;
        }
        String value = raw.trim();
        if (!value.matches("-?\\d+")) {
            return defaultValue; // Không phải số nguyên → dùng giá trị mặc định
        }
        return Integer.parseInt(value);
    }

    @Override
    public String getServletInfo() {
        return "Cart Controller";
    }
}
