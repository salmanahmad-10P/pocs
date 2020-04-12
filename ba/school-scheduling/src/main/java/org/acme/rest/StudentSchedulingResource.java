package org.acme.rest;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.domain.Lesson;
import org.acme.domain.Room;
import org.acme.domain.SchoolSchedule;
import org.acme.domain.TimeSlot;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/schoolSchedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentSchedulingResource {

    private static final Logger logger = LoggerFactory.getLogger(StudentSchedulingResource.class);
    public static final Long SINGLETON_SCHOOL_SCHEDULE_ID = 1L;

    @Inject
    SolverManager<SchoolSchedule, Long> solverManager;

    @Inject
    ScoreManager<SchoolSchedule> scoreManager;


    @GET
    @Path("/")
    public SchoolSchedule getSchoolSchedule() {

        // Get the solver status before loading the solution
        // to avoid the race condition that the solver terminates between them
        SolverStatus solverStatus = getSolverStatus();

        SchoolSchedule solution = getPlanningSolution(SINGLETON_SCHOOL_SCHEDULE_ID);
        solution.setSolverStatus(solverStatus);
        
        scoreManager.updateScore(solution); // Sets the score
        logger.info("getSchoolSchedule() solverStatus = "+solverStatus.name()+" : score = "+solution.getScore());
        return solution;
    }

    
    @POST
    @Path("/solve")
    public void solve(){
        
        // Return every best solution as they are identified by Optaplanner
        SolverJob sJob = solverManager.solveAndListen(SINGLETON_SCHOOL_SCHEDULE_ID,
        this::getPlanningSolution,
        this::bestSolutionConsumer,
        this::solverExceptionHandler
        );

        logger.info("startSolving() ...."+sJob.getSolverStatus());
    }
    
    @POST
    @Path("/stopSolving")
    public void stopSolving() {
        logger.info("stopSolving() ....");
        solverManager.terminateEarly(SINGLETON_SCHOOL_SCHEDULE_ID);
    }
    
    @Transactional
    protected SchoolSchedule getPlanningSolution(Long id){
        if (!SINGLETON_SCHOOL_SCHEDULE_ID.equals(id)) {
            throw new IllegalStateException("There is no school schedule with id (" + id + ").");
        }
        List<TimeSlot> tSlots = TimeSlot.listAll();
        List<Room> rooms = Room.listAll();
        List<Lesson> lessons = Lesson.listAll();
        return new SchoolSchedule(tSlots, rooms, lessons);
    }
    
    @Transactional
    protected void bestSolutionConsumer(SchoolSchedule sSchedule) {
        for(Lesson lesson : sSchedule.getLessonList()){
            logger.info("save() "+lesson.toString());
            Lesson attachedLesson = Lesson.findById(lesson.getId());
            attachedLesson.setTimeSlot(lesson.getTimeSlot());
            attachedLesson.setRoom(lesson.getRoom());
        }
    }
    
    private SolverStatus getSolverStatus() {
        return solverManager.getSolverStatus(SINGLETON_SCHOOL_SCHEDULE_ID);
    }

    private void solverExceptionHandler(Long tenantId, Throwable x){
        x.printStackTrace();
    }

}