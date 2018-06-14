package ru.greatbit.whoru.example.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/all")
public class NonAuthenticableResource extends BaseResource{

    @GET
    @Path("/")
    public Response all() {
        return Response.ok("Sucess").build();
    }

}
