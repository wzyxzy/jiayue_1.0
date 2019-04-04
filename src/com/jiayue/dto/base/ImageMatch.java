package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.Date;
//@Table(name="image_match")
public class ImageMatch implements Serializable {

	// @Id(column="_id")
	private Date IM_CreatTime;// 添加时间
	private String IM_ChapterId;// 章节ID
	private String IM_BookId;// 图书ID
	private String IM_Id;// 识别图片ID
	private String IM_PicName;// 识别图片文件名
	private String IM_SaveName;// 识别图片存储名
	private String IM_SavePath;// 识别图片存储路径
	private String IM_OrderName;// 目标图片文件名
	private String IM_OrderSaveName;// 目标图片存储名
	private String IM_OrderPath;// 目标图片存储路径

	public String getIM_Id() {
		return IM_Id;
	}

	@Override
	public String toString() {
		return "ImageMatch [IM_BookId=" + IM_BookId + ", IM_Id=" + IM_Id
				+ ", IM_ChapterId=" + IM_ChapterId + ", IM_CreatTime="
				+ IM_CreatTime + ", IM_PicName=" + IM_PicName
				+ ", IM_SaveName=" + IM_SaveName + ", IM_SavePath="
				+ IM_SavePath + ", IM_OrderName=" + IM_OrderName
				+ ", IM_OrderSaveName=" + IM_OrderSaveName + ", IM_OrderPath="
				+ IM_OrderPath + "]";
	}

	public void setIM_Id(String iM_Id) {
		IM_Id = iM_Id;
	}

	public String getIM_BookId() {
		return IM_BookId;
	}

	public void setIM_BookId(String iM_BookId) {
		IM_BookId = iM_BookId;
	}

	public String getIM_ChapterId() {
		return IM_ChapterId;
	}

	public void setIM_ChapterId(String iM_ChapterId) {
		IM_ChapterId = iM_ChapterId;
	}

	public Date getIM_CreatTime() {
		return IM_CreatTime;
	}

	public void setIM_CreatTime(Date iM_CreatTime) {
		IM_CreatTime = iM_CreatTime;
	}

	public String getIM_PicName() {
		return IM_PicName;
	}

	public void setIM_PicName(String iM_PicName) {
		IM_PicName = iM_PicName;
	}

	public String getIM_SaveName() {
		return IM_SaveName;
	}

	public void setIM_SaveName(String iM_SaveName) {
		IM_SaveName = iM_SaveName;
	}

	public String getIM_SavePath() {
		return IM_SavePath;
	}

	public void setIM_SavePath(String iM_SavePath) {
		IM_SavePath = iM_SavePath;
	}

	public String getIM_OrderName() {
		return IM_OrderName;
	}

	public void setIM_OrderName(String iM_OrderName) {
		IM_OrderName = iM_OrderName;
	}

	public String getIM_OrderSaveName() {
		return IM_OrderSaveName;
	}

	public void setIM_OrderSaveName(String iM_OrderSaveName) {
		IM_OrderSaveName = iM_OrderSaveName;
	}

	public String getIM_OrderPath() {
		return IM_OrderPath;
	}

	public void setIM_OrderPath(String iM_OrderPath) {
		IM_OrderPath = iM_OrderPath;
	}

}
