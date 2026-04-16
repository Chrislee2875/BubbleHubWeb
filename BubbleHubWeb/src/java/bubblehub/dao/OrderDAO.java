// Package chứa các lớp DAO
package bubblehub.dao;

// Import model Order, OrderItem và thư viện SQL, Collection
import bubblehub.model.Order;
import bubblehub.model.OrderItem;
import java.sql.*;
import java.util.*;

// DAO quản lý các thao tác với bảng Order và OrderItem
public class OrderDAO extends DBContext {

    // Thêm mới đơn hàng và trả về orderId được sinh tự động
    public int insertOrder(Order o) {
        String sql = "INSERT INTO [Order](uid,total_amount,status) VALUES(?,?,'PENDING')";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getUid());
            ps.setDouble(2, o.getTotalAmount());
            ps.executeUpdate();
            // Lấy ID được database sinh tự động (IDENTITY)
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("OrderDAO.insertOrder: " + e.getMessage());
        }
        return -1; // Trả về -1 nếu thất bại
    }

    // Thêm một dòng sản phẩm vào đơn hàng
    public boolean insertOrderItem(OrderItem item) {
        String sql = "INSERT INTO OrderItem(order_id,pid,sugar_level,ice_level,quantity,price) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getPid());
            ps.setInt(3, item.getSugarLevel());
            ps.setString(4, item.getIceLevel());
            ps.setInt(5, item.getQuantity());
            ps.setDouble(6, item.getPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("OrderDAO.insertOrderItem: " + e.getMessage());
        }
        return false;
    }

    // Lấy danh sách đơn hàng của một người dùng (mới nhất trước)
    public List<Order> getByUser(int uid) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM [Order] WHERE uid=? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, uid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapOrder(rs));
        } catch (SQLException e) {
            System.out.println("OrderDAO.getByUser: " + e.getMessage());
        }
        return list;
    }

    // Lấy tất cả đơn hàng (admin), có thể lọc theo trạng thái
    public List<Order> getAll(String statusFilter) {
        List<Order> list = new ArrayList<>();
        // JOIN với bảng [User] để lấy tên khách hàng
        String sql = "SELECT o.*, u.name AS customerName FROM [Order] o JOIN [User] u ON o.uid=u.uid";
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql += " WHERE o.status=?"; // Thêm điều kiện lọc nếu có
        }
        sql += " ORDER BY o.created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                ps.setString(1, statusFilter);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = mapOrder(rs);
                o.setCustomerName(rs.getString("customerName")); // Gán tên khách hàng
                list.add(o);
            }
        } catch (SQLException e) {
            System.out.println("OrderDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    // Lấy thông tin chi tiết 1 đơn hàng theo orderId (kèm tên khách hàng)
    public Order getById(int orderId) {
        String sql = "SELECT o.*, u.name AS customerName FROM [Order] o JOIN [User] u ON o.uid=u.uid WHERE o.order_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order o = mapOrder(rs);
                o.setCustomerName(rs.getString("customerName"));
                return o;
            }
        } catch (SQLException e) {
            System.out.println("OrderDAO.getById: " + e.getMessage());
        }
        return null;
    }

    // Lấy danh sách sản phẩm thuộc một đơn hàng (kèm tên sản phẩm và danh mục)
    public List<OrderItem> getItemsByOrder(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS productName, c.name AS categoryName "
                + "FROM OrderItem oi "
                + "JOIN Product p ON oi.pid=p.pid "
                + "JOIN Category c ON p.cid=c.cid "
                + "WHERE oi.order_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Ánh xạ từng dòng thành OrderItem
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setPid(rs.getInt("pid"));
                item.setSugarLevel(rs.getInt("sugar_level"));
                item.setIceLevel(rs.getString("ice_level"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                item.setProductName(rs.getString("productName"));
                item.setCategoryName(rs.getString("categoryName"));
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("OrderDAO.getItemsByOrder: " + e.getMessage());
        }
        return list;
    }

    // Cập nhật trạng thái đơn hàng (PENDING → PAID → SERVED / CANCELLED)
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE [Order] SET status=? WHERE order_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("OrderDAO.updateStatus: " + e.getMessage());
        }
        return false;
    }

    // Tính tổng doanh thu từ các đơn hàng đã thanh toán hoặc đã phục vụ
    public double getTotalRevenue() {
        String sql = "SELECT ISNULL(SUM(total_amount),0) FROM [Order] WHERE status IN ('PAID','SERVED')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.out.println("OrderDAO.getTotalRevenue: " + e.getMessage());
        }
        return 0;
    }

    // Đếm số đơn hàng theo trạng thái (dùng cho dashboard admin)
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM [Order] WHERE status=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("OrderDAO.countByStatus: " + e.getMessage());
        }
        return 0;
    }

    // Hàm nội bộ: ánh xạ ResultSet thành đối tượng Order
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setUid(rs.getInt("uid"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
