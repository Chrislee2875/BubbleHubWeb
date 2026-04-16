<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="auth-page">
    <div class="form-container auth-form-container">
        <h2>📝 Đăng ký</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/MainController?action=Register">
            <div class="form-group">
                <label for="name">Họ tên</label>
                <input type="text" id="name" name="name" class="form-control" placeholder="Nhập họ tên..." required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="Nhập email..." required>
            </div>
            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Nhập mật khẩu..." required>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Xác nhận mật khẩu</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Nhập lại mật khẩu..." required>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%">Đăng ký</button>
        </form>
        <p style="text-align:center;margin-top:16px;">
            Đã có tài khoản? <a href="${pageContext.request.contextPath}/MainController?action=Login">Đăng nhập</a>
        </p>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
