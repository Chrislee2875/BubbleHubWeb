<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="admin-wrapper">
    <div class="admin-sidebar">
        <h3>⚙ Quản lý</h3>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminDashboard">📊 Bảng điều khiển</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminProducts" class="active">🧋 Sản phẩm</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminCategories">📂 Danh mục</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders">📋 Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminUsers">👥 Người dùng</a>
    </div>
    <div class="admin-main">
        <div class="page-header">
            <h1>${empty product ? '➕ Thêm sản phẩm' : '✏ Sửa sản phẩm'}</h1>
        </div>
        <div style="background:var(--card-bg);border-radius:10px;padding:24px;max-width:560px;box-shadow:0 2px 8px rgba(0,0,0,0.06)">
            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminProducts" accept-charset="UTF-8">
                <input type="hidden" name="subAction" value="${empty product ? 'add' : 'edit'}">
                <c:if test="${not empty product}">
                    <input type="hidden" name="pid" value="${product.pid}">
                </c:if>
                <div class="form-group">
                    <label>Tên sản phẩm</label>
                    <input type="text" name="name" class="form-control" value="${product.name}" required>
                </div>
                <div class="form-group">
                    <label>Danh mục</label>
                    <select name="cid" class="form-control" required>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.cid}" ${product.cid == cat.cid ? 'selected' : ''}>${cat.name}</option>
                        </c:forEach>
                    </select>
                    <small style="display:block;margin-top:6px;color:var(--gray)">
                        Danh mục có chứa chữ "Topping" sẽ được xử lý như topping (chỉ bán kèm thức uống).
                    </small>
                </div>
                <div class="form-group">
                    <label>Giá (đ)</label>
                    <input type="number" name="price" class="form-control" value="${product.price}" min="0" step="1000" required>
                </div>
                <div class="form-group">
                    <label>Đường dẫn ảnh</label>
                    <input type="text" name="image_path" class="form-control" value="${product.imagePath}" placeholder="vd: milk_tea.jpg">
                </div>
                <div class="form-group">
                    <label style="display:flex;align-items:center;gap:8px;cursor:pointer">
                        <input type="checkbox" name="is_available" value="1" ${(empty product || product.available) ? 'checked' : ''}>
                        Đang bán
                    </label>
                </div>
                <div style="display:flex;gap:10px">
                    <button type="submit" class="btn btn-primary">${empty product ? '➕ Thêm' : '💾 Lưu'}</button>
                    <a href="${pageContext.request.contextPath}/MainController?action=AdminProducts" class="btn btn-secondary">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
