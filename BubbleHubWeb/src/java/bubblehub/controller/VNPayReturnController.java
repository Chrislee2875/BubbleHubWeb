// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.OrderDAO;
import bubblehub.dao.PaymentDAO;
import bubblehub.vnpay.VNPayConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

// Servlet xử lý callback từ VNPay sau khi giao dịch hoàn tất
@WebServlet(name = "VNPayReturnController", urlPatterns = {"/VNPayReturnController"})
public class VNPayReturnController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Thu thập tất cả tham số VNPay trả về trong request
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = req.getParameter(fieldName);

            // Bỏ qua tham số "action" của MainController (không phải từ VNPay)
            if ("action".equals(fieldName)) {
                continue;
            }

            if (fieldValue != null && fieldValue.length() > 0) {
                // Encode tên và giá trị để tạo chữ ký xác minh
                String encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                fields.put(encodedName, encodedValue);
            }
        }

        // Lấy chữ ký từ VNPay và loại bỏ khỏi map trước khi tính lại
        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Tính lại chữ ký từ dữ liệu nhận được để xác minh
        String signValue = VNPayConfig.hashAllFields(fields);
        String orderId = req.getParameter("vnp_TxnRef");    // Mã đơn hàng
        String amountStr = req.getParameter("vnp_Amount");   // Số tiền (×100)

        boolean transResult = false;
        if (signValue.equals(vnp_SecureHash)) {
            // Chữ ký hợp lệ → cập nhật trạng thái đơn hàng và thanh toán
            OrderDAO orderDAO = new OrderDAO();
            PaymentDAO paymentDAO = new PaymentDAO();
            int orderIdInt = Integer.parseInt(orderId);

            if ("00".equals(req.getParameter("vnp_TransactionStatus"))) {
                // "00" = thanh toán thành công
                orderDAO.updateStatus(orderIdInt, "PAID");
                paymentDAO.updateStatus(orderIdInt, "SUCCESS", new Timestamp(System.currentTimeMillis()));
                transResult = true;
            } else {
                // Giao dịch thất bại hoặc bị từ chối
                orderDAO.updateStatus(orderIdInt, "CANCELLED");
                paymentDAO.updateStatus(orderIdInt, "FAILED", new Timestamp(System.currentTimeMillis()));
            }
        }
        // Nếu chữ ký không khớp: có thể bị giả mạo, không cập nhật gì

        // Chuyển đổi amount về đơn vị VND thực tế (chia 100)
        double amount = 0;
        if (amountStr != null) {
            try { amount = Double.parseDouble(amountStr) / 100; } catch (NumberFormatException ignored) {}
        }

        // Truyền kết quả sang JSP để hiển thị thông báo cho người dùng
        req.setAttribute("transResult", transResult);
        req.setAttribute("orderId", orderId);
        req.setAttribute("amount", amount);
        req.getRequestDispatcher("jsp/paymentResult.jsp").forward(req, resp);
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
        return "VNPayReturnController";
    }
}
