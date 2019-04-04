package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class GoodsListBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String codeInfo;
	private List<GoodsBean> data;

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

	public List<GoodsBean> getData() {
		return data;
	}

	public void setData(List<GoodsBean> data) {
		this.data = data;
	}
}
