package com.recipeplatform.servlet;

import com.recipeplatform.dao.ActivityDao;
import com.recipeplatform.dao.RecipeDao;
import com.recipeplatform.dao.RecipeDaoImpl;
import com.recipeplatform.model.User;
import com.recipeplatform.util.HtmlUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Add a recipe: GET /recipes/add shows the form, POST saves it as PENDING. Login required (AuthFilter). */
public class AddRecipeServlet extends BaseServlet {

    private static final int MAX_TITLE = 120;

    private final RecipeDao recipeDao = new RecipeDaoImpl();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, "Add Recipe", form("", "", "", null));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = currentUser(req);
        String title = value(req, "title");
        String ingredients = value(req, "ingredients");
        String instructions = value(req, "instructions");

        String error = null;
        if (title.isEmpty()) {
            error = "Please enter a title.";
        } else if (title.length() > MAX_TITLE) {
            error = "The title must be at most " + MAX_TITLE + " characters.";
        } else if (ingredients.isEmpty()) {
            error = "Please list at least one ingredient.";
        } else if (instructions.isEmpty()) {
            error = "Please write the instructions.";
        }
        if (error != null) {
            render(req, resp, "Add Recipe", form(title, ingredients, instructions, error));
            return;
        }

        try {
            int id = recipeDao.create(title, ingredients, instructions, user.getId());
            activityDao.log(user.getId(), "RECIPE_SUBMITTED", "Recipe #" + id + ": " + title);
            redirect(resp, "/dashboard", "msg", "Recipe submitted! It is pending approval.");
        } catch (SQLException e) {
            log("Could not save recipe", e);
            render(req, resp, "Add Recipe", form(title, ingredients, instructions,
                    "Could not save the recipe. Please try again."));
        }
    }

    private static String value(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    private String form(String title, String ingredients, String instructions, String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"form-card wide\"><h1>Add a recipe</h1>");
        sb.append("<p class=\"muted\">New recipes are reviewed before they appear in Discover.</p>");
        if (error != null) {
            sb.append(HtmlUtil.alert("error", error));
        }
        sb.append("<form method=\"post\" action=\"/recipes/add\">");
        sb.append("<label for=\"title\">Title</label>");
        sb.append("<input type=\"text\" id=\"title\" name=\"title\" maxlength=\"").append(MAX_TITLE)
                .append("\" required value=\"").append(HtmlUtil.esc(title)).append("\">");
        sb.append("<label for=\"ingredients\">Ingredients (one per line)</label>");
        sb.append("<textarea id=\"ingredients\" name=\"ingredients\" rows=\"6\" required>")
                .append(HtmlUtil.esc(ingredients)).append("</textarea>");
        sb.append("<label for=\"instructions\">Instructions</label>");
        sb.append("<textarea id=\"instructions\" name=\"instructions\" rows=\"8\" required>")
                .append(HtmlUtil.esc(instructions)).append("</textarea>");
        sb.append("<button class=\"btn btn-primary\" type=\"submit\">Submit Recipe</button>");
        sb.append(" <a class=\"btn btn-outline-dark\" href=\"/dashboard\">Cancel</a>");
        sb.append("</form></div>");
        return sb.toString();
    }
}
