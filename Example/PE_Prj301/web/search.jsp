<%-- 
    Document   : search
    Created on : Apr 26, 2025, 8:59:02 AM
    Author     : Computing Fundamental - HCM Campus
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ingredient List</title>
    </head>
    <body>
        <div style="${empty sessionScope.LOGIN_USER_NAME ? '' : 'display:none;'}">
            <p style="color:red;">Please login first</p>
            <a href="login.jsp">Back to Login</a>
        </div>

        <div style="${empty sessionScope.LOGIN_USER_NAME ? 'display:none;' : ''}">
            <h1>Ingredient List</h1>
            <h3>
                Welcome, ${sessionScope.LOGIN_USER_NAME}
                <a href="MainController?action=Logout">Logout</a>
            </h3>

            <p>
                <a href="MainController?action=CreatePage">Create New Ingredient</a> |
                <a href="MainController?action=ShelfList">Show Shelf</a>
            </p>

            <form action="MainController" method="GET">
                Search:
                <input type="text" name="search" value="${requestScope.SEARCH}" />
                <button type="submit" name="action" value="Search">Search</button>
            </form>

            <p style="color:green; ${empty requestScope.MESSAGE ? 'display:none;' : ''}">
                ${requestScope.MESSAGE}
            </p>
            <p style="color:red; ${empty requestScope.ERROR ? 'display:none;' : ''}">
                ${requestScope.ERROR}
            </p>

            <table border="1">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Quantity</th>
                        <th>Unit</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty requestScope.INGREDIENT_LIST}">
                            <c:forEach var="item" items="${requestScope.INGREDIENT_LIST}" varStatus="counter">
                                <tr>
                                    <td>${counter.count}</td>
                                    <td>${item.ingredientID}</td>
                                    <td>${item.ingredientName}</td>
                                    <td>${item.quantity}</td>
                                    <td>${item.unit}</td>
                                    <td>
                                        <form action="MainController" method="GET" style="display:inline;">
                                            <input type="hidden" name="action" value="Detail" />
                                            <input type="hidden" name="ingredientID" value="${item.ingredientID}" />
                                            <button type="submit">Detail</button>
                                        </form>
                                        <form action="MainController" method="POST" style="display:inline;">
                                            <input type="hidden" name="action" value="AddToShelf" />
                                            <input type="hidden" name="ingredientID" value="${item.ingredientID}" />
                                            <input type="hidden" name="search" value="${requestScope.SEARCH}" />
                                            <button type="submit">Add To Shelf</button>
                                        </form>
                                        <form action="MainController" method="POST" style="display:inline;">
                                            <input type="hidden" name="action" value="Delete" />
                                            <input type="hidden" name="ingredientID" value="${item.ingredientID}" />
                                            <input type="hidden" name="search" value="${requestScope.SEARCH}" />
                                            <button type="submit">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6">No ingredients found</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </body>
</html>
