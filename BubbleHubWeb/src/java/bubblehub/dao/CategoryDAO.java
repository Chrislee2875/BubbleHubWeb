// Package chứa các lớp DAO
package bubblehub.dao;

// Import model Category và các thư viện SQL, Collection
import bubblehub.model.Category;
import java.sql.*;
import java.util.*;

// DAO quản lý thao tác CRUD (Tạo/Đọc/Cập nhật/Xóa) cho bảng Category
public class CategoryDAO extends DBContext {

    // Lấy toàn bộ danh mục, sắp xếp theo cid
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category ORDER BY cid";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Ánh xạ từng dòng ResultSet thành đối tượng Category
                Category c = new Category();
                c.setCid(rs.getInt("cid"));
                c.setName(rs.getString("name"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("CategoryDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    // Lấy một danh mục theo ID
    public Category getById(int cid) {
        String sql = "SELECT * FROM Category WHERE cid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Category c = new Category();
                c.setCid(rs.getInt("cid"));
                c.setName(rs.getString("name"));
                return c;
            }
        } catch (SQLException e) {
            System.out.println("CategoryDAO.getById: " + e.getMessage());
        }
        return null; // Không tìm thấy
    }

    // Thêm mới danh mục vào database
    public boolean insert(Category c) {
        String sql = "INSERT INTO Category(name) VALUES(?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            return ps.executeUpdate() > 0; // true nếu thêm thành công
        } catch (SQLException e) {
            System.out.println("CategoryDAO.insert: " + e.getMessage());
        }
        return false;
    }

    // Cập nhật tên danh mục theo cid
    public boolean update(Category c) {
        String sql = "UPDATE Category SET name=? WHERE cid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setInt(2, c.getCid());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("CategoryDAO.update: " + e.getMessage());
        }
        return false;
    }

    // Xóa danh mục theo cid
    public boolean delete(int cid) {
        String sql = "DELETE FROM Category WHERE cid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("CategoryDAO.delete: " + e.getMessage());
        }
        return false;
    }
}
