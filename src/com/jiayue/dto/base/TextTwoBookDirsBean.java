package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ping on 2015-10-20.
 */
public class TextTwoBookDirsBean implements Serializable {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"id":5,"name":"第二章第一节"},{"id":6,"name":"第二章第一节"}]
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
         * id : 5
         * name : 第二章第一节
         */

        private int id;
        private String name;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
