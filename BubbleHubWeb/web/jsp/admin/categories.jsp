<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="admin-wrapper">
    <div class="admin-sidebar">
        <h3>⚙ Quản lý</h3>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminDashboard">📊 Bảng điều khiển</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminProducts">🧋 Sản phẩm</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminCategories" class="active">📂 Danh mục</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders">📋 Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminUsers">👥 Người dùng</a>
    </div>
    <div class="admin-main">
        <h1>📂 Quản lý danh mục</h1>

        <!-- Add form -->
        <div style="background:var(--card-bg);border-radius:10px;padding:20px;max-width:400px;margin-bottom:24px;box-shadow:0 2px 8px rgba(0,0,0,0.06)">
            <h3 style="margin-bottom:12px;color:var(--primary)">➕ Thêm danh mục</h3>
            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminCategories" accept-charset="UTF-8"
                  style="display:flex;gap:8px">
                <input type="hidden" name="subAction" value="add">
                <input type="text" name="name" class="form-control" placeholder="Tên danh mục..." required>
                <button type="submit" class="btn btn-success">Thêm</button>
            </form>
        </div>

        <table class="data-table">
            <thead>
                <tr><th>ID</th><th>Tên danh mục</th><th>Thao tác</th></tr>
            </thead>
            <tbody>
                <c:forEach var="cat" items="${categories}">
                    <tr>
                        <td>${cat.cid}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminCategories" accept-charset="UTF-8"
                                  style="display:flex;gap:8px;align-items:center">
                                <input type="hidden" name="subAction" value="edit">
                                <input type="hidden" name="cid" value="${cat.cid}">
                                <input type="text" name="name" class="form-control" value="${cat.name}"
                                       style="max-width:200px" required>
                                <button type="submit" class="btn btn-warning btn-sm">💾 Lưu</button>
                            </form>
                        </td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminCategories" accept-charset="UTF-8"
                                  style="display:inline"
                                  onsubmit="return confirm('Xóa danh mục này?')">
                                <input type="hidden" name="subAction" value="delete">
                                <input type="hidden" name="cid" value="${cat.cid}">
                                <button type="submit" class="btn btn-danger btn-sm">🗑 Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty categories}">
                    <tr><td colspan="3" style="text-align:center;color:var(--gray)">Chưa có danh mục nào.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
