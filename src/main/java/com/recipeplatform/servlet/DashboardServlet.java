package com.recipeplatform.servlet;

import com.recipeplatform.dao.RecipeDao;
import com.recipeplatform.dao.RecipeDaoImpl;
import com.recipeplatform.model.Recipe;
import com.recipeplatform.model.User;
import com.recipeplatform.util.HtmlUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Dashboard: GET /dashboard shows the user's recipes and their approval status. Login required. */
public class DashboardServlet extends BaseServlet {

    private final RecipeDao recipeDao = new RecipeDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);

        StringBuilder body = new StringBuilder();
        body.append("<section class=\"banner\"><h1>Welcome back, ").append(HtmlUtil.esc(user.getName())).append("!</h1>");
        body.append("<span class=\"role-pill\">").append(HtmlUtil.esc(user.getRole())).append("</span></section>");

        try {
            List<Recipe> mine = recipeDao.findByUser(user.getId());

            int approved = 0;
            int pending = 0;
            int rejected = 0;
            for (Recipe r : mine) {
                if ("APPROVED".equals(r.getStatus())) {
                    approved++;
                } else if ("PENDING".equals(r.getStatus())) {
                    pending++;
                } else if ("REJECTED".equals(r.getStatus())) {
                    rejected++;
                }
            }

            body.append("<div class=\"stats\">");
            body.append(stat(approved, "Approved", "stat-green"));
            body.append(stat(pending, "Pending", "stat-yellow"));
            body.append(stat(rejected, "Rejected", "stat-red"));
            body.append("</div>");

            body.append("<div class=\"actions\">");
            body.append("<a class=\"btn btn-primary\" href=\"/recipes/add\">+ Add Recipe</a>");
            body.append("<a class=\"btn btn-outline-dark\" href=\"/recipes\">Discover Recipes</a>");
            body.append("</div>");

            body.append("<h2>My Recipes</h2>");
            if (mine.isEmpty()) {
                body.append("<p class=\"empty\">You have not shared any recipes yet.</p>");
            } else {
                body.append("<div class=\"table-wrap\"><table><thead><tr>");
                body.append("<th>Title</th><th>Status</th><th>Submitted</th></tr></thead><tbody>");
                for (Recipe r : mine) {
                    body.append("<tr><td><a href=\"/recipe?id=").append(r.getId()).append("\">")
                            .append(HtmlUtil.esc(r.getTitle())).append("</a></td>");
                    body.append("<td>").append(HtmlUtil.badge(r.getStatus())).append("</td>");
                    body.append("<td>").append(HtmlUtil.esc(HtmlUtil.shortDate(r.getCreatedAt()))).append("</td></tr>");
                }
                body.append("</tbody></table></div>");
            }
        } catch (SQLException e) {
            log("Could not load dashboard", e);
            body.append(HtmlUtil.alert("error", "Could not load your recipes. Please try again."));
        }

        render(req, resp, "Dashboard", body.toString());
    }

    private String stat(int number, String label, String cssClass) {
        return "<div class=\"stat " + cssClass + "\"><div class=\"stat-number\">" + number
                + "</div><div>" + label + "</div></div>";
    }
}
