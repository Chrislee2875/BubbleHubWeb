package pe.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pe.model.IngredientDao;
import pe.model.IngredientDto;

@WebServlet(name = "AddToShelfController", urlPatterns = {"/AddToShelfController"})
public class AddToShelfController extends HttpServlet {

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

                IngredientDao dao = new IngredientDao();
                Integer ingredientID = parseIngredientID(request.getParameter("ingredientID"));
                if (ingredientID == null) {
                    request.setAttribute("ERROR", "Ingredient ID is required");
                } else {
                    IngredientDto ingredient = dao.getById(ingredientID);
                    if (ingredient == null) {
                        request.setAttribute("ERROR", "Ingredient not found");
                    } else {
                        List<IngredientDto> shelfList = (List<IngredientDto>) session.getAttribute("SHELF_LIST");
                        if (shelfList == null) {
                            shelfList = new ArrayList<>();
                            session.setAttribute("SHELF_LIST", shelfList);
                        }
                        shelfList.add(ingredient);
                        request.setAttribute("MESSAGE", "Added to shelf");
                    }
                }

                List<IngredientDto> list = dao.search(search.trim());
                request.setAttribute("SEARCH", search);
                request.setAttribute("INGREDIENT_LIST", list);
                url = SUCCESS;
            }
        } catch (ClassNotFoundException | SQLException e) {
            log("Error at AddToShelfController: " + e.toString());
            request.setAttribute("ERROR", "Cannot add ingredient to shelf");
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
