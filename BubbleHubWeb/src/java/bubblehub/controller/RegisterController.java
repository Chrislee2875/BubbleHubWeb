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

// Servlet xử lý đăng ký tài khoản mới
@WebServlet(name = "RegisterController", urlPatterns = {"/RegisterController"})
public class RegisterController extends HttpServlet {

    // Trang đăng ký
    private static final String REGISTER_PAGE = "jsp/register.jsp";
    // Sau khi đăng ký thành công, chuyển qua trang đăng nhập kèm thông báo
    private static final String SUCCESS = "MainController?action=Login&registered=1";

    // Xử lý form đăng ký (POST)
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = REGISTER_PAGE;
        try {
            // Lấy thông tin từ form
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            // Nếu chưa submit form đầy đủ → hiển thị trang đăng ký
            if (name == null || email == null || password == null || confirmPassword == null) {
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // Kiểm tra mật khẩu xác nhận có khớp không
            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            UserDAO dao = new UserDAO();
            // Kiểm tra email đã tồn tại chưa
            if (dao.emailExists(email)) {
                request.setAttribute("error", "Email đã được sử dụng!");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // Tạo đối tượng User và lưu vào database
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPassword(password);

            if (dao.register(u)) {
                // Đăng ký thành công → chuyển về trang đăng nhập
                response.sendRedirect(SUCCESS);
                return;
            } else {
                request.setAttribute("error", "Đăng ký thất bại, vui lòng thử lại!");
            }
        } catch (Exception e) {
            log("Error at RegisterController: " + e.toString());
            request.setAttribute("error", "Không thể đăng ký lúc này");
        }
        request.getRequestDispatcher(url).forward(request, response);
    }

    // GET request → chỉ hiển thị trang đăng ký
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.getRequestDispatcher(REGISTER_PAGE).forward(request, response);
    }

    // POST request → xử lý đăng ký
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Register Controller";
    }
}
