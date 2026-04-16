<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Edit Ingredient</title>
    </head>
    <body>
        <div style="${empty sessionScope.LOGIN_USER_NAME ? '' : 'display:none;'}">
            <p style="color:red;">Please login first</p>
            <a href="login.jsp">Back to Login</a>
        </div>

        <div style="${empty sessionScope.LOGIN_USER_NAME ? 'display:none;' : ''}">
            <h1>Edit Ingredient</h1>

            <p style="color:red; ${empty requestScope.ERROR ? 'display:none;' : ''}">
                ${requestScope.ERROR}
            </p>

            <c:if test="${not empty requestScope.INGREDIENT}">
                <form action="MainController" method="POST">
                    <input type="hidden" name="action" value="Update" />
                    ID:
                    <input type="text" name="ingredientID" value="${requestScope.INGREDIENT.ingredientID}" readonly="" />
                    <br/>
                    Name:
                    <input type="text" name="ingredientName" value="${requestScope.INGREDIENT.ingredientName}" />
                    <br/>
                    Quantity:
                    <input type="number" step="0.01" name="quantity" value="${requestScope.INGREDIENT.quantity}" />
                    <br/>
                    Unit:
                    <input type="text" name="unit" value="${requestScope.INGREDIENT.unit}" />
                    <br/>
                    <button type="submit">Save</button>
                </form>
            </c:if>

            <p>
                <a href="MainController?action=Detail&ingredientID=${requestScope.INGREDIENT.ingredientID}">Back to Detail</a> |
                <a href="MainController?action=Search">Back to List</a>
            </p>
        </div>
    </body>
</html>
