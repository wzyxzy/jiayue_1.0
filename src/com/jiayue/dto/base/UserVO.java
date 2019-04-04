package com.jiayue.dto.base;

import java.io.Serializable;

public class UserVO implements Serializable {
	private String userName;
	private String userPwd;
	private String userEmail;
	private String userId;
	private String booksData;
	private String isRememberPd;
	// private String publishName;//绑定的出版社
	// private String publishId;

	private int userStatus;
	
	
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}
	public UserVO() {
		super();
	}
	public UserVO(String userName, String password, String email,
			String userId, String booksData, String isRememberPd) {
		super();
		this.userName = userName;
		this.userPwd = password;
		this.userEmail = email;
		this.userId = userId;
		this.booksData = booksData;
		this.isRememberPd = isRememberPd;
		// this.publishName = publishName;
		// this.publishId = publishId;
	}

	// public String getPublishId() {
	// return publishId;
	// }
	// public void setPublishId(String publishId) {
	// this.publishId = publishId;
	// }
	// public String getPublishName() {
	// return publishName;
	// }
	// public void setPublishName(String publishName) {
	// this.publishName = publishName;
	// }
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return userPwd;
	}
	public void setPassword(String password) {
		this.userPwd = password;
	}
	public String getEmail() {
		return userEmail;
	}
	public void setEmail(String email) {
		this.userEmail = email;
	}
	public String getBooksData() {
		return booksData;
	}
	public void setBooksData(String booksData) {
		this.booksData = booksData;
	}

	public String getIsRememberPd() {
		return isRememberPd;
	}

	public void setIsRememberPd(String isRememberPd) {
		this.isRememberPd = isRememberPd;
	}

}
