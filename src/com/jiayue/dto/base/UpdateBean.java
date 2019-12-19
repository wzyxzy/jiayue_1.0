package com.jiayue.dto.base;

import java.io.Serializable;

public class UpdateBean implements Serializable{
	/**
     * {}code : SUCCESS
     * codeInfo : 发现新版本!
     * data : {"isUpdate":"2",
     * "url":"http://123.56.111.31:8080/umybook_server/download/jiayue.apk",
     * "desc":"android更新内容：$1.修复了下载时不能暂停的问题; $2.修复了低版本不能安装问题."}}
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

    public static class Data implements Serializable{
        /**
         * isUpdate : 2
         * url : http://123.56.111.31:8080/umybook_server/download/jiayue.apk
         * desc : android更新内容：$1.修复了下载时不能暂停的问题; $2.修复了低版本不能安装问题.
         */

        private String isUpdate;
        private String url;
        private String desc;

        public void setIsUpdate(String isUpdate) {
            this.isUpdate = isUpdate;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getIsUpdate() {
            return isUpdate;
        }

        public String getUrl() {
            return url;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "isUpdate='" + isUpdate + '\'' +
                    ", url='" + url + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}
