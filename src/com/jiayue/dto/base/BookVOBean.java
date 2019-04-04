package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class BookVOBean implements Serializable{

	private String code;
	private String codeInfo;
	private List<BookVO> data;
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
	public List<BookVO> getData() {
		return data;
	}
	public void setData(List<BookVO> data) {
		this.data = data;
	}
}
