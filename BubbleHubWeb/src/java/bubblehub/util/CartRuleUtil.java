// Package chứa các lớp tiện ích dùng chung
package bubblehub.util;

import bubblehub.model.CartItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

// Lớp tiện ích (không thể khởi tạo) chứa các quy tắc nghiệp vụ cho giỏ hàng
// Quy tắc chính: mỗi topping phải được gắn với 1 thức uống,
//               số lượng topping không được vượt quá số lượng thức uống
public final class CartRuleUtil {

    // Constructor private: ngăn không cho tạo instance của lớp này
    private CartRuleUtil() {}

    // Đảm bảo mỗi CartItem đều có lineKey (UUID duy nhất)
    // Gọi trước khi thao tác với giỏ hàng để tránh lỗi null key
    public static void ensureLineKeys(List<CartItem> cart) {
        if (cart == null) {
            return;
        }
        for (CartItem item : cart) {
            if (item.getLineKey() == null || item.getLineKey().trim().isEmpty()) {
                item.setLineKey(UUID.randomUUID().toString()); // Sinh key ngẫu nhiên
            }
        }
    }

    // Kiểm tra giỏ hàng có ít nhất 1 thức uống (không phải topping) không
    public static boolean hasDrinkItem(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }
        for (CartItem item : cart) {
            if (!item.isTopping()) {
                return true; // Tìm thấy ít nhất 1 thức uống
            }
        }
        return false;
    }

    // Trả về danh sách chỉ chứa các thức uống (bỏ qua topping)
    public static List<CartItem> getDrinkItems(List<CartItem> cart) {
        List<CartItem> drinks = new ArrayList<>();
        if (cart == null || cart.isEmpty()) {
            return drinks;
        }
        for (CartItem item : cart) {
            if (!item.isTopping()) {
                drinks.add(item);
            }
        }
        return drinks;
    }

    // Tìm thức uống theo lineKey trong giỏ hàng
    public static CartItem findDrinkByLineKey(List<CartItem> cart, String lineKey) {
        if (cart == null || lineKey == null || lineKey.trim().isEmpty()) {
            return null;
        }
        for (CartItem item : cart) {
            // Chỉ tìm trong các item là thức uống, khớp lineKey
            if (!item.isTopping() && lineKey.equals(item.getLineKey())) {
                return item;
            }
        }
        return null;
    }

    // Tính tổng số lượng topping đang gắn vào một thức uống (theo drinkLineKey)
    public static int totalAttachedToppingQty(List<CartItem> cart, String drinkLineKey) {
        if (cart == null || drinkLineKey == null || drinkLineKey.trim().isEmpty()) {
            return 0;
        }
        int total = 0;
        for (CartItem item : cart) {
            if (item.isTopping() && drinkLineKey.equals(item.getAttachedDrinkKey())) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    // Tính tổng số lượng topping gắn vào thức uống, ngoại trừ 1 topping cụ thể (toppingLineKey)
    // Dùng khi cập nhật topping để tính slot còn trống
    public static int totalAttachedToppingQtyExcept(List<CartItem> cart, String drinkLineKey, String toppingLineKey) {
        if (cart == null || drinkLineKey == null || drinkLineKey.trim().isEmpty()) {
            return 0;
        }
        int total = 0;
        for (CartItem item : cart) {
            if (!item.isTopping()) continue;                              // Bỏ qua thức uống
            if (!drinkLineKey.equals(item.getAttachedDrinkKey())) continue; // Không phải topping của drink này
            if (toppingLineKey != null && toppingLineKey.equals(item.getLineKey())) continue; // Bỏ qua topping cần loại trừ
            total += item.getQuantity();
        }
        return total;
    }

    // Xóa tất cả topping gắn vào một thức uống cụ thể (khi xóa thức uống khỏi giỏ)
    public static boolean removeLinkedToppings(List<CartItem> cart, String drinkLineKey) {
        if (cart == null || cart.isEmpty() || drinkLineKey == null || drinkLineKey.trim().isEmpty()) {
            return false;
        }
        // Xóa các item là topping và có attachedDrinkKey trùng với drinkLineKey
        return cart.removeIf(item -> item.isTopping() && drinkLineKey.equals(item.getAttachedDrinkKey()));
    }

    // Chuẩn hóa toàn bộ giỏ hàng: sửa liên kết lỗi, điều chỉnh số lượng topping theo slot
    // Trả về true nếu có thay đổi trong giỏ hàng
    public static boolean normalizeToppingLinks(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }

        ensureLineKeys(cart); // Đảm bảo tất cả item có lineKey
        boolean changed = false;

        // Tìm thức uống dự phòng (fallback) - dùng khi topping mất liên kết
        CartItem fallbackDrink = null;
        for (CartItem item : cart) {
            if (!item.isTopping()) {
                fallbackDrink = item;
                break;
            }
        }

        // Duyệt qua tất cả topping, kiểm tra và sửa liên kết
        Iterator<CartItem> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (!item.isTopping()) {
                continue; // Chỉ xử lý topping
            }

            // Tìm thức uống parent của topping này
            CartItem parent = findDrinkByLineKey(cart, item.getAttachedDrinkKey());
            if (parent == null && fallbackDrink != null) {
                // Không tìm thấy drink gốc → gắn vào drink dự phòng
                item.setAttachedDrinkKey(fallbackDrink.getLineKey());
                item.setAttachedDrinkName(fallbackDrink.getProductName());
                parent = fallbackDrink;
                changed = true;
            }

            if (parent == null) {
                // Không có drink nào → xóa topping này khỏi giỏ
                iterator.remove();
                changed = true;
                continue;
            }

            // Đồng bộ tên drink nếu tên đã thay đổi
            if (item.getAttachedDrinkName() == null || !item.getAttachedDrinkName().equals(parent.getProductName())) {
                item.setAttachedDrinkName(parent.getProductName());
                changed = true;
            }
        }

        // Điều chỉnh số lượng topping không vượt quá số lượng thức uống tương ứng
        for (CartItem drink : getDrinkItems(cart)) {
            int quota = Math.max(0, drink.getQuantity()); // Số slot topping tối đa
            int used = 0;
            for (CartItem topping : cart) {
                if (!topping.isTopping() || !drink.getLineKey().equals(topping.getAttachedDrinkKey())) {
                    continue;
                }
                int available = quota - used;
                if (available <= 0) {
                    topping.setQuantity(0); // Không còn slot → xóa khỏi giỏ (sẽ remove ở dưới)
                    changed = true;
                    continue;
                }
                if (topping.getQuantity() > available) {
                    topping.setQuantity(available); // Cắt giảm số lượng cho vừa slot
                    changed = true;
                }
                used += topping.getQuantity();
            }
        }

        // Xóa các item có số lượng = 0
        boolean removedZeroQty = cart.removeIf(item -> item.getQuantity() <= 0);
        if (removedZeroQty) {
            changed = true;
        }

        return changed;
    }

    // Kiểm tra tính hợp lệ của toàn bộ liên kết topping-drink trong giỏ hàng
    // Trả về false nếu có topping nào không có drink parent hoặc vượt số lượng cho phép
    public static boolean hasValidToppingLinks(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return true; // Giỏ trống là hợp lệ
        }

        for (CartItem topping : cart) {
            if (!topping.isTopping()) {
                continue;
            }
            // Topping phải có drink parent tồn tại trong giỏ
            CartItem parent = findDrinkByLineKey(cart, topping.getAttachedDrinkKey());
            if (parent == null) {
                return false;
            }
        }

        // Tổng topping của mỗi drink không được vượt quá số lượng drink
        for (CartItem drink : getDrinkItems(cart)) {
            if (totalAttachedToppingQty(cart, drink.getLineKey()) > drink.getQuantity()) {
                return false;
            }
        }
        return true;
    }
}
