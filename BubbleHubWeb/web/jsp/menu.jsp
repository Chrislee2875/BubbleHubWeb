<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>

<div class="menu-layout">

    <!-- ===== SIDEBAR TRÁI ===== -->
    <aside class="menu-sidebar">

        <!-- Categories -->
        <nav class="sidebar-block" aria-label="Danh mục sản phẩm">
            <h3 class="sidebar-block-header">📋 Danh mục</h3>
            <ul class="sidebar-category-list">
                <li class="sidebar-category-item">
                    <a href="${pageContext.request.contextPath}/MainController?action=Menu<c:if test="${not empty keyword}">&amp;keyword=${keyword}</c:if>"
                       class="sidebar-category-link ${empty selectedCid ? 'active' : ''}">
                        🧋 Tất cả
                    </a>
                </li>
                <c:forEach var="cat" items="${categories}">
                    <li class="sidebar-category-item">
                        <a href="${pageContext.request.contextPath}/MainController?action=Menu&cid=${cat.cid}<c:if test="${not empty keyword}">&amp;keyword=${keyword}</c:if>"
                           class="sidebar-category-link ${selectedCid == cat.cid ? 'active' : ''}">
                            ${cat.name}
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </nav>

    </aside>

    <!-- ===== NỘI DUNG CHÍNH ===== -->
    <main class="menu-main">

        <!-- Tiêu đề -->
        <header class="menu-main-header">
            <h1 class="menu-main-title">Sản phẩm</h1>
            <span class="product-count">${products.size()} sản phẩm</span>
        </header>

        <!-- Products -->
        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-state">
                    <div class="empty-icon">🧋</div>
                    <p>Không tìm thấy sản phẩm nào.</p>
                    <a href="${pageContext.request.contextPath}/MainController?action=Menu" class="btn btn-primary">Xem tất cả</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach var="p" items="${products}">
                        <div class="product-card">
                            <c:choose>
                                <c:when test="${not empty p.imagePath}">
                                    <img src="${pageContext.request.contextPath}/images/${p.imagePath}" alt="${p.name}">
                                </c:when>
                                <c:otherwise>
                                    <div class="product-card-img-placeholder">🧋</div>
                                </c:otherwise>
                            </c:choose>
                            <div class="product-card-body">
                                <h3>
                                    ${p.name}
                                    <c:if test="${p.topping}">
                                        <span class="badge badge-paid" style="margin-left:6px">Topping</span>
                                    </c:if>
                                </h3>
                                <div class="category-label">📂 ${p.categoryName}</div>
                                <div class="price">
                                    <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>đ
                                </div>
                            </div>
                            <div class="product-card-footer">
                                <a href="${pageContext.request.contextPath}/MainController?action=ProductDetail&pid=${p.pid}"
                                   class="btn btn-primary" style="width:100%;text-align:center">Xem chi tiết</a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

</div>

<jsp:include page="/jsp/common/footer.jsp"/>
