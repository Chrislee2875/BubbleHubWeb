// Package chứa các lớp model của ứng dụng
package bubblehub.model;

// Lớp đại diện cho một dòng hàng trong đơn hàng (1 sản phẩm cụ thể với số lượng, tùy chọn)
public class OrderItem {
    // ID dòng hàng, ID đơn hàng, ID sản phẩm
    private int orderItemId, orderId, pid;
    // Mức đường (0-100%), số lượng
    private int sugarLevel, quantity;
    // Mức đá (none/less/normal), tên sản phẩm, tên danh mục
    private String iceLevel, productName, categoryName;
    // Giá tại thời điểm đặt hàng
    private double price;

    // Constructor mặc định
    public OrderItem() {}

    // --- Getters & Setters ---
    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }
    public int getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(int sugarLevel) { this.sugarLevel = sugarLevel; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getIceLevel() { return iceLevel; }
    public void setIceLevel(String iceLevel) { this.iceLevel = iceLevel; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // Kiểm tra dòng hàng này có phải topping không (dựa vào categoryName)
    public boolean isTopping() { return Product.isToppingCategory(categoryName); }
}
