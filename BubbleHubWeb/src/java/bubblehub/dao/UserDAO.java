// Package chứa các lớp DAO
package bubblehub.dao;

// Import model User và thư viện SQL, Collection
import bubblehub.model.User;
import java.sql.*;
import java.util.*;

// DAO quản lý các thao tác với bảng [User]
public class UserDAO extends DBContext {

    // Đăng nhập: kiểm tra email + password và tài khoản đang active (status=1)
    public User login(String email, String password) {
        String sql = "SELECT * FROM [User] WHERE email=? AND password=? AND status=1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs); // Trả về User nếu đúng thông tin
        } catch (SQLException e) {
            System.out.println("UserDAO.login: " + e.getMessage());
        }
        return null; // Trả về null nếu sai hoặc tài khoản bị khóa
    }

    // Đăng ký tài khoản mới với role mặc định là CUSTOMER
    public boolean register(User u) {
        String sql = "INSERT INTO [User](name,email,password,role) VALUES(?,?,?,'CUSTOMER')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("UserDAO.register: " + e.getMessage());
        }
        return false;
    }

    // Kiểm tra email đã tồn tại trong database chưa (tránh trùng khi đăng ký)
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM [User] WHERE email=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0; // true nếu đã có email này
        } catch (SQLException e) {
            System.out.println("UserDAO.emailExists: " + e.getMessage());
        }
        return false;
    }

    // Cập nhật tên hiển thị của người dùng
    public boolean updateProfile(User u) {
        String sql = "UPDATE [User] SET name=? WHERE uid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setInt(2, u.getUid());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("UserDAO.updateProfile: " + e.getMessage());
        }
        return false;
    }

    // Đổi mật khẩu: phải cung cấp mật khẩu cũ đúng mới được cập nhật
    public boolean changePassword(int uid, String oldPwd, String newPwd) {
        String sql = "UPDATE [User] SET password=? WHERE uid=? AND password=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPwd);
            ps.setInt(2, uid);
            ps.setString(3, oldPwd); // Kiểm tra mật khẩu cũ đúng mới update
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("UserDAO.changePassword: " + e.getMessage());
        }
        return false;
    }

    // Lấy tất cả user (admin), có thể tìm kiếm theo tên hoặc email
    public List<User> getAllUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM [User]";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " WHERE name LIKE ? OR email LIKE ?"; // Tìm kiếm gần đúng
        }
        sql += " ORDER BY uid";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) {
            System.out.println("UserDAO.getAllUsers: " + e.getMessage());
        }
        return list;
    }

    // Bật/tắt trạng thái tài khoản (khóa hoặc mở khóa user)
    public boolean toggleStatus(int uid) {
        String sql = "UPDATE [User] SET status = CASE WHEN status=1 THEN 0 ELSE 1 END WHERE uid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, uid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("UserDAO.toggleStatus: " + e.getMessage());
        }
        return false;
    }

    // Xóa user khỏi database theo uid
    public boolean deleteUser(int uid) {
        String sql = "DELETE FROM [User] WHERE uid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, uid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("UserDAO.deleteUser: " + e.getMessage());
        }
        return false;
    }

    // Lấy thông tin user theo uid
    public User getById(int uid) {
        String sql = "SELECT * FROM [User] WHERE uid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, uid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            System.out.println("UserDAO.getById: " + e.getMessage());
        }
        return null;
    }

    // Hàm nội bộ: ánh xạ ResultSet thành đối tượng User
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUid(rs.getInt("uid"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getBoolean("status"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
