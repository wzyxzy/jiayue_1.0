package com.jiayue.download2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MySQLiteDao {

	private MySQLiteOpenHelper helper;

	public MySQLiteDao(Context context) {
		helper = new MySQLiteOpenHelper(context);
	}

	public boolean add(String bookId, String chapId, String chapName,
			String fileName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("book_id", bookId);
		values.put("chap_id", chapId);
		values.put("chap_name", chapName);
		values.put("file_name", fileName);
		long result = db.insert("info", null, values);
		db.close();
		if (result == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 查询
	 */
	public String findChapId(String bookId) {
		String chap_id = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		// Cursor cursor = db.rawQuery("select phone from info where name=?",
		// new String[]{name});
		Cursor cursor = db.query("info", new String[]{"chap_id"}, "book_id=?",
				new String[]{bookId}, null, null, null);
		if (cursor.moveToNext()) {
			chap_id = cursor.getString(0);
		}
		cursor.close();// 释放内存。
		db.close();
		return chap_id;
	}
	/**
	 * 查询
	 */
	public String findChapName(String chapId) {
		String chap_name = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		// Cursor cursor = db.rawQuery("select phone from info where name=?",
		// new String[]{name});
		Cursor cursor = db.query("info", new String[]{"chap_name"}, "chapId=?",
				new String[]{chapId}, null, null, null);
		if (cursor.moveToNext()) {
			chap_name = cursor.getString(0);
		}
		cursor.close();// 释放内存。
		db.close();
		return chap_name;
	}
}
