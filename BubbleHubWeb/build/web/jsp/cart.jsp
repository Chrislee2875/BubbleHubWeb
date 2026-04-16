<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container page-content-left">
    <div class="page-header">
        <h1 class="page-title">🛒 Giỏ hàng</h1>
        <c:if test="${not empty sessionScope.cart}">
            <form method="post" action="${pageContext.request.contextPath}/MainController?action=Cart">
                <input type="hidden" name="subAction" value="clear">
                <button type="submit" class="btn btn-danger btn-sm">🗑 Xóa tất cả</button>
            </form>
        </c:if>
    </div>

    <c:if test="${not empty cartError}">
        <div class="alert alert-error">${cartError}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty sessionScope.cart}">
            <div class="empty-state">
                <div class="empty-icon">🛒</div>
                <p>Giỏ hàng của bạn đang trống.</p>
                <a href="${pageContext.request.contextPath}/MainController?action=Menu" class="btn btn-primary">Xem thực đơn</a>
            </div>
        </c:when>
        <c:otherwise>
            <form id="cart-update-form" method="post" action="${pageContext.request.contextPath}/MainController?action=Cart">
                <input type="hidden" name="subAction" value="bulk-update">
            </form>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>STT</th>
                        <th>Sản phẩm</th>
                        <th>🍬 Đường</th>
                        <th>🧊 Đá</th>
                        <th>Số lượng</th>
                        <th>Đơn giá</th>
                        <th>Thành tiền</th>
                        <th>Xóa</th>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="total" value="0"/>
                    <c:forEach var="item" items="${sessionScope.cart}" varStatus="st">
                        <tr>
                            <td>${st.index + 1}</td>
                            <td>
                                ${item.productName}
                                <c:if test="${item.topping}">
                                    <span class="badge badge-paid" style="margin-left:6px">Topping</span>
                                    <div style="font-size:0.82rem;color:var(--gray);margin-top:4px">
                                        Kèm: ${item.attachedDrinkName}
                                    </div>
                                </c:if>
                                <c:if test="${not item.topping}">
                                    <div style="margin-top:6px">
                                        <a href="${pageContext.request.contextPath}/MainController?action=ToppingSelection&drinkLineKey=${item.lineKey}"
                                           class="btn btn-warning btn-sm">+ Chọn topping</a>
                                    </div>
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
                            <td>
                                <input type="hidden" name="lineKey" value="${item.lineKey}" form="cart-update-form">
                                <input type="number" name="quantity" value="${item.quantity}"
                                       min="1" max="20" class="form-control" style="width:65px"
                                       data-price="${item.price}" data-idx="${st.index}"
                                       oninput="recalc()" form="cart-update-form">
                            </td>
                            <td><fmt:formatNumber value="${item.price}" type="number" groupingUsed="true"/>đ</td>
                            <td id="sub-${st.index}"><fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/>đ</td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/MainController?action=Cart">
                                    <input type="hidden" name="subAction" value="remove">
                                    <input type="hidden" name="index" value="${st.index}">
                                    <button type="submit" class="btn btn-danger btn-sm">✕</button>
                                </form>
                            </td>
                        </tr>
                        <c:set var="total" value="${total + item.subtotal}"/>
                    </c:forEach>
                </tbody>
            </table>

            <div class="cart-total">
                Tổng tiền: <span id="grand-total"><fmt:formatNumber value="${total}" type="number" groupingUsed="true"/></span>đ
            </div>

            <div class="cart-actions">
                <button type="submit" class="btn btn-secondary" form="cart-update-form">
                    ✓ Xác nhận thay đổi
                </button>
                <form method="post" action="${pageContext.request.contextPath}/MainController?action=Checkout">
                    <button type="submit" class="btn btn-success" style="font-size:1.05rem;padding:12px 28px">
                        💳 Thanh toán qua VNPAY
                    </button>
                </form>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<script>
function recalc() {
    var total = 0;
    document.querySelectorAll('input[data-price]').forEach(function(inp) {
        var qty = Math.max(1, parseInt(inp.value) || 1);
        var price = parseFloat(inp.dataset.price);
        var sub = price * qty;
        total += sub;
        var cell = document.getElementById('sub-' + inp.dataset.idx);
        if (cell) cell.textContent = sub.toLocaleString('vi-VN') + 'đ';
    });
    var gt = document.getElementById('grand-total');
    if (gt) gt.textContent = total.toLocaleString('vi-VN');
}
</script>
<jsp:include page="/jsp/common/footer.jsp"/>
