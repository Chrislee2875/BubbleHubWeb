// Package chứa các Servlet admin controller
package bubblehub.controller.admin;

import bubblehub.dao.OrderDAO;
import bubblehub.dao.ProductDAO;
import bubblehub.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// Admin controller hiển thị tổng quan hệ thống (dashboard thống kê)
@WebServlet(name = "AdminDashboardController", urlPatterns = {"/AdminDashboardController"})
public class AdminDashboardController extends HttpServlet {

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
        OrderDAO orderDAO = new OrderDAO();
        ProductDAO productDAO = new ProductDAO();
        UserDAO userDAO = new UserDAO();

        // Lấy dữ liệu thống kê và đẩy vào request để JSP hiển thị
        req.setAttribute("totalRevenue", orderDAO.getTotalRevenue());           // Tổng doanh thu
        req.setAttribute("countPending", orderDAO.countByStatus("PENDING"));   // Số đơn chờ thanh toán
        req.setAttribute("countPaid", orderDAO.countByStatus("PAID"));         // Số đơn đã thanh toán
        req.setAttribute("countServed", orderDAO.countByStatus("SERVED"));     // Số đơn đã phục vụ
        req.setAttribute("totalProducts", productDAO.getAll(null, null).size()); // Tổng sản phẩm
        req.setAttribute("totalUsers", userDAO.getAllUsers(null).size());        // Tổng người dùng

        req.getRequestDispatcher("jsp/admin/dashboard.jsp").forward(req, resp);
    }

    @Override
    public String getServletInfo() {
        return "AdminDashboardController";
    }
}
