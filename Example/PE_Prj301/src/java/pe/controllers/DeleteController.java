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

@WebServlet(name = "DeleteController", urlPatterns = {"/DeleteController"})
public class DeleteController extends HttpServlet {

    private static final String LOGIN_PAGE = "login.jsp";
    private static final String SUCCESS = "search.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = LOGIN_PAGE;
        HttpSession session = request.getSession(false);
        try {
            if (session == null || session.getAttribute("LOGIN_USER_NAME") == null) {
                request.setAttribute("ERROR", "Please login first");
            } else {
                String search = request.getParameter("search");
                if (search == null) {
                    search = "";
                }

                Integer ingredientID = parseIngredientID(request.getParameter("ingredientID"));
                IngredientDao dao = new IngredientDao();
                if (ingredientID == null) {
                    request.setAttribute("ERROR", "Ingredient ID is required");
                } else if (dao.delete(ingredientID)) {
                    request.setAttribute("MESSAGE", "Delete successfully");
                } else {
                    request.setAttribute("ERROR", "Delete failed");
                }

                List<IngredientDto> list = dao.search(search.trim());
                request.setAttribute("SEARCH", search);
                request.setAttribute("INGREDIENT_LIST", list);
                url = SUCCESS;
            }
        } catch (ClassNotFoundException | SQLException e) {
            log("Error at DeleteController: " + e.toString());
            request.setAttribute("ERROR", "Cannot delete ingredient");
            url = SUCCESS;
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private Integer parseIngredientID(String ingredientIDText) {
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
