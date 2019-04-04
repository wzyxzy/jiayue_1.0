package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class WangqiUrlBean implements Serializable{
	private String code;
	private String codeInfo;
	private List<WangqiUrlDataBean> data;
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
	public List<WangqiUrlDataBean> getData() {
		return data;
	}
	public void setData(List<WangqiUrlDataBean> data) {
		this.data = data;
	}
	
}
