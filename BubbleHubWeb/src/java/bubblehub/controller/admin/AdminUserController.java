// Package chứa các Servlet admin controller
package bubblehub.controller.admin;

import bubblehub.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Admin controller quản lý người dùng: xem danh sách, khóa/mở khóa, xóa tài khoản
@WebServlet(name = "AdminUserController", urlPatterns = {"/AdminUserController"})
public class AdminUserController extends HttpServlet {

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
            // Xử lý hành động quản lý user (toggle status hoặc xóa)
            String action = req.getParameter("subAction");
            if (action == null || action.trim().isEmpty()) {
                action = req.getParameter("action");
            }
            UserDAO dao = new UserDAO();

            if ("toggle".equals(action)) {
                // Khóa hoặc mở khóa tài khoản người dùng
                dao.toggleStatus(Integer.parseInt(req.getParameter("uid")));
            } else if ("delete".equals(action)) {
                // Xóa tài khoản người dùng
                dao.deleteUser(Integer.parseInt(req.getParameter("uid")));
            }
            // Redirect sau POST để tránh resubmit
            resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminUsers");
            return;
        }

        // GET: hiển thị danh sách người dùng, hỗ trợ tìm kiếm theo tên hoặc email
        String keyword = req.getParameter("keyword");
        UserDAO dao = new UserDAO();
        req.setAttribute("users", dao.getAllUsers(keyword));
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("jsp/admin/users.jsp").forward(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "AdminUserController";
    }
}
