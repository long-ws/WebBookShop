package servlet.general;

import java.util.List;

import beans.Category;
import dao.CategoryDAO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.ImageUtils;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ImageUtils.init(sce.getServletContext());

        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAll();

        sce.getServletContext().setAttribute("categories", categories);
        System.out.println("Categories loaded into application scope: " + categories.size());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
