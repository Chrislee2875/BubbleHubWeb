// Package chứa các lớp model của ứng dụng
package bubblehub.model;

// Import Timestamp để lưu thời điểm tạo tài khoản
import java.sql.Timestamp;

// Lớp đại diện cho người dùng trong hệ thống (khách hàng hoặc admin)
public class User {
    // ID người dùng (khóa chính)
    private int uid;
    // Tên hiển thị, email đăng nhập, mật khẩu, vai trò (CUSTOMER/ADMIN)
    private String name, email, password, role;
    // Trạng thái tài khoản: true = đang hoạt động, false = bị khóa
    private boolean status;
    // Thời điểm tạo tài khoản
    private Timestamp createdAt;

    // Constructor mặc định
    public User() {}

    // --- Getters & Setters ---
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
