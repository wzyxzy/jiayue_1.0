package com.jiayue.dto.base;
public class ReaderMainContent {
	private String title;
	private String addTime;
	private String content;
	// private String browseCount;
	private String QACount;
	private String id;
	private String hasNewReply;

	@Override
	public String toString() {
		return "MainContent [title=" + title + ", addTime=" + addTime
				+ ", content=" + content + ", QACount=" + QACount + ", id="
				+ id + ", hasNewReply=" + hasNewReply + "]";
	}
	public String getHasNewReply() {
		return hasNewReply;
	}
	public void setHasNewReply(String hasNewReply) {
		this.hasNewReply = hasNewReply;
	}
	public String getTitle() {
		return title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getQACount() {
		return QACount;
	}
	public void setQACount(String qACount) {
		QACount = qACount;
	}
}