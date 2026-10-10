package com.recipeplatform.servlet;

import com.recipeplatform.dao.ActivityDao;
import com.recipeplatform.dao.UserDao;
import com.recipeplatform.dao.UserDaoImpl;
import com.recipeplatform.util.HtmlUtil;
import com.recipeplatform.util.PasswordUtil;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Sign up: GET /signup shows the form, POST /signup validates and creates the account. */
public class SignupServlet extends BaseServlet {

    private static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final UserDao userDao = new UserDaoImpl();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, "Sign Up", form("", "", null));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = trim(req.getParameter("name"));
        String email = trim(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirm") == null ? "" : req.getParameter("confirm");

        // Validation: every rule has its own clear message
        String error = null;
        if (name.isEmpty()) {
            error = "Please enter your name.";
        } else if (!email.matches(EMAIL_PATTERN)) {
            error = "Please enter a valid email address.";
        } else if (password.length() < 6) {
            error = "Password must be at least 6 characters long.";
        } else if (!password.equals(confirm)) {
            error = "The two passwords do not match.";
        }
        if (error != null) {
            render(req, resp, "Sign Up", form(name, email, error));
            return;
        }

        try {
            if (userDao.findByEmail(email) != null) {
                render(req, resp, "Sign Up", form(name, email, "An account with this email already exists."));
                return;
            }
            int id = userDao.create(name, email, PasswordUtil.hash(password), "USER");
            activityDao.log(id, "SIGNUP", "New account created");
            redirect(resp, "/login", "msg", "Account created! You can log in now.");
        } catch (SQLException e) {
            log("Signup failed", e);
            render(req, resp, "Sign Up", form(name, email, "Something went wrong. Please try again."));
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String form(String name, String email, String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"form-card\"><h1>Create your account</h1>");
        sb.append("<p class=\"muted\">Join the community and share your recipes.</p>");
        if (error != null) {
            sb.append(HtmlUtil.alert("error", error));
        }
        sb.append("<form method=\"post\" action=\"/signup\">");
        sb.append("<label for=\"name\">Name</label>");
        sb.append("<input type=\"text\" id=\"name\" name=\"name\" required value=\"").append(HtmlUtil.esc(name)).append("\">");
        sb.append("<label for=\"email\">Email</label>");
        sb.append("<input type=\"email\" id=\"email\" name=\"email\" required value=\"").append(HtmlUtil.esc(email)).append("\">");
        sb.append("<label for=\"password\">Password (at least 6 characters)</label>");
        sb.append("<input type=\"password\" id=\"password\" name=\"password\" required>");
        sb.append("<label for=\"confirm\">Confirm password</label>");
        sb.append("<input type=\"password\" id=\"confirm\" name=\"confirm\" required>");
        sb.append("<button class=\"btn btn-primary btn-block\" type=\"submit\">Sign Up</button>");
        sb.append("</form>");
        sb.append("<p class=\"muted center\">Already registered? <a href=\"/login\">Log in</a></p>");
        sb.append("</div>");
        return sb.toString();
    }
}
