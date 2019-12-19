package com.jiayue.dto.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 音乐条目
 *
 * @author Administrator
 */
public class AudioItem implements Parcelable {

    private String title;//音频的名称

    private String duration;//音乐描述

    private String size;//音乐大小

    private String data;//音乐路径

    private String oldData;//音乐下载路径

    private String bookId;//bookId

    private String artist;//艺术家名字

    protected AudioItem(Parcel in) {
        title = in.readString();
        duration = in.readString();
        size = in.readString();
        data = in.readString();
        oldData = in.readString();
        bookId = in.readString();
        artist = in.readString();
    }

    public static final Creator<AudioItem> CREATOR = new Creator<AudioItem>() {
        @Override
        public AudioItem createFromParcel(Parcel in) {
            return new AudioItem(in);
        }

        @Override
        public AudioItem[] newArray(int size) {
            return new AudioItem[size];
        }
    };

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public AudioItem() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "VideoItem [title=" + title + ", duration=" + duration
                + ", size=" + size + ", data=" + data + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(duration);
        dest.writeString(size);
        dest.writeString(data);
        dest.writeString(oldData);
        dest.writeString(bookId);
        dest.writeString(artist);
    }
}
