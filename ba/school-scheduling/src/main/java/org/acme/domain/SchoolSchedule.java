package org.acme.domain;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;


@PlanningSolution
public class SchoolSchedule {

	// Provides the planning values that can be used for a PlanningVariable.
	@ValueRangeProvider(id = "timeSlotRange")
	private List<TimeSlot> timeSlotList;
	
	// Provides the planning values that can be used for a PlanningVariable.
	@ValueRangeProvider(id = "roomRange")
	private List<Room> roomList;
	
	@PlanningEntityCollectionProperty
	private List<Lesson> lessonList;

	@PlanningScore
	private HardSoftScore score;

	// Ignored by OptaPlanner, used by the UI to display solve or stop solving button
	private SolverStatus solverStatus;


	public SchoolSchedule() {
	}
	public SchoolSchedule(List<TimeSlot> timeSlotList, List<Room> roomList, List<Lesson> lessonList) {
	
		this.timeSlotList = timeSlotList;
		this.roomList = roomList;
		this.lessonList = lessonList;
	}

	public List<TimeSlot> getTimeSlotList() {
		return timeSlotList;
	}
	public void setTimeSlotList(List<TimeSlot> timeSlotList) {
		this.timeSlotList = timeSlotList;
	}
	public List<Room> getRoomList() {
		return roomList;
	}
	public void setRoomList(List<Room> roomList) {
		this.roomList = roomList;
	}
	public List<Lesson> getLessonList() {
		return lessonList;
	}
	public void setLessonList(List<Lesson> lessonList) {
		this.lessonList = lessonList;
	}
	public HardSoftScore getScore() {
		return score;
	}
	public void setScore(HardSoftScore score) {
		this.score = score;
	}
	public SolverStatus getSolverStatus() {
		return solverStatus;
	}
	public void setSolverStatus(SolverStatus solverStatus) {
		this.solverStatus = solverStatus;
	}

}