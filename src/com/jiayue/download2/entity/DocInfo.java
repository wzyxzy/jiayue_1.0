package com.jiayue.download2.entity;

import java.io.Serializable;

import com.jiayue.download2.db.DataBaseFiledParams;

/**
 * 文档信息类
 * 
 * @author mingjuan.liang
 * 
 */
public class DocInfo implements Serializable {

	private static final long serialVersionUID = 5081610806660863176L;

	private String chapId;

	// private String chapterName;
	/** 保存名称 */
	private String name;

	/** 文档名称 */
	private String bookName;

	/** 文档路径 */
	private String url;

	/** 文档id */
	private int id;

	/** 文档所在文件夹id */
	private int pid;

	/** 文档路径 */
	private String filePath;

	private String bookId;
	/** 是否是文件夹 */
	private boolean isDirectoty;

	/** 是否下载完成 */
	private boolean hasDone = false;

	/**
	 * 下载进度
	 */
	private int downloadProgress;

	/**
	 * 已完成大小
	 */
	private long completedSize;

	/**
	 * 文件大小
	 */
	private long fileSize;

	/** 是否被选中 */
	private boolean isChecked = false;

	/**
	 * 下载速度
	 */
	private double speed;

	/**
	 * 下载状态
	 */
	private int status = DataBaseFiledParams.WAITING;
	
	/**
	 * @param name
	 *            名称
	 * @param url
	 *            下载路径
	 * @param id
	 *            文件或文件夹id
	 * @param pid
	 *            所在的文件夹id
	 * @param filePath
	 *            路径
	 * @param isDirectory
	 *            是否是文件目录
	 */
	public DocInfo(String name, String bookName, String url, int id, int pid,
			String filePath, boolean isDirectory,String chapId) {
		super();
		this.name = name;
		this.bookName = bookName;
		this.url = url;
		this.id = id;
		this.pid = pid;
		this.filePath = filePath;
		this.isDirectoty = isDirectory;
		this.chapId=chapId;
	}

	// public String getChapterName() {
	// return chapterName;
	// }
	//
	// public void setChapterName(String chapterName) {
	// this.chapterName = chapterName;
	// }

	
	
	public String getChapId() {
		return chapId;
	}

	public void setChapId(String chapId) {
		this.chapId = chapId;
	}

	public DocInfo() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isDirectoty() {
		return isDirectoty;
	}

	public void setDirectoty(boolean isDirectoty) {
		this.isDirectoty = isDirectoty;
	}

	/**
	 * @return the hasDone
	 */
	public boolean isHasDone() {
		return hasDone;
	}

	/**
	 * 
	 * @param hasDone
	 *            the hasDone to set
	 */
	public void setHasDone(boolean hasDone) {
		this.hasDone = hasDone;
	}

	/**
	 * @return the downloadProgress
	 */
	public int getDownloadProgress() {
		return downloadProgress;
	}

	/**
	 * @param downloadProgress
	 *            the downloadProgress to set
	 */
	public void setDownloadProgress(int downloadProgress) {
		this.downloadProgress = downloadProgress;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (object instanceof DocInfo) {
			DocInfo info = (DocInfo) object;
			return this.getName().equals(info.getName());
		}
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	/**
	 * @return the completedSize
	 */
	public long getCompletedSize() {
		return completedSize;
	}

	/**
	 * @param completedSize
	 *            the completedSize to set
	 */
	public void setCompletedSize(long completedSize) {
		this.completedSize = completedSize;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the isChecked
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/**
	 * @param isChecked
	 *            the isChecked to set
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	@Override
	public String toString() {
		return "DocInfo{" +
				"chapId='" + chapId + '\'' +
				", name='" + name + '\'' +
				", bookName='" + bookName + '\'' +
				", url='" + url + '\'' +
				", id=" + id +
				", pid=" + pid +
				", filePath='" + filePath + '\'' +
				", bookId='" + bookId + '\'' +
				", isDirectoty=" + isDirectoty +
				", hasDone=" + hasDone +
				", downloadProgress=" + downloadProgress +
				", completedSize=" + completedSize +
				", fileSize=" + fileSize +
				", isChecked=" + isChecked +
				", speed=" + speed +
				", status=" + status +
				'}';
	}
}
