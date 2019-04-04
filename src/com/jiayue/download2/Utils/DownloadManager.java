package com.jiayue.download2.Utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jiayue.download2.db.DataBaseFiledParams;
import com.jiayue.download2.db.DataBaseHelper;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.download2.observer.DateObserverable;
import com.jiayue.download2.thread.DownloadThread1;
import com.jiayue.dto.base.AttachOne;
import com.jiayue.dto.base.AttachTwo;
import com.jiayue.dto.base.ImageMatch;
import com.jiayue.util.ActivityUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 下载管理器
 * 
 * @author mingjuan.liang
 */
public class DownloadManager {
	/**
	 * 同时最大任务数
	 */
	public static final int MAX_CONNECTIONS = 3;
	/**
	 * 当前下载队列
	 */
	private List<Thread> active = new ArrayList<Thread>();
	/**
	 * 当前等待队列
	 */
	private List<Thread> queue = new ArrayList<Thread>();
	/**
	 * 当前下载及等待列表 用于任务和下载线程的对应
	 */
	private static Map<DocInfo, DownloadThread1> map = new HashMap<DocInfo, DownloadThread1>();
	/**
	 * 下载列表数据库
	 */
	private DataBaseHelper mDataBaseHelper;

	private static DownloadManager manager;

	private static DateObserverable o = new DateObserverable();

	private DownloadManager(Context context) {
		mDataBaseHelper = new DataBaseHelper(context);
	}

	public static DownloadManager getInstance(Context context) {
		if (manager == null) {
			return new DownloadManager(context);
		} else {
			return manager;
		}
	}

	public void push(Thread runnable) {
		queue.add(runnable);
		if (active.size() < MAX_CONNECTIONS) {
			startNext();
		}
	}

	private void startNext() {
		if (!queue.isEmpty()) {
			Thread next = queue.get(0);
			queue.remove(next);
			active.add(next);
			next.start();
		}
	}

	public void didComplete(Thread runnable) {
		active.remove(runnable);
		startNext();
	}

	/**
	 * 开始下载
	 * 
	 * @param doc
	 */
	public void startForActivity(DocInfo doc) {
		// 判断数据库里面是否包含下载记录
		DocInfo info = mDataBaseHelper.getInfoByName(doc.getName());
		if (null != info) {
			if (info.getStatus() == DataBaseFiledParams.DONE) {
				mDataBaseHelper.deleteValue(info);
				ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
				start(doc);
			} else if (info.getStatus() == DataBaseFiledParams.FAILED) {
				mDataBaseHelper.deleteValue(info);
				ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
				start(doc);
			} else if (info.getStatus() == DataBaseFiledParams.PAUSING) {
				startHasId(info);
			} else if (info.getStatus() == DataBaseFiledParams.WAITING) {
				DownloadThread1 thread = map.get(info);// 看下载的进程里面还有没有
				if (thread != null) {
					return;// 如果进程中有，那么就不进行二次下载
				} else {
					startHasId(info);
				}
			} else if (info.getStatus() == DataBaseFiledParams.LOADING) {
				DownloadThread1 thread = map.get(doc);
				if (thread != null) {
					return;// 如果进程中有，那么就不进行二次下载
				} else {
					// 如果数据库有记录进程中没有，删除记录重新下载
					startHasId(info);
				}
			}
		} else {
			start(doc);
		}
	}

	/**
	 * 开始下载
	 * 
	 * @param doc
	 */
	public void start(DocInfo doc) {
		doc.setStatus(DataBaseFiledParams.WAITING);
		mDataBaseHelper.insertValue(doc);
		DocInfo info = mDataBaseHelper.getInfoByName(doc.getName());
		DownloadThread1 r = new DownloadThread1(info, mDataBaseHelper, this);
		map.put(info, r);
		push(r);
	}

	/**
	 * 开始下载
	 * 
	 * @param doc
	 */
	public void startHasId(DocInfo doc) {
		doc.setStatus(DataBaseFiledParams.WAITING);
		mDataBaseHelper.updateValue(doc);
		DownloadThread1 r = new DownloadThread1(doc, mDataBaseHelper, this);
		map.put(doc, r);
		push(r);
	}

	/**
	 * 暂停下载
	 * 
	 * @param doc
	 */
	public void pause(DocInfo doc) {
		doc.setStatus(DataBaseFiledParams.PAUSING);
		stopDownload(doc);
		mDataBaseHelper.updateValue(doc);
	}

	/**
	 * 取消下载
	 * 
	 * @param doc
	 */
	public void cancel(DocInfo doc) {
		DownloadThread1 thread = getThreadByDocInfo(doc);
		if (thread != null) {
			stopDownload(doc);
		}
		mDataBaseHelper.deleteValue(doc);
		ActivityUtils.deleteBookFormSD(doc.getBookId(), doc.getName());
		ActivityUtils.deleteFoder(new File(ActivityUtils.getSDPath(doc.getBookId()), doc.getBookName()));
		if (doc.getBookName().endsWith(".zip")) {
			String[] s = doc.getBookName().split("\\.");
			if (!TextUtils.isEmpty(s[0]))
				ActivityUtils.deleteFoder(new File(ActivityUtils.getSDPath(doc.getBookId()), s[0]));
		}
		ActivityUtils.deleteFoder(new File(ActivityUtils.getSDPath(doc.getBookId()), "__MACOSX"));
		if (!ActivityUtils.isExistAndRead(doc.getBookId())) {
			ActivityUtils.deleteFoder(new File(ActivityUtils.getSDPath(doc.getBookId()), "images"));
		}
	}

	/**
	 * @param doc
	 */
	private void stopDownload(DocInfo doc) {
		DownloadThread1 thread = map.get(doc);
		if (thread == null) {
			Log.i("other", "==========null=======");
			return;
		} else {
			Log.i("other", "==========not null=======");
		}
		thread.stopDownload();
		active.remove(thread);
		queue.remove(thread);
		if (active.size() < MAX_CONNECTIONS) {
			startNext();
		}
	}

	/**
	 * 更新进度
	 * 
	 * @param info
	 */
	public void updateProgress(DocInfo info) {
		if (listeners != null) {
			for (DownloadListener listener : listeners) {
				listener.onUpdateProgress(info);
			}
		}
	}

	/**
	 * 下载完成
	 * 
	 * @param info
	 */
	public void downloadCompleted(DocInfo info) {
		if (listeners != null) {
			for (DownloadListener listener : listeners) {
				listener.onDownloadCompleted(info);
			}
		}
	}

	/**
	 * 下载失败
	 * 
	 * @param info
	 */
	public void downloadFailed(DocInfo info) {
		if (listeners != null) {
			for (DownloadListener listener : listeners) {
				listener.onDownloadFailed(info);
				ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
				mDataBaseHelper.deleteValue(info);
			}
		}
	}

	/**
	 * 下载监听回调接口
	 * 
	 * @author wangping
	 */
	public interface DownloadListener {
		/**
		 * 进度更新
		 * 
		 * @param info
		 */
		void onUpdateProgress(DocInfo info);

		/**
		 * 下载完成
		 * 
		 * @param info
		 */
		void onDownloadCompleted(DocInfo info);

		/**
		 * 下载失败
		 * 
		 * @param info
		 */
		void onDownloadFailed(DocInfo info);
	}

	/**
	 * 下载监听
	 */
	private static List<DownloadListener> listeners = new ArrayList<DownloadManager.DownloadListener>();

	public void addDownloadListener(DownloadListener l) {
		if (l == null) {
			return;
		}
		listeners.add(l);
	}

	public void removeDownloadListener() {
		listeners.clear();
	}

	/**
	 * 获取正在下载的列表
	 */
	public List<DocInfo> getListDownloading() {
		List<DocInfo> infos = new ArrayList<DocInfo>();
		Set<DocInfo> key = map.keySet();
		for (Iterator<DocInfo> it = key.iterator(); it.hasNext();) {
			DocInfo info = (DocInfo) it.next();
			infos.add(info);
		}
		return infos;
	}

	/**
	 * 获取正在下载的线程
	 */
	public DownloadThread1 getThreadByDocInfo(DocInfo doc) {
		Set<DocInfo> key = map.keySet();
		for (Iterator<DocInfo> it = key.iterator(); it.hasNext();) {
			DocInfo info = (DocInfo) it.next();
			if (info.getName().equals(doc.getName())) {
				return map.get(info);
			}
		}
		return null;
	}

	/**
	 * 获取当前已下载的列表
	 */
	public List<DocInfo> getListDone() {
		return mDataBaseHelper.getDoneInfos();
	}

	/**
	 * 获取当前正在下载的列表
	 */
	public List<DocInfo> getListDoing() {
		return mDataBaseHelper.getDoingInfos();
	}

	/**
	 * 获取当前未下载的列表
	 */
	public boolean isDownloading(AttachOne attach) {
		List<DocInfo> docInfos = mDataBaseHelper.getDoingInfos();
		if (docInfos != null && docInfos.size() > 0) {
			for (DocInfo docInfo : docInfos) {
				if (docInfo != null && !TextUtils.isEmpty(docInfo.getName().toString())) {
					if (!TextUtils.isEmpty(attach.getAttachOneSaveName()) && attach.getAttachOneSaveName().equals(docInfo.getName().toString())) {
						return true;
					}
					if ((attach.getAttachOneSaveName() + "." + attach.getAttachOneType()).equals(docInfo.getName().toString())) {
						return true;
					}
					if ((attach.getAttachOneId() + "." + attach.getAttachOneType()).equals(docInfo.getName().toString())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取当前未下载的列表
	 */
	public boolean isDownloading(ImageMatch im) {
		List<DocInfo> docInfos = mDataBaseHelper.getDoingInfos();
		if (docInfos != null) {
			for (DocInfo docInfo : docInfos) {
				if ((im.getIM_SaveName()).equals(docInfo.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public void resetDownloadStatus() {
		mDataBaseHelper.resetDownloadStatus();
		/*
		 * List<DocInfo> docInfos = mDataBaseHelper.getDoingInfos(); if(docInfos
		 * != null){ for(DocInfo docInfo :docInfos){ startForActivity(docInfo);
		 * } }
		 */
	}

	public boolean isDownloading(AttachTwo attachTwo) {
		List<DocInfo> docInfos = mDataBaseHelper.getDoingInfos();
		if (docInfos != null) {
			for (DocInfo docInfo : docInfos) {
				if (attachTwo.getAttachTwoSaveName().equals(docInfo.getName())) {
					return true;
				}
			}
		}
		return false;

	}

}
