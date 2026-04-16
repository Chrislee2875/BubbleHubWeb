// Package chứa các lớp model của ứng dụng
package bubblehub.model;

// Import Locale để chuẩn hóa chuỗi không phân biệt ngôn ngữ
import java.util.Locale;

// Lớp đại diện cho một sản phẩm trong thực đơn (thức uống hoặc topping)
public class Product {
    // ID sản phẩm và ID danh mục (khóa ngoại)
    private int pid, cid;
    // Tên sản phẩm, đường dẫn ảnh, tên danh mục (join từ bảng Category)
    private String name, imagePath, categoryName;
    // Giá sản phẩm
    private double price;
    // Trạng thái bán: true = đang bán, false = ngừng bán
    private boolean available;

    // Constructor mặc định
    public Product() {}

    // --- Getters & Setters ---
    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }
    public int getCid() { return cid; }
    public void setCid(int cid) { this.cid = cid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    // Kiểm tra xem sản phẩm này có phải topping không (dựa vào tên danh mục)
    public boolean isTopping() {
        return isToppingCategory(categoryName);
    }

    // Hàm tĩnh: kiểm tra tên danh mục có chứa từ khóa topping không
    // Xử lý các biến thể: "topping", "toping", "addon" (bỏ qua dấu cách và dấu gạch ngang)
    public static boolean isToppingCategory(String categoryName) {
        if (categoryName == null) {
            return false;
        }
        // Chuẩn hóa chuỗi: chữ thường, loại bỏ dấu cách và dấu gạch ngang
        String normalized = categoryName.trim().toLowerCase(Locale.ROOT);
        String condensed = normalized.replace(" ", "").replace("-", "");
        return condensed.contains("topping")
                || condensed.contains("toping")
                || condensed.contains("addon");
    }
}
