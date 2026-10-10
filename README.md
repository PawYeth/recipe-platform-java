# Online Recipe Sharing Platform

A Java web application where users share and discover recipes. Built with **Java Servlets**, **JDBC** and **SQLite**, using the DAO pattern and a Maven build. The servlets generate the HTML pages and a single CSS file styles them.

This repository contains the **Review 1** version: project structure, database design, database connectivity and the user interface.

## Features (Review 1)

- Sign up with validation (name, email, password of at least 6 characters, password confirmation)
- Log in and log out, with passwords stored as hashes
- Route protection: an authentication filter blocks pages that need a logged-in user
- User dashboard with counts of approved, pending and rejected recipes and a "My Recipes" table
- Add a recipe (title, ingredients one per line, instructions). New recipes start as `PENDING`
- Discover page listing approved recipes, with search by title or ingredient and average star ratings
- Recipe detail page with ingredients, instructions, author, date, status and rating
- Responsive layout that works on phone-width screens
- Demo data is added automatically on first start

Ratings and comments exist as database tables. Their pages, the admin panel (approve or reject recipes, manage users), profile management and image upload are planned for the next phases.

## Technology

| Part | Technology |
|---|---|
| Language | Java 17 (tested on JDK 21) |
| Web layer | Java Servlets 4.0 |
| Database access | JDBC with the DAO pattern |
| Database | SQLite (`recipe.db`, created automatically) |
| Build tool | Maven (WAR project) |
| Server | Jetty 10 through the Maven Jetty plugin |
| Front end | HTML and CSS |

## Project structure

```
recipe-platform-java
|-- pom.xml
|-- README.md
`-- src/main
    |-- java/com/recipeplatform
    |   |-- dao        UserDao, UserDaoImpl, RecipeDao, RecipeDaoImpl, ActivityDao
    |   |-- filter     AuthFilter
    |   |-- model      User, Recipe
    |   |-- servlet    BaseServlet, HomeServlet, SignupServlet, LoginServlet, LogoutServlet,
    |   |              DashboardServlet, RecipesServlet, RecipeDetailServlet, AddRecipeServlet
    |   `-- util       DBUtil, DbInitListener, HtmlUtil, PasswordUtil
    |-- resources
    |   `-- schema.sql
    `-- webapp
        |-- index.html
        |-- css/style.css
        `-- WEB-INF/web.xml
```

## Database design

Six tables, created from `src/main/resources/schema.sql` when the app starts:

| Table | Purpose |
|---|---|
| `users` | Accounts: name, email (unique), password hash, role (`USER` or `ADMIN`) |
| `recipes` | Title, ingredients, instructions, photo path, status (`PENDING`, `APPROVED`, `REJECTED`), owner |
| `ratings` | One star rating (1 to 5) per user per recipe |
| `comments` | Comments written on recipes |
| `user_activity` | Log of signups, logins and recipe submissions |
| `settings` | Site-wide key and value settings |

One user has many recipes. Each recipe has many ratings and comments. Foreign keys use `ON DELETE CASCADE`.

## How the database connection works

1. `DbInitListener` runs when the server starts. It executes `schema.sql` and adds demo data if the database is empty.
2. `DBUtil` opens JDBC connections to the `recipe.db` SQLite file.
3. Servlets call the DAO interfaces (`UserDao`, `RecipeDao`, `ActivityDao`). The DAO implementations contain the SQL, so servlets never write SQL.

## Requirements

- JDK 17 or newer (run `javac -version` to check)
- Apache Maven 3.9 or newer (run `mvn -v` to check)

## How to run

1. Open a terminal in the project folder (the one containing `pom.xml`).
2. Run:

   ```
   mvn jetty:run
   ```

3. The first run downloads libraries and takes a few minutes. Wait for the message `Database ready`.
4. Open **http://localhost:8080** in a browser.
5. Press `Ctrl + C` in the terminal to stop the server.

If Maven shows a certificate error (`PKIX path building failed`) on Windows, run this first in the same terminal and then try again:

```
set MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT -Djavax.net.ssl.trustStore=NUL
```

## Demo accounts

All demo accounts use the password `password123`.

| Email | Role |
|---|---|
| `admin@recipe.com` | ADMIN |
| `user1@recipe.com` | USER |
| `user2@recipe.com` | USER |

## Planned next phases

- Rating and comment pages
- Profile management
- Admin panel: approve or reject recipes, manage users
- System settings and activity log screens
- Image upload for recipes
- Testing and final documentation
