package com.recipeplatform.servlet;

import com.recipeplatform.dao.ActivityDao;
import com.recipeplatform.dao.UserDao;
import com.recipeplatform.dao.UserDaoImpl;
import com.recipeplatform.model.User;
import com.recipeplatform.util.HtmlUtil;
import com.recipeplatform.util.PasswordUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Log in: GET /login shows the form, POST /login checks the password. */
public class LoginServlet extends BaseServlet {

    private final UserDao userDao = new UserDaoImpl();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            resp.sendRedirect("/dashboard");
            return;
        }
        render(req, resp, "Log In", form("", null));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        if (email.isEmpty() || password.isEmpty()) {
            render(req, resp, "Log In", form(email, "Please enter your email and password."));
            return;
        }

        try {
            User user = userDao.findByEmail(email);
            if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
                render(req, resp, "Log In", form(email, "Incorrect email or password."));
                return;
            }

            // Start a fresh session after login (protects against session fixation)
            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            req.getSession(true).setAttribute(SESSION_USER, user);

            activityDao.log(user.getId(), "LOGIN", "Logged in");
            resp.sendRedirect("/dashboard");
        } catch (SQLException e) {
            log("Login failed", e);
            render(req, resp, "Log In", form(email, "Something went wrong. Please try again."));
        }
    }

    private String form(String email, String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"form-card\"><h1>Welcome back</h1>");
        sb.append("<p class=\"muted\">Log in to manage your recipes.</p>");
        if (error != null) {
            sb.append(HtmlUtil.alert("error", error));
        }
        sb.append("<form method=\"post\" action=\"/login\">");
        sb.append("<label for=\"email\">Email</label>");
        sb.append("<input type=\"email\" id=\"email\" name=\"email\" required value=\"").append(HtmlUtil.esc(email)).append("\">");
        sb.append("<label for=\"password\">Password</label>");
        sb.append("<input type=\"password\" id=\"password\" name=\"password\" required>");
        sb.append("<button class=\"btn btn-primary btn-block\" type=\"submit\">Log In</button>");
        sb.append("</form>");
        sb.append("<p class=\"muted center\">New here? <a href=\"/signup\">Create an account</a></p>");
        sb.append("</div>");
        return sb.toString();
    }
}
