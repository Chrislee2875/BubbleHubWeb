<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Shelf List</title>
    </head>
    <body>
        <div style="${empty sessionScope.LOGIN_USER_NAME ? '' : 'display:none;'}">
            <p style="color:red;">Please login first</p>
            <a href="login.jsp">Back to Login</a>
        </div>

        <div style="${empty sessionScope.LOGIN_USER_NAME ? 'display:none;' : ''}">
            <h1>Shelf List</h1>
            <h3>
                Welcome, ${sessionScope.LOGIN_USER_NAME}
                <a href="MainController?action=Logout">Logout</a>
            </h3>

            <table border="1">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Quantity</th>
                        <th>Unit</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty requestScope.SHELF_LIST}">
                            <c:forEach var="item" items="${requestScope.SHELF_LIST}" varStatus="counter">
                                <tr>
                                    <td>${counter.count}</td>
                                    <td>${item.ingredientID}</td>
                                    <td>${item.ingredientName}</td>
                                    <td>${item.quantity}</td>
                                    <td>${item.unit}</td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5">Shelf is empty</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <p>
                <a href="MainController?action=Search">Back to List</a>
            </p>
        </div>
    </body>
</html>
