package com.jiayue.dto.base;

import java.io.Serializable;

/**
 * Created by BAO on 2016-08-10.
 */
public class GoodsBean implements Serializable{
    private float price;
    private int goodId;
    private String photoUrl;
    private String goodDisc;
    private String goodsName;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGoodDisc() {
        return goodDisc;
    }

    public void setGoodDisc(String goodDisc) {
        this.goodDisc = goodDisc;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
