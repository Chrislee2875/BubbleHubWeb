<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ingredient Detail</title>
    </head>
    <body>
        <div style="${empty sessionScope.LOGIN_USER_NAME ? '' : 'display:none;'}">
            <p style="color:red;">Please login first</p>
            <a href="login.jsp">Back to Login</a>
        </div>

        <div style="${empty sessionScope.LOGIN_USER_NAME ? 'display:none;' : ''}">
            <h1>Ingredient Detail</h1>

            <p style="color:green; ${empty requestScope.MESSAGE ? 'display:none;' : ''}">
                ${requestScope.MESSAGE}
            </p>
            <p style="color:red; ${empty requestScope.ERROR ? 'display:none;' : ''}">
                ${requestScope.ERROR}
            </p>

            <c:if test="${not empty requestScope.INGREDIENT}">
                <table border="1">
                    <tr><th>ID</th><td>${requestScope.INGREDIENT.ingredientID}</td></tr>
                    <tr><th>Name</th><td>${requestScope.INGREDIENT.ingredientName}</td></tr>
                    <tr><th>Quantity</th><td>${requestScope.INGREDIENT.quantity}</td></tr>
                    <tr><th>Unit</th><td>${requestScope.INGREDIENT.unit}</td></tr>
                </table>

                <p>
                    <a href="MainController?action=EditPage&ingredientID=${requestScope.INGREDIENT.ingredientID}">Edit</a> |
                    <a href="MainController?action=Search">Back to List</a>
                </p>
            </c:if>
        </div>
    </body>
</html>
