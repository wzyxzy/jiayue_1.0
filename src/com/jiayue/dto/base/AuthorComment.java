package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;


public class AuthorComment implements Serializable {

	private String author;
	private String bookImgPath;
	private String bookImg;
	private String bookName;
	// private String concern;
	private String authorInfo;
	// private String topicCount;
	private List<AuthorMainContent> mainContents;

	@Override
	public String toString() {
		return "AuthorComment [author=" + author + ", bookImgPath="
				+ bookImgPath + ", bookImg=" + bookImg + ", bookName="
				+ bookName + ", authorInfo=" + authorInfo + ", mainContents="
				+ mainContents + "]";
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBookImgPath() {
		return bookImgPath;
	}
	public void setBookImgPath(String bookImgPath) {
		this.bookImgPath = bookImgPath;
	}
	public String getBookImg() {
		return bookImg;
	}
	public void setBookImg(String bookImg) {
		this.bookImg = bookImg;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthorInfo() {
		return authorInfo;
	}
	public void setAuthorInfo(String authorInfo) {
		this.authorInfo = authorInfo;
	}
}