package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class BuyListBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String codeInfo;
	private List<BuyBean> data;

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

	public List<BuyBean> getData() {
		return data;
	}

	public void setData(List<BuyBean> data) {
		this.data = data;
	}
}
