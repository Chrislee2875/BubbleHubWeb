<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container">
    <div class="page-header">
        <h1>➕ Chọn topping cho thức uống</h1>
    </div>

    <c:if test="${not empty sessionScope.cartError}">
        <div class="alert alert-error">${sessionScope.cartError}</div>
        <c:remove var="cartError" scope="session"/>
    </c:if>

    <c:if test="${not empty drinkItem}">
        <div style="background:var(--card-bg);border-radius:10px;padding:20px;box-shadow:0 2px 8px rgba(0,0,0,0.06);margin-bottom:16px">
            <p><strong>Thức uống:</strong> ${drinkItem.productName}</p>
            <p><strong>Số lượng ly:</strong> ${drinkItem.quantity}</p>
            <p><strong>Topping còn có thể chọn:</strong> ${availableSlots}</p>
        </div>

        <c:choose>
            <c:when test="${availableSlots > 0 and not empty toppings}">
                <form method="post" action="${pageContext.request.contextPath}/MainController?action=ToppingSelection"
                      style="background:var(--card-bg);border-radius:10px;padding:20px;box-shadow:0 2px 8px rgba(0,0,0,0.06);margin-bottom:12px">
                    <input type="hidden" name="drinkLineKey" value="${drinkItem.lineKey}">
                    <input type="hidden" name="subAction" value="add">

                    <div class="form-group">
                        <label>Chọn topping</label>
                        <select name="toppingPid" class="form-control" required>
                            <c:forEach var="t" items="${toppings}">
                                <option value="${t.pid}">
                                    ${t.name} - <fmt:formatNumber value="${t.price}" type="number" groupingUsed="true"/>đ
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Số lượng topping</label>
                        <input type="number" name="quantity" class="form-control"
                               min="1" max="${availableSlots}" value="${suggestedQty}" style="width:120px">
                    </div>

                    <button type="submit" class="btn btn-primary">✅ Thêm topping và về giỏ hàng</button>
                </form>
            </c:when>
            <c:otherwise>
                <div class="alert alert-info">Thức uống này hiện không thể thêm topping.</div>
            </c:otherwise>
        </c:choose>

        <form method="post" action="${pageContext.request.contextPath}/MainController?action=ToppingSelection">
            <input type="hidden" name="drinkLineKey" value="${drinkItem.lineKey}">
            <input type="hidden" name="subAction" value="skip">
            <button type="submit" class="btn btn-secondary">Bỏ qua topping</button>
        </form>
    </c:if>

    <c:if test="${empty drinkItem}">
        <div class="alert alert-error">Không tìm thấy thức uống để chọn topping.</div>
        <a href="${pageContext.request.contextPath}/MainController?action=Cart" class="btn btn-secondary">Quay lại giỏ hàng</a>
    </c:if>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
