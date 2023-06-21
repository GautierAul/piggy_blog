package org.eclipse.jakarta.resource;

import java.util.List;

import org.eclipse.jakarta.model.Blog;
import org.eclipse.jakarta.utils.PiggyUtils;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("blog")
public class BlogResource {

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    UserTransaction userTransaction;

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleBlog(@HeaderParam("Authorization") String authorizationHeader,
            @PathParam("id") String id) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Long longId = Long.parseLong(id);
        try {
            userTransaction.begin();
            Blog blog = entityManager.find(Blog.class, longId);
            userTransaction.commit();

            if (blog == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(blog).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBlogs(@HeaderParam("Authorization") String authorizationHeader) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {

            userTransaction.begin();
            List<Blog> blogList = entityManager.createQuery("SELECT b FROM Blog b",
                    Blog.class).getResultList();
            userTransaction.commit();
            return Response.ok(blogList).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postBlog(@HeaderParam("Authorization") String authorizationHeader, Blog newBlog) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            userTransaction.begin();

            entityManager.persist(newBlog);
            entityManager.flush();

            userTransaction.commit();

            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("{id}")
    @DELETE
    public Response deleteBlog(@HeaderParam("Authorization") String authorizationHeader, @PathParam("id") String id) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long longId = Long.parseLong(id);

        try {
            userTransaction.begin();

            Blog blog = entityManager.find(Blog.class, longId);
            if (blog == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            entityManager.remove(blog);
            entityManager.flush();

            userTransaction.commit();

            return Response.status(Response.Status.OK).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
