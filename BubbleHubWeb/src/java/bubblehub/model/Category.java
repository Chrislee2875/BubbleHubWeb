// Package chứa các lớp model (đối tượng dữ liệu) của ứng dụng
package bubblehub.model;

// Lớp đại diện cho danh mục sản phẩm (ví dụ: Trà sữa, Topping, Nước uống)
public class Category {
    // ID danh mục (khóa chính trong database)
    private int cid;
    // Tên danh mục
    private String name;

    // Constructor mặc định (không tham số)
    public Category() {}

    // Getter và Setter cho từng thuộc tính
    public int getCid() { return cid; }
    public void setCid(int cid) { this.cid = cid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
