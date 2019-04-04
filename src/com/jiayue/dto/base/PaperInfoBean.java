package com.jiayue.dto.base;

import java.io.Serializable;

/**
 * Created by ping on 2015-10-20.
 */
public class PaperInfoBean implements Serializable {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"claTesPapId":"6","paperCode":"201510280159557613","name":"dfk"}
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
         * claTesPapId : 6
         * paperCode : 201510280159557613
         * name : dfk
         */

        private String claTesPapId;
        private String paperCode;
        private String name;

        public void setClaTesPapId(String claTesPapId) {
            this.claTesPapId = claTesPapId;
        }

        public void setPaperCode(String paperCode) {
            this.paperCode = paperCode;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClaTesPapId() {
            return claTesPapId;
        }

        public String getPaperCode() {
            return paperCode;
        }

        public String getName() {
            return name;
        }
    }
}

