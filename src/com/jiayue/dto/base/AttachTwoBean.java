package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class AttachTwoBean implements Serializable{
	private String code;
	private String codeInfo;
	private Data data;
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
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}

	public static class Data implements Serializable{
		private int attachOneIsPay;
		private float attachOneTotalPrice;
		private List<AttachTwo> attachTwoList;

		public int getAttachOneIsPay() {
			return attachOneIsPay;
		}

		public void setAttachOneIsPay(int attachOneIsPay) {
			this.attachOneIsPay = attachOneIsPay;
		}

		public float getAttachOneTotalPrice() {
			return attachOneTotalPrice;
		}

		public void setAttachOneTotalPrice(float attachOneTotalPrice) {
			this.attachOneTotalPrice = attachOneTotalPrice;
		}

		public List<AttachTwo> getAttachTwoList() {
			return attachTwoList;
		}

		public void setAttachTwoList(List<AttachTwo> attachTwoList) {
			this.attachTwoList = attachTwoList;
		}
	}
}
