package com.recipeplatform.servlet;

import com.recipeplatform.dao.RecipeDao;
import com.recipeplatform.dao.RecipeDaoImpl;
import com.recipeplatform.model.Recipe;
import com.recipeplatform.util.HtmlUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Discover page: GET /recipes lists approved recipes, optionally filtered with ?q=search. */
public class RecipesServlet extends BaseServlet {

    private final RecipeDao recipeDao = new RecipeDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = req.getParameter("q") == null ? "" : req.getParameter("q").trim();

        StringBuilder body = new StringBuilder();
        body.append("<h1 class=\"page-title\">Discover Recipes</h1>");
        body.append("<form class=\"search-bar\" method=\"get\" action=\"/recipes\">");
        body.append("<input type=\"text\" name=\"q\" placeholder=\"Search by title or ingredient\" value=\"")
                .append(HtmlUtil.esc(q)).append("\">");
        body.append("<button class=\"btn btn-primary\" type=\"submit\">Search</button>");
        body.append("</form>");

        try {
            List<Recipe> recipes = recipeDao.findApproved(q);
            if (recipes.isEmpty()) {
                body.append("<p class=\"empty\">No recipes found. Try a different search word.</p>");
            } else {
                body.append("<div class=\"grid\">");
                for (Recipe r : recipes) {
                    body.append(HtmlUtil.recipeCard(r));
                }
                body.append("</div>");
            }
        } catch (SQLException e) {
            log("Could not load recipes", e);
            body.append(HtmlUtil.alert("error", "Could not load recipes. Please try again."));
        }

        render(req, resp, "Discover Recipes", body.toString());
    }
}
