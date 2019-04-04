package com.jiayue.download2.db;

/**
 * 数据库参数 数据库名称 表名 列名 其他数据库使用的常量
 * 
 * @author mingjuanliang
 * 
 */
public class DataBaseFiledParams {

	/** 数据库文件名 */
	public static final String DB_FILE_NAME = "globle_download";

	/** 数据库表名 */
	public static final String DOWNlOAD_HISTORY_TABLE_NAME = "download_history";

	/** id列 */
	public static final String ID = "id";

	/** pid列 */
	public static final String PID = "pid";

	/** 文件名列 */
	public static final String FILE_NAME = "file_name";

	/** 书名 */
	public static final String BOOK_NAME = "book_name";

	/** 文件名列 */
	public static final String FILE_URL = "file_url";
	/** 文件名列 */
	public static final String BOOK_ID = "book_id";

	/** 文件路径列 */
	public static final String FILE_PATH = "file_path";

	/** 是否已完成 */
	public static final String HAS_DONE = "has_done";

	/** 下载状态 */
	public static final String DOWNLOAD_STATUS = "download_status";

	/** 插入数据库时间 */
	public static final String INSERT_TIME = "insert_time";

	/** 文件大小 */
	public static final String FILE_SIZE = "file_size";

	/** 是否被选中 */
	public static final String IS_CHECKED = "is_checked";

	/** 已下载大小 */
	public static final String DOWNLOAD_SIZE = "download_size";

	/** 下载进度 */
	public static final String DOWNLOAD_PROGRESS = "download_progress";

	public static final String CHAP_ID = "chap_id";

	public static final String CHAP_NAME = "chapter_name";
	

	/** 已完成标识 */
	public static final int DONE = 1;

	/** 未完成标识 */
	public static final int UNDONE = 0;

	/** 暂停 */
	public static final int PAUSING = 4;

	/** 正在下载 */
	public static final int LOADING = 2;

	/** 等待中 */
	public static final int WAITING = 3;

	/** 失败 */
	public static final int FAILED = 5;

}
