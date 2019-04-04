package com.jiayue.dto.base;

import java.io.Serializable;

public class ZhiboInfoBean implements Serializable{
	
	private String code;
	private String codeInfo;
	private ZhiboInfoDataBean data;
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
	public ZhiboInfoDataBean getData() {
		return data;
	}
	public void setData(ZhiboInfoDataBean data) {
		this.data = data;
	}
	
	
}
