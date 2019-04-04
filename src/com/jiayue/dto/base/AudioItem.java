package com.jiayue.dto.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ����һ����Ƶ
 * @author Administrator
 *
 */
public class AudioItem  implements Parcelable{

	private String title;//��Ƶ����
	
	private String duration;//��Ƶ����ʱ��
	
	private String size;//��Ƶ���ļ���С
	
	private String data;//��Ƶ�Ĳ��ž���·��
	
	private String artist;//�ݳ���

	
	
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(title);
		dest.writeString(duration);
		dest.writeString(size);
		dest.writeString(data);
		dest.writeString(artist);
	}
	
	public static final Parcelable.Creator<AudioItem> CREATOR = new Creator<AudioItem>()
		    {
		        @Override
		        public AudioItem[] newArray(int size)
		        {
		            return new AudioItem[size];
		        }
		        
		        @Override
		        public AudioItem createFromParcel(Parcel in)
		        {
		            return new AudioItem(in);
		        }
		    };
		    
		    public AudioItem(Parcel in)
		    {
		    	title = in.readString();
		    	duration = in.readString();
		    	size = in.readString();
		    	data = in.readString();
		    	artist = in.readString();
		    }
	
	
	
	
}
