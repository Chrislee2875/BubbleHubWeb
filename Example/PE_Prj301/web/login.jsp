<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <h1>Login</h1>

        <form action="MainController" method="POST">
            Username:
            <input type="text" name="username" value="${requestScope.USERNAME}" required="" />
            <br/>
            Password:
            <input type="password" name="password" required="" />
            <br/>
            <button type="submit" name="action" value="Login">Login</button>
        </form>

        <p style="color:red; ${empty requestScope.ERROR ? 'display:none;' : ''}">
            ${requestScope.ERROR}
        </p>
    </body>
</html>
