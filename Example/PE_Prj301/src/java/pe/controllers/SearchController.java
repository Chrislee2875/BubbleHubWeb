package pe.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pe.model.IngredientDao;
import pe.model.IngredientDto;

@WebServlet(name = "SearchController", urlPatterns = {"/SearchController"})
public class SearchController extends HttpServlet {

    private static final String LOGIN_PAGE = "login.jsp";
    private static final String SUCCESS = "search.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = LOGIN_PAGE;
        HttpSession session = request.getSession(false);
        try {
            if (session != null && session.getAttribute("LOGIN_USER_NAME") != null) {
                String search = request.getParameter("search");
                if (search == null) {
                    search = "";
                }
                IngredientDao dao = new IngredientDao();
                List<IngredientDto> list = dao.search(search.trim());
                request.setAttribute("SEARCH", search);
                request.setAttribute("INGREDIENT_LIST", list);
                url = SUCCESS;
            } else {
                request.setAttribute("ERROR", "Please login first");
            }
        } catch (ClassNotFoundException | SQLException e) {
            log("Error at SearchController: " + e.toString());
            request.setAttribute("ERROR", "Cannot load ingredient list");
            url = SUCCESS;
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
