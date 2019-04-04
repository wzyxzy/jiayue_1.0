package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ping on 2015-10-20.
 */
public class CoursesBean implements Serializable {



    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"id":"1","groupName":"fsdjdff","groupCode":"1445508965796"},{"id":"2","groupName":"fsdjdff","groupCode":"1445509645989"},{"id":"3","groupName":"fsdjdff","groupCode":"1446003795063"},{"id":"4","groupName":"fsd1111111111111jdff","groupCode":"1446003886159"}]
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
         * groupName : fsdjdff
         * groupCode : 1445508965796
         */

        private String id;
        private String groupName;
        private String groupCode;

        public void setId(String id) {
            this.id = id;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public void setGroupCode(String groupCode) {
            this.groupCode = groupCode;
        }

        public String getId() {
            return id;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupCode() {
            return groupCode;
        }
    }
}

