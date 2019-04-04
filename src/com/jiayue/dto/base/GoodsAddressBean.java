package com.jiayue.dto.base;

import java.io.Serializable;

/**
 * Created by BAO on 2016-08-10.
 */
public class GoodsAddressBean implements Serializable{
    String address;
    String name;
    int id;
    String telephone;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
