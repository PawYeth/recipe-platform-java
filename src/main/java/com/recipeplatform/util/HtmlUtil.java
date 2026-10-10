package com.recipeplatform.util;

import com.recipeplatform.model.Recipe;
import com.recipeplatform.model.User;
import java.util.Locale;

/** Small helpers that build the HTML shared by all pages (layout, navbar, cards). */
public final class HtmlUtil {

    private HtmlUtil() {
    }

    /** Escapes text so user input can never be run as HTML (prevents XSS). */
    public static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /** A coloured message box. kind is "success" or "error". */
    public static String alert(String kind, String text) {
        return "<div class=\"alert alert-" + kind + "\">" + esc(text) + "</div>";
    }

    /** Text such as "4.5 stars (2)" or "No ratings yet". */
    public static String stars(double avg, int count) {
        if (count == 0) {
            return "No ratings yet";
        }
        return "&#9733; " + String.format(Locale.US, "%.1f", avg) + " (" + count + ")";
    }

    /** A small coloured label for a recipe status. */
    public static String badge(String status) {
        String s = status == null ? "" : status;
        return "<span class=\"badge badge-" + esc(s.toLowerCase(Locale.ROOT)) + "\">" + esc(s) + "</span>";
    }

    /** Shortens long text for use on cards. */
    public static String snippet(String text, int max) {
        if (text == null) {
            return "";
        }
        String flat = text.replaceAll("\\s+", " ").trim();
        return flat.length() <= max ? flat : flat.substring(0, max) + "...";
    }

    /** Keeps only the date part of a timestamp such as 2026-10-10 12:30:00. */
    public static String shortDate(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.length() >= 10 ? timestamp.substring(0, 10) : timestamp;
    }

    /** One recipe card for the Discover grid. */
    public static String recipeCard(Recipe r) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a class=\"card\" href=\"/recipe?id=").append(r.getId()).append("\">");
        sb.append("<div class=\"card-img\">&#127859;</div>");
        sb.append("<div class=\"card-body\">");
        sb.append("<h3>").append(esc(r.getTitle())).append("</h3>");
        sb.append("<p class=\"muted\">by ").append(esc(r.getAuthorName())).append("</p>");
        sb.append("<p class=\"rating\">").append(stars(r.getAvgRating(), r.getRatingCount())).append("</p>");
        sb.append("<p class=\"snippet\">").append(esc(snippet(r.getIngredients(), 80))).append("</p>");
        sb.append("</div></a>");
        return sb.toString();
    }

    /** Wraps page content in the full HTML document with navbar and footer. */
    public static String page(String title, User user, String body) {
        StringBuilder sb = new StringBuilder(4096);
        sb.append("<!DOCTYPE html><html lang=\"en\"><head>");
        sb.append("<meta charset=\"UTF-8\">");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        sb.append("<title>").append(esc(title)).append(" - RecipePlatform</title>");
        sb.append("<link rel=\"stylesheet\" href=\"/css/style.css\">");
        sb.append("</head><body>");

        sb.append("<header class=\"navbar\"><div class=\"nav-inner\">");
        sb.append("<a class=\"brand\" href=\"/home\">RecipePlatform</a>");
        sb.append("<nav class=\"nav-links\">");
        sb.append("<a href=\"/recipes\">Discover Recipes</a>");
        if (user != null) {
            sb.append("<a href=\"/dashboard\">Dashboard</a>");
            sb.append("<a href=\"/recipes/add\">Add Recipe</a>");
            sb.append("<span class=\"nav-user\">").append(esc(user.getName())).append("</span>");
            sb.append("<a class=\"nav-logout\" href=\"/logout\">Log Out</a>");
        } else {
            sb.append("<a href=\"/login\">Log In</a>");
            sb.append("<a class=\"btn btn-primary btn-sm\" href=\"/signup\">Sign Up</a>");
        }
        sb.append("</nav></div></header>");

        sb.append("<main class=\"container\">").append(body).append("</main>");
        sb.append("<footer class=\"footer\">RecipePlatform &middot; Java Servlets + JDBC project</footer>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
