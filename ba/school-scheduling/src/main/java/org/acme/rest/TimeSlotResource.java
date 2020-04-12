package org.acme.rest;

import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.domain.TimeSlot;

import io.quarkus.panache.common.Sort;

@Path("/timeSlots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class TimeSlotResource {

    @GET
    public List<TimeSlot> getAllTimeslots() {
        return TimeSlot.listAll(Sort.by("dayOfWeek").and("startTime").and("endTime").and("id"));
    }

    @POST
    public Response add(TimeSlot timeslot) {
        TimeSlot.persist(timeslot);
        return Response.accepted(timeslot).build();
    }

    @DELETE
    @Path("{timeSlotId}")
    public Response delete(@PathParam("timeSlotId") Long timeslotId) {
        TimeSlot timeslot = TimeSlot.findById(timeslotId);
        if (timeslot == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        timeslot.delete();
        return Response.status(Response.Status.OK).build();
    }

}
