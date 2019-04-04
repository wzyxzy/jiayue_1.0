package com.jiayue.dto.base;

import java.io.Serializable;

public class TextPart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int type = 0;
	private String content;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TextPart(int type, String content) {
		super();
		this.type = type;
		this.content = content;
	}
	public TextPart() {
		super();
		// TODO Auto-generated constructor stub
	}

}
