<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container page-content-left">
    <h1 class="page-title">📋 Lịch sử đơn hàng</h1>
    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <div class="empty-icon">📋</div>
                <p>Bạn chưa có đơn hàng nào.</p>
                <a href="${pageContext.request.contextPath}/MainController?action=Menu" class="btn btn-primary">Đặt hàng ngay</a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th>Chi tiết</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td>#${o.orderId}</td>
                            <td><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
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
                            <td>
                                <a href="${pageContext.request.contextPath}/MainController?action=Order&subAction=detail&orderId=${o.orderId}"
                                   class="btn btn-primary btn-sm">Xem chi tiết</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
