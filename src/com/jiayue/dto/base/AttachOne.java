package com.jiayue.dto.base;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "AttachOne")
public class AttachOne implements Serializable ,Parcelable{

	// private String fid;//附件ID
	@Column(name = "bookId")
	private String bookId; // 附件包中文件的父id，如视频的bookid就是视频包的Id
	@Column(name = "attachOneId",isId = true,autoGen = false)
	private String attachOneId; // 附件ID
	@Column(name = "attachOneName")
	private String attachOneName;
	@Column(name = "attachOneIspackage")
	private int attachOneIspackage;
	@Column(name = "attachOneType")
	private String attachOneType; // 文件类型。pdf/txt之类
	@Column(name = "attachOneSaveName")
	private String attachOneSaveName; // 保存的文件名
	@Column(name = "attachOnePath")
	private String attachOnePath; // 保存路径
	@Column(name = "attach_flag")
	private String attachOneFlag;
	@Column(name = "isEncode")
	private String isEncode;
	@Column(name = "isSendAtta1")
	private String isSendAtta1;
	@Column(name = "shareUrl")
	private String shareUrl;
	@Column(name = "updateTime")
	private String updateTime;
	@Column(name = "isPay")
	private int isPay;
	@Column(name = "price")
	private float price;
	//以下两个属性用于二级目录
	@Column(name = "attachOneIsPay")
	private int attachOneIsPay;//一级文件夹是否已经付费
	@Column(name = "attachOneTotalPrice")
	private float attachOneTotalPrice;//二级文件总价格


	public int getAttachOneIsPay() {
		return attachOneIsPay;
	}

	public void setAttachOneIsPay(int attachOneIsPay) {
		this.attachOneIsPay = attachOneIsPay;
	}

	public float getAttachOneTotalPrice() {
		return attachOneTotalPrice;
	}

	public void setAttachOneTotalPrice(float attachOneTotalPrice) {
		this.attachOneTotalPrice = attachOneTotalPrice;
	}

	public int getIsPay() {
		return isPay;
	}

	public void setIsPay(int isPay) {
		this.isPay = isPay;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	

	public AttachOne() {
		super();
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getIsSendAtta1() {
		return isSendAtta1;
	}
	public void setIsSendAtta1(String isSendAtta1) {
		this.isSendAtta1 = isSendAtta1;
	}
	public String getAttachOneFlag() {
		return attachOneFlag;
	}
	public void setAttachOneFlag(String attachOneFlag) {
		this.attachOneFlag = attachOneFlag;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getAttachOneId() {
		return attachOneId;
	}
	public void setAttachOneId(String attachOneId) {
		this.attachOneId = attachOneId;
	}
	public String getAttachOneName() {
		return attachOneName;
	}
	public void setAttachOneName(String attachOneName) {
		this.attachOneName = attachOneName;
	}
	public int getAttachOneIspackage() {
		return attachOneIspackage;
	}
	public void setAttachOneIspackage(int attachOneIspackage) {
		this.attachOneIspackage = attachOneIspackage;
	}
	public String getAttachOneType() {
		return attachOneType;
	}
	public void setAttachOneType(String attachOneType) {
		this.attachOneType = attachOneType;
	}
	public String getAttachOneSaveName() {
		return attachOneSaveName;
	}
	public void setAttachOneSaveName(String attachOneSaveName) {
		this.attachOneSaveName = attachOneSaveName;
	}
	public String getAttachOnePath() {
		return attachOnePath;
	}
	public void setAttachOnePath(String attachOnePath) {
		this.attachOnePath = attachOnePath;
	}
	@Override
	public String toString() {
		return "AttachOne [bookId=" + bookId + ", attachOneId=" + attachOneId
				+ ", attachOneName=" + attachOneName + ", attachOneIspackage="
				+ attachOneIspackage + ", attachOneType=" + attachOneType
				+ ", attachOneSaveName=" + attachOneSaveName
				+ ", attachOnePath=" + attachOnePath + ", attachOneFlag="
				+ attachOneFlag + ", isEncode=" + isEncode + ", isSendAtta1="
				+ isSendAtta1 + ", shareUrl=" + shareUrl + "]";
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(bookId);
		dest.writeString(attachOneId);
		dest.writeString(attachOneName);
		dest.writeInt(attachOneIspackage);
		dest.writeString(attachOneType);
		dest.writeString(attachOneSaveName);
		dest.writeString(attachOnePath);
		dest.writeString(attachOneFlag);
		dest.writeString(isEncode);
		dest.writeString(isSendAtta1);
		dest.writeString(shareUrl);
		dest.writeString(updateTime);
		
	}
	
	public static final Parcelable.Creator<AttachOne> CREATOR = new Creator<AttachOne>()
		    {
		        @Override
		        public AttachOne[] newArray(int size)
		        {
		            return new AttachOne[size];
		        }
		        
		        @Override
		        public AttachOne createFromParcel(Parcel in)
		        {
		            return new AttachOne(in);
		        }
		    };
		    
		    public AttachOne(Parcel in)
		    {
				bookId = in.readString();
				attachOneId = in.readString();
				attachOneName = in.readString();
				attachOneIspackage = in.readInt();
				attachOneType = in.readString();
				attachOneSaveName = in.readString();
				attachOnePath = in.readString();
				attachOneFlag = in.readString();
				isEncode = in.readString();
				isSendAtta1 = in.readString();
				shareUrl = in.readString();
				updateTime = in.readString();
		    }

	// private Date lastTime;
	// private Date updateTime;
	// private int updateRemind;
	// private Date creatTime;
	// private String fileName;
	// private int fileSize;
	// private int order;
	// private String dataType; //文件类型。 1为普通文件、2图片包、3音频包、4文档包、5PPT包
	// private String fileLevel; //文件层级 0为跟目录
	// private String remark; //备注
	// private String price; //价格
	// private String discount; //折扣
	// private String author;
	// private String intro;

	// public String getIntro() {
	// return intro;
	// }
	// public void setIntro(String intro) {
	// this.intro = intro;
	// }
	// public String getAuthor() {
	// return author;
	// }
	// public void setAuthor(String author) {
	// this.author = author;
	// }

	// public Date getLastTime() {
	// return lastTime;
	// }
	// public void setLastTime(Date lastTime) {
	// this.lastTime = lastTime;
	// }
	// public Date getUpdateTime() {
	// return updateTime;
	// }
	// public void setUpdateTime(Date updateTime) {
	// this.updateTime = updateTime;
	// }
	// public int getUpdateRemind() {
	// return updateRemind;
	// }
	// public void setUpdateRemind(int updateRemind) {
	// this.updateRemind = updateRemind;
	// }
	// public Date getCreatTime() {
	// return creatTime;
	// }
	// public void setCreatTime(Date creatTime) {
	// this.creatTime = creatTime;
	// }
	// public String getFileName() {
	// return fileName;
	// }
	// public void setFileName(String fileName) {
	// this.fileName = fileName;
	// }
	// public int getFileSize() {
	// return fileSize;
	// }
	// public void setFileSize(int fileSize) {
	// this.fileSize = fileSize;
	// }
	// public int getOrder() {
	// return order;
	// }
	// public void setOrder(int order) {
	// this.order = order;
	// }
	// public String getType() {
	// return attachOneType;
	// }
	// public void setType(String attachOneType) {
	// this.attachOneType = attachOneType;
	// }
	// public String getDataType() {
	// return dataType;
	// }
	// public void setDataType(String dataType) {
	// this.dataType = dataType;
	// }

	// public String getFileLevel() {
	// return fileLevel;
	// }
	// public void setFileLevel(String fileLevel) {
	// this.fileLevel = fileLevel;
	// }
	// public String getRemark() {
	// return remark;
	// }
	// public void setRemark(String remark) {
	// this.remark = remark;
	// }
	// public String getPrice() {
	// return price;
	// }
	// public void setPrice(String price) {
	// this.price = price;
	// }
	// public String getDiscount() {
	// return discount;
	// }
	// public void setDiscount(String discount) {
	// this.discount = discount;
	// }

}
