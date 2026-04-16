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

@WebServlet(name = "CreateController", urlPatterns = {"/CreateController"})
public class CreateController extends HttpServlet {

    private static final String LOGIN_PAGE = "login.jsp";
    private static final String CREATE_PAGE = "ingredientCreate.jsp";
    private static final String DETAIL_PAGE = "ingredientDetail.jsp";

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
                if ("CreatePage".equals(action)) {
                    url = showCreatePage(request);
                } else {
                    url = createIngredient(request);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            log("Error at CreateController: " + e.toString());
            request.setAttribute("ERROR", "Cannot process create request");
            url = CREATE_PAGE;
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private String showCreatePage(HttpServletRequest request) throws ClassNotFoundException, SQLException {
        IngredientDao dao = new IngredientDao();
        int newID = dao.generateNextId();
        request.setAttribute("NEW_ID", newID);
        return CREATE_PAGE;
    }

    private String createIngredient(HttpServletRequest request) throws ClassNotFoundException, SQLException {
        IngredientDao dao = new IngredientDao();

        String idText = getTrimmed(request, "ingredientID");
        String name = getTrimmed(request, "ingredientName");
        String quantityText = getTrimmed(request, "quantity");
        String unit = getTrimmed(request, "unit");

        if (idText.isEmpty()) {
            idText = String.valueOf(dao.generateNextId());
        }

        request.setAttribute("NEW_ID", idText);
        request.setAttribute("FORM_NAME", name);
        request.setAttribute("FORM_QUANTITY", quantityText);
        request.setAttribute("FORM_UNIT", unit);

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            request.setAttribute("ERROR", "All fields are required");
            return CREATE_PAGE;
        }

        Integer ingredientID = parseIngredientID(idText);
        Double quantity = parseQuantity(quantityText);
        if (ingredientID == null || quantity == null) {
            request.setAttribute("ERROR", "Ingredient ID and Quantity must be numeric");
            return CREATE_PAGE;
        }

        IngredientDto ingredient = new IngredientDto(ingredientID, name, quantity, unit);
        if (dao.create(ingredient)) {
            request.setAttribute("INGREDIENT", dao.getById(ingredientID));
            request.setAttribute("MESSAGE", "Create successfully");
            return DETAIL_PAGE;
        }
        request.setAttribute("ERROR", "Create failed");
        return CREATE_PAGE;
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
