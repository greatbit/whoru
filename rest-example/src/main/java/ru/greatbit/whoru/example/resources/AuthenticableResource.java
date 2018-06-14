package ru.greatbit.whoru.example.resources;

import ru.greatbit.whoru.jaxrs.Authenticable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Authenticable
@Path("/usersonly")
public class AuthenticableResource extends BaseResource {

    @GET
    @Path("/")
    public Response all() {
        return Response.ok("Sucess").build();
    }
}
