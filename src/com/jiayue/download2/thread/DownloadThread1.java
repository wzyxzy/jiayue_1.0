package com.jiayue.download2.thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.Utils.Global;
import com.jiayue.download2.Utils.Util;
import com.jiayue.download2.db.DataBaseFiledParams;
import com.jiayue.download2.db.DataBaseHelper;
import com.jiayue.download2.entity.DocInfo;


/**
 * 下载线程
 * 
 * @author mingjuan.liang
 * 
 */
public class DownloadThread1 extends Thread {
	/**
	 * 此线程下载的文件
	 */
	private DocInfo mDocInfo;
	/**
	 * 是否停止下载
	 */
	private boolean stopDownload;

	private DataBaseHelper mDataBaseHelper;

	/**
	 * 更新进度定时器
	 */
	private Timer timer;

	/**
	 * 是否发送进度标志位
	 */
	protected boolean bool;

	/** 下载列表数据库 */

	private Context mContext;

	/** 输入流 */
	private InputStream inputStream;

	/** 需要保存的文件 */
	private File savefile;

	private DownloadManager mManager;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Global.FAIL_MESSAGE :
					mManager.downloadFailed(mDocInfo);
					break;
				case Global.NORMAL_MESSAGE :
					if (mDocInfo.getDownloadProgress() == 100) {
						mManager.downloadCompleted(mDocInfo);
					} else {
						mManager.updateProgress(mDocInfo);
					}
					break;
				default :
					break;
			}

		};

	};

	public DownloadThread1(DocInfo info, DataBaseHelper dataBaseHelper,
			DownloadManager manager) {
		mDocInfo = info;
		mDataBaseHelper = dataBaseHelper;
		mManager = manager;
	}

	public void stopDownload() {
		stopDownload = true;
	}

	@Override
	public void run() {
		if (stopDownload) {
			try {
				sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = Util
					.getMessage(0, mDocInfo, Global.INTERRUPT_MESSAGE);
			handler.sendMessage(msg);
			return;
		}
		mDocInfo.setStatus(DataBaseFiledParams.LOADING);
		mDataBaseHelper.updateValue(mDocInfo);
		download();
	}

	/**
	 * 执行下载
	 */
	private void download() {
		try {
			startTimer();
			String saveName = mDocInfo.getName();
			savefile = new File(mDocInfo.getFilePath());
			if (!savefile.exists()) {
				savefile.mkdirs();
			}
			/* 1.获取文件大小 */
			long fileSize = getFileSize();

			if (fileSize <= 0) {
				// Message message;
				// mDocInfo.setStatus(DataBaseFiledParams.FAILED);
				// message = Util.getMessage(mDocInfo.getDownloadProgress(),
				// mDocInfo, Global.FAIL_MESSAGE);
				// handler.sendMessage(message);
				// mDataBaseHelper.updateValue(mDocInfo);
				mDataBaseHelper.deleteValue(mDocInfo);
				return;
			}
			/* 2. 创建本地文件 */
			savefile = new File(mDocInfo.getFilePath() + "/" + saveName);

			RandomAccessFile accessFile = new RandomAccessFile(savefile, "rwd");
			// 寻找断点
			accessFile.seek(mDocInfo.getCompletedSize());
			/* 3. 定义缓冲 */
			byte[] buffer = new byte[4096];

			/* 4.写入数据 */
			int byteCount = 0;
			long loadedLenth = mDocInfo.getCompletedSize();
//			Log.i("other===", "loadedLenth=" + loadedLenth);
			long oldLength = 0;
			Message message;
			long time1 = System.currentTimeMillis();
			while ((byteCount = inputStream.read(buffer, 0, 4096)) != -1) {
				accessFile.write(buffer, 0, byteCount);
//				Log.i("other===", "byteCount=" + byteCount);
				loadedLenth += byteCount;
				if (bool) {
					/* 5.发送消息 更新进度 */
					int progress = (int) (loadedLenth * 100 / fileSize);
//					Log.i("other===", "progress=" + progress);
					if (progress == 100) {
						break;
					}
					mDocInfo.setDownloadProgress(progress);
					mDocInfo.setCompletedSize(loadedLenth);
					DocInfo info = mDataBaseHelper.getInfo(mDocInfo.getId());
					if (info != null) {
						mDocInfo.setChecked(info.isChecked());
					} else {
						mDocInfo.setChecked(false);
					}
					bool = false;
					/* 获取下载速度 */
					long time2 = System.currentTimeMillis();
					long tempLenth = loadedLenth - oldLength;
					long tempTime = time2 - time1;
					double speed = tempLenth * 1000 / tempTime;
					mDocInfo.setSpeed(speed);
					message = Util.getMessage(progress, mDocInfo,
							Global.NORMAL_MESSAGE);
					mDataBaseHelper.updateValue(mDocInfo);
					//
					handler.sendMessage(message);
					time1 = System.currentTimeMillis();
					oldLength = loadedLenth;
				}
				if (stopDownload) {
					Message msg = Util.getMessage(0, mDocInfo,
							Global.INTERRUPT_MESSAGE);
					handler.sendMessage(msg);
					return;
				}
				if (accessFile.length() == fileSize) {
					Log.v("download", "downloadCompleted----------------");
					break;
				}
			}
			accessFile.close();
			/* 6.发送下载完成消息 */
			mDocInfo.setDownloadProgress(100);
//			savefile.renameTo(new File(mDocInfo.getFilePath() + "/" + saveName.substring(5, saveName.length()-1)));
			
			if (mDataBaseHelper != null) {
				mDocInfo.setHasDone(true);
				mDocInfo.setStatus(DataBaseFiledParams.DONE);
				mDocInfo.setCompletedSize(loadedLenth);
				mDocInfo.setDownloadProgress(100);
				DocInfo info = mDataBaseHelper.getInfo(mDocInfo.getId());
				if (info != null) {
					mDocInfo.setChecked(info.isChecked());
				} else {
					mDocInfo.setChecked(false);
				}
				mDataBaseHelper.updateValue(mDocInfo);
			}
			timer.cancel();
			message = Util.getMessage(100, mDocInfo, Global.NORMAL_MESSAGE);
			handler.sendMessage(message);
			//
		} catch (Exception e) {
			e.printStackTrace();
			DocInfo info = mDataBaseHelper.getInfo(mDocInfo.getId());
			if (info != null) {
				mDocInfo.setChecked(info.isChecked());
			} else {
				mDocInfo.setChecked(false);
			}
			Message message;
			if (mDocInfo.getDownloadProgress() == 100) {
				mDocInfo.setStatus(DataBaseFiledParams.DONE);
				message = Util.getMessage(mDocInfo.getDownloadProgress(),
						mDocInfo, Global.NORMAL_MESSAGE);
				//
			} else {
				mDocInfo.setStatus(DataBaseFiledParams.FAILED);
				message = Util.getMessage(mDocInfo.getDownloadProgress(),
						mDocInfo, Global.FAIL_MESSAGE);
				//
			}
			handler.sendMessage(message);
			mDataBaseHelper.updateValue(mDocInfo);
		} finally {
			/* 7. 关闭流 */
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				mManager.didComplete(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private long getFileSize() throws UnsupportedEncodingException,
			MalformedURLException, IOException {
		/* 1.初始化url */
		URL sourceUrl = getURL();

		/* 2.发送http请求 */
		HttpURLConnection conn = (HttpURLConnection) sourceUrl.openConnection();
		/* 3. 获取文件大小 */
		long fileSize = mDocInfo.getFileSize();
		if (mDocInfo.getFileSize() <= 0) {
			// 如果本地没保存，则请求获取
			fileSize = conn.getContentLength();
			mDocInfo.setFileSize(fileSize);
			conn.disconnect();
		}

		/* 4.重新打开连接，从上次的断点处请求数据 */
		conn = (HttpURLConnection) sourceUrl.openConnection();
		conn.setRequestProperty("RANGE", "bytes=" + mDocInfo.getCompletedSize()
				+ "-");
		String type = conn.getHeaderField("Content-Type");
		if (null == type || "".equals(type)
				|| type.equals("application/json;charset=UTF-8")) {
			return 0;
		}
		// + (mDocInfo.getFileSize() - 1)
		inputStream = conn.getInputStream();
		return fileSize;
	}

	/**
	 * 获取下载的url
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	private URL getURL() throws UnsupportedEncodingException,
			MalformedURLException {
		String filePath = mDocInfo.getUrl();
		String url = Global.DOCUMENT_DOWNLOAD_URL + filePath;
		URL sourceUrl = new URL(url);
		return sourceUrl;
	}

	/**
	 * 开始计时器
	 */
	private void startTimer() {
		timer = new Timer();
		/* 定时任务，定时打开开关，向界面发送下载进度 */
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				bool = true;
			}
		};
		timer.schedule(task, 200, 2000);
	}

}
