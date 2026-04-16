// Package chứa các Servlet controller của ứng dụng
package bubblehub.controller;

import bubblehub.dao.CategoryDAO;
import bubblehub.dao.ProductDAO;
import bubblehub.model.Category;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Servlet hiển thị trang thực đơn với hỗ trợ tìm kiếm và lọc theo danh mục
@WebServlet(name = "MenuController", urlPatterns = {"/MenuController"})
public class MenuController extends HttpServlet {

    // Trang JSP hiển thị thực đơn
    private static final String SUCCESS = "jsp/menu.jsp";

    // Xử lý request hiển thị menu: lấy danh sách danh mục và sản phẩm
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = SUCCESS;
        try {
            // Lấy tham số tìm kiếm từ request
            String keyword = request.getParameter("keyword"); // Từ khóa tìm kiếm theo tên
            String cidParam = request.getParameter("cid");    // Lọc theo danh mục
            Integer cid = null;
            if (cidParam != null && !cidParam.isEmpty()) {
                try {
                    cid = Integer.parseInt(cidParam);
                } catch (NumberFormatException ignored) {
                    // Bỏ qua nếu cid không phải số hợp lệ
                }
            }

            CategoryDAO catDao = new CategoryDAO();
            ProductDAO prodDao = new ProductDAO();
            List<Category> categories = catDao.getAll(); // Lấy tất cả danh mục

            // Xác định tên danh mục đang được chọn để hiển thị tiêu đề
            String selectedCategoryName = "Tất cả sản phẩm";
            boolean matchedCategory = false;
            if (cid != null) {
                for (Category category : categories) {
                    if (category.getCid() == cid) {
                        selectedCategoryName = category.getName();
                        matchedCategory = true;
                        break;
                    }
                }
                if (!matchedCategory) {
                    cid = null; // cid không tồn tại → bỏ lọc
                }
            }

            // Đẩy dữ liệu vào request để JSP sử dụng
            request.setAttribute("categories", categories);
            request.setAttribute("products", prodDao.getAvailable(keyword, cid)); // Chỉ lấy sản phẩm đang bán
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedCid", cid);
            request.setAttribute("selectedCategoryName", selectedCategoryName);
        } catch (Exception e) {
            log("Error at MenuController: " + e.toString());
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
        return "Menu Controller";
    }
}
