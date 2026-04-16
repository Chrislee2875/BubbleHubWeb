# Bug Fixes After MVC2 Refactoring

## Critical Payment Issue - FIXED! ⚠️

### ⚠️ VNPay Payment Always Fails - CRITICAL FIX
**Issue:** Thanh toán VNPay luôn thất bại sau khi chuyển sang MVC2 (trước đó thành công)  
**Cause:** VNPayReturnController đang include parameter `action` vào signature verification. Parameter `action=VNPayReturn` là từ MainController, không phải từ VNPay, nên khi đưa vào hash sẽ làm signature không khớp.  

**Root Cause Analysis:**
- Old URL: `http://localhost:8080/BubbleHubWeb/vnpayReturn`  
  → Chỉ có VNPay parameters
- New URL: `http://localhost:8080/BubbleHubWeb/MainController?action=VNPayReturn`  
  → Có thêm parameter `action` từ MainController
- VNPayReturnController encode TẤT CẢ parameters → signature verification fail

**Fix:** Updated `VNPayReturnController.java`:
```java
// Before: Encode all parameters including 'action'
for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();) {
    String fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII.toString());
    String fieldValue = URLEncoder.encode(req.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
    if (fieldValue != null && fieldValue.length() > 0) {
        fields.put(fieldName, fieldValue);
    }
}

// After: Skip 'action' parameter - it's not from VNPay
for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();) {
    String fieldName = params.nextElement();
    String fieldValue = req.getParameter(fieldName);
    
    // Skip MainController's action parameter - it's not from VNPay
    if ("action".equals(fieldName)) {
        continue;
    }
    
    if (fieldValue != null && fieldValue.length() > 0) {
        String encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());
        String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
        fields.put(encodedName, encodedValue);
    }
}
```

---

## Issues Reported and Fixed

### 1. ✅ VNPay Return Not Available
**Issue:** `/BubbleHubWeb/vnpayReturn` resource not available - Thanh toán không được  
**Cause:** VNPayConfig still using old direct URL path  
**Fix:** Updated `VNPayConfig.java` line 15:
```java
// Before:
public static String vnp_ReturnUrl = "http://localhost:8080/BubbleHubWeb/vnpayReturn";

// After:
public static String vnp_ReturnUrl = "http://localhost:8080/BubbleHubWeb/MainController?action=VNPayReturn";
```

### 2. ✅ Admin Action Redirects - ALL Fixed
**Issue:** After performing admin actions (add/edit/delete/toggle), redirects to wrong page or menu  
**Cause:** Wrong action names in admin controller redirects (singular instead of plural)  

**Fixes Applied:**

#### 2a. AdminProductController ✅
```java
// Line 74 - Before:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminProduct");
// After:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminProducts");
```

#### 2b. AdminCategoryController ✅ (Reported: "thêm danh mục bị out ra menu")
```java
// Line 47 - Before:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminCategory");
// After:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminCategories");
```

#### 2c. AdminUserController ✅
```java
// Line 37 - Before:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminUser");
// After:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminUsers");
```

#### 2d. AdminOrderController ✅
```java
// Line 38 - Before:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminOrder");
// After:
resp.sendRedirect(req.getContextPath() + "/MainController?action=AdminOrders");
```

### 3. ✅ Menu Doesn't Show Products on First Load
**Issue:** Menu page doesn't automatically display products when opened without parameters  
**Cause:** MainController doesn't have default action handler  
**Fix:** Updated `MainController.java` to handle empty/null action:
```java
// Added default handler:
if (action == null || action.trim().isEmpty()) {
    url = "MenuController";
}
```

### 4. ✅ Order View/Detail Error
**Issue:** Clicking "Xem đơn hàng" (View Order) causes error - Bấm xem đơn hàng bị lỗi  
**Cause:** OrderController had problematic logic mixing `action` and `subAction` parameters  
**Fix:** Updated `OrderController.java` processRequest method:
```java
// Before: Mixed action/subAction logic that could fail
String action = req.getParameter("subAction");
if (action == null || action.trim().isEmpty()) {
    action = req.getParameter("action");
}

// After: Clear subAction handling with null checks
String subAction = req.getParameter("subAction");
if ("detail".equals(subAction)) {
    String orderIdStr = req.getParameter("orderId");
    if (orderIdStr != null && !orderIdStr.isEmpty()) {
        try {
            int orderId = Integer.parseInt(orderIdStr);
            // Show order detail
        } catch (NumberFormatException e) {
            // Show history if invalid orderId
        }
    }
} else {
    // Default: show order history
}
```

### 5. ✅ Bonus Fix: index.jsp Action Name
**Issue:** index.jsp using lowercase 'menu' instead of 'Menu'  
**Fix:** Updated `index.jsp`:
```jsp
// Before:
response.sendRedirect(request.getContextPath() + "/MainController?action=menu");

// After:
response.sendRedirect(request.getContextPath() + "/MainController?action=Menu");
```

## Action Name Standardization

All admin actions now use **plural form** consistently:
- ✅ `AdminDashboard` - Dashboard (no plural needed)
- ✅ `AdminProducts` - NOT AdminProduct
- ✅ `AdminCategories` - NOT AdminCategory
- ✅ `AdminOrders` - NOT AdminOrder
- ✅ `AdminUsers` - NOT AdminUser

## Testing Checklist

Please test these scenarios:

### Payment Flow
- [ ] Add products to cart
- [ ] Proceed to checkout
- [ ] Complete VNPay payment (sandbox)
- [ ] Verify redirect back to payment result page works
- [ ] Check order status updated to PAID

### Order Management
- [ ] Click "Đơn hàng" in header - should show order history
- [ ] Click on an order - should show order details
- [ ] View order detail with orderId parameter
- [ ] Navigate back to order history

### Admin Product Management
- [ ] Login as admin
- [ ] Go to Admin Products page
- [ ] Add new product - should stay on Admin Products
- [ ] Edit product - should stay on Admin Products
- [ ] Toggle product availability - should stay on Admin Products
- [ ] Delete product - should stay on Admin Products

### Admin Category Management ✨
- [ ] Go to Admin Categories page
- [ ] Add new category - should stay on Admin Categories (NOT menu)
- [ ] Edit category - should stay on Admin Categories
- [ ] Delete category - should stay on Admin Categories

### Admin Order Management ✨
- [ ] Go to Admin Orders page
- [ ] Update order status - should stay on Admin Orders
- [ ] View order detail
- [ ] Filter by status

### Admin User Management ✨
- [ ] Go to Admin Users page
- [ ] Toggle user status - should stay on Admin Users
- [ ] Delete user - should stay on Admin Users
- [ ] Search users

### Menu Page
- [ ] Open application homepage (index.jsp)
- [ ] Verify automatically shows menu with all products
- [ ] Search for products - should work
- [ ] Filter by category - should work
- [ ] Navigate back to menu - should show products

## Files Modified

1. `src/java/bubblehub/vnpay/VNPayConfig.java` - VNPay return URL
2. `src/java/bubblehub/controller/admin/AdminProductController.java` - Redirect action name
3. `src/java/bubblehub/controller/admin/AdminCategoryController.java` - Redirect action name ✨
4. `src/java/bubblehub/controller/admin/AdminUserController.java` - Redirect action name ✨
5. `src/java/bubblehub/controller/admin/AdminOrderController.java` - Redirect action name ✨
6. `src/java/bubblehub/controller/MainController.java` - Default action handler
7. `src/java/bubblehub/controller/OrderController.java` - Fixed subAction handling
8. `web/index.jsp` - Action name case

## Summary

**Total Fixes:** 8 files modified, covering:
- 1 VNPay configuration fix
- 4 Admin controller redirect fixes (Product, Category, Order, User)
- 1 MainController default handler
- 1 OrderController logic fix
- 1 index.jsp case fix

**All admin operations now redirect correctly to their respective pages!**

## Notes

- All changes maintain the MVC2 pattern
- No business logic changed
- Only routing and URL corrections made
- Consistent action naming: plural form for list pages
- Added proper null/error handling in OrderController
- These fixes ensure proper flow through MainController dispatcher

---
**Fixed on:** March 22, 2026  
**Status:** ✅ All reported issues resolved (8 fixes total)  
**Latest Update:** Fixed all admin redirect issues - categories, orders, users
