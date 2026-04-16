// Package chứa các lớp model của ứng dụng
package bubblehub.model;

// Import Timestamp để lưu thời điểm thanh toán
import java.sql.Timestamp;

// Lớp đại diện cho thông tin thanh toán của một đơn hàng
public class Payment {
    // ID thanh toán và ID đơn hàng liên kết
    private int paymentId, orderId;
    // Phương thức thanh toán (VD: VNPAY) và trạng thái (PENDING/SUCCESS/FAILED)
    private String paymentMethod, paymentStatus;
    // Số tiền thanh toán
    private double amount;
    // Thời điểm thanh toán thành công
    private Timestamp paidAt;

    // Constructor mặc định
    public Payment() {}

    // --- Getters & Setters ---
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }
}
