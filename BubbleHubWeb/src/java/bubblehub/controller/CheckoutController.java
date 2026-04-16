// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.OrderDAO;
import bubblehub.dao.PaymentDAO;
import bubblehub.model.*;
import bubblehub.util.CartRuleUtil;
import bubblehub.vnpay.VNPayConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

// Servlet xử lý thanh toán: tạo đơn hàng và chuyển hướng sang cổng VNPay
@WebServlet(name = "CheckoutController", urlPatterns = {"/CheckoutController"})
public class CheckoutController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        // Nếu giỏ hàng trống → về trang giỏ hàng
        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Chuẩn hóa liên kết topping
        CartRuleUtil.normalizeToppingLinks(cart);

        // Phải có ít nhất 1 thức uống mới được thanh toán
        if (!CartRuleUtil.hasDrinkItem(cart)) {
            session.setAttribute("cartError", "Đơn hàng cần có ít nhất 1 thức uống.");
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Kiểm tra liên kết topping hợp lệ
        if (!CartRuleUtil.hasValidToppingLinks(cart)) {
            session.setAttribute("cartError", "Giỏ hàng topping không hợp lệ. Vui lòng kiểm tra lại giỏ hàng.");
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Tính tổng tiền đơn hàng
        double total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }

        OrderDAO orderDAO = new OrderDAO();
        PaymentDAO paymentDAO = new PaymentDAO();

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUid(user.getUid());
        order.setTotalAmount(total);

        int orderId = orderDAO.insertOrder(order);
        if (orderId < 1) {
            // Tạo đơn thất bại → về giỏ hàng
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // Lưu từng sản phẩm trong giỏ vào OrderItem
        for (CartItem cartItem : cart) {
            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setPid(cartItem.getPid());
            item.setSugarLevel(cartItem.getSugarLevel());
            item.setIceLevel(cartItem.getIceLevel());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPrice());
            orderDAO.insertOrderItem(item);
        }

        // Tạo bản ghi thanh toán trạng thái PENDING
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentMethod("VNPAY");
        payment.setPaymentStatus("PENDING");
        payment.setAmount(total);
        paymentDAO.insert(payment);

        // Xóa giỏ hàng sau khi đã tạo đơn
        cart.clear();

        // --- Xây dựng URL thanh toán VNPay ---
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = (long) (total * 100); // VNPay yêu cầu đơn vị là đồng (nhân 100)

        // Tập hợp các tham số gửi lên VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId)); // Mã tham chiếu giao dịch
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + orderId);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl); // URL callback
        vnp_Params.put("vnp_IpAddr", VNPayConfig.getIpAddress(req));

        // Thêm thời gian tạo và hết hạn giao dịch (GMT+7, hết hạn sau 15 phút)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sắp xếp các field theo alphabet để tạo chữ ký và query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder(); // Chuỗi dùng để tạo chữ ký
        StringBuilder query = new StringBuilder();    // Chuỗi query string URL
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                // Encode từng field để ghép vào URL
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // Tạo chữ ký HMAC-SHA512 để VNPay xác thực tính toàn vẹn
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

        // Chuyển người dùng sang cổng thanh toán VNPay
        resp.sendRedirect(paymentUrl);
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
        return "CheckoutController";
    }
}
