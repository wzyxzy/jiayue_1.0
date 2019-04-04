package com.jiayue.dto.base;

import java.io.Serializable;

public class ShopListAllBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String codeInfo;
	private ShopListDataBean data;
	
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
	public ShopListDataBean getData() {
		return data;
	}
	public void setData(ShopListDataBean data) {
		this.data = data;
	}

	
}
