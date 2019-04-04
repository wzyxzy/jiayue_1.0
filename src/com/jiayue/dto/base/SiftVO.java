package com.jiayue.dto.base;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "siftvo")
public class SiftVO{
	@Column(name="id",isId=true,autoGen=true)
	private int id;
	@Column(name = "bookId")
	private String bookId;
	@Column(name = "time")
	private long time;
	@Column(name = "siftName")
	private String siftName;
	@Column(name = "SiftType")
	private String SiftType;
	@Column(name = "attachFlag")
	private String attachFlag;
	@Column(name = "confidence")
	private int confidence;
	@Column(name = "imageName")
	private String imageName;
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSiftName() {
		return siftName;
	}
	public void setSiftName(String siftName) {
		this.siftName = siftName;
	}
	public String getSiftType() {
		return SiftType;
	}
	public void setSiftType(String siftType) {
		SiftType = siftType;
	}
	public String getAttachFlag() {
		return attachFlag;
	}
	public void setAttachFlag(String attachFlag) {
		this.attachFlag = attachFlag;
	}

	public int getConfidence() {
		return confidence;
	}

	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
