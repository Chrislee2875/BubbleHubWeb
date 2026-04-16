package pe.controllers;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pe.model.IngredientDao;
import pe.model.IngredientDto;

@WebServlet(name = "DetailController", urlPatterns = {"/DetailController"})
public class DetailController extends HttpServlet {

    private static final String LOGIN_PAGE = "login.jsp";
    private static final String DETAIL_PAGE = "ingredientDetail.jsp";
    private static final String LIST_CONTROLLER = "SearchController";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = LOGIN_PAGE;
        HttpSession session = request.getSession(false);
        try {
            if (session == null || session.getAttribute("LOGIN_USER_NAME") == null) {
                request.setAttribute("ERROR", "Please login first");
            } else {
                Integer ingredientID = getIngredientID(request);
                if (ingredientID == null) {
                    request.setAttribute("ERROR", "Ingredient ID is required");
                    url = LIST_CONTROLLER;
                } else {
                    IngredientDao dao = new IngredientDao();
                    IngredientDto ingredient = dao.getById(ingredientID);
                    if (ingredient == null) {
                        request.setAttribute("ERROR", "Ingredient not found");
                        url = LIST_CONTROLLER;
                    } else {
                        request.setAttribute("INGREDIENT", ingredient);
                        url = DETAIL_PAGE;
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            log("Error at DetailController: " + e.toString());
            request.setAttribute("ERROR", "Cannot load ingredient detail");
            url = LIST_CONTROLLER;
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private Integer getIngredientID(HttpServletRequest request) {
        String ingredientIDText = request.getParameter("ingredientID");
        if (ingredientIDText == null || ingredientIDText.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(ingredientIDText.trim());
        } catch (NumberFormatException e) {
            return null;
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
