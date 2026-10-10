package com.recipeplatform.servlet;

import com.recipeplatform.model.User;
import com.recipeplatform.util.HtmlUtil;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Common behaviour shared by all page servlets (session user, page rendering, redirects). */
public abstract class BaseServlet extends HttpServlet {

    /** Name of the session attribute that holds the logged-in User. */
    protected static final String SESSION_USER = "user";

    /** Returns the logged-in user, or null for visitors. */
    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(SESSION_USER);
    }

    /** Sends a full HTML page (navbar + body) to the browser. */
    protected void render(HttpServletRequest req, HttpServletResponse resp, String title, String body)
            throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(HtmlUtil.page(title, currentUser(req), alerts(req) + body));
    }

    /** Shows the ?msg= (success) and ?err= (error) text from the address bar, if present. */
    protected String alerts(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        String msg = req.getParameter("msg");
        String err = req.getParameter("err");
        if (msg != null && !msg.isBlank()) {
            sb.append(HtmlUtil.alert("success", msg));
        }
        if (err != null && !err.isBlank()) {
            sb.append(HtmlUtil.alert("error", err));
        }
        return sb.toString();
    }

    /** Redirects to a page and passes a message along. kind is "msg" or "err". */
    protected void redirect(HttpServletResponse resp, String path, String kind, String text) throws IOException {
        String url = path + "?" + kind + "=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
        resp.sendRedirect(url);
    }
}
