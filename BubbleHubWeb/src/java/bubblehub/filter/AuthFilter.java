// Package chứa các lớp filter (bộ lọc request)
package bubblehub.filter;

// Import model User để kiểm tra session
import bubblehub.model.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

// Đăng ký filter áp dụng cho tất cả các URL cần xác thực đăng nhập
@WebFilter({"/MainController", "/main", "/cart", "/profile", "/checkout", "/order", "/vnpayReturn", "/topping-selection", "/admin/*"})
public class AuthFilter implements Filter {

    // Phương thức chính: kiểm tra xác thực và phân quyền trước khi cho phép request qua
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Đặt encoding UTF-8 cho tất cả POST request (hỗ trợ tiếng Việt)
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");
        }

        // Lấy thông tin user từ session (null nếu chưa đăng nhập)
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Xác định đường dẫn và hành động hiện tại
        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        String route = req.getParameter("action");
        if (route == null || route.trim().isEmpty()) {
            route = req.getParameter("go"); // Thử tham số "go" nếu không có "action"
        }

        // Kiểm tra đây có phải route của MainController không
        boolean isMainRoute = path.equals(contextPath + "/MainController")
                || path.equals(contextPath + "/main");
        boolean needsAuth = false;  // Route này có cần đăng nhập không?
        boolean needsAdmin = false; // Route này có cần quyền ADMIN không?

        if (isMainRoute) {
            if (route == null || route.trim().isEmpty()) {
                route = "menu"; // Mặc định là trang menu khi không có action
            }
            // Các action bắt đầu bằng "admin-" yêu cầu quyền admin
            needsAdmin = route.startsWith("admin-");
            // Các action sau đây yêu cầu đăng nhập
            needsAuth = needsAdmin
                    || "cart".equals(route)
                    || "profile".equals(route)
                    || "checkout".equals(route)
                    || "order".equals(route)
                    || "vnpayReturn".equals(route)
                    || "topping-selection".equals(route)
                    || "repay".equals(route);
        } else {
            // Với URL pattern không phải MainController
            needsAdmin = path.startsWith(contextPath + "/admin");
            needsAuth = needsAdmin
                    || path.equals(contextPath + "/cart")
                    || path.equals(contextPath + "/profile")
                    || path.equals(contextPath + "/checkout")
                    || path.equals(contextPath + "/order")
                    || path.equals(contextPath + "/vnpayReturn")
                    || path.equals(contextPath + "/topping-selection")
                    || path.equals(contextPath + "/repay");
        }

        // Nếu cần đăng nhập mà chưa có session user → chuyển về trang login
        if (needsAuth && user == null) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=login");
            return;
        }

        // Nếu cần quyền admin mà user không phải ADMIN → chuyển về trang menu
        if (needsAdmin && user != null && !"ADMIN".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=menu");
            return;
        }

        // Xác thực thành công, cho phép request tiếp tục
        chain.doFilter(request, response);
    }

    // Khởi tạo filter (không cần xử lý gì thêm)
    @Override
    public void init(FilterConfig config) throws ServletException {}

    // Hủy filter (không cần giải phóng tài nguyên)
    @Override
    public void destroy() {}
}
