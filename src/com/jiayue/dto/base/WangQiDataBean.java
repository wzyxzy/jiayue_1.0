package com.jiayue.dto.base;

import java.io.Serializable;

public class WangQiDataBean implements Serializable{
	private String fileId;
	private String fileName;
	private String dutation;
	private String createTime;
	private String imageUrl;
	private String author;
	private String vipType;
	private String shelfEncode;
	private String content;
	private String keyword;
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDutation() {
		return dutation;
	}
	public void setDutation(String dutation) {
		this.dutation = dutation;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getVipType() {
		return vipType;
	}
	public void setVipType(String vipType) {
		this.vipType = vipType;
	}
	public String getShelfEncode() {
		return shelfEncode;
	}
	public void setShelfEncode(String shelfEncode) {
		this.shelfEncode = shelfEncode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	
}
