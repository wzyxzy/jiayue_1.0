package com.jiayue.dto.base;

import java.util.List;

/**
 * Created by BAO on 2016-09-29.
 */
public class VipBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"name":"fsad"},{"name":"fsad"}]
     */

    private String code;
    private String codeInfo;
    /**
     * name : fsad
     */

    private List<Data> data;

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
