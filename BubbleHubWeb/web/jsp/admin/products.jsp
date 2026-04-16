<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            <h1>🧋 Quản lý sản phẩm</h1>
            <a href="${pageContext.request.contextPath}/MainController?action=AdminProducts&subAction=form" class="btn btn-success">+ Thêm sản phẩm</a>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/MainController" class="search-bar" style="max-width:400px">
            <input type="hidden" name="action" value="AdminProducts">
            <input type="text" name="keyword" class="form-control" placeholder="Tìm kiếm..." value="${keyword}">
            <button type="submit" class="btn btn-primary">🔍</button>
        </form>
        <table class="data-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên sản phẩm</th>
                    <th>Phân loại</th>
                    <th>Danh mục</th>
                    <th>Giá</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${products}">
                    <tr>
                        <td>${p.pid}</td>
                        <td>${p.name}</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.topping}">
                                    <span class="badge badge-paid">Topping</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-served">Thức uống</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${p.categoryName}</td>
                        <td><fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>đ</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.available}"><span class="badge badge-active">Đang bán</span></c:when>
                                <c:otherwise><span class="badge badge-inactive">Ngừng bán</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/MainController?action=AdminProducts&subAction=form&pid=${p.pid}"
                               class="btn btn-warning btn-sm">✏ Sửa</a>
                            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminProducts" style="display:inline">
                                <input type="hidden" name="subAction" value="toggle">
                                <input type="hidden" name="pid" value="${p.pid}">
                                <button type="submit" class="btn btn-secondary btn-sm">⇄ Chuyển trạng thái</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/MainController?action=AdminProducts"
                                  style="display:inline"
                                  onsubmit="return confirm('Xóa sản phẩm này?')">
                                <input type="hidden" name="subAction" value="delete">
                                <input type="hidden" name="pid" value="${p.pid}">
                                <button type="submit" class="btn btn-danger btn-sm">🗑 Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty products}">
                    <tr><td colspan="7" style="text-align:center;color:var(--gray)">Không có sản phẩm nào.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
