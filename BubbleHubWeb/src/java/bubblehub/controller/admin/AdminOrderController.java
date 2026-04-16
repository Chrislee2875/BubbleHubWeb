// Package chứa các Servlet admin controller
package bubblehub.controller.admin;

import bubblehub.dao.OrderDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Admin controller quản lý đơn hàng: xem danh sách, xem chi tiết, cập nhật trạng thái
@WebServlet(name = "AdminOrderController", urlPatterns = {"/AdminOrderController"})
public class AdminOrderController extends HttpServlet {

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
        // Lấy sub-action từ request
        String action = req.getParameter("subAction");
        if (action == null || action.trim().isEmpty()) {
            action = req.getParameter("action");
        }
        OrderDAO dao = new OrderDAO();

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            // Xử lý cập nhật trạng thái đơn hàng (ví dụ: PENDING → SERVED)
            if ("updateStatus".equals(action)) {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                String status = req.getParameter("status");
                dao.updateStatus(orderId, status);
            }
            // Redirect sau khi xử lý POST
            resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminOrders");
            return;
        }

        // GET: xem chi tiết 1 đơn hàng hoặc danh sách tất cả đơn
        if ("detail".equals(action)) {
            // Xem chi tiết đơn hàng kèm danh sách sản phẩm
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            req.setAttribute("order", dao.getById(orderId));
            req.setAttribute("items", dao.getItemsByOrder(orderId));
            req.getRequestDispatcher("jsp/admin/orderDetail.jsp").forward(req, resp);
        } else {
            // Xem danh sách đơn hàng, có thể lọc theo trạng thái
            String statusFilter = req.getParameter("statusFilter");
            req.setAttribute("orders", dao.getAll(statusFilter));
            req.setAttribute("statusFilter", statusFilter);
            req.getRequestDispatcher("jsp/admin/orders.jsp").forward(req, resp);
        }
    }

    @Override
    public String getServletInfo() {
        return "AdminOrderController";
    }
}
