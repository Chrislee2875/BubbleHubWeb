// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// MainController: điểm vào trung tâm của ứng dụng - điều phối tất cả request theo tham số "action"
public class MainController extends HttpServlet {

    // Trang mặc định khi chưa có action
    private static final String WELCOME = "jsp/menu.jsp";

    // Phương thức xử lý chung: đọc tham số action và chuyển hướng (forward) sang controller tương ứng
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = WELCOME; // Mặc định hiển thị menu
        try {
            String action = request.getParameter("action");
            // Điều phối theo giá trị của action
            if (action == null || action.trim().isEmpty()) {
                url = "MenuController";                      // Không có action → menu
            } else if ("Login".equals(action)) {
                url = "LoginController";                     // Đăng nhập
            } else if ("Register".equals(action)) {
                url = "RegisterController";                  // Đăng ký
            } else if ("Logout".equals(action)) {
                url = "LogoutController";                    // Đăng xuất
            } else if ("Menu".equals(action)) {
                url = "MenuController";                      // Xem thực đơn
            } else if ("ProductDetail".equals(action)) {
                url = "ProductDetailController";             // Xem chi tiết sản phẩm
            } else if ("Cart".equals(action)) {
                url = "CartController";                      // Giỏ hàng
            } else if ("Checkout".equals(action)) {
                url = "CheckoutController";                  // Thanh toán
            } else if ("Order".equals(action)) {
                url = "OrderController";                     // Lịch sử đơn hàng
            } else if ("Profile".equals(action)) {
                url = "ProfileController";                   // Hồ sơ cá nhân
            } else if ("ToppingSelection".equals(action)) {
                url = "ToppingSelectionController";          // Chọn topping
            } else if ("VNPayReturn".equals(action)) {
                url = "VNPayReturnController";               // Callback VNPay
            } else if ("Repay".equals(action)) {
                url = "RepayController";                     // Thanh toán lại
            } else if ("AdminDashboard".equals(action)) {
                url = "AdminDashboardController";            // Admin: tổng quan
            } else if ("AdminProducts".equals(action)) {
                url = "AdminProductController";              // Admin: quản lý sản phẩm
            } else if ("AdminCategories".equals(action)) {
                url = "AdminCategoryController";             // Admin: quản lý danh mục
            } else if ("AdminOrders".equals(action)) {
                url = "AdminOrderController";                // Admin: quản lý đơn hàng
            } else if ("AdminUsers".equals(action)) {
                url = "AdminUserController";                 // Admin: quản lý người dùng
            }
        } catch (Exception e) {
            log("Error at MainController: " + e.toString());
        } finally {
            // Forward đến controller/trang đích tương ứng
            request.getRequestDispatcher(url).forward(request, response);
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
        return "Main Controller";
    }
}
