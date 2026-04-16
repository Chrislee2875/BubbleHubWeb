<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container">
    <div class="payment-result">
        <c:choose>
            <c:when test="${transResult == true}">
                <div class="result-icon">✅</div>
                <h2 style="color:#155724">Thanh toán thành công!</h2>
                <p style="color:var(--gray);margin-bottom:8px">
                    Mã đơn hàng: <strong>#${orderId}</strong>
                </p>
                <p style="color:var(--gray);margin-bottom:20px">
                    Số tiền: <strong><fmt:formatNumber value="${amount}" type="number" groupingUsed="true"/>đ</strong>
                </p>
                <div class="alert alert-success">
                    Cảm ơn bạn đã đặt hàng! Chúng tôi sẽ phục vụ bạn ngay.
                </div>
            </c:when>
            <c:otherwise>
                <div class="result-icon">❌</div>
                <h2 style="color:#721c24">Thanh toán thất bại!</h2>
                <p style="color:var(--gray);margin-bottom:20px">Giao dịch không thành công hoặc đã bị hủy.</p>
                <div class="alert alert-error">
                    Vui lòng thử lại hoặc liên hệ hỗ trợ.
                </div>
            </c:otherwise>
        </c:choose>
        <div style="display:flex;gap:12px;justify-content:center;margin-top:24px">
            <a href="${pageContext.request.contextPath}/MainController?action=Menu" class="btn btn-primary">🏠 Trang chủ</a>
            <a href="${pageContext.request.contextPath}/MainController?action=Order&subAction=history" class="btn btn-secondary">📋 Lịch sử đơn hàng</a>
        </div>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
