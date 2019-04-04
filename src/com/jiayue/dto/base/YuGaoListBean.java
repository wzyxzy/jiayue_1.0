package com.jiayue.dto.base;

import java.util.List;

public class YuGaoListBean {
	private String code;
	private String codeInfo;
	private List<YuGaoDataBean> data;
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
	public List<YuGaoDataBean> getData() {
		return data;
	}
	public void setData(List<YuGaoDataBean> data) {
		this.data = data;
	}
	
	
}
