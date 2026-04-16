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
        <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders" class="active">📋 Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/MainController?action=AdminUsers">👥 Người dùng</a>
    </div>
    <div class="admin-main">
        <h1>📋 Quản lý đơn hàng</h1>

        <!-- Filter tabs -->
        <div class="category-tabs" style="margin-bottom:20px">
            <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders"
               class="category-tab ${empty statusFilter ? 'active' : ''}">Tất cả</a>
            <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders&statusFilter=PENDING"
               class="category-tab ${statusFilter == 'PENDING' ? 'active' : ''}">⏳ Chờ xử lý</a>
            <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders&statusFilter=PAID"
               class="category-tab ${statusFilter == 'PAID' ? 'active' : ''}">💳 Đã thanh toán</a>
            <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders&statusFilter=SERVED"
               class="category-tab ${statusFilter == 'SERVED' ? 'active' : ''}">✅ Đã phục vụ</a>
            <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders&statusFilter=CANCELLED"
               class="category-tab ${statusFilter == 'CANCELLED' ? 'active' : ''}">❌ Đã hủy</a>
        </div>

        <table class="data-table">
            <thead>
                <tr>
                    <th>Mã đơn</th>
                    <th>Khách hàng</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                    <th>Ngày đặt</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="o" items="${orders}">
                    <tr>
                        <td>#${o.orderId}</td>
                        <td>${o.customerName}</td>
                        <td><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true"/>đ</td>
                        <td>
                            <c:choose>
                                <c:when test="${o.status == 'PENDING'}"><span class="badge badge-pending">Chờ xử lý</span></c:when>
                                <c:when test="${o.status == 'PAID'}"><span class="badge badge-paid">Đã thanh toán</span></c:when>
                                <c:when test="${o.status == 'SERVED'}"><span class="badge badge-served">Đã phục vụ</span></c:when>
                                <c:when test="${o.status == 'CANCELLED'}"><span class="badge badge-cancelled">Đã hủy</span></c:when>
                                <c:otherwise><span class="badge">${o.status}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/MainController?action=AdminOrders&subAction=detail&orderId=${o.orderId}"
                               class="btn btn-primary btn-sm">👁 Chi tiết</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty orders}">
                    <tr><td colspan="6" style="text-align:center;color:var(--gray)">Không có đơn hàng nào.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
