<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="admin-wrapper">
    <div class="admin-sidebar">
        <h3>⚙ Quản lý</h3>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminDashboard" class="active">📊 Bảng điều khiển</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminProducts">🧋 Sản phẩm</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminCategories">📂 Danh mục</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders">📋 Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminUsers">👥 Người dùng</a>
    </div>
    <div class="admin-main">
        <h1>📊 Bảng điều khiển</h1>
        <div class="stat-cards">
            <div class="stat-card">
                <div class="stat-value">
                    <fmt:formatNumber value="${totalRevenue}" type="number" groupingUsed="true"/>đ
                </div>
                <div class="stat-label">💰 Tổng doanh thu</div>
            </div>
            <div class="stat-card" style="border-left-color:#ffc107">
                <div class="stat-value" style="color:#856404">${countPending}</div>
                <div class="stat-label">⏳ Đơn chờ xử lý</div>
            </div>
            <div class="stat-card" style="border-left-color:#007bff">
                <div class="stat-value" style="color:#004085">${countPaid}</div>
                <div class="stat-label">💳 Đã thanh toán</div>
            </div>
            <div class="stat-card" style="border-left-color:#28a745">
                <div class="stat-value" style="color:#155724">${countServed}</div>
                <div class="stat-label">✅ Đã phục vụ</div>
            </div>
            <div class="stat-card" style="border-left-color:#D2691E">
                <div class="stat-value" style="color:#D2691E">${totalProducts}</div>
                <div class="stat-label">🧋 Tổng sản phẩm</div>
            </div>
            <div class="stat-card" style="border-left-color:#6c757d">
                <div class="stat-value" style="color:#495057">${totalUsers}</div>
                <div class="stat-label">👥 Tổng người dùng</div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
