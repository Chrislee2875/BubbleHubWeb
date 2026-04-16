// Khai báo package chứa các lớp liên quan đến thanh toán VNPay
package bubblehub.vnpay;

// Import các thư viện cần thiết để tạo chữ ký HMAC và mã hóa
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
// Import để đọc thông tin request HTTP (lấy IP người dùng)
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
// Import để dùng thuật toán băm MD5, SHA-256
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

// Lớp cấu hình VNPay: chứa các thông số kết nối và các hàm tiện ích mã hóa
public class VNPayConfig {

    // URL cổng thanh toán của VNPay (môi trường sandbox - thử nghiệm)
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    // URL callback mà VNPay sẽ gọi về sau khi giao dịch hoàn tất
    public static String vnp_ReturnUrl = "http://localhost:8080/BubbleHubWeb/MainController?action=VNPayReturn";

    // Mã định danh merchant (đơn vị bán hàng) trên hệ thống VNPay
    public static String vnp_TmnCode = "4YUP19I4";

    // Khóa bí mật dùng để tạo chữ ký HMAC-SHA512, đảm bảo tính toàn vẹn dữ liệu
    public static String secretKey = "MDUIFDCRAKLNBPOFIAFNEKFRNMFBYEPX";

    // URL API của VNPay dùng để truy vấn trạng thái giao dịch
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    // Hàm băm chuỗi bằng thuật toán MD5, trả về chuỗi hex 32 ký tự
    public static String md5(String message) {
        String digest = null;
        try {
            // Khởi tạo đối tượng MessageDigest với thuật toán MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Thực hiện băm chuỗi đầu vào (encode UTF-8)
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            // Chuyển mảng byte thành chuỗi hex (mỗi byte → 2 ký tự hex)
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            // Nếu có lỗi, trả về chuỗi rỗng
            digest = "";
        }
        return digest;
    }

    // Hàm băm chuỗi bằng thuật toán SHA-256, bảo mật hơn MD5
    public static String Sha256(String message) {
        String digest = null;
        try {
            // Khởi tạo đối tượng MessageDigest với thuật toán SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Thực hiện băm chuỗi đầu vào (encode UTF-8)
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            // Chuyển mảng byte thành chuỗi hex
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            // Nếu có lỗi, trả về chuỗi rỗng
            digest = "";
        }
        return digest;
    }

    // Hàm tạo chữ ký cho toàn bộ các trường dữ liệu trong Map (dùng cho request VNPay)
    public static String hashAllFields(Map fields) {
        // Lấy danh sách tên field và sắp xếp theo thứ tự alphabet
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);

        // Duyệt qua từng field để ghép thành chuỗi dạng key=value&key=value...
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);

            // Chỉ thêm vào nếu giá trị không rỗng hoặc null
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }

            // Thêm dấu '&' giữa các cặp key=value (trừ phần tử cuối)
            if (itr.hasNext()) {
                sb.append("&");
            }
        }

        // Ký chuỗi đã ghép bằng HMAC-SHA512 với secretKey
        return hmacSHA512(secretKey, sb.toString());
    }

    // Hàm tạo chữ ký HMAC-SHA512: đảm bảo tính xác thực và toàn vẹn của dữ liệu
    public static String hmacSHA512(final String key, final String data) {
        try {
            // Kiểm tra đầu vào không được null
            if (key == null || data == null) {
                throw new NullPointerException();
            }

            // Khởi tạo đối tượng Mac với thuật toán HmacSHA512
            final Mac hmac512 = Mac.getInstance("HmacSHA512");

            // Tạo SecretKeySpec từ key (khóa bí mật) để cấu hình thuật toán
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKeySpec = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKeySpec);

            // Thực hiện ký dữ liệu đầu vào (encode UTF-8)
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            // Chuyển kết quả byte[] thành chuỗi hex
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            // Nếu có lỗi, trả về chuỗi rỗng
            return "";
        }
    }

    // Hàm lấy địa chỉ IP thực của người dùng từ HTTP request
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress;
        try {
            // Ưu tiên lấy IP từ header X-FORWARDED-FOR (khi request đi qua proxy/load balancer)
            ipAddress = request.getHeader("X-FORWARDED-FOR");

            // Nếu không có header đó, lấy IP trực tiếp từ kết nối
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Nếu có lỗi, trả về thông báo lỗi kèm nội dung exception
            ipAddress = "Invalid IP:" + e.getMessage();
        }
        return ipAddress;
    }

    // Hàm tạo chuỗi số ngẫu nhiên có độ dài cho trước (dùng làm mã giao dịch)
    public static String getRandomNumber(int len) {
        Random rnd = new Random();

        // Tập ký tự nguồn chỉ gồm các chữ số 0-9
        String chars = "0123456789";

        StringBuilder sb = new StringBuilder(len);

        // Lặp len lần, mỗi lần chọn ngẫu nhiên 1 ký tự từ tập chars
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
