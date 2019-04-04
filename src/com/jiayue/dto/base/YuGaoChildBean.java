package com.jiayue.dto.base;

import java.io.Serializable;

public class YuGaoChildBean implements Serializable{
	private int courseId;
	private String courseName;
	private ZhiboTimeBean startTime;
	private ZhiboTimeBean endTime;
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public ZhiboTimeBean getStartTime() {
		return startTime;
	}
	public void setStartTime(ZhiboTimeBean startTime) {
		this.startTime = startTime;
	}
	public ZhiboTimeBean getEndTime() {
		return endTime;
	}
	public void setEndTime(ZhiboTimeBean endTime) {
		this.endTime = endTime;
	}

	
	
}
