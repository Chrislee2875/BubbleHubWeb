<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container">
    <a href="${pageContext.request.contextPath}/MainController?action=Order&subAction=history" class="btn btn-secondary" style="margin-bottom:20px">← Lịch sử đơn hàng</a>
    <c:if test="${not empty order}">
        <div style="background:var(--card-bg);border-radius:10px;padding:24px;margin-bottom:20px;box-shadow:0 2px 8px rgba(0,0,0,0.06)">
            <h2 style="color:var(--primary);margin-bottom:12px">Đơn hàng #${order.orderId}</h2>
            <p><strong>Ngày đặt:</strong> <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
            <p><strong>Trạng thái:</strong>
                <c:choose>
                    <c:when test="${order.status == 'PENDING'}"><span class="badge badge-pending">Chờ xử lý</span></c:when>
                    <c:when test="${order.status == 'PAID'}"><span class="badge badge-paid">Đã thanh toán</span></c:when>
                    <c:when test="${order.status == 'SERVED'}"><span class="badge badge-served">Đã phục vụ</span></c:when>
                    <c:when test="${order.status == 'CANCELLED'}"><span class="badge badge-cancelled">Đã hủy</span></c:when>
                    <c:otherwise><span class="badge">${order.status}</span></c:otherwise>
                </c:choose>
            </p>
            <p><strong>Tổng tiền: </strong>
                <span style="font-size:1.2rem;color:var(--primary);font-weight:bold">
                    <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/>đ
                </span>
            </p>
            <c:if test="${order.status == 'PENDING'}">
                <form method="post" action="${pageContext.request.contextPath}/MainController?action=Repay" style="margin-top:14px">
                    <input type="hidden" name="orderId" value="${order.orderId}">
                    <button type="submit" class="btn btn-success">💳 Tiếp tục thanh toán VNPAY</button>
                </form>
            </c:if>
        </div>
        <h3 style="color:var(--primary);margin-bottom:12px">Chi tiết sản phẩm</h3>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Sản phẩm</th>
                    <th>🍬 Đường</th>
                    <th>🧊 Đá</th>
                    <th>SL</th>
                    <th>Đơn giá</th>
                    <th>Thành tiền</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${items}">
                    <tr>
                        <td>
                            ${item.productName}
                            <c:if test="${item.topping}">
                                <span class="badge badge-paid" style="margin-left:6px">Topping</span>
                            </c:if>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${item.topping}">-</c:when>
                                <c:otherwise>${item.sugarLevel}%</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${item.topping}">-</c:when>
                                <c:when test="${item.iceLevel == 'no'}">Không đá</c:when>
                                <c:when test="${item.iceLevel == 'less'}">Ít đá</c:when>
                                <c:otherwise>Đá vừa</c:otherwise>
                            </c:choose>
                        </td>
                        <td>${item.quantity}</td>
                        <td><fmt:formatNumber value="${item.price}" type="number" groupingUsed="true"/>đ</td>
                        <td><fmt:formatNumber value="${item.price * item.quantity}" type="number" groupingUsed="true"/>đ</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
