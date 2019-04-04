package com.jiayue.download2.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class Util {
	/**
	 * 获取消息实例
	 * 
	 * @param msgArg1
	 * @param msagArg2
	 * @param obj
	 * @param what
	 * @return
	 */
	public static Message getMessage(int msgArg1, Object obj, int what) {
		Message msg = new Message();
		msg.arg1 = msgArg1;
		msg.what = what;
		msg.obj = obj;
		return msg;
	}

	/**
	 * 将链接中的中文加密
	 * 
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodingURL(String url)
			throws UnsupportedEncodingException {
		if (url != null) {
			url = url.replace("/", "....");
			url = url.replace(":", "___");
			url = URLEncoder.encode(url, "utf-8");
			url = url.replace("....", "/");
			url = url.replace("___", ":");
		}
		return url;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		String fileExtension = "";
		if (fileName.contains(".")) {
			String[] tempString = fileName.split("\\.");
			fileExtension = tempString[tempString.length - 1];
		}
		return fileExtension;
	}

	/**
	 * 显示提示信息
	 * 
	 * @param context
	 * @param text
	 */
	public static void makeToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 根据文件类型返回打开文件的方式
	 * 
	 * @param file
	 * @return
	 */
	public static Intent getFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		String fileExtension = getFileExtension(file);
		if (fileExtension.equalsIgnoreCase("ppt")) {
			intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		} else if (fileExtension.equalsIgnoreCase("pptx")) {
			intent.setDataAndType(uri,
					"application/vnd.openxmlformats-officedocument.presentationml.presentation");
		} else if (fileExtension.equalsIgnoreCase("doc")) {
			intent.setDataAndType(uri, "application/msword");
		} else if (fileExtension.equalsIgnoreCase("docx")) {
			intent.setDataAndType(uri,
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		} else if (fileExtension.equalsIgnoreCase("xls")) {
			intent.setDataAndType(uri, "application/vnd.ms-excel");
		} else if (fileExtension.equalsIgnoreCase("xlsx")) {
			intent.setDataAndType(uri,
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		} else if (fileExtension.equalsIgnoreCase("txt")) {
			intent.setDataAndType(uri, "text/plain");
		} else if (fileExtension.equalsIgnoreCase("jpg")
				|| fileExtension.equalsIgnoreCase("jpeg")
				|| fileExtension.equalsIgnoreCase("bmp")
				|| fileExtension.equalsIgnoreCase("png")) {
			intent.setDataAndType(uri, "image/*");
		} else if (fileExtension.equalsIgnoreCase("pdf")) {
			intent.setDataAndType(uri, "application/pdf");
		} else {
			return null;
		}
		return intent;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}

	private static File savefile;

	private static Timer timer;

	protected static boolean bool;

	private static InputStream inputStream;

	public static final int MSG_NORMAL = 1;

	public static final int MSG_SUCCESS = 2;

	public static final int MSG_FAIL = 3;

	public static void download(final Handler handler, final String url,
			final String savePath, final String saveName) {
		new Thread(new Runnable() {
			/* 输出流 */
			private FileOutputStream outputStream;

			public void run() {
				startTimer();
				try {
					savefile = new File(savePath);
					if (!savefile.exists()) {
						savefile.mkdirs();
					}
					/* 获取url */
					URL sourceUrl = new URL(url);
					Log.e("filename", saveName + "/" + savePath);
					/* 建立连接 */
					long fileSize = getFileSize(url);
					/* 建立新文件 */
					savefile = new File(savePath + "/" + saveName);
					if (savefile.exists()) {
						savefile.delete();
					}
					savefile.createNewFile();
					outputStream = new FileOutputStream(savePath + "/"
							+ saveName, true);
					/* 建立缓冲区 */
					byte[] buffer = new byte[1024];
					/* 开始下载 */
					int byteCount;
					int loadedLength = 0;
					while ((byteCount = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, byteCount);
						loadedLength += byteCount;
						if (bool) {
							int progress = (int) (loadedLength * 100 / fileSize);
							handler.sendMessage(getMessage(progress, null,
									MSG_NORMAL));
							bool = false;
						}
					}
					outputStream.flush();
					handler.sendMessage(getMessage(100, null, MSG_SUCCESS));
					/* 下载完成 */
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendMessage(getMessage(0, null, MSG_FAIL));
				} finally {
					/* 关闭流 */
					try {
						if (inputStream != null) {
							inputStream.close();
						}
						if (outputStream != null) {
							outputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static long getFileSize(String url)
			throws UnsupportedEncodingException, MalformedURLException,
			IOException {
		/* 1.初始化url */
		URL sourceUrl = new URL(url);

		/* 2.发送http请求 */
		HttpURLConnection conn = (HttpURLConnection) sourceUrl.openConnection();
		// 如果本地没保存，则请求获取
		long fileSize = conn.getContentLength();
		conn.disconnect();

		/* 4.重新打开连接，从上次的断点处请求数据 */
		conn = (HttpURLConnection) sourceUrl.openConnection();
		// + (mDocInfo.getFileSize() - 1)
		inputStream = conn.getInputStream();
		return fileSize;
	}

	/**
	 * 开始计时器
	 */
	private static void startTimer() {
		timer = new Timer();
		/* 定时任务，定时打开开关，向界面发送下载进度 */
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				bool = true;
			}
		};
		timer.schedule(task, 0, 500);
	}

	/**
	 * 文件大小换算
	 * 
	 * @param bytes
	 *            byte的大小
	 */
	public static String byteToKOrM(double bytes) {
		if (bytes < 0) {
			return 0 + "kb";
		}
		if (bytes < 1024) {
			return bytes + "b";
		}
		double kbytes = bytes / 1024;
		if (kbytes < 1024) {
			return formatDecimal(kbytes) + "kb";
		} else {
			double mbytes = kbytes / 1024;
			return formatDecimal(kbytes) + "mb";
		}
	}

	/**
	 * 将double类型数据保留两位小数
	 * 
	 * @param doulbe
	 *            需要格式化的double类型数据
	 * @return 保留到两位小数的字符串
	 */
	public static String formatDecimal(Double doulbe) {
		DecimalFormat df = new DecimalFormat("#0.00");

		return df.format(doulbe);
	}

}
