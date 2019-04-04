package com.jiayue.dto.base;

public class ZhiboListBean {
	
//	private ZhiboTimeBean startTime;
//	private String channel_name;
//	private String create_time;
//	private String channel_status;
//	private ZhiboTimeBean endTime;
//	private String photoUrl;
//	private String channel_id;
//	private String channel_describe;
//	private String groupId;
	
	private int channId;
	private int courseId;
	private String courseName;
	private String courseDisc;
	private String lphotoUrl;
	private int pid;
	private ZhiboTimeBean startTime;
	private ZhiboTimeBean endTime;
	private int isFree;
	private boolean IsPay;
	private String groupId;
	private String expert;
	private String url;
	
	

	public boolean isIsPay() {
		return IsPay;
	}

	public void setIsPay(boolean isPay) {
		IsPay = isPay;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getExpert() {
		return expert;
	}

	public void setExpert(String expert) {
		this.expert = expert;
	}

	public boolean isPay() {
		return IsPay;
	}

	public void setPay(boolean pay) {
		IsPay = pay;
	}

	public int getIsFree() {
		return isFree;
	}

	public void setIsFree(int isFree) {
		this.isFree = isFree;
	}

	public int getChannId() {
		return channId;
	}
	public void setChannId(int channId) {
		this.channId = channId;
	}
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
	public String getCourseDisc() {
		return courseDisc;
	}
	public void setCourseDisc(String courseDisc) {
		this.courseDisc = courseDisc;
	}

	public String getLphotoUrl() {
		return lphotoUrl;
	}

	public void setLphotoUrl(String lphotoUrl) {
		this.lphotoUrl = lphotoUrl;
	}

	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
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
	//	public String getGroupId() {
//		return groupId;
//	}
//	public void setGroupId(String groupId) {
//		this.groupId = groupId;
//	}
//	public String getChannel_describe() {
//		return channel_describe;
//	}
//	public void setChannel_describe(String channel_describe) {
//		this.channel_describe = channel_describe;
//	}
//	public ZhiboTimeBean getStartTime() {
//		return startTime;
//	}
//	public void setStartTime(ZhiboTimeBean startTime) {
//		this.startTime = startTime;
//	}
//	public String getChannel_name() {
//		return channel_name;
//	}
//	public void setChannel_name(String channel_name) {
//		this.channel_name = channel_name;
//	}
//	public String getCreate_time() {
//		return create_time;
//	}
//	public void setCreate_time(String create_time) {
//		this.create_time = create_time;
//	}
//	public String getChannel_status() {
//		return channel_status;
//	}
//	public void setChannel_status(String channel_status) {
//		this.channel_status = channel_status;
//	}
//	public ZhiboTimeBean getEndTime() {
//		return endTime;
//	}
//	public void setEndTime(ZhiboTimeBean endTime) {
//		this.endTime = endTime;
//	}
//	public String getPhotoUrl() {
//		return photoUrl;
//	}
//	public void setPhotoUrl(String photoUrl) {
//		this.photoUrl = photoUrl;
//	}
//	public String getChannel_id() {
//		return channel_id;
//	}
//	public void setChannel_id(String channel_id) {
//		this.channel_id = channel_id;
//	}
	
	
	
}
