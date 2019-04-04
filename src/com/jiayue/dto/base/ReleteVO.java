package com.jiayue.dto.base;

import java.io.Serializable;

public class ReleteVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 文件id
	private String fileId;
	private String mainId;
	private String fileName;
	private String imgPath;
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getMainId() {
		return mainId;
	}
	public void setMainId(String mainId) {
		this.mainId = mainId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

}
