//package com.jiayue;
//
//import java.io.File;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Map.Entry;
//
//import org.springframework.util.MultiValueMap;
//
//import com.jiayue.R;
//import com.jiayue.constants.Preferences;
//import com.jiayue.controller.BookController;
//import com.jiayue.download.DownloadProgressListener;
//import com.jiayue.download.FileDownloader;
//import com.jiayue.dto.base.Update;
//import com.jiayue.main.IoC;
//import com.jiayue.soap.BookJson;
//import com.jiayue.task.BaseTask;
//import com.jiayue.task.TaskListener;
//import com.jiayue.task.TaskManager;
//import com.jiayue.util.ActivityUtils;
//import com.jiayue.util.DialogUtils;
//import com.jiayue.util.MyPreferences;
//import com.jiayue.util.UpdateInfo;
//import com.jiayue.util.UpdateInfoParser;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.LinearLayout.LayoutParams;
//
//public class UpdateActivity extends BaseActivity {
//	UpdateInfo info;
//	String client_version;
//	LinearLayout ll_container;
//	LinearLayout ll_content;
//	ProgressBar progressBar;
//	ProgressBar pb_syn;
//	TextView tv_errorMsg;
//	String errorMsg;
//	TextView tv_updateInfo;
//	Button btn_update;
//
//	Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case 0:
//					addContent();
//					tv_updateInfo.setText("当前已是最新版本:wyst" + client_version);
//					btn_update.setVisibility(View.GONE);
//
//					break;
//				case 1:
//					addContent();
//					tv_updateInfo.setText("已有新版本：wyst" + info.getVersion());
//					btn_update.setVisibility(View.VISIBLE);
//					break;
//				case 2:
//					addTextViewForError("未连上服务器");
//					break;
//				case 3:
//					pb_syn.setProgress(msg.getData().getInt("size"));
//					float result = (float) pb_syn.getProgress() / (float) pb_syn.getMax();
//					// int num = (int)(result * 100);
//					if (pb_syn.getProgress() == pb_syn.getMax()) {
//						pb_syn.setVisibility(View.GONE);
//						File file = new File(ActivityUtils.getSDPath(), "wyst.apk");
//						installApk(file);
//					}
//					break;
//
//				case -1:
//					ActivityUtils.showToastForFail(UpdateActivity.this, "下载失败");
//					break;
//				default:
//					break;
//			}
//		};
//	};
//
//	private Update update;
//	private BookController bookController;
//	private TaskManager tm;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getApplicationContext().addActivity(this);
//		setContentView(R.layout.update);
//		initView();
//	}
//
//	public void initView() {
//		ll_container = (LinearLayout) findViewById(R.id.ll_container);
//		ll_content = (LinearLayout) findViewById(R.id.ll_content);
//		tv_updateInfo = (TextView) findViewById(R.id.tv_updateInfo);
//		btn_update = (Button) findViewById(R.id.btn_update);
//		pb_syn = (ProgressBar) findViewById(R.id.pb_syn);
//		progressBar = new ProgressBar(this);
//		progressBar.setLayoutParams(new LayoutParams(100, 100));
//		tv_errorMsg = new TextView(this);
//		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//		client_version = getClientVersion();
//		bookController = IoC.getInstance(BookController.class);
//
//		tm = getApplicationContext().getTaskManager();
//
//		addProgressBar();
//		CheckVersion();
//
//	}
//
//	private void CheckVersion() {
//
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("version", getClientVersion());
//		params.put("systemType", "android");
//		Log.i("BookSynActivity", "version" + getClientVersion());
//
//		tm.createNewTask(new TaskListener() {
//			@Override
//			public String getName() {
//				return null;
//			}
//
//			@Override
//			public void onPreExecute(BaseTask task) {
//			}
//
//			@Override
//			public void onPostExecute(BaseTask task, String errorMsg) {
//
//				// if (errorMsg != null) {// 获取数据出现异常
//				// // addTextViewForError("加载失败," + errorMsg);
//				//
//				// } else {
//				// BookJson bookJson = bookController.getModel();
//				// if (null == bookJson) {
//				// // addTextViewForError("加载失败," + "返回空数据");
//				// return;
//				// } else {
//				// if (bookJson.getCode().equals(MyPreferences.SUCCESS)&& null
//				// != bookJson.getUpdate()) {
//				//
//				// return;
//				// } else if (bookJson.getCode()
//				// .equals(MyPreferences.FAIL)) {
//				// // addTextViewForError("加载失败,"
//				// // + bookJson.getCodeInfo());
//				// return;
//				// }
//				// }
//				// }
//				//
//			}
//
//			@Override
//			public void onProgressUpdate(BaseTask task, Object param) {
//			}
//
//			@Override
//			public void onCancelled(BaseTask task) {
//			}
//
//			@Override
//			public String onDoInBackground(BaseTask task, MultiValueMap<String, String> param) {
//				try {
//					String errorMsg = bookController.getUpdate(param);
//					BookJson bookJson = bookController.getModel();
//					update = bookJson.getUpdate();
//					if (update != null) {
//
//						Message msg = Message.obtain();
//						msg.what = 1;
//						handler.sendMessage(msg);
//
//					} else {
//						Message msg = Message.obtain();
//						msg.what = 0;
//						handler.sendMessage(msg);
//					}
//
//					return errorMsg;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//		}).execute(params);
//
//	}
//
//	/**
//	 * 显示错误信息
//	 * 
//	 * @param errorMsg
//	 */
//	public void addTextViewForError(String errorMsg) {
//		tv_errorMsg.setText(errorMsg);
//		tv_errorMsg.setTextColor(this.getResources().getColor(R.color.grey01));
//		tv_errorMsg.setTextSize(22);
//		if (ll_container.getChildCount() > 0) {
//			ll_container.removeAllViews();
//		}
//		ll_container.addView(tv_errorMsg);
//		ll_container.setGravity(Gravity.CENTER);
//	}
//
//	/**
//	 * 显示进度条对话框
//	 * 
//	 * @param errorMsg
//	 */
//	public void addProgressBar() {
//		if (ll_container.getChildCount() > 0) {
//			ll_container.removeAllViews();
//		}
//		ll_container.addView(progressBar);
//		ll_container.setGravity(Gravity.CENTER);
//	}
//
//	/**
//	 * 显示内容
//	 * 
//	 * @param errorMsg
//	 */
//	public void addContent() {
//		if (ll_container.getChildCount() > 0) {
//			ll_container.removeAllViews();
//		}
//		ll_container.addView(ll_content);
//		ll_container.setGravity(Gravity.TOP);
//	}
//
//	public void btnBack(View v) {
//		this.finish();
//	}
//
//	public void updateApp(View v) {
//		DialogUtils.showMyDialog(this, MyPreferences.SHOW_CONFIRM_DIALOG, "版本更新", update.getDesc().replace("$", "<br>"), new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				pb_syn.setVisibility(View.VISIBLE);
//				btn_update.setVisibility(View.GONE);
//				String path = update.getUrl();
//
//				 if (Environment.getExternalStorageState().equals(
//				 Environment.MEDIA_MOUNTED)) {
//				 download(ActivityUtils.getSDPath(), path);
//				 } else {
//				 ActivityUtils.showToastForFail(UpdateActivity.this,
//				 "未检测到SD卡");
//				 }
//				DialogUtils.dismissMyDialog();
//			}
//		});
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		getApplicationContext().removeActivity(this);
//		super.onDestroy();
//	}
//
//	/**
//	 * 检查服务器获取 最新的版本信息
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//
//	/**
//	 * 获取当前程序的版本号
//	 * 
//	 * @return
//	 */
//	private String getClientVersion() {
//		try {
//			PackageManager packageManager = this.getPackageManager();
//			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
//			return packInfo.versionName;
//		} catch (NameNotFoundException e) {
//			return "100";
//		}
//	}
//
//	// 完成文件的下载
//	private void download(File saveDir, String path) {
//		new Thread(new DownloadFileTask(saveDir, path)).start();
//	}
//
//	private final class DownloadFileTask implements Runnable {
//		private File saveDir;
//		private String path;
//
//		public DownloadFileTask(File saveDir, String path) {
//			this.saveDir = saveDir;
//			this.path = path;
//		}
//
//		public void run() {
//			try {
//				FileDownloader loader = new FileDownloader(getApplicationContext(), path, saveDir, 1, "wyst.apk");
//				pb_syn.setMax(loader.getFileSize());// 设置进度条的最大刻度为文件长度
//				loader.download(new DownloadProgressListener() {
//					public void onDownloadSize(int size) {
//						Message msg = new Message();
//						msg.what = 3;
//						msg.getData().putInt("size", size);
//						handler.sendMessage(msg);
//					}
//				});
//			} catch (Exception e) {
//				handler.sendMessage(handler.obtainMessage(-1));
//			}
//		}
//
//	}
//
//	/**
//	 * 安装下载成功的apk
//	 * 
//	 * @param file
//	 *            apk的文件对象
//	 */
//	protected void installApk(File file) {
//		Intent intent = new Intent();
//		// 查看的意图 (动作)
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//		finish();
//		startActivity(intent);
//	}
//
//}
