package com.jiayue.dto.base;

import java.util.List;

public class WangQiListBean {
	private String code;
	private String codeInfo;
	private List<WangQiDataBean> data;
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
	public List<WangQiDataBean> getData() {
		return data;
	}
	public void setData(List<WangQiDataBean> data) {
		this.data = data;
	}
	
	
}
