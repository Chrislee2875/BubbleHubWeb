<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container page-content-left">
    <h1 class="page-title">👤 Trang cá nhân</h1>

    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="profile-forms">
        <!-- Update Profile -->
        <div class="profile-form-card">
            <h3 style="color:var(--primary);margin-bottom:16px">✏ Cập nhật thông tin</h3>
            <form method="post" action="${pageContext.request.contextPath}/MainController?action=Profile">
                <input type="hidden" name="subAction" value="update">
                <div class="form-group">
                    <label>Họ tên</label>
                    <input type="text" name="name" class="form-control" value="${user.name}" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" class="form-control" value="${user.email}" disabled>
                </div>
                <button type="submit" class="btn btn-primary">Cập nhật</button>
            </form>
        </div>

        <!-- Change Password -->
        <div class="profile-form-card">
            <h3 style="color:var(--primary);margin-bottom:16px">🔒 Đổi mật khẩu</h3>
            <form method="post" action="${pageContext.request.contextPath}/MainController?action=Profile">
                <input type="hidden" name="subAction" value="changePassword">
                <div class="form-group">
                    <label>Mật khẩu cũ</label>
                    <input type="password" name="oldPassword" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Mật khẩu mới</label>
                    <input type="password" name="newPassword" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Xác nhận mật khẩu mới</label>
                    <input type="password" name="confirmPassword" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-warning">Đổi mật khẩu</button>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
