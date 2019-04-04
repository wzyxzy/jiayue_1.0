package com.jiayue.dto.base;

import java.io.Serializable;

public class PublishVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String publishId;
	private String publishName;

	public String getPublishId() {
		return publishId;
	}
	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}
	public String getPublishName() {
		return publishName;
	}
	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}

}
