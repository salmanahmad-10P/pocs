package org.acme;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.domain.Lesson;
import org.acme.domain.Room;
import org.acme.domain.SchoolSchedule;
import org.acme.domain.TimeSlot;

@Path("/schoolSchedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentSchedulingResource {

    @GET
    public SchoolSchedule getSchoolScheduling() {
        return new SchoolSchedule(TimeSlot.listAll(), Room.listAll(), Lesson.listAll());
    }

}