package com.jiayue.dto.base;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "AttachTwo")
public class AttachTwo implements Serializable {

	
	@Column(name = "bookId")
	private String bookId;
	@Column(name = "attachTwoId",isId = true,autoGen = false)
	private String attachTwoId;
	@Column(name = "attachOneId")
	private String attachOneId;
	@Column(name = "attachTwoName")
	private String attachTwoName;
	@Column(name = "attachTwoIspackage")
	private int attachTwoIspackage;
	@Column(name = "attachTwoType")
	private String attachTwoType;
	@Column(name = "attachTwoSaveName")
	private String attachTwoSaveName;
	@Column(name = "attachTwoPath")
	private String attachTwoPath;
	@Column(name = "attchtwo_flag")
	private String attachTwoFlag;
	@Column(name = "isSendAtta2")
	private String isSendAtta2;
	@Column(name = "shareUrl")
	private String shareUrl;
	@Column(name = "updateTime")
	private String updateTime;
	@Column(name = "isPay")
	private int isPay;
	@Column(name = "price")
	private float price;


	public int getIsPay() {
		return isPay;
	}

	public void setIsPay(int isPay) {
		this.isPay = isPay;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getIsSendAtta2() {
		return isSendAtta2;
	}
	public void setIsSendAtta2(String isSendAtta2) {
		this.isSendAtta2 = isSendAtta2;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getAttachTwoId() {
		return attachTwoId;
	}
	public void setAttachTwoId(String attachTwoId) {
		this.attachTwoId = attachTwoId;
	}
	public String getAttachOneId() {
		return attachOneId;
	}
	public void setAttachOneId(String attachOneId) {
		this.attachOneId = attachOneId;
	}
	public String getAttachTwoName() {
		return attachTwoName;
	}
	public void setAttachTwoName(String attachTwoName) {
		this.attachTwoName = attachTwoName;
	}
	public int getAttachTwoIspackage() {
		return attachTwoIspackage;
	}
	public void setAttachTwoIspackage(int attachTwoIspackage) {
		this.attachTwoIspackage = attachTwoIspackage;
	}
	public String getAttachTwoType() {
		return attachTwoType;
	}
	public void setAttachTwoType(String attachTwoType) {
		this.attachTwoType = attachTwoType;
	}
	public String getAttachTwoSaveName() {
		return attachTwoSaveName;
	}
	public void setAttachTwoSaveName(String attachTwoSaveName) {
		this.attachTwoSaveName = attachTwoSaveName;
	}
	public String getAttachTwoPath() {
		return attachTwoPath;
	}
	public void setAttachTwoPath(String attachTwoPath) {
		this.attachTwoPath = attachTwoPath;
	}
	public String getAttachTwoFlag() {
		return attachTwoFlag;
	}
	public void setAttachTwoFlag(String attachTwoFlag) {
		this.attachTwoFlag = attachTwoFlag;
	}
	@Override
	public String toString() {
		return "AttachTwo [bookId=" + bookId + ", attachTwoId=" + attachTwoId
				+ ", attachOneId=" + attachOneId + ", attachTwoName="
				+ attachTwoName + ", attachTwoIspackage=" + attachTwoIspackage
				+ ", attachTwoType=" + attachTwoType + ", attachTwoSaveName="
				+ attachTwoSaveName + ", attachTwoPath=" + attachTwoPath
				+ ", attachTwoFlag=" + attachTwoFlag + ", isSendAtta2="
				+ isSendAtta2 + ", shareUrl=" + shareUrl + "]";
	}

}
