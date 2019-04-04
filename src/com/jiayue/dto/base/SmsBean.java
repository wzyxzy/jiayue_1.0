package com.jiayue.dto.base;

public class SmsBean {
	/**
     * code : SUCCESS
     * codeInfo : 发送成功!
     * data : {"checkCode":"762429"}
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
         * checkCode : 762429
         */

        private String checkCode;

        public void setCheckCode(String checkCode) {
            this.checkCode = checkCode;
        }

        public String getCheckCode() {
            return checkCode;
        }
    }
}
