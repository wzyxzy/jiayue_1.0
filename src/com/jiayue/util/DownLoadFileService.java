package com.jiayue.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.os.Environment;
import android.widget.ProgressBar;

public class DownLoadFileService {
	/**
	 * 下载文件的帮助方法
	 * 
	 * @param path
	 *            文件在服务器上的路径
	 * @return 下载完毕后的文件对应的file对象
	 */
	public static File downLoad(String path, ProgressBar pd) throws Exception {
		// 判断sd卡是否可用
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(ActivityUtils.getSDPath(), getFileName(path));
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			int max = conn.getContentLength();
			pd.setMax(max);
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			int total = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				pd.setProgress(total);
			}
			fos.flush();
			fos.close();
			is.close();
			return file;
		} else {
			return null;
		}

	}
	public static String getFileName(String path) {
		int start = path.lastIndexOf("/") + 1;
		return path.substring(start);
	}
}
