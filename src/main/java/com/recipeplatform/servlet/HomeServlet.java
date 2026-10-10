package com.recipeplatform.servlet;

import com.recipeplatform.dao.RecipeDao;
import com.recipeplatform.dao.RecipeDaoImpl;
import com.recipeplatform.model.User;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Landing page: GET /home */
public class HomeServlet extends BaseServlet {

    private final RecipeDao recipeDao = new RecipeDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);

        int approved = 0;
        try {
            approved = recipeDao.countApproved();
        } catch (SQLException e) {
            log("Could not count recipes", e);
        }

        StringBuilder body = new StringBuilder();
        body.append("<section class=\"hero\">");
        body.append("<h1>Share &amp; Discover Amazing Recipes</h1>");
        body.append("<p>Explore home-cooked meals from chefs and food enthusiasts around the world.</p>");
        body.append("<div class=\"hero-actions\">");
        body.append("<a class=\"btn btn-light\" href=\"/recipes\">Browse Recipes</a>");
        if (user == null) {
            body.append("<a class=\"btn btn-dark\" href=\"/signup\">Get Started</a>");
            body.append("<a class=\"btn btn-outline\" href=\"/login\">Log In</a>");
        } else {
            body.append("<a class=\"btn btn-dark\" href=\"/dashboard\">My Dashboard</a>");
        }
        body.append("</div></section>");

        body.append("<section class=\"stat-box\"><div class=\"stat-number\">").append(approved).append("</div>");
        body.append("<div class=\"muted\">Publicly approved recipes ready to cook</div></section>");

        render(req, resp, "Home", body.toString());
    }
}
