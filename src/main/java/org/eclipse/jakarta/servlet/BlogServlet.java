package org.eclipse.jakarta.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.eclipse.jakarta.model.Blog;
import org.eclipse.jakarta.model.Account;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.core.Response;

@WebServlet("/blog/*")
public class BlogServlet extends HttpServlet {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private UserTransaction userTransaction;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            try {

                userTransaction.begin();
                List<Blog> blogs = entityManager.createQuery("SELECT b FROM Blog b",
                        Blog.class).getResultList();
                userTransaction.commit();

                request.setAttribute("blogs", blogs);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/src/blog/index.jsp");
                dispatcher.forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String path = pathInfo.substring(1);
                String mainInfo = path.split("/")[0];

                String id = "";
                if (path.split("/").length > 1) {
                    id = path.split("/")[1];
                }

                if (mainInfo.equals("create")) {
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/src/blog/create.jsp");
                    dispatcher.forward(request, response);
                    return;
                } else if (mainInfo.equals("edit")) {
                    userTransaction.begin();
                    Blog blog = entityManager.find(Blog.class, Long.parseLong(id));
                    userTransaction.commit();

                    request.setAttribute("blog", blog);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/src/blog/edit.jsp");
                    dispatcher.forward(request, response);
                    return;
                } else if (mainInfo.equals("delete")) {
                    userTransaction.begin();
                    Blog blog = entityManager.find(Blog.class, Long.parseLong(id));
                    userTransaction.commit();

                    request.setAttribute("blog", blog);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/src/blog/delete.jsp");
                    dispatcher.forward(request, response);
                    return;
                } else {

                    userTransaction.begin();
                    Blog blog = entityManager.find(Blog.class, Long.parseLong(mainInfo));
                    Account author = entityManager.find(Account.class, Long.parseLong(blog.getCreatedBy()));
                    userTransaction.commit();

                    request.setAttribute("blog", blog);
                    request.setAttribute("author", author);

                    RequestDispatcher dispatcher = request.getRequestDispatcher("/src/blog/blog_detail.jsp");
                    dispatcher.forward(request, response);
                    return;

                }

            } catch (NotSupportedException | SystemException | SecurityException | IllegalStateException
                    | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getParameter("_method");
        if (method != null && method.equals("PUT")) {
            doPut(request, response);
            return;
        } else if (method != null && method.equals("DELETE")) {
            doDelete(request, response);
            return;
        }

        String titre = request.getParameter("title");
        String contenu = request.getParameter("content");
        String createdBy = request.getParameter("createdBy");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = dateFormat.format(new Date());

        Blog blog = new Blog();
        blog.setTitle(titre);
        blog.setContent(contenu);
        blog.setCreatedAt(today);
        blog.setCreatedBy(createdBy);

        try {
            userTransaction.begin();

            entityManager.persist(blog);
            entityManager.flush();
            userTransaction.commit();
            response.sendRedirect("/blog/" + blog.getId());

        } catch (IOException | NotSupportedException | SystemException | SecurityException | IllegalStateException
                | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        try {

            if (id != null && title != null && content != null) {
                Long blogId = Long.parseLong(id);

                Blog blog = entityManager.find(Blog.class, blogId);

                if (blog != null) {
                    blog.setTitle(title);
                    blog.setContent(content);
                    userTransaction.begin();

                    entityManager.merge(blog);

                    userTransaction.commit();

                    response.sendRedirect("/blog/" + blog.getId());

                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().println("Blog non trouvé");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Paramètres invalides");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");

        try {

            if (id != null) {
                Long blogId = Long.parseLong(id);

                Blog blog = entityManager.find(Blog.class, blogId);

                if (blog != null) {
                    userTransaction.begin();
                    blog = entityManager.merge(blog);

                    entityManager.remove(blog);

                    userTransaction.commit();

                    response.sendRedirect("/blog");

                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().println("Blog non trouvé");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Paramètres invalides");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
