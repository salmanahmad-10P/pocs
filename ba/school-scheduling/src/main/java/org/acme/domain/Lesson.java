package org.acme.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@PlanningEntity
public class Lesson extends PanacheEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotBlank
    private String subject;

    @NotBlank
    private String teacher;

    @NotBlank
    private String gradeLevel;

    // Changes during planner by Optaplanner
    @ManyToOne
    private TimeSlot timeSlot;

    // Changes during planner by Optaplanner
    @ManyToOne
    private Room room;

    public Lesson(){}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getGradeLevel() {
		return gradeLevel;
	}
	public void setGradeLevel(String gradeLevel) {
		this.gradeLevel = gradeLevel;
	}
	public Lesson(Long id, String subject, String teacher, String gradeLevel) {
		this.subject = subject;
		this.teacher = teacher;
		this.gradeLevel = gradeLevel;
	}
	public TimeSlot getTimeSlot() {
		return timeSlot;
	}
	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}

}