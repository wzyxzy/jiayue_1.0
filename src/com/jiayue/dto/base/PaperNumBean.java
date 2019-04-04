package com.jiayue.dto.base;

/**
 * Created by BAO on 2018-07-19.
 */

public class PaperNumBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"result":"false","number":0}
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
         * result : false
         * number : 0
         */

        private String result;
        private int number;

        public void setResult(String result) {
            this.result = result;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getResult() {
            return result;
        }

        public int getNumber() {
            return number;
        }
    }
}
