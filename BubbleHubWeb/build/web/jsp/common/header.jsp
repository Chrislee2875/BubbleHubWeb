<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bubble Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20260323a">
</head>
<body>
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/MainController?action=Menu">
        <span class="navbar-brand-icon" aria-hidden="true">🧋</span>
        <span class="navbar-brand-text">Bubble Hub</span>
    </a>

    <!-- Search bar giữa navbar -->
    <form method="get" action="${pageContext.request.contextPath}/MainController" class="navbar-search">
        <input type="hidden" name="action" value="Menu">
        <input type="text" name="keyword" class="navbar-search-input"
               placeholder="🔍 Tìm trà sữa..." value="${param.keyword}">
        <button type="submit" class="navbar-search-btn">Tìm</button>
    </form>

    <ul class="navbar-nav">
        <c:if test="${not empty sessionScope.user}">
            <li>
                <a href="${pageContext.request.contextPath}/MainController?action=Cart">
                    🛒 Giỏ hàng
                    <c:if test="${not empty sessionScope.cart}">
                        <span class="cart-badge">${sessionScope.cart.size()}</span>
                    </c:if>
                </a>
            </li>
            <li><a href="${pageContext.request.contextPath}/MainController?action=Order&subAction=history">Đơn hàng</a></li>
            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                <li><a href="${pageContext.request.contextPath}/MainController?action=AdminDashboard">⚙ Quản lý</a></li>
            </c:if>
            <li><a href="${pageContext.request.contextPath}/MainController?action=Profile">👤 ${sessionScope.user.name}</a></li>
            <li><a href="${pageContext.request.contextPath}/MainController?action=Logout">Đăng xuất</a></li>
        </c:if>
        <c:if test="${empty sessionScope.user}">
            <li><a href="${pageContext.request.contextPath}/MainController?action=Login">Đăng nhập</a></li>
            <li><a href="${pageContext.request.contextPath}/MainController?action=Register">Đăng ký</a></li>
        </c:if>
    </ul>
</nav>
