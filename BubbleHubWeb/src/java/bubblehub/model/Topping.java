// Package chứa các lớp model của ứng dụng
package bubblehub.model;

// Lớp Topping kế thừa Product - đại diện đặc biệt cho các sản phẩm thuộc danh mục topping
public class Topping extends Product {

    // Constructor mặc định
    public Topping() {}

    // Constructor copy: tạo Topping từ một đối tượng Product có sẵn
    public Topping(Product source) {
        if (source != null) {
            // Sao chép toàn bộ thuộc tính từ Product nguồn
            setPid(source.getPid());
            setCid(source.getCid());
            setName(source.getName());
            setImagePath(source.getImagePath());
            setCategoryName(source.getCategoryName());
            setPrice(source.getPrice());
            setAvailable(source.isAvailable());
        }
    }

    // Factory method: chuyển đổi Product thành Topping
    public static Topping fromProduct(Product source) {
        return new Topping(source);
    }

    // Override: Topping luôn trả về true cho isTopping()
    @Override
    public boolean isTopping() {
        return true;
    }
}
