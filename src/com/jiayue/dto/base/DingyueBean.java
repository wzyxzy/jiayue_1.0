package com.jiayue.dto.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BAO on 2016-08-02.
 */
public class DingyueBean implements Serializable{
    private String code;
    private String codeInfo;
    private List<DingyueDataBean> data;

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

    public List<DingyueDataBean> getData() {
        return data;
    }

    public void setData(List<DingyueDataBean> data) {
        this.data = data;
    }
}
