package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class YuGaoDataBean implements Serializable{
	private int courseId;
	private String courseName;
	private String courseDisc;
	private ZhiboTimeBean startTime;
	private ZhiboTimeBean endTime;
	private String cphotoUrl;
	private int isFree;
	private float price;
	private int discount;
	private String expert;
	private String expertInfo;
	private List<YuGaoChildBean> children;
	private boolean IsPay;

	public boolean isPay() {
		return IsPay;
	}
	public void setPay(boolean pay) {
		IsPay = pay;
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
	public String getCphotoUrl() {
		return cphotoUrl;
	}
	public void setCphotoUrl(String cphotoUrl) {
		this.cphotoUrl = cphotoUrl;
	}
	public int getIsFree() {
		return isFree;
	}
	public void setIsFree(int isFree) {
		this.isFree = isFree;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public String getExpert() {
		return expert;
	}
	public void setExpert(String expert) {
		this.expert = expert;
	}
	public String getExpertInfo() {
		return expertInfo;
	}
	public void setExpertInfo(String expertInfo) {
		this.expertInfo = expertInfo;
	}
	public List<YuGaoChildBean> getChildren() {
		return children;
	}
	public void setChildren(List<YuGaoChildBean> children) {
		this.children = children;
	}

	
}
