package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

public class AuthorReplyBean implements Serializable {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"addTime":"2015-08-11","subTitle":"fsafsd","subContent":"fsdfsdfsdfdsfsd","surname":"test"},{"addTime":"2015-08-11","subTitle":"fsfads","subContent":"fsdfsdfsd","surname":"test"},{"addTime":"2015-08-11","subTitle":"bcxbxcvbc","subContent":"bcbcvxbxcb","surname":"test"}]
     */

    private String code;
    private String codeInfo;
    private List<Data> data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {
        /**
         * addTime : 2015-08-11
         * subTitle : fsafsd
         * subContent : fsdfsdfsdfdsfsd
         * surname : test
         */

        private String addTime;
        private String subTitle;
        private String subContent;
        private String surname;

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public void setSubContent(String subContent) {
            this.subContent = subContent;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getAddTime() {
            return addTime;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public String getSubContent() {
            return subContent;
        }

        public String getSurname() {
            return surname;
        }
    }
}
