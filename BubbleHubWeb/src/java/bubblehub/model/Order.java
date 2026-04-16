// Package chứa các lớp model của ứng dụng
package bubblehub.model;

// Import Timestamp để lưu thời điểm tạo đơn hàng
import java.sql.Timestamp;

// Lớp đại diện cho một đơn hàng trong hệ thống
public class Order {
    // ID đơn hàng và ID người dùng tạo đơn
    private int orderId, uid;
    // Tổng tiền của đơn hàng
    private double totalAmount;
    // Trạng thái đơn hàng: PENDING / PAID / SERVED / CANCELLED
    // Tên khách hàng (join từ bảng User khi cần hiển thị)
    private String status, customerName;
    // Thời điểm tạo đơn hàng
    private Timestamp createdAt;

    // Constructor mặc định
    public Order() {}

    // --- Getters & Setters ---
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
