package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ping on 2015-10-20.
 */
public class TestPaperBean implements Serializable {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"id":"1","paperCode":"123456","name":"fdsaf"}]
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
         * id : 1
         * paperCode : 123456
         * name : fdsaf
         */

        private String id;
        private String paperCode;
        private String name;

        public void setId(String id) {
            this.id = id;
        }

        public void setPaperCode(String paperCode) {
            this.paperCode = paperCode;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getPaperCode() {
            return paperCode;
        }

        public String getName() {
            return name;
        }
    }
    
}

