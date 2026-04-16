// Khai báo package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

// Import DAO để thao tác dữ liệu đơn hàng từ database
import bubblehub.dao.OrderDAO;
// Import model User để lấy thông tin người dùng từ session
import bubblehub.model.User;
import javax.servlet.ServletException;
// Annotation để đăng ký Servlet với URL pattern
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Đăng ký Servlet này xử lý các request tới đường dẫn /OrderController
@WebServlet(name = "OrderController", urlPatterns = {"/OrderController"})
public class OrderController extends HttpServlet {

    // Phương thức xử lý chung cho cả GET và POST request
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Lấy tham số subAction từ request để biết hành động cần thực hiện
        String subAction = req.getParameter("subAction");

        // Lấy thông tin người dùng đang đăng nhập từ session
        User user = (User) req.getSession().getAttribute("user");

        // Khởi tạo DAO để truy vấn dữ liệu đơn hàng
        OrderDAO dao = new OrderDAO();

        // Kiểm tra nếu người dùng muốn xem chi tiết một đơn hàng cụ thể
        if ("detail".equals(subAction)) {

            // Lấy ID đơn hàng từ tham số request
            String orderIdStr = req.getParameter("orderId");

            // Chỉ xử lý nếu orderId được cung cấp và không rỗng
            if (orderIdStr != null && !orderIdStr.isEmpty()) {
                try {
                    // Chuyển orderId từ String sang int
                    int orderId = Integer.parseInt(orderIdStr);

                    // Truy vấn thông tin đơn hàng và danh sách sản phẩm trong đơn
                    req.setAttribute("order", dao.getById(orderId));
                    req.setAttribute("items", dao.getItemsByOrder(orderId));

                    // Chuyển hướng đến trang chi tiết đơn hàng
                    req.getRequestDispatcher("jsp/orderDetail.jsp").forward(req, resp);

                } catch (NumberFormatException e) {
                    // orderId không hợp lệ (không phải số) → quay về trang lịch sử đơn hàng
                    req.setAttribute("orders", dao.getByUser(user.getUid()));
                    req.getRequestDispatcher("jsp/orderHistory.jsp").forward(req, resp);
                }
            } else {
                // Không có orderId → hiển thị toàn bộ lịch sử đơn hàng của người dùng
                req.setAttribute("orders", dao.getByUser(user.getUid()));
                req.getRequestDispatcher("jsp/orderHistory.jsp").forward(req, resp);
            }
        } else {
            // Mặc định (không có subAction hoặc subAction khác): hiển thị lịch sử đơn hàng
            req.setAttribute("orders", dao.getByUser(user.getUid()));
            req.getRequestDispatcher("jsp/orderHistory.jsp").forward(req, resp);
        }
    }

    // Xử lý HTTP GET request → gọi processRequest
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Xử lý HTTP POST request → gọi processRequest
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Trả về tên mô tả của Servlet này
    @Override
    public String getServletInfo() {
        return "OrderController";
    }
}
