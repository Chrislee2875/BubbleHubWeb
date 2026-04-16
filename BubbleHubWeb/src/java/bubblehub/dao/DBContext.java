// Package chứa các lớp DAO (Data Access Object) - tầng truy cập database
package bubblehub.dao;

// Import các lớp cần thiết để kết nối database
import java.sql.*;

// Lớp cơ sở: thiết lập kết nối đến SQL Server, được kế thừa bởi tất cả DAO khác
public class DBContext {
    // Đối tượng Connection dùng chung cho các câu truy vấn SQL
    protected Connection connection;

    // Constructor: tự động mở kết nối khi khởi tạo DAO
    public DBContext() {
        try {
            // Nạp JDBC Driver cho Microsoft SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Mở kết nối đến database BubbleHub trên localhost cổng 1433
            // sendStringParametersAsUnicode=true: hỗ trợ tiếng Việt
            connection = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=BubbleHub;sendStringParametersAsUnicode=true", "sa", "12345");
        } catch (Exception e) {
            // In lỗi ra console nếu kết nối thất bại
            System.out.println("DBContext error: " + e.getMessage());
        }
    }
}
