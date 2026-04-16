// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.UserDAO;
import bubblehub.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Servlet quản lý hồ sơ người dùng: xem, cập nhật tên, đổi mật khẩu
@WebServlet(name = "ProfileController", urlPatterns = {"/ProfileController"})
public class ProfileController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String method = req.getMethod();

        if ("GET".equals(method)) {
            // GET: hiển thị trang hồ sơ với thông tin user hiện tại từ session
            User user = (User) req.getSession().getAttribute("user");
            req.setAttribute("user", user);
            req.getRequestDispatcher("jsp/profile.jsp").forward(req, resp);

        } else if ("POST".equals(method)) {
            // POST: xử lý cập nhật hoặc đổi mật khẩu
            String action = req.getParameter("subAction");
            if (action == null || action.trim().isEmpty()) {
                action = req.getParameter("action");
            }
            User user = (User) req.getSession().getAttribute("user");
            UserDAO dao = new UserDAO();

            if ("update".equals(action)) {
                // Cập nhật tên hiển thị
                String name = req.getParameter("name");
                user.setName(name);
                if (dao.updateProfile(user)) {
                    // Cập nhật lại user trong session để hiển thị tên mới ngay lập tức
                    req.getSession().setAttribute("user", user);
                    req.setAttribute("message", "Cập nhật thông tin thành công!");
                } else {
                    req.setAttribute("error", "Cập nhật thất bại!");
                }

            } else if ("changePassword".equals(action)) {
                // Đổi mật khẩu: lấy mật khẩu cũ, mới và xác nhận
                String oldPwd = req.getParameter("oldPassword");
                String newPwd = req.getParameter("newPassword");
                String confirmPwd = req.getParameter("confirmPassword");

                if (!newPwd.equals(confirmPwd)) {
                    req.setAttribute("error", "Mật khẩu mới không khớp!"); // Hai lần nhập không khớp
                } else if (dao.changePassword(user.getUid(), oldPwd, newPwd)) {
                    req.setAttribute("message", "Đổi mật khẩu thành công!");
                } else {
                    req.setAttribute("error", "Mật khẩu cũ không đúng!"); // Sai mật khẩu cũ
                }
            }

            // Ghi lại user vào request để JSP hiển thị thông tin mới nhất
            req.setAttribute("user", user);
            req.getRequestDispatcher("jsp/profile.jsp").forward(req, resp);
        }
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
        return "ProfileController";
    }
}
