package com.jiayue.download2.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import com.jiayue.download2.entity.DocInfo;
import com.jiayue.util.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作类 操作下载历史表
 * 
 * @author mingjuan.liang
 */
@SuppressLint("HandlerLeak")
public class DataBaseHelper extends SQLiteOpenHelper {

	private Context context;
	/** 数据库锁 */
	private static String Lock = "dblock";

	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);

		this.context = context;
	}

	public DataBaseHelper(Context context) {
		super(context, DataBaseFiledParams.DB_FILE_NAME, null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + "(" + DataBaseFiledParams.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DataBaseFiledParams.HAS_DONE + " INTEGER," + DataBaseFiledParams.DOWNLOAD_STATUS + " INTEGER," + DataBaseFiledParams.INSERT_TIME + " TEXT," + DataBaseFiledParams.FILE_NAME + " TEXT," + DataBaseFiledParams.BOOK_NAME + " TEXT," + DataBaseFiledParams.FILE_PATH + " TEXT," + DataBaseFiledParams.DOWNLOAD_SIZE + " LONG," + DataBaseFiledParams.FILE_URL + " TEXT," + DataBaseFiledParams.BOOK_ID + " TEXT," + DataBaseFiledParams.FILE_SIZE + " LONG," + DataBaseFiledParams.DOWNLOAD_PROGRESS + " INTEGER," + DataBaseFiledParams.IS_CHECKED + " INTEGER," + DataBaseFiledParams.CHAP_ID + " TEXT,"
		// + DataBaseFiledParams.CHAP_NAME + " LONG,"
		+ DataBaseFiledParams.PID + " INTEGER);");
	}

	/**
	 * 插入单条数据
	 * 
	 * @param info
	 */
	public void insertValue(DocInfo info) {
		if (info != null) {
			synchronized (Lock) {
				SQLiteDatabase db = getWritableDatabase();
				ContentValues values = new ContentValues();
				String curentTime = Long.toString(System.currentTimeMillis());
				// values.put(DataBaseFiledParams.ID, info.getId());
				values.put(DataBaseFiledParams.FILE_NAME, info.getName());
				values.put(DataBaseFiledParams.BOOK_NAME, info.getBookName());
				values.put(DataBaseFiledParams.FILE_PATH, info.getFilePath());
				values.put(DataBaseFiledParams.FILE_URL, info.getUrl());
				values.put(DataBaseFiledParams.BOOK_ID, info.getBookId());
				values.put(DataBaseFiledParams.PID, info.getPid());
				values.put(DataBaseFiledParams.FILE_SIZE, info.getFileSize());
				values.put(DataBaseFiledParams.DOWNLOAD_SIZE, info.getCompletedSize());
				values.put(DataBaseFiledParams.DOWNLOAD_STATUS, info.getStatus());
				values.put(DataBaseFiledParams.IS_CHECKED, transferJudgment(info.isChecked()));
				values.put(DataBaseFiledParams.HAS_DONE, info.isHasDone() ? DataBaseFiledParams.DONE : DataBaseFiledParams.UNDONE);
				// values.put(DataBaseFiledParams.CHAP_NAME,
				// info.getChapterName());
				values.put(DataBaseFiledParams.INSERT_TIME, curentTime);
				values.put(DataBaseFiledParams.CHAP_ID, info.getChapId());
				db.insert(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 批量插入数据
	 * 
	 * @param infos
	 */
	public void insertValues(List<DocInfo> infos) {
		for (DocInfo docInfo : infos) {
			insertValue(docInfo);
		}
	}

	/**
	 * 删除单条数据
	 * 
	 * @param info
	 */
	public void deleteValue(DocInfo info) {
		if (info != null) {
			synchronized (Lock) {
				SQLiteDatabase db = getWritableDatabase();
				db.delete(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, DataBaseFiledParams.ID + "=?", new String[] { info.getId() + "" });
			}
		}
	}

	/**
	 * 删除单条数据通过文件名
	 * 
	 * @param info
	 */
	public void deleteValueByName(DocInfo info) {
		if (info != null) {
			synchronized (Lock) {
				SQLiteDatabase db = getWritableDatabase();
				db.delete(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, DataBaseFiledParams.FILE_NAME + "=?", new String[] { info.getName() + "" });
			}
		}
	}

	/**
	 * 获取所有下载过的信息
	 */
	public List<DocInfo> getInfos() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " order by " + DataBaseFiledParams.INSERT_TIME + " DESC";
		List<DocInfo> infos = getDataFromCursor(db, sql);
		return infos;
	}

	/**
	 * 获取下载未完成数据
	 * 
	 * @return
	 */
	public List<DocInfo> getUndoneInfos() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.HAS_DONE + "=" + DataBaseFiledParams.UNDONE + " order by " + DataBaseFiledParams.INSERT_TIME + " DESC";
		List<DocInfo> infos = getDataFromCursor(db, sql);
		return infos;
	}

	/**
	 * 获取未完成数据
	 * 
	 * @return
	 */
	public List<DocInfo> getDoingInfos() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.DOWNLOAD_STATUS + "!=" + DataBaseFiledParams.DONE + " order by " + DataBaseFiledParams.INSERT_TIME + " DESC";
		List<DocInfo> infos = getDataFromCursor(db, sql);
		return infos;
	}

	/**
	 * 获取下载完成数据
	 * 
	 * @return
	 */
	public List<DocInfo> getDoneInfos() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.DOWNLOAD_STATUS + "=" + DataBaseFiledParams.DONE + " order by " + DataBaseFiledParams.INSERT_TIME + " DESC";
		List<DocInfo> infos = getDataFromCursor(db, sql);
		return infos;
	}

	/**
	 * 根据id获取文档信息
	 * 
	 * @param id
	 * @return
	 */
	public DocInfo getInfo(int id) {
		DocInfo info = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.ID + "=" + id;
		List<DocInfo> infos = getDataFromCursor(db, sql);
		if (infos != null && infos.size() != 0) {
			info = infos.get(0);
		}
		return info;
	}

	public List<DocInfo> getInfo(String chapterid) {
		DocInfo info = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.CHAP_ID + " = " + "\"" + chapterid + "\"";
		List<DocInfo> infos = getDataFromCursor(db, sql);

		return infos;
	}

	public List<DocInfo> getInfo2(String bookid) {
		DocInfo info = null;
		SQLiteDatabase db = getWritableDatabase();
//		db.beginTransaction();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.BOOK_ID + " = " + "\"" + bookid + "\"";
		List<DocInfo> infos = getDataFromCursor(db, sql);
//		db.endTransaction();
		db.close();
		return infos;
	}

	/**
	 * 根据name获取文档信息
	 * 
	 * @param
	 * @return
	 */
	public DocInfo getInfoByName(String name) {
		DocInfo info = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.FILE_NAME + "='" + name + "'";
		List<DocInfo> infos = getDataFromCursor(db, sql);
		if (infos != null && infos.size() != 0) {
			info = infos.get(0);
		}
		return info;
	}

	/**
	 * 从查询游标内获取数据
	 * 
	 * @param db
	 * @param sql
	 * @return
	 */
	private List<DocInfo> getDataFromCursor(SQLiteDatabase db, String sql) {
		Cursor cursor;
		cursor = db.rawQuery(sql, null);
		List<DocInfo> infos = new ArrayList<DocInfo>();
		try {
			while (cursor != null && cursor.moveToNext()) {
				DocInfo info = new DocInfo();
				info.setId(cursor.getInt(cursor.getColumnIndex(DataBaseFiledParams.ID)));
				info.setPid(cursor.getInt(cursor.getColumnIndex(DataBaseFiledParams.PID)));
				info.setName(cursor.getString(cursor.getColumnIndex(DataBaseFiledParams.FILE_NAME)));
				info.setBookName(cursor.getString(cursor.getColumnIndex(DataBaseFiledParams.BOOK_NAME)));
				info.setFilePath(cursor.getString(cursor.getColumnIndex(DataBaseFiledParams.FILE_PATH)));
				info.setHasDone(transferJudgment(cursor.getColumnIndex(DataBaseFiledParams.HAS_DONE)));
				info.setFileSize(cursor.getLong(cursor.getColumnIndex(DataBaseFiledParams.FILE_SIZE)));
				info.setUrl(cursor.getString(cursor.getColumnIndex(DataBaseFiledParams.FILE_URL)));
				info.setBookId(cursor.getString(cursor.getColumnIndex(DataBaseFiledParams.BOOK_ID)));
				info.setDownloadProgress(cursor.getInt(cursor.getColumnIndex(DataBaseFiledParams.DOWNLOAD_PROGRESS)));
				info.setStatus(cursor.getInt(cursor.getColumnIndex(DataBaseFiledParams.DOWNLOAD_STATUS)));
				info.setChecked(transferJudgment(cursor.getInt((cursor.getColumnIndex(DataBaseFiledParams.IS_CHECKED)))));
				info.setCompletedSize(cursor.getLong(cursor.getColumnIndex(DataBaseFiledParams.DOWNLOAD_SIZE)));
				info.setChapId(cursor.getString(cursor.getColumnIndex(DataBaseFiledParams.CHAP_ID)));
				infos.add(info);
			}
		} catch (Exception e) {
			// TODO: handle exception
			try {
				mHandler.sendEmptyMessage(0);
			} catch (Exception e2) {
				// TODO: handle exception
			}
//			ActivityUtils.showToastForFail(context, "此功能需要存储权限，请开启。");
		}
		cursor.close();
		return infos;
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ActivityUtils.showToastForFail(context, "此功能需要存储权限，请开启。");
		};
	};

	/**
	 * 将数据内作为判断的int类型标识转换为bool类型
	 * 
	 * @param intValue
	 * @return
	 */
	private boolean transferJudgment(int intValue) {
		boolean bool = false;
		if (intValue == 1) {
			bool = true;
		}
		return bool;
	}

	/**
	 * 将数据属性内的bool类型转换为入库的int类型
	 * 
	 * @param bool
	 * @return
	 */
	private int transferJudgment(boolean bool) {
		int result = 0;
		if (bool) {
			result = 1;
		}
		return result;
	}

	/**
	 * 批量删除数据
	 * 
	 * @param infos
	 */
	public void deleteValues(List<DocInfo> infos) {
		for (DocInfo docInfo : infos) {
			this.deleteValue(docInfo);
		}
	}

	/**
	 * 删除所有数据
	 */
	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "delete from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME;
		db.execSQL(sql);
	}

	/**
	 * 更新数据
	 * 
	 * @param info
	 */
	public void updateValue(DocInfo info) {
		if (info == null) {
			return;
		}
		synchronized (Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
//			db.beginTransaction();
			ContentValues values = new ContentValues();
			values.put(DataBaseFiledParams.HAS_DONE, transferJudgment(info.isHasDone()));
			values.put(DataBaseFiledParams.DOWNLOAD_STATUS, info.getStatus());
			values.put(DataBaseFiledParams.DOWNLOAD_PROGRESS, info.getDownloadProgress());
			values.put(DataBaseFiledParams.IS_CHECKED, transferJudgment(info.isChecked()));
			values.put(DataBaseFiledParams.DOWNLOAD_SIZE, info.getCompletedSize());
			values.put(DataBaseFiledParams.FILE_SIZE, info.getFileSize());
			db.update(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, values, DataBaseFiledParams.ID + "=?", new String[] { info.getId() + "" });
//			db.endTransaction();
			db.close();
		}
	}

	/**
	 * 批量更新数据
	 * 
	 * @param infos
	 */
	public void updateValues(List<DocInfo> infos) {
		for (DocInfo docInfo : infos) {
			updateValue(docInfo);
		}
	}

	/**
	 * 检查数据是否已经存在
	 * 
	 * @param info
	 * @return
	 */
	public boolean getHasInserted(DocInfo info) {
		boolean bool = false;
		SQLiteDatabase db = getReadableDatabase();
		int id = info.getId();
		String sql = "select * from " + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + " where " + DataBaseFiledParams.ID + "=" + id;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			bool = true;
		}
		cursor.close();
		return bool;
	}

	/**
	 * 取消全选
	 * 
	 */
	public void deselectAll() {
		synchronized (Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DataBaseFiledParams.IS_CHECKED, 0);
			db.update(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, values, DataBaseFiledParams.IS_CHECKED + "=?", new String[] { 1 + "" });
		}
	}

	/**
	 * 全部选中
	 */
	public void selectAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DataBaseFiledParams.IS_CHECKED, 1);
		db.update(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, values, DataBaseFiledParams.IS_CHECKED + "=?", new String[] { 0 + "" });
	}

	/**
	 * 删除选中条目
	 */
	public void deleteSelected() {
		synchronized (Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, DataBaseFiledParams.IS_CHECKED, new String[] { 1 + "" });
		}
	}

	/**
	 * 重置登录状态 将由于程序异常退出重启后而造成的下载状态错误进行重置 主要讲未完成的下载任务状态修改为暂停
	 */
	public void resetDownloadStatus() {
		synchronized (Lock) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DataBaseFiledParams.DOWNLOAD_STATUS, DataBaseFiledParams.PAUSING);
			db.update(DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME, values, DataBaseFiledParams.HAS_DONE + "=?", new String[] { DataBaseFiledParams.UNDONE + "" });
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// String sql = "ALTER TABLE"
		// + DataBaseFiledParams.DOWNlOAD_HISTORY_TABLE_NAME + "ADD";
		db.setVersion(newVersion);
	}
}
