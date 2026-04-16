// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.ProductDAO;
import bubblehub.model.Product;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Servlet hiển thị chi tiết của một sản phẩm cụ thể
@WebServlet(name = "ProductDetailController", urlPatterns = {"/ProductDetailController"})
public class ProductDetailController extends HttpServlet {

    // Trang JSP hiển thị chi tiết sản phẩm
    private static final String SUCCESS = "jsp/productDetail.jsp";
    // Chuyển hướng về menu nếu sản phẩm không tồn tại
    private static final String ERROR = "MainController?action=Menu";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = SUCCESS;
        try {
            // Lấy pid từ request
            String pidParam = request.getParameter("pid");
            if (pidParam == null) {
                response.sendRedirect(ERROR); // Không có pid → về menu
                return;
            }
            int pid = Integer.parseInt(pidParam);
            ProductDAO dao = new ProductDAO();
            Product product = dao.getById(pid);

            if (product == null) {
                response.sendRedirect(ERROR); // Sản phẩm không tồn tại → về menu
                return;
            }
            // Đẩy sản phẩm vào request để JSP hiển thị
            request.setAttribute("product", product);
        } catch (NumberFormatException e) {
            // pid không phải số hợp lệ
            log("Error at ProductDetailController: " + e.toString());
            response.sendRedirect(ERROR);
            return;
        } catch (Exception e) {
            log("Error at ProductDetailController: " + e.toString());
            response.sendRedirect(ERROR);
            return;
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
        return "Product Detail Controller";
    }
}
