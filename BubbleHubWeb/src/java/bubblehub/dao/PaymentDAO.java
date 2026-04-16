// Package chứa các lớp DAO
package bubblehub.dao;

// Import model Payment và các thư viện SQL
import bubblehub.model.Payment;
import java.sql.*;

// DAO quản lý các thao tác với bảng Payment (thanh toán)
public class PaymentDAO extends DBContext {

    // Thêm mới một bản ghi thanh toán vào database
    public boolean insert(Payment p) {
        String sql = "INSERT INTO Payment(order_id,payment_method,payment_status,amount) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, p.getOrderId());
            ps.setString(2, p.getPaymentMethod()); // VD: VNPAY
            ps.setString(3, p.getPaymentStatus()); // VD: PENDING
            ps.setDouble(4, p.getAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("PaymentDAO.insert: " + e.getMessage());
        }
        return false;
    }

    // Cập nhật trạng thái thanh toán sau khi nhận phản hồi từ VNPay
    public boolean updateStatus(int orderId, String status, Timestamp paidAt) {
        String sql = "UPDATE Payment SET payment_status=?, paid_at=? WHERE order_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);    // SUCCESS hoặc FAILED
            ps.setTimestamp(2, paidAt); // Thời điểm thanh toán
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("PaymentDAO.updateStatus: " + e.getMessage());
        }
        return false;
    }

    // Lấy thông tin thanh toán theo orderId
    public Payment getByOrderId(int orderId) {
        String sql = "SELECT * FROM Payment WHERE order_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Ánh xạ dòng dữ liệu thành đối tượng Payment
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("payment_id"));
                p.setOrderId(rs.getInt("order_id"));
                p.setPaymentMethod(rs.getString("payment_method"));
                p.setPaymentStatus(rs.getString("payment_status"));
                p.setAmount(rs.getDouble("amount"));
                p.setPaidAt(rs.getTimestamp("paid_at"));
                return p;
            }
        } catch (SQLException e) {
            System.out.println("PaymentDAO.getByOrderId: " + e.getMessage());
        }
        return null;
    }
}
