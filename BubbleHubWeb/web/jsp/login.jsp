<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="auth-page">
    <div class="form-container auth-form-container">
        <h2>🧋 Đăng nhập</h2>
        <c:if test="${param.registered == '1'}">
            <div class="alert alert-success">Đăng ký thành công! Vui lòng đăng nhập.</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/MainController?action=Login">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="Nhập email..." required>
            </div>
            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Nhập mật khẩu..." required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Đăng nhập</button>
        </form>
        <p style="text-align:center;margin-top:16px;">
            Chưa có tài khoản? <a href="${pageContext.request.contextPath}/MainController?action=Register">Đăng ký ngay</a>
        </p>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
