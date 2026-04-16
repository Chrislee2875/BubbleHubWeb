# Before & After Examples - MVC2 Refactoring

This document shows concrete before/after code examples for each refactored controller.

---

## 1. CheckoutController - Complete Example

### BEFORE
```java
@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=cart");
            return;
        }

        // ... complex business logic ...
        
        resp.sendRedirect(paymentUrl);
    }
}
```

### AFTER
```java
@WebServlet(name = "CheckoutController", urlPatterns = {"/CheckoutController"})
public class CheckoutController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/MainController?action=Cart");
            return;
        }

        // ... same complex business logic ...
        
        resp.sendRedirect(paymentUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "CheckoutController";
    }
}
```

---

## 2. OrderController - Complete Example

### BEFORE
```java
@WebServlet("/order")
public class OrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("subAction");
        if (action == null || action.trim().isEmpty()) {
            action = req.getParameter("action");
        }
        User user = (User) req.getSession().getAttribute("user");
        OrderDAO dao = new OrderDAO();

        if ("detail".equals(action)) {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            req.setAttribute("order", dao.getById(orderId));
            req.setAttribute("items", dao.getItemsByOrder(orderId));
            req.getRequestDispatcher("/jsp/orderDetail.jsp").forward(req, resp);
        } else {
            req.setAttribute("orders", dao.getByUser(user.getUid()));
            req.getRequestDispatcher("/jsp/orderHistory.jsp").forward(req, resp);
        }
    }
}
```

### AFTER
```java
@WebServlet(name = "OrderController", urlPatterns = {"/OrderController"})
public class OrderController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("subAction");
        if (action == null || action.trim().isEmpty()) {
            action = req.getParameter("action");
        }
        User user = (User) req.getSession().getAttribute("user");
        OrderDAO dao = new OrderDAO();

        if ("detail".equals(action)) {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            req.setAttribute("order", dao.getById(orderId));
            req.setAttribute("items", dao.getItemsByOrder(orderId));
            req.getRequestDispatcher("jsp/orderDetail.jsp").forward(req, resp);
        } else {
            req.setAttribute("orders", dao.getByUser(user.getUid()));
            req.getRequestDispatcher("jsp/orderHistory.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "OrderController";
    }
}
```

**Key Differences:**
- Moved logic from `doGet()` to `processRequest()`
- Updated JSP paths: `/jsp/` → `jsp/`
- Both `doGet()` and `doPost()` now call `processRequest()`
- Added `getServletInfo()` method

---

## 3. ProfileController - Complete Example

### BEFORE
```java
@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        req.setAttribute("user", user);
        req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("subAction");
        if (action == null || action.trim().isEmpty()) {
            action = req.getParameter("action");
        }
        User user = (User) req.getSession().getAttribute("user");
        UserDAO dao = new UserDAO();

        if ("update".equals(action)) {
            String name = req.getParameter("name");
            user.setName(name);
            if (dao.updateProfile(user)) {
                req.getSession().setAttribute("user", user);
                req.setAttribute("message", "Cập nhật thông tin thành công!");
            } else {
                req.setAttribute("error", "Cập nhật thất bại!");
            }
        } else if ("changePassword".equals(action)) {
            // ... password change logic ...
        }
        req.setAttribute("user", user);
        req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
    }
}
```

### AFTER
```java
@WebServlet(name = "ProfileController", urlPatterns = {"/ProfileController"})
public class ProfileController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String method = req.getMethod();
        
        if ("GET".equals(method)) {
            User user = (User) req.getSession().getAttribute("user");
            req.setAttribute("user", user);
            req.getRequestDispatcher("jsp/profile.jsp").forward(req, resp);
        } else if ("POST".equals(method)) {
            String action = req.getParameter("subAction");
            if (action == null || action.trim().isEmpty()) {
                action = req.getParameter("action");
            }
            User user = (User) req.getSession().getAttribute("user");
            UserDAO dao = new UserDAO();

            if ("update".equals(action)) {
                String name = req.getParameter("name");
                user.setName(name);
                if (dao.updateProfile(user)) {
                    req.getSession().setAttribute("user", user);
                    req.setAttribute("message", "Cập nhật thông tin thành công!");
                } else {
                    req.setAttribute("error", "Cập nhật thất bại!");
                }
            } else if ("changePassword".equals(action)) {
                // ... same password change logic ...
            }
            req.setAttribute("user", user);
            req.getRequestDispatcher("jsp/profile.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "ProfileController";
    }
}
```

**Key Differences:**
- Consolidated GET and POST handling in single `processRequest()` method
- Updated JSP path: `/jsp/profile.jsp` → `jsp/profile.jsp`
- Both `doGet()` and `doPost()` now call `processRequest()`
- Added `getServletInfo()` method

---

## 4. ToppingSelectionController - Key Changes

### BEFORE - Redirect
```java
resp.sendRedirect(req.getContextPath() + "/MainController?action=topping-selection&drinkLineKey=" + value);
```

### AFTER - Redirect
```java
resp.sendRedirect(req.getContextPath() + "/MainController?action=ToppingSelection&drinkLineKey=" + value);
```

### BEFORE - JSP Dispatch
```java
req.getRequestDispatcher("/jsp/toppingSelection.jsp").forward(req, resp);
```

### AFTER - JSP Dispatch
```java
req.getRequestDispatcher("jsp/toppingSelection.jsp").forward(req, resp);
```

---

## 5. VNPayReturnController - Key Changes

### BEFORE
```java
@WebServlet("/vnpayReturn")
public class VNPayReturnController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ... logic here ...
        req.getRequestDispatcher("/jsp/paymentResult.jsp").forward(req, resp);
    }
}
```

### AFTER
```java
@WebServlet(name = "VNPayReturnController", urlPatterns = {"/VNPayReturnController"})
public class VNPayReturnController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ... same logic here ...
        req.getRequestDispatcher("jsp/paymentResult.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "VNPayReturnController";
    }
}
```

---

## 6. RepayController - Key Changes

### BEFORE - Redirects
```java
resp.sendRedirect(req.getContextPath() + "/MainController?action=login");
resp.sendRedirect(req.getContextPath() + "/MainController?action=order&subAction=history");
```

### AFTER - Redirects
```java
resp.sendRedirect(req.getContextPath() + "/MainController?action=Login");
resp.sendRedirect(req.getContextPath() + "/MainController?action=Order&subAction=history");
```

---

## Summary of Pattern Changes

| Aspect | Before | After |
|--------|--------|-------|
| **@WebServlet** | `@WebServlet("/path")` | `@WebServlet(name = "ControllerName", urlPatterns = {"/ControllerName"})` |
| **Main Logic** | In `doGet()` or `doPost()` | In `processRequest()` |
| **doGet()** | Contains logic | Calls `processRequest()` |
| **doPost()** | Contains logic | Calls `processRequest()` |
| **getServletInfo()** | Missing | Returns controller name |
| **Redirects** | `?action=cart` | `?action=Cart` (capitalized) |
| **JSP Dispatch** | `/jsp/file.jsp` | `jsp/file.jsp` (no leading slash) |

---

## Integration Points

When integrating these changes, ensure:

1. **MainController** routing matches the new controller names
2. **All JSP forms** use capitalized action names (Cart, Order, Login, etc.)
3. **Navigation links** point to capitalized action names
4. **HTML form actions** reference the controller names correctly

Example MainController routing:
```java
String action = request.getParameter("action");
if ("Checkout".equals(action)) {
    url = "CheckoutController";
} else if ("Order".equals(action)) {
    url = "OrderController";
} else if ("Profile".equals(action)) {
    url = "ProfileController";
}
// ... etc ...
```
