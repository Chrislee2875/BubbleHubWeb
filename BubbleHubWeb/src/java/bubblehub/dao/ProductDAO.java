// Package chứa các lớp DAO
package bubblehub.dao;

// Import model Product, Topping và thư viện SQL, Collection
import bubblehub.model.Product;
import bubblehub.model.Topping;
import java.sql.*;
import java.util.*;

// DAO quản lý các thao tác với bảng Product
public class ProductDAO extends DBContext {

    // Lấy tất cả sản phẩm (bao gồm cả ngừng bán), có thể lọc theo từ khóa và/hoặc danh mục
    public List<Product> getAll(String keyword, Integer cid) {
        List<Product> list = new ArrayList<>();
        // Base query: JOIN với Category để lấy tên danh mục
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.name AS categoryName FROM Product p JOIN Category c ON p.cid=c.cid WHERE 1=1");
        // Thêm điều kiện tìm kiếm theo tên nếu có keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND p.name LIKE ?");
        }
        // Thêm điều kiện lọc theo danh mục nếu có cid
        if (cid != null && cid > 0) {
            sql.append(" AND p.cid=?");
        }
        sql.append(" ORDER BY p.pid");
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%"); // Tìm kiếm gần đúng (chứa keyword)
            }
            if (cid != null && cid > 0) {
                ps.setInt(idx++, cid);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProduct(rs));
        } catch (SQLException e) {
            System.out.println("ProductDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    // Lấy các sản phẩm đang bán (is_available=1), có hỗ trợ lọc keyword và danh mục
    public List<Product> getAvailable(String keyword, Integer cid) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.name AS categoryName FROM Product p JOIN Category c ON p.cid=c.cid WHERE p.is_available=1");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND p.name LIKE ?");
        }
        if (cid != null && cid > 0) {
            sql.append(" AND p.cid=?");
        }
        sql.append(" ORDER BY p.pid");
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (cid != null && cid > 0) {
                ps.setInt(idx++, cid);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProduct(rs));
        } catch (SQLException e) {
            System.out.println("ProductDAO.getAvailable: " + e.getMessage());
        }
        return list;
    }

    // Lấy thông tin 1 sản phẩm theo ID (kèm tên danh mục)
    public Product getById(int pid) {
        String sql = "SELECT p.*, c.name AS categoryName FROM Product p JOIN Category c ON p.cid=c.cid WHERE p.pid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProduct(rs);
        } catch (SQLException e) {
            System.out.println("ProductDAO.getById: " + e.getMessage());
        }
        return null;
    }

    // Lấy danh sách topping đang bán (lọc từ tất cả sản phẩm available)
    public List<Product> getAvailableToppings() {
        List<Product> toppings = new ArrayList<>();
        for (Product product : getAvailable(null, null)) {
            if (product.isTopping()) {
                toppings.add(product);
            }
        }
        return toppings;
    }

    // Thêm mới sản phẩm vào database
    public boolean insert(Product p) {
        String sql = "INSERT INTO Product(cid,name,price,image_path,is_available) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, p.getCid());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getImagePath());
            ps.setBoolean(5, p.isAvailable());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ProductDAO.insert: " + e.getMessage());
        }
        return false;
    }

    // Cập nhật thông tin sản phẩm theo pid
    public boolean update(Product p) {
        String sql = "UPDATE Product SET cid=?,name=?,price=?,image_path=?,is_available=? WHERE pid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, p.getCid());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getImagePath());
            ps.setBoolean(5, p.isAvailable());
            ps.setInt(6, p.getPid());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ProductDAO.update: " + e.getMessage());
        }
        return false;
    }

    // Bật/tắt trạng thái bán của sản phẩm (toggle is_available)
    public boolean toggleAvailability(int pid) {
        String sql = "UPDATE Product SET is_available = CASE WHEN is_available=1 THEN 0 ELSE 1 END WHERE pid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ProductDAO.toggleAvailability: " + e.getMessage());
        }
        return false;
    }

    // Xóa sản phẩm khỏi database theo pid
    public boolean delete(int pid) {
        String sql = "DELETE FROM Product WHERE pid=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ProductDAO.delete: " + e.getMessage());
        }
        return false;
    }

    // Hàm nội bộ: ánh xạ ResultSet thành đối tượng Product (hoặc Topping nếu là topping)
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setPid(rs.getInt("pid"));
        p.setCid(rs.getInt("cid"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setImagePath(rs.getString("image_path"));
        p.setAvailable(rs.getBoolean("is_available"));
        p.setCategoryName(rs.getString("categoryName"));
        // Nếu sản phẩm thuộc danh mục topping → trả về đối tượng Topping thay vì Product
        if (p.isTopping()) {
            return Topping.fromProduct(p);
        }
        return p;
    }
}
