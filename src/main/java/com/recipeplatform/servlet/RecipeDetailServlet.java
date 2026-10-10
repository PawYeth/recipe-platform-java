package com.recipeplatform.servlet;

import com.recipeplatform.dao.RecipeDao;
import com.recipeplatform.dao.RecipeDaoImpl;
import com.recipeplatform.model.Recipe;
import com.recipeplatform.model.User;
import com.recipeplatform.util.HtmlUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * One recipe: GET /recipe?id=5
 * Approved recipes are public. Pending or rejected ones are visible only to their owner and admins.
 */
public class RecipeDetailServlet extends BaseServlet {

    private final RecipeDao recipeDao = new RecipeDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            notFound(req, resp);
            return;
        }

        try {
            Recipe recipe = recipeDao.findById(id);
            User user = currentUser(req);

            boolean visible = recipe != null && ("APPROVED".equals(recipe.getStatus())
                    || (user != null && (user.isAdmin() || user.getId() == recipe.getUserId())));
            if (!visible) {
                notFound(req, resp);
                return;
            }
            render(req, resp, recipe.getTitle(), detailHtml(recipe));
        } catch (SQLException e) {
            log("Could not load recipe", e);
            render(req, resp, "Error", HtmlUtil.alert("error", "Could not load this recipe. Please try again."));
        }
    }

    private String detailHtml(Recipe r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<article class=\"detail\">");
        sb.append("<a class=\"muted\" href=\"/recipes\">&larr; Back to recipes</a>");
        sb.append("<h1>").append(HtmlUtil.esc(r.getTitle())).append("</h1>");
        sb.append("<p class=\"muted\">by ").append(HtmlUtil.esc(r.getAuthorName()))
                .append(" &middot; ").append(HtmlUtil.esc(HtmlUtil.shortDate(r.getCreatedAt())))
                .append(" &middot; ").append(HtmlUtil.badge(r.getStatus())).append("</p>");
        sb.append("<p class=\"rating\">").append(HtmlUtil.stars(r.getAvgRating(), r.getRatingCount())).append("</p>");

        sb.append("<h2>Ingredients</h2><ul class=\"ingredients\">");
        for (String line : r.getIngredients().split("\\R")) {
            if (!line.isBlank()) {
                sb.append("<li>").append(HtmlUtil.esc(line.trim())).append("</li>");
            }
        }
        sb.append("</ul>");

        sb.append("<h2>Instructions</h2>");
        sb.append("<p class=\"instructions\">").append(HtmlUtil.esc(r.getInstructions())).append("</p>");
        sb.append("</article>");
        return sb.toString();
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        render(req, resp, "Not found",
                "<div class=\"empty\"><h1>Recipe not found</h1><p><a href=\"/recipes\">Back to recipes</a></p></div>");
    }
}
