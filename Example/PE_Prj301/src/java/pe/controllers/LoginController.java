package pe.controllers;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pe.model.UserDao;
import pe.model.UserDto;

@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    private static final String ERROR = "login.jsp";
    private static final String SUCCESS = "SearchController";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            if (username == null) {
                username = "";
            }
            if (password == null) {
                password = "";
            }

            UserDao dao = new UserDao();
            UserDto user = dao.checkLogin(username.trim(), password.trim());
            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("LOGIN_USER", user);
                session.setAttribute("LOGIN_USER_NAME", user.getName());
                session.setAttribute("LOGIN_USERNAME", user.getUsername());
                url = SUCCESS;
            } else {
                request.setAttribute("USERNAME", username);
                request.setAttribute("ERROR", "Incorrect username or password");
            }
        } catch (ClassNotFoundException | SQLException e) {
            log("Error at LoginController: " + e.toString());
            request.setAttribute("ERROR", "Cannot login now");
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
