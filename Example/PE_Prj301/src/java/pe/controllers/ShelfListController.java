package pe.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pe.model.IngredientDto;

@WebServlet(name = "ShelfListController", urlPatterns = {"/ShelfListController"})
public class ShelfListController extends HttpServlet {

    private static final String LOGIN_PAGE = "login.jsp";
    private static final String SUCCESS = "shelfList.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = LOGIN_PAGE;
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("LOGIN_USER_NAME") == null) {
            request.setAttribute("ERROR", "Please login first");
        } else {
            List<IngredientDto> shelfList = (List<IngredientDto>) session.getAttribute("SHELF_LIST");
            if (shelfList == null) {
                shelfList = new ArrayList<>();
            }
            request.setAttribute("SHELF_LIST", shelfList);
            url = SUCCESS;
        }
        request.getRequestDispatcher(url).forward(request, response);
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
