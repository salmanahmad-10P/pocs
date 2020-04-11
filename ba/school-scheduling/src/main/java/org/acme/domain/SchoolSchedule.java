package org.acme.domain;
import java.util.List;



public class SchoolSchedule {

    private List<TimeSlot> timeSlotList;
    private List<Room> roomList;
    private List<Lesson> lessonList;
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

}