// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.OrderDAO;
import bubblehub.model.Order;
import bubblehub.model.User;
import bubblehub.vnpay.VNPayConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

// Servlet xử lý "thanh toán lại": cho phép thanh toán đơn hàng PENDING chưa được thanh toán
@WebServlet(name = "RepayController", urlPatterns = {"/RepayController"})
public class RepayController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        // Người dùng chưa đăng nhập → về trang login
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Login");
            return;
        }

        // Lấy orderId từ request
        int orderId;
        try {
            orderId = Integer.parseInt(req.getParameter("orderId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Order&subAction=history");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        Order order = orderDAO.getById(orderId);

        // Chỉ cho phép thanh toán lại nếu đơn PENDING và thuộc user hiện tại
        if (order == null || order.getUid() != user.getUid() || !"PENDING".equals(order.getStatus())) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Order&subAction=history");
            return;
        }

        // Chuyển tiền sang đơn vị VNPay yêu cầu (×100)
        long amount = (long) (order.getTotalAmount() * 100);

        // Xây dựng tham số gửi lên VNPay (tương tự CheckoutController)
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + orderId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VNPayConfig.getIpAddress(req));

        // Thêm thời gian tạo và hết hạn (15 phút)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Sắp xếp field, build chuỗi hash và query
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) { query.append('&'); hashData.append('&'); }
            }
        }

        // Tạo chữ ký và chuyển hướng sang VNPay
        String secureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        resp.sendRedirect(VNPayConfig.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + secureHash);
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
        return "RepayController";
    }
}
