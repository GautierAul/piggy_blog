package org.eclipse.jakarta.resource;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jakarta.model.Account;
import org.eclipse.jakarta.model.Token;
import org.eclipse.jakarta.utils.PasswordHasher;
import org.eclipse.jakarta.utils.PiggyUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("account")
public class AccountResource {

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    UserTransaction userTransaction;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    public Response connectUser(Account account) {

        try {
            userTransaction.begin();

            List<Account> userList = entityManager.createQuery("SELECT u FROM Account u",
                    Account.class).getResultList();

            Stream<Account> stream = userList.stream();

            Account accountFound = stream.filter(
                    v -> {

                        return v.getUsername().equals(account.getUsername())
                                && PasswordHasher.verifyPassword(account.getPassword(), v.getPassword());
                    })
                    .findFirst().orElse(null);

            if (accountFound == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            String token = PiggyUtils.makeJwt(accountFound);
            PiggyUtils.verifyJWT(token);
            userTransaction.commit();

            Token tokenObj = new Token(token);
            return Response.ok(tokenObj).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create")
    public Response createUser(Account account, @Context HttpSession session, @Context UriInfo uriInfo) {
        try {
            userTransaction.begin();

            List<Account> userList = entityManager.createQuery("SELECT u FROM Account u",
                    Account.class).getResultList();

            Stream<Account> stream = userList.stream();

            Account accountFound = stream.filter(
                    v -> v.getUsername().equals(account.getUsername()))
                    .findFirst().orElse(null);

            if (accountFound != null) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            account.setPassword(PasswordHasher.hashPassword(account.getPassword()));

            entityManager.persist(account);
            entityManager.flush();
            String token = PiggyUtils.makeJwt(account);
            PiggyUtils.verifyJWT(token);
            userTransaction.commit();

            Token tokenObj = new Token(token);
            return Response.ok(tokenObj).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed("Bearer")
    public Response getAccount(@HeaderParam("Authorization") String authorizationHeader, @PathParam("id") String id) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Long longUserId = Long.parseLong(id);
        try {
            userTransaction.begin();
            Account account = entityManager.find(Account.class, longUserId);
            userTransaction.commit();

            return Response.ok(account).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam("Authorization") String authorizationHeader) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            userTransaction.begin();
            List<Account> userList = entityManager.createQuery("SELECT u FROM Account u",
                    Account.class).getResultList();
            userTransaction.commit();
            return Response.ok(userList).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Path("{userId}")
    @DELETE
    public Response deleteByName(@HeaderParam("Authorization") String authorizationHeader,
            @PathParam("userId") String userId) {
        if (!PiggyUtils.verifyJWT(authorizationHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Long longUserId = Long.parseLong(userId);

        try {
            userTransaction.begin();

            Account account = entityManager.find(Account.class, longUserId);
            if (account == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            entityManager.remove(account);

            userTransaction.commit();

            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
