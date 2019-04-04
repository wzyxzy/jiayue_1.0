package com.jiayue.dto.base;

import java.io.Serializable;

/**
 * Created by BAO on 2016-07-22.
 */
public class ZhiboAddressBean implements Serializable{

    private int rate_type;
    private String hls_downstream_address;

    public int getRate_type() {
        return rate_type;
    }

    public void setRate_type(int rate_type) {
        this.rate_type = rate_type;
    }

    public String getHls_downstream_address() {
        return hls_downstream_address;
    }

    public void setHls_downstream_address(String hls_downstream_address) {
        this.hls_downstream_address = hls_downstream_address;
    }

}
