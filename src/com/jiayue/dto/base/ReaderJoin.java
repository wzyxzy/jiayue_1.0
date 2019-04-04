package com.jiayue.dto.base;

import java.io.Serializable;

/**
 * ------------------------------------------------------------------
 * 创建时间：2015-9-17 上午10:04:28 项目名称：wyst
 * 
 * @author Ping Wang
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：AuthorReplyList.java 类说明：
 *        ------------------------------------------------------------------
 */
public class ReaderJoin implements Serializable {

	private String addTime;
	private String subTitle;
	private String subContent;
	private String surname;

	@Override
	public String toString() {
		return "AuthorReplyList [addTime=" + addTime + ", subTitle=" + subTitle
				+ ", subContent=" + subContent + ", surname=" + surname + "]";
	}

	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getSubContent() {
		return subContent;
	}
	public void setSubContent(String subContent) {
		this.subContent = subContent;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}

}
