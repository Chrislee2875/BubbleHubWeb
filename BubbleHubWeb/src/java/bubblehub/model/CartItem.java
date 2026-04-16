// Package chứa các lớp model của ứng dụng
package bubblehub.model;

// Lớp đại diện cho một mặt hàng trong giỏ hàng (lưu trong session, không lưu DB)
public class CartItem {

    // ID sản phẩm, số lượng, mức đường (0-100%)
    private int pid, quantity, sugarLevel;
    // Tên sản phẩm, mức đá, tên danh mục
    private String productName, iceLevel, categoryName;
    // lineKey: khóa định danh duy nhất cho dòng này trong giỏ hàng (UUID)
    // attachedDrinkKey: lineKey của thức uống mà topping này gắn vào
    // attachedDrinkName: tên thức uống mà topping gắn vào (để hiển thị)
    private String lineKey, attachedDrinkKey, attachedDrinkName;
    // Giá đơn vị của sản phẩm
    private double price;
    // Đánh dấu đây là topping (true) hay thức uống (false)
    private boolean topping;

    // Constructor mặc định
    public CartItem() {
    }

    // --- Getters & Setters ---
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSugarLevel() {
        return sugarLevel;
    }
    public void setSugarLevel(int sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getIceLevel() {
        return iceLevel;
    }
    public void setIceLevel(String iceLevel) {
        this.iceLevel = iceLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // Khóa nhận dạng duy nhất của dòng cart item này
    public String getLineKey() {
        return lineKey;
    }
    public void setLineKey(String lineKey) {
        this.lineKey = lineKey;
    }

    // Khóa của thức uống mà topping này được gắn vào
    public String getAttachedDrinkKey() {
        return attachedDrinkKey;
    }
    public void setAttachedDrinkKey(String attachedDrinkKey) {
        this.attachedDrinkKey = attachedDrinkKey;
    }

    // Tên thức uống được gắn (dùng để hiển thị trên giao diện)
    public String getAttachedDrinkName() {
        return attachedDrinkName;
    }
    public void setAttachedDrinkName(String attachedDrinkName) {
        this.attachedDrinkName = attachedDrinkName;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isTopping() {
        return topping;
    }
    public void setTopping(boolean topping) {
        this.topping = topping;
    }

    // Tính thành tiền: giá × số lượng
    public double getSubtotal() {
        return price * quantity;
    }
}
