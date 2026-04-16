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

@WebServlet(name = "EditController", urlPatterns = {"/EditController"})
public class EditController extends HttpServlet {

    private static final String LOGIN_PAGE = "login.jsp";
    private static final String EDIT_PAGE = "ingredientEdit.jsp";
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
                String action = request.getParameter("action");
                if ("EditPage".equals(action)) {
                    url = showEditPage(request);
                } else {
                    url = updateIngredient(request);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            log("Error at EditController: " + e.toString());
            request.setAttribute("ERROR", "Cannot process edit request");
            url = EDIT_PAGE;
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private String showEditPage(HttpServletRequest request) throws ClassNotFoundException, SQLException {
        Integer ingredientID = parseIngredientID(request.getParameter("ingredientID"));
        if (ingredientID == null) {
            request.setAttribute("ERROR", "Ingredient ID is required");
            return LIST_CONTROLLER;
        }
        IngredientDao dao = new IngredientDao();
        IngredientDto ingredient = dao.getById(ingredientID);
        if (ingredient == null) {
            request.setAttribute("ERROR", "Ingredient not found");
            return LIST_CONTROLLER;
        }
        request.setAttribute("INGREDIENT", ingredient);
        return EDIT_PAGE;
    }

    private String updateIngredient(HttpServletRequest request) throws ClassNotFoundException, SQLException {
        String idText = getTrimmed(request, "ingredientID");
        String name = getTrimmed(request, "ingredientName");
        String quantityText = getTrimmed(request, "quantity");
        String unit = getTrimmed(request, "unit");

        if (idText.isEmpty() || name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            request.setAttribute("ERROR", "All fields are required");
            request.setAttribute("INGREDIENT", buildPreview(idText, name, quantityText, unit));
            return EDIT_PAGE;
        }

        Integer ingredientID = parseIngredientID(idText);
        Double quantity = parseQuantity(quantityText);
        if (ingredientID == null || quantity == null) {
            request.setAttribute("ERROR", "Ingredient ID and Quantity must be numeric");
            request.setAttribute("INGREDIENT", buildPreview(idText, name, quantityText, unit));
            return EDIT_PAGE;
        }

        IngredientDao dao = new IngredientDao();
        IngredientDto ingredient = new IngredientDto(ingredientID, name, quantity, unit);
        if (dao.update(ingredient)) {
            request.setAttribute("INGREDIENT", dao.getById(ingredientID));
            request.setAttribute("MESSAGE", "Update successfully");
            return DETAIL_PAGE;
        }
        request.setAttribute("ERROR", "Update failed");
        request.setAttribute("INGREDIENT", ingredient);
        return EDIT_PAGE;
    }

    private IngredientDto buildPreview(String idText, String name, String quantityText, String unit) {
        int ingredientID = 0;
        double quantity = 0;
        Integer parsedID = parseIngredientID(idText);
        Double parsedQuantity = parseQuantity(quantityText);
        if (parsedID != null) {
            ingredientID = parsedID;
        }
        if (parsedQuantity != null) {
            quantity = parsedQuantity;
        }
        return new IngredientDto(ingredientID, name, quantity, unit);
    }

    private String getTrimmed(HttpServletRequest request, String parameter) {
        String value = request.getParameter(parameter);
        return value == null ? "" : value.trim();
    }

    private Integer parseIngredientID(String idText) {
        if (idText == null || idText.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(idText.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseQuantity(String quantityText) {
        if (quantityText == null || quantityText.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(quantityText.trim());
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
