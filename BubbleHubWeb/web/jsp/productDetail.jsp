<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/jsp/common/header.jsp"/>
<div class="container">
    <a href="${pageContext.request.contextPath}/MainController?action=Menu" class="btn btn-secondary" style="margin-bottom:20px">← Quay lại menu</a>
    <div class="product-detail-card">
        <c:choose>
            <c:when test="${not empty product.imagePath}">
                <img src="${pageContext.request.contextPath}/images/${product.imagePath}"
                     alt="${product.name}" style="width:100%;border-radius:10px;margin-bottom:20px">
            </c:when>
            <c:otherwise>
                <div class="product-card-img-placeholder" style="border-radius:10px;margin-bottom:20px;font-size:6rem;height:200px">🧋</div>
            </c:otherwise>
        </c:choose>
        <h2 style="color:var(--primary);margin-bottom:8px">${product.name}</h2>
        <p style="color:var(--gray);margin-bottom:8px">📂 ${product.categoryName}</p>
        <p style="font-size:1.4rem;font-weight:bold;color:var(--light-brown);margin-bottom:20px">
            <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/>đ
        </p>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <c:choose>
                    <c:when test="${product.topping}">
                        <div class="alert alert-info">
                            Topping được chọn ở bước sau khi bạn thêm thức uống vào giỏ hàng.
                        </div>
                        <a href="${pageContext.request.contextPath}/MainController?action=Menu" class="btn btn-primary" style="width:100%;text-align:center">
                            Quay lại menu thức uống
                        </a>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/MainController?action=Cart">
                            <input type="hidden" name="subAction" value="add">
                            <input type="hidden" name="pid" value="${product.pid}">
                            <div class="form-group">
                                <label>🍬 Mức đường</label>
                                <div class="radio-group">
                                    <label><input type="radio" name="sugarLevel" value="0"> <span>0%</span></label>
                                    <label><input type="radio" name="sugarLevel" value="25"> <span>25%</span></label>
                                    <label><input type="radio" name="sugarLevel" value="50" checked> <span>50%</span></label>
                                    <label><input type="radio" name="sugarLevel" value="75"> <span>75%</span></label>
                                    <label><input type="radio" name="sugarLevel" value="100"> <span>100%</span></label>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>🧊 Mức đá</label>
                                <div class="radio-group">
                                    <label><input type="radio" name="iceLevel" value="no"> <span>Không đá</span></label>
                                    <label><input type="radio" name="iceLevel" value="less"> <span>Ít đá</span></label>
                                    <label><input type="radio" name="iceLevel" value="normal" checked> <span>Đá vừa</span></label>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="qty">Số lượng</label>
                                <input type="number" id="qty" name="quantity" class="form-control"
                                       value="1" min="1" max="20" style="width:100px">
                            </div>

                            <button type="submit" class="btn btn-primary" style="width:100%;font-size:1rem;padding:12px">
                                🛒 Thêm thức uống & chọn topping
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <div class="alert alert-info">Vui lòng đăng nhập để thêm vào giỏ hàng.</div>
                <a href="${pageContext.request.contextPath}/MainController?action=Login" class="btn btn-primary" style="width:100%;text-align:center">
                    Đăng nhập
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<jsp:include page="/jsp/common/footer.jsp"/>
