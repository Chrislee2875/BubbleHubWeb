<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container">
    <div class="form-container" style="max-width:600px">
        <h2>💳 Xác nhận thanh toán</h2>
        <c:choose>
            <c:when test="${empty sessionScope.cart}">
                <div class="empty-state">
                    <p>Giỏ hàng trống.</p>
                    <a href="${pageContext.request.contextPath}/MainController?action=Menu" class="btn btn-primary">Xem thực đơn</a>
                </div>
            </c:when>
            <c:otherwise>
                <table class="data-table" style="margin-bottom:16px">
                    <thead>
                        <tr><th>Sản phẩm</th><th>SL</th><th>Thành tiền</th></tr>
                    </thead>
                    <tbody>
                        <c:set var="total" value="0"/>
                        <c:forEach var="item" items="${sessionScope.cart}">
                            <tr>
                                <td>${item.productName}<br>
                                    <small style="color:var(--gray)">Đường ${item.sugarLevel}% |
                                        <c:choose>
                                            <c:when test="${item.iceLevel == 'no'}">Không đá</c:when>
                                            <c:when test="${item.iceLevel == 'less'}">Ít đá</c:when>
                                            <c:otherwise>Đá vừa</c:otherwise>
                                        </c:choose>
                                    </small>
                                </td>
                                <td>${item.quantity}</td>
                                <td><fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/>đ</td>
                            </tr>
                            <c:set var="total" value="${total + item.subtotal}"/>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="cart-total">
                    Tổng tiền: <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/>đ
                </div>
                <form method="post" action="${pageContext.request.contextPath}/MainController?action=Checkout" style="margin-top:20px">
                    <button type="submit" class="btn btn-success" style="width:100%;font-size:1.05rem;padding:12px">
                        🏦 Thanh toán qua VNPAY
                    </button>
                </form>
                <a href="${pageContext.request.contextPath}/MainController?action=Cart" class="btn btn-secondary"
                   style="width:100%;text-align:center;margin-top:10px;display:block">← Quay lại giỏ hàng</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
