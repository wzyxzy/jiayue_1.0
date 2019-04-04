package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class AttachOneBean implements Serializable{
	private String code;
	private String codeInfo;
	private List<AttachOne> data;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCodeInfo() {
		return codeInfo;
	}
	public void setCodeInfo(String codeInfo) {
		this.codeInfo = codeInfo;
	}
	public List<AttachOne> getData() {
		return data;
	}
	public void setData(List<AttachOne> data) {
		this.data = data;
	}
}
