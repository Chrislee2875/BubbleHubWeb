// Package chứa các Servlet admin controller
package bubblehub.controller.admin;

import bubblehub.dao.CategoryDAO;
import bubblehub.dao.ProductDAO;
import bubblehub.model.Product;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Admin controller quản lý sản phẩm: xem, thêm, sửa, bật/tắt, xóa
@WebServlet(name = "AdminProductController", urlPatterns = {"/AdminProductController"})
public class AdminProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8"); // Hỗ trợ tiếng Việt trong tên sản phẩm
        }

        String action = req.getParameter("subAction");
        if (action == null || action.trim().isEmpty()) {
            action = req.getParameter("action");
        }

        // Hiển thị form thêm hoặc sửa sản phẩm (GET hoặc POST với action=form)
        if ("form".equals(action)) {
            CategoryDAO catDao = new CategoryDAO();
            req.setAttribute("categories", catDao.getAll()); // Danh sách danh mục để chọn
            String pidParam = req.getParameter("pid");
            if (pidParam != null) {
                // Nếu có pid → load sản phẩm hiện tại để điền sẵn vào form (edit mode)
                ProductDAO prodDao = new ProductDAO();
                req.setAttribute("product", prodDao.getById(Integer.parseInt(pidParam)));
            }
            req.getRequestDispatcher("jsp/admin/productForm.jsp").forward(req, resp);
            return;
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            ProductDAO dao = new ProductDAO();

            if ("add".equals(action)) {
                // Thêm sản phẩm mới từ form
                Product p = new Product();
                p.setCid(Integer.parseInt(req.getParameter("cid")));
                p.setName(req.getParameter("name"));
                p.setPrice(Double.parseDouble(req.getParameter("price")));
                p.setImagePath(req.getParameter("image_path"));
                // Checkbox is_available: "on" (HTML checkbox) hoặc "1" (hidden input)
                p.setAvailable("on".equals(req.getParameter("is_available")) || "1".equals(req.getParameter("is_available")));
                dao.insert(p);

            } else if ("edit".equals(action)) {
                // Cập nhật thông tin sản phẩm
                Product p = new Product();
                p.setPid(Integer.parseInt(req.getParameter("pid")));
                p.setCid(Integer.parseInt(req.getParameter("cid")));
                p.setName(req.getParameter("name"));
                p.setPrice(Double.parseDouble(req.getParameter("price")));
                p.setImagePath(req.getParameter("image_path"));
                p.setAvailable("on".equals(req.getParameter("is_available")) || "1".equals(req.getParameter("is_available")));
                dao.update(p);

            } else if ("toggle".equals(action)) {
                // Bật/tắt trạng thái bán của sản phẩm
                dao.toggleAvailability(Integer.parseInt(req.getParameter("pid")));

            } else if ("delete".equals(action)) {
                // Xóa sản phẩm
                dao.delete(Integer.parseInt(req.getParameter("pid")));
            }

            // Redirect sau POST
            resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminProducts");
            return;
        }

        // GET mặc định: hiển thị danh sách sản phẩm, có hỗ trợ tìm kiếm theo keyword
        String keyword = req.getParameter("keyword");
        ProductDAO prodDao = new ProductDAO();
        req.setAttribute("products", prodDao.getAll(keyword, null));
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("jsp/admin/products.jsp").forward(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "AdminProductController";
    }
}
