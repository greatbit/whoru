package ru.greatbit.whoru.example.resources;

import org.springframework.beans.factory.annotation.Autowired;
import ru.greatbit.whoru.auth.AuthProvider;
import ru.greatbit.whoru.auth.Session;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/login")
public class LoginResource extends BaseResource{
    @Autowired
    AuthProvider authProvider;

    @GET
    @Path("/session")
    public Session getSession() {
        return authProvider.getSession(request);
    }

    //@POST
    @GET
    @Path("/auth")
    public Response login(@QueryParam("login") String login,
                          @QueryParam("password") String password) {
        authProvider.doAuth(request, response);
        return Response.ok().build();
    }

    @DELETE
    @Path("/session")
    public Response logout() {
        authProvider.doLogout(request, response);
        return Response.ok().build();
    }

}
