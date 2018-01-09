package com.redhat.gpte;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

@Stateless
@Path("/rs/")
public class SimpleREST {

    /**
     * sample usage :
     *   curl -X GET -HAccept:text/plain $HOSTNAME:8080/simpleREST/rs/sanityCheck
     **/
    @GET
    @Path("/sanityCheck")
    @Produces({ "text/plain" })
    public Response sanityCheck() {
        String response = "good to go";
        ResponseBuilder builder = Response.ok(response);
        System.out.println(response);
        return builder.build();
    }

}
