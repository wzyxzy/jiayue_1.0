package com.jiayue.dto.base;

import java.io.Serializable;

public class GoodsFapiaoAllBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String codeInfo;
	private GoodsFapiaoBean data;

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

	public GoodsFapiaoBean getData() {
		return data;
	}

	public void setData(GoodsFapiaoBean data) {
		this.data = data;
	}
}
