package com.jiayue.dto.base;

import java.io.Serializable;

public class OrderBean implements Serializable{


	/**
	 * code : SUCCESS
	 * codeInfo : 操作成功!
	 * data : {"message":"订单添加成功，请发起支付","attachCode":"20178221503387671767"}
	 */

	private String code;
	private String codeInfo;
	private Data data;

	public void setCode(String code) {
		this.code = code;
	}

	public void setCodeInfo(String codeInfo) {
		this.codeInfo = codeInfo;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public String getCodeInfo() {
		return codeInfo;
	}

	public Data getData() {
		return data;
	}

	public static class Data {
		/**
		 * message : 订单添加成功，请发起支付
		 * attachCode : 20178221503387671767
		 */

		private String message;
		private String attachCode;

		public void setMessage(String message) {
			this.message = message;
		}

		public void setAttachCode(String attachCode) {
			this.attachCode = attachCode;
		}

		public String getMessage() {
			return message;
		}

		public String getAttachCode() {
			return attachCode;
		}
	}
}
