package org.eclipse.jakarta.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jakarta.model.Comment;
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

@Path("comment")
public class CommentResource {

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    UserTransaction userTransaction;

    @Path("db/drop")
    @GET
    public Response deleteBlog() {
        try {
            userTransaction.begin();
            entityManager.createNativeQuery("DROP TABLE comment").executeUpdate();
            userTransaction.commit();
            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleComment(@HeaderParam("Authorization") String authorizationHeader,
            @PathParam("id") String id) {

        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Long longId = Long.parseLong(id);
        try {
            userTransaction.begin();
            Comment comment = entityManager.find(Comment.class, longId);
            userTransaction.commit();

            if (comment == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(comment).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("blog/{commentId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommentByBlogId(@HeaderParam("Authorization") String authorizationHeader,
            @PathParam("commentId") String commentId) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Long longId = Long.parseLong(commentId);
        try {
            userTransaction.begin();
            List<Comment> commentList = entityManager.createQuery("SELECT c FROM Comment c",
                    Comment.class).getResultList();
            userTransaction.commit();
            List<Comment> blogComments = commentList.stream().filter(b -> {
                return b.getBlogId().equals(longId);
            })
                    .collect(Collectors.toList());

            return Response.ok(blogComments).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllComments(@HeaderParam("Authorization") String authorizationHeader) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {

            userTransaction.begin();
            List<Comment> commentList = entityManager.createQuery("SELECT c FROM Comment c",
                    Comment.class).getResultList();
            userTransaction.commit();
            return Response.ok(commentList).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postComment(Comment newComment, @HeaderParam("Authorization") String authorizationHeader) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            userTransaction.begin();

            entityManager.persist(newComment);
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
    public Response deleteComment(@PathParam("id") String id,
            @HeaderParam("Authorization") String authorizationHeader) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long longId = Long.parseLong(id);

        try {
            userTransaction.begin();

            Comment comment = entityManager.find(Comment.class, longId);
            if (comment == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            entityManager.remove(comment);
            entityManager.flush();

            userTransaction.commit();

            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
