package com.jiayue.dto.base;

import java.io.Serializable;

public class BookLiveBean implements Serializable{
	int courseId;
	int livePression;//0是没权限  1是有权限观看
	String groupId;
	String expert;
	String url;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getExpert() {
		return expert;
	}
	public void setExpert(String expert) {
		this.expert = expert;
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public int getLivePression() {
		return livePression;
	}
	public void setLivePression(int livePression) {
		this.livePression = livePression;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	
}
