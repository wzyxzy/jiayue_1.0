package com.jiayue.download;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.Utils.DownloadManager.DownloadListener;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.util.ActivityUtils;
/**
 * 下载后台服务机制
 * 
 * @author wangping
 * 
 */
public class TestService extends Service {

	DownloadManager m;
	// String s = Environment.getExternalStorageDirectory().getPath() + "/" +
	// "umybook";
	@Override
	public void onCreate() {
		super.onCreate();
		// try {
		// Thread.sleep(5000);
		// Intent intent=new Intent();
		//
		// intent.setAction("android.intent.action.test");//action与接收器相同
		// intent.putExtra("test", "hehe");
		// sendBroadcast(intent);
		//
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		m = DownloadManager.getInstance(this);
		/*
		 * m.setDownloadListener(new DownloadListener() {
		 * 
		 * @Override public void onUpdateProgress(DocInfo info) { Intent
		 * intent=new Intent();
		 * intent.setAction("android.intent.action.test");//action与接收器相同 Bundle
		 * bundle = new Bundle(); bundle.putSerializable("info", info);
		 * intent.putExtra("bundle", bundle); intent.putExtra("flag", "update");
		 * sendBroadcast(intent); System.out.println("progress----------------"
		 * + info.getDownloadProgress());
		 * System.out.println("speed----------------" + info.getSpeed());
		 * System.out.println("name----------------" + info.getName()); }
		 * 
		 * @Override public void onDownloadFailed(DocInfo info) { Intent
		 * intent=new Intent();
		 * intent.setAction("android.intent.action.test");//action与接收器相同 Bundle
		 * bundle = new Bundle(); bundle.putSerializable("info", info);
		 * intent.putExtra("bundle", bundle); intent.putExtra("flag", "failed");
		 * sendBroadcast(intent); System.out.println("progress----------------"
		 * + info.getName());
		 * 
		 * }
		 * 
		 * @Override public void onDownloadCompleted(DocInfo info) { Intent
		 * intent=new Intent();
		 * intent.setAction("android.intent.action.test");//action与接收器相同 Bundle
		 * bundle = new Bundle(); bundle.putSerializable("info", info);
		 * intent.putExtra("bundle", bundle); intent.putExtra("flag",
		 * "success"); sendBroadcast(intent);
		 * System.out.println("progress----------------" + info.getName()); }
		 * });
		 */
		m.addDownloadListener(new DownloadListener() {
			@Override
			public void onUpdateProgress(DocInfo info) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.test");// action与接收器相同
				Bundle bundle = new Bundle();
				bundle.putSerializable("info", info);
				intent.putExtra("bundle", bundle);
				intent.putExtra("flag", "update");
				sendBroadcast(intent);
				System.out.println("progress----------------"
						+ info.getDownloadProgress());
				System.out.println("speed----------------" + info.getSpeed());
				System.out.println("name----------------" + info.getName());
			}

			@Override
			public void onDownloadFailed(DocInfo info) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.test");// action与接收器相同
				Bundle bundle = new Bundle();
				bundle.putSerializable("info", info);
				intent.putExtra("bundle", bundle);
				intent.putExtra("flag", "failed");
				sendBroadcast(intent);
				System.out.println("progress----------------" + info.getName());

			}

			@Override
			public void onDownloadCompleted(DocInfo info) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.test");// action与接收器相同
				Bundle bundle = new Bundle();
				bundle.putSerializable("info", info);
				intent.putExtra("bundle", bundle);
				intent.putExtra("flag", "success");
				sendBroadcast(intent);
				// String name=info.getName();
				//
				// String bookName = info.getName();
				// if(bookName.contains(".zip")){
				// String file =
				// ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath()+"/"+bookName;
				// ZipService.unzip(file,
				// ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
				// ActivityUtils.deleteBookFormSD(info.getBookId() ,
				// info.getName());
				// }
				System.out.println("progress----------------" + info.getName());
			}
		});
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!ActivityUtils.isNetworkAvailable(getBaseContext())) {
			return super.onStartCommand(intent, flags, startId);

		}
		if (null != intent) {
			Bundle bundle = intent.getBundleExtra("bundle");
			if (null != bundle) {
				DocInfo info = (DocInfo) bundle.getSerializable("info");
				String path = ActivityUtils.getSDPath(info.getBookId())
						.getAbsolutePath();
				info.setFilePath(path);
				m.startForActivity(info);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
