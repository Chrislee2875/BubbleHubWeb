// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Servlet xử lý đăng xuất người dùng
@WebServlet(name = "LogoutController", urlPatterns = {"/LogoutController"})
public class LogoutController extends HttpServlet {

    // Sau khi đăng xuất, chuyển về trang đăng nhập
    private static final String SUCCESS = "MainController?action=Login";

    // Xử lý đăng xuất: hủy session và chuyển về trang login
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            // Lấy session hiện tại (nếu có) và hủy bỏ
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // Xóa toàn bộ dữ liệu session (user, cart, ...)
            }
        } catch (Exception e) {
            log("Error at LogoutController: " + e.toString());
        } finally {
            // Luôn chuyển về trang login dù có lỗi hay không
            response.sendRedirect(SUCCESS);
        }
    }

    // Xử lý GET request
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Xử lý POST request
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Logout Controller";
    }
}
