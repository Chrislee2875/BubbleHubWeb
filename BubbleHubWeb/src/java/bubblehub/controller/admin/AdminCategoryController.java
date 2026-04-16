// Package chứa các Servlet admin controller
package bubblehub.controller.admin;

import bubblehub.dao.CategoryDAO;
import bubblehub.model.Category;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Admin controller quản lý danh mục sản phẩm (CRUD)
@WebServlet(name = "AdminCategoryController", urlPatterns = {"/AdminCategoryController"})
public class AdminCategoryController extends HttpServlet {

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
            // Xử lý các thao tác thêm/sửa/xóa danh mục (POST request)
            req.setCharacterEncoding("UTF-8");
            String action = req.getParameter("subAction");
            if (action == null || action.trim().isEmpty()) {
                action = req.getParameter("action");
            }
            CategoryDAO dao = new CategoryDAO();

            if ("add".equals(action)) {
                // Thêm danh mục mới
                Category c = new Category();
                c.setName(req.getParameter("name"));
                dao.insert(c);
            } else if ("edit".equals(action)) {
                // Sửa tên danh mục theo cid
                Category c = new Category();
                c.setCid(Integer.parseInt(req.getParameter("cid")));
                c.setName(req.getParameter("name"));
                dao.update(c);
            } else if ("delete".equals(action)) {
                // Xóa danh mục theo cid
                dao.delete(Integer.parseInt(req.getParameter("cid")));
            }
            // Sau khi xử lý POST → redirect để tránh resubmit khi F5
            resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminCategories");
            return;
        }

        // GET: hiển thị danh sách tất cả danh mục
        CategoryDAO dao = new CategoryDAO();
        req.setAttribute("categories", dao.getAll());
        req.getRequestDispatcher("jsp/admin/categories.jsp").forward(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "AdminCategoryController";
    }
}
