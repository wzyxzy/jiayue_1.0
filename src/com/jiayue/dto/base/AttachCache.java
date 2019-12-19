package com.jiayue.dto.base;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AttachCache {

    private String bookId; // 附件包中文件的父id，如视频的bookid就是视频包的Id
    private String attachId; // 附件ID
    private String attachName;
    private int attachIspackage;
    private String attachType; // 文件类型。pdf/txt之类
    private String attachSaveName; // 保存的文件名
    private String attachPath; // 保存路径
//    private String packageId; // 保存路径
    private boolean isChoose; // 被选中


    public AttachCache(String bookId, String attachId, String attachName, int attachIspackage, String attachType, String attachSaveName, String attachPath, boolean isChoose) {
        this.bookId = bookId;
        this.attachId = attachId;
        this.attachName = attachName;
        this.attachIspackage = attachIspackage;
        this.attachType = attachType;
        this.attachSaveName = attachSaveName;
        this.attachPath = attachPath;
        this.isChoose = isChoose;
    }

    public AttachCache() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAttachId() {
        return attachId;
    }

    public void setAttachId(String attachId) {
        this.attachId = attachId;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public int getAttachIspackage() {
        return attachIspackage;
    }

    public void setAttachIspackage(int attachIspackage) {
        this.attachIspackage = attachIspackage;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public String getAttachSaveName() {
        return attachSaveName;
    }

    public void setAttachSaveName(String attachSaveName) {
        this.attachSaveName = attachSaveName;
    }

    public String getAttachPath() {
        return attachPath;
    }

    public void setAttachPath(String attachPath) {
        this.attachPath = attachPath;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

//    public String getPackageId() {
//        return packageId;
//    }
//
//    public void setPackageId(String packageId) {
//        this.packageId = packageId;
//    }
}
