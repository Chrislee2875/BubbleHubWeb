<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="admin-wrapper">
    <div class="admin-sidebar">
        <h3>⚙ Quản lý</h3>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminDashboard">📊 Bảng điều khiển</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminProducts">🧋 Sản phẩm</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminCategories">📂 Danh mục</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders">📋 Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminUsers" class="active">👥 Người dùng</a>
    </div>
    <div class="admin-main">
        <h1>👥 Quản lý người dùng</h1>
        <form method="get" action="${pageContext.request.contextPath}/MainController" class="search-bar" style="max-width:400px">
            <input type="hidden" name="action" value="AdminUsers">
            <input type="text" name="keyword" class="form-control" placeholder="Tìm theo tên, email..." value="${keyword}">
            <button type="submit" class="btn btn-primary">🔍</button>
        </form>
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Họ tên</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Trạng thái</th>
                    <th>Ngày tạo</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td>${u.uid}</td>
                        <td>${u.name}</td>
                        <td>${u.email}</td>
                        <td>
                            <span class="badge ${u.role == 'ADMIN' ? 'badge-paid' : ''}" style="${u.role == 'ADMIN' ? '' : 'background:#e2e3e5;color:#383d41'}">
                                ${u.role}
                            </span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${u.status}"><span class="badge badge-active">Hoạt động</span></c:when>
                                <c:otherwise><span class="badge badge-inactive">Bị khóa</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatDate value="${u.createdAt}" pattern="dd/MM/yyyy"/></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminUsers" style="display:inline">
                                <input type="hidden" name="subAction" value="toggle">
                                <input type="hidden" name="uid" value="${u.uid}">
                                <button type="submit" class="btn btn-warning btn-sm">
                                    ${u.status ? '🔒 Khóa' : '🔓 Mở'}
                                </button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminUsers"
                                  style="display:inline"
                                  onsubmit="return confirm('Xóa người dùng này?')">
                                <input type="hidden" name="subAction" value="delete">
                                <input type="hidden" name="uid" value="${u.uid}">
                                <button type="submit" class="btn btn-danger btn-sm">🗑 Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}">
                    <tr><td colspan="7" style="text-align:center;color:var(--gray)">Không tìm thấy người dùng nào.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
