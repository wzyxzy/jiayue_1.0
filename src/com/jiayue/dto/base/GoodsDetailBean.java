package com.jiayue.dto.base;

/**
 * Created by BAO on 2016-08-10.
 */
public class GoodsDetailBean {
    private String photourl;
    private String title;
    private float price;
    private String content;
    private String code;//订单号
    private String name;
    private String address;
    private String telephone;
    private String fapiao;
    private int fapiaoId;
    private int receiverId;
    private boolean isFapiao = false;
    private String beizhu;

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public boolean isFapiao() {
        return isFapiao;
    }

    public void setisFapiao(boolean fapiao) {
        isFapiao = fapiao;
    }

    public int getFapiaoId() {
        return fapiaoId;
    }

    public void setFapiaoId(int fapiaoId) {
        this.fapiaoId = fapiaoId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFapiao() {
        return fapiao;
    }

    public void setFapiao(String fapiao) {
        this.fapiao = fapiao;
    }
}
