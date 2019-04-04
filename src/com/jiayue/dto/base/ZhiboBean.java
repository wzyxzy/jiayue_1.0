package com.jiayue.dto.base;

import java.util.List;

public class ZhiboBean {
	private String code;
	private String codeInfo;
	private List<ZhiboListBean> data;
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
	public List<ZhiboListBean> getData() {
		return data;
	}
	public void setData(List<ZhiboListBean> data) {
		this.data = data;
	}
	
}
