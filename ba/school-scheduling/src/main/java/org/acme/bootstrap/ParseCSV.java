package org.acme.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.acme.domain.Lesson;
import org.acme.domain.Room;
import org.acme.domain.Student;
import org.acme.domain.TimeSlot;
import org.acme.utils.Constants;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ParseCSV extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ParseCSV.class);
    
    @Inject
    io.vertx.mutiny.core.Vertx vertx;

    @Inject
    @ConfigProperty(name = Constants.CSV_PATH)
    private String csvPath;

    @Inject
    @ConfigProperty(name = Constants.CSV_FILE_NAME_STUDENTS)
    private String csvFileNameStudents;

    @Inject
    @ConfigProperty(name = Constants.CSV_FILE_NAME_ROOMS)
    private String csvFileNameRooms;

    @Inject
    @ConfigProperty(name = Constants.CSV_FILE_NAME_TIMESLOTS)
    private String csvFileNameTimeSlots;

    @Inject
    @ConfigProperty(name = Constants.CSV_FILE_NAME_LESSONS)
    private String csvFileNameLessons;

    @Transactional
    void start(@Observes StartupEvent ev)  throws Exception {

        persistRoom();
        persistTimeSlot();
        persistLesson();
        persistStudents();
    }

    private void persistRoom() throws IOException, ParseException {

        BufferedReader fReader = null;
        try {
            String filePath = csvPath + csvFileNameRooms;
            File fFile = new File(filePath);
            if(!fFile.exists()) {
                throw new RuntimeException("No file at: "+filePath);
            }
            fReader = new BufferedReader(new FileReader(fFile));
            String line;
            List<Room> rooms = new ArrayList<Room>();
            while ((line = fReader.readLine()) != null) {
                String[] sArray = line.split(Constants.COMMA);
                Room room = new Room();
                room.setName(sArray[0]);
                rooms.add(room);
            }
            Room.persist(rooms);
        }finally{
            if(fReader != null)
              fReader.close();
        }
    }

    private void persistTimeSlot() throws IOException, ParseException {
        BufferedReader fReader = null;
        try {
            String filePath = csvPath + csvFileNameTimeSlots;
            File fFile = new File(filePath);
            if(!fFile.exists()) {
                throw new RuntimeException("No file at: "+filePath);
            }
            fReader = new BufferedReader(new FileReader(fFile));
            String line;
            List<TimeSlot> tSlots = new ArrayList<TimeSlot>();
            while ((line = fReader.readLine()) != null) {
                String[] sArray = line.split(Constants.COMMA);
                TimeSlot tSlot = new TimeSlot();
                tSlot.setDayOfWeek(DayOfWeek.valueOf(sArray[0]));
                tSlot.setStartTime(LocalTime.parse(sArray[1]));
                tSlot.setEndTime(LocalTime.parse(sArray[2]));
                tSlots.add(tSlot);
            }
            TimeSlot.persist(tSlots);
        }finally{
            if(fReader != null)
              fReader.close();
        }
    }

    private void persistLesson() throws IOException, ParseException {
        BufferedReader fReader = null;
        try {
            String filePath = csvPath + csvFileNameLessons;
            File fFile = new File(filePath);
            if(!fFile.exists()) {
                throw new RuntimeException("No file at: "+filePath);
            }
            fReader = new BufferedReader(new FileReader(fFile));
            String line;
            List<Lesson> tSlots = new ArrayList<Lesson>();
            while ((line = fReader.readLine()) != null) {
                String[] sArray = line.split(Constants.COMMA);
                Lesson tSlot = new Lesson();
                tSlot.setSubject(sArray[0]);
                tSlot.setTeacher(sArray[1]);
                tSlot.setGradeLevel(sArray[2]);
                tSlots.add(tSlot);
            }
            TimeSlot.persist(tSlots);
        }finally{
            if(fReader != null)
              fReader.close();
        }
    }

   
    private void persistStudents() throws IOException, ParseException{

        BufferedReader fReader = null;
        try {
            String filePath = csvPath + csvFileNameStudents;
            File fFile = new File(filePath);
            if(!fFile.exists()) {
                throw new RuntimeException("No file at: "+filePath);
            }
            fReader = new BufferedReader(new FileReader(fFile));
            String line;
            List<Student> students = new ArrayList<Student>();
            while ((line = fReader.readLine()) != null) {
                String[] sArray = line.split(Constants.COMMA);
                Student student = new Student();
                student.setFirstName(sArray[0]);
                student.setLastName(sArray[1]);
                student.setDob(Constants.dfObject.parse(sArray[2]));
                student.setSsn(sArray[3]);
                students.add(student);
            }
            Student.persist(students);
        }finally{
            if(fReader != null)
              fReader.close();
        }
    }

    @Override
    public void configure() throws Exception {

        /*
        StringBuilder sBuilder = new StringBuilder("file:"+csvPath+"?fileName="+csvFileNameStudents+"&noop=false");
        logger.info("configure() csv file path = "+sBuilder.toString());

        from(sBuilder.toString())
            .log(sBuilder.toString())
            .unmarshal().csv()
            .log("configure() body = ${body}");
        */

    }

    void end(@Observes ShutdownEvent ev) {
        logger.info("onStop() stopping...");
    }

}