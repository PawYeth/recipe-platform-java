package com.recipeplatform.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Sends visitors who are not logged in to the login page (route protection). */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // no set-up needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("user") != null;

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            String message = URLEncoder.encode("Please log in first.", StandardCharsets.UTF_8);
            resp.sendRedirect("/login?err=" + message);
        }
    }

    @Override
    public void destroy() {
        // nothing to release
    }
}
