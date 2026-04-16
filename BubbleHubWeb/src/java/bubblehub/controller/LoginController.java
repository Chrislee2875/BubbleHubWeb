// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.UserDAO;
import bubblehub.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Servlet xử lý đăng nhập người dùng
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    // Hằng số đường dẫn trang đăng nhập
    private static final String LOGIN_PAGE = "jsp/login.jsp";
    // Đường dẫn chuyển hướng sau đăng nhập thành công (phân theo role)
    private static final String ADMIN_SUCCESS = "MainController?action=AdminDashboard";
    private static final String USER_SUCCESS = "MainController?action=Menu";

    // Xử lý POST: xác thực thông tin đăng nhập
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = LOGIN_PAGE;
        try {
            // Lấy email và password từ form
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Nếu chưa submit form thì hiển thị trang đăng nhập
            if (email == null || password == null) {
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // Kiểm tra thông tin đăng nhập trong database
            UserDAO dao = new UserDAO();
            User user = dao.login(email, password);

            if (user != null) {
                // Đăng nhập thành công → lưu user vào session
                request.getSession().setAttribute("user", user);
                // Chuyển hướng theo role: ADMIN hoặc CUSTOMER
                if ("ADMIN".equals(user.getRole())) {
                    response.sendRedirect(ADMIN_SUCCESS);
                } else {
                    response.sendRedirect(USER_SUCCESS);
                }
                return;
            } else {
                // Sai email hoặc mật khẩu → hiện thông báo lỗi
                request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            }
        } catch (Exception e) {
            log("Error at LoginController: " + e.toString());
            request.setAttribute("error", "Không thể đăng nhập lúc này");
        }
        request.getRequestDispatcher(url).forward(request, response);
    }

    // GET request → chỉ hiển thị trang đăng nhập (không xử lý form)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
    }

    // POST request → xử lý đăng nhập
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Login Controller";
    }
}
