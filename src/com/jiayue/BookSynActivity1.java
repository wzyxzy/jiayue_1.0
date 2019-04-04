//package com.jiayue;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.util.MultiValueMap;
//
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnLongClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.jiayue.constants.Preferences;
//import com.jiayue.controller.BookController;
//import com.jiayue.download.TestService;
//import com.jiayue.download2.Utils.DownloadManager;
//import com.jiayue.download2.Utils.DownloadManager.DownloadListener;
//import com.jiayue.download2.db.DataBaseFiledParams;
//import com.jiayue.download2.db.DataBaseHelper;
//import com.jiayue.download2.entity.DocInfo;
//import com.jiayue.dto.base.AttachOne;
//import com.jiayue.dto.base.AttachTwo;
//import com.jiayue.dto.base.BookVO;
//import com.jiayue.main.IoC;
//import com.jiayue.service.ZipService;
//import com.jiayue.soap.BookJson;
//import com.jiayue.task.BaseTask;
//import com.jiayue.task.TaskListener;
//import com.jiayue.task.TaskManager;
//import com.jiayue.util.ActivityUtils;
//import com.jiayue.util.DialogUtils;
//import com.jiayue.util.MyPreferences;
//import com.jiayue.util.SPUtility;
//import com.jiayue.util.SerializableUtil;
//import com.jiayue.util.StringUtil;
//import com.lidroid.xutils.DbUtils;
//import com.lidroid.xutils.db.sqlite.Selector;
//import com.lidroid.xutils.exception.DbException;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
//import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.skytree.epubtest.HomeActivity;
//
//@SuppressLint("ResourceAsColor")
//public class BookSynActivity1 extends BaseActivity {
//	Context context;
//	LinearLayout sv_container;
//	LinearLayout ll_content;
//	ProgressBar progressBar;
//	TextView tv_errorMsg;
//	TextView tv_fujian;
//	LinearLayout ll_fujian;
//	AttachOne attachOne;
//	BookVO bookVO;
//	String bookId;
//	String imageMatchId;
//	String bookName;
//	ImageView btn_syn;
//	TaskManager tm;
//	BookController bookController;
//	String errorMsg;
//	GridView gv_fujian;
//	GridView gv_match;
//	GridView gv_releted;
//	FileAttachAdapter adapte_fujian;
//	DisplayImageOptions options;
//	int flag_download = -1;
//	ImageView iv_point;
//	List<AttachOne> attachOnes = new ArrayList<AttachOne>();
//	ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
//	BroadcastReceiver broadcastReciver;
//	DownloadManager downloadManager;
//	Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//				case 1:
//					DialogUtils.dismissMyDialog();
//					ActivityUtils.readFile(BookSynActivity.this, bookId, msg.getData().getString("fileName"), msg.getData().getString("fileType"), msg.getData().getString("bookName"));
//
//					break;
//				default:
//					break;
//			}
//		}
//	};
//
//	private String image_url_str;
//	private Button btn_read;
//	private TextView tv_header_title;
//	private LinearLayout btn_download;
//	private ViewPager viewPager;
//	private ArrayList<View> pageview;
//	private String image_url;
//	private int iv_bookId;
//	private ImageView iv_book;
//	private String bookSaveName;
//	private LinearLayout ll_communication;
//	private DocInfo info;
//
//	/*
//	 * Title: onCreate Description:
//	 * 
//	 * @param savedInstanceState
//	 * 
//	 * @see com.jiayue.BaseActivity#onCreate(android.os.Bundle)
//	 */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getApplicationContext().addActivity(this);
//		setContentView(R.layout.book_syn);
//		context = this;
//		tm = getApplicationContext().getTaskManager();
//		bookController = IoC.getInstance(BookController.class);
//		downloadManager = DownloadManager.getInstance(this);
//
//		// 默认一张图片
//		int cover_normal = getResources().getIdentifier("cover_normal", "drawable", getPackageName());
//		options = new DisplayImageOptions.Builder().showStubImage(cover_normal)
//		// 设置图片下载期间显示的图片
//				.showImageForEmptyUri(cover_normal)
//				// 设置图片Uri为空或是错误的时候显示的图片
//				.showImageOnFail(cover_normal)
//				// 设置图片加载或解码过程中发生错误显示的图片
//				.cacheInMemory(true)
//				// 设置下载的图片是否缓存在内存中
//				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
//		bookId = getIntent().getStringExtra("bookId");
//		bookName = getIntent().getStringExtra("bookName");
//		image_url_str = getIntent().getStringExtra("image_url");
//		DbUtils db = DbUtils.create(this, "book_vo.db");
//		try {
//			BookVO bookVO = db.findFirst(Selector.from(BookVO.class).where("book_id", "=", bookId));
//			if (bookVO != null) {
//				bookSaveName = bookVO.getBookSaveName();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		initView();
//		initDownload(this);
//		if (bookVO != null && !ActivityUtils.isNetworkAvailable(context))
//			ActivityUtils.showToast(this.context, "请联网更新后查看哦！");
//		BookJson bookJson = SerializableUtil.unSerializeBookJson(context, bookId);
//		if (bookJson != null) {
//			attachOnes = bookJson.getAttachOnes();
//		}
//	}
//
//	/**
//	 * 设置download监听
//	 * 
//	 * @param cotext
//	 */
//	public void initDownload(Context cotext) {
//		downloadManager.removeDownloadListener();
//		downloadManager.addDownloadListener(new DownloadListener() {
//
//			private DocInfo docInfo;
//
//			@Override
//			public void onUpdateProgress(DocInfo info) {
//				Intent intent = new Intent();
//				intent.setAction("android.intent.action.test");// action与接收器相同
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("info", info);
//				intent.putExtra("bundle", bundle);
//				intent.putExtra("flag", "update");
//				sendBroadcast(intent);
//			}
//
//			@Override
//			public void onDownloadFailed(DocInfo info) {
//				Intent intent = new Intent();
//				intent.setAction("android.intent.action.test");// action与接收器相同
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("info", info);
//				intent.putExtra("bundle", bundle);
//				intent.putExtra("flag", "failed");
//				sendBroadcast(intent);
//			}
//
//			@Override
//			public void onDownloadCompleted(DocInfo info) {
//				Intent intent = new Intent();
//				intent.setAction("android.intent.action.test");// action与接收器相同
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("info", info);
//				intent.putExtra("bundle", bundle);
//				intent.putExtra("flag", "success");
//				sendBroadcast(intent);
//				docInfo = info;
//				new Thread() {
//					public void run() {
//
//						unZip(docInfo);
//					};
//				}.start();
//			}
//
//			public void unZip(DocInfo info) {
//				String bookName = info.getName();
//				if (bookName.endsWith(info.getBookId() + ".zip")) {
//					String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
//					ZipService.unzip(file, ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
//					ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
//				} else if (bookName.endsWith(".zip")) {
//					Log.i("BookSynActivity", "bookName=" + bookName);
//					String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
//					Log.i("BookSynActivity", "file=" + file);
//					ActivityUtils.unLock(info.getBookId(), bookName, bookName + "copy.zip");
//					ZipService.unzip(file + "copy.zip", ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
//					ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName() + "copy.zip");
//				}
//			}
//		});
//	}
//
//	public void initView() {
//		btn_download = (LinearLayout) findViewById(R.id.btn_download);
//		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
//		ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
//		ll_communication = (LinearLayout) findViewById(R.id.ll_communication);
//		gv_fujian = (GridView) findViewById(R.id.gv_Fujian);
//		adapte_fujian = new FileAttachAdapter(this);
//		tv_bookname = (TextView) findViewById(R.id.tv_bookname);
//		tv_author = (TextView) findViewById(R.id.tv_author);
//
//		iv_bookId = getResources().getIdentifier("iv_book", "id", getPackageName());
//		iv_book = (ImageView) this.findViewById(iv_bookId);
//		imageLoader.displayImage(image_url_str, iv_book, options);
//		iv_point = (ImageView) findViewById(R.id.iv_point);
//		iv_wdian = (ImageView) findViewById(R.id.iv_wdian);
//		iv_dian = (ImageView) findViewById(R.id.iv_dian);
//		viewPager = (ViewPager) findViewById(R.id.viewPager);
//		loadViewPager();
//
//		tv_errorMsg = new TextView(this);
//		if (ActivityUtils.isExistAndRead(bookId) || ActivityUtils.epubIsExistAndRead(bookId, bookSaveName)) {
//			// btn_syn.setImageResource(player);
//		}
//		try {
//			getBookIntroduction();
//			getAttachOne();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("e========" + e.getLocalizedMessage());
//		}
//
//		broadcastReciver = new BroadcastReceiver() {
//
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				String action = intent.getAction();
//				if (action.equals("android.intent.action.test")) {
//					String flag = intent.getStringExtra("flag");
//					Bundle bundle = intent.getBundleExtra("bundle");
//					info = (DocInfo) bundle.getSerializable("info");
//					if (info.getName().equals(bookId + ".zip") || info.getName().equals(bookSaveName + ".epub")) {
//						DownloadManager manager = DownloadManager.getInstance(BookSynActivity.this);
//						if (flag.equals("success")) {
//							ActivityUtils.showToastForSuccess(BookSynActivity.this, "下载完成");
//							flag_download = 1;
//							iv_point.setVisibility(View.GONE);
//						}
//					}
//					if (flag.equals("success") && isFileDownloaded(info.getName())) {
//						adapte_fujian.notifyDataSetChanged();
//						gv_fujian.invalidateViews();
//					}
//
//					if (flag.equals("update")) {
//						adapte_fujian.notifyDataSetChanged();
//
//						System.out.println("info.getDownloadProgress" + info.getBookName() + "=" + info.getDownloadProgress());
//						System.out.println("info.getDownloadProgress finish");
//						return;
//					}
//
//				} else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//					if (null != intent.getStringExtra("reason") && intent.getStringExtra("reason").equals("homekey")) {
//						deleteFileAttach();
//						return;
//					}
//				}
//			}
//		};
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("android.intent.action.test");
//		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//		registerReceiver(broadcastReciver, filter);
//	}
//
//	/**
//	 * Title: loadViewPager Description: 加载viewpager
//	 */
//	private void loadViewPager() {
//
//		// 查找布局文件用LayoutInflater.inflate
//		LayoutInflater inflater = getLayoutInflater();
//		View view1 = inflater.inflate(R.layout.item01, null);
//		View view2 = inflater.inflate(R.layout.item02, null);
//		pageview = new ArrayList<View>();
//		pageview.add(view1);
//		pageview.add(view2);
//		PagerAdapter mPagerAdapter = new PagerAdapter() {
//
//			@Override
//			// 获取当前窗体界面数
//			public int getCount() {
//				return pageview.size();
//			}
//
//			@Override
//			// 断是否由对象生成界面
//			public boolean isViewFromObject(View arg0, Object arg1) {
//				return arg0 == arg1;
//			}
//
//			// 是从ViewGroup中移出当前View
//			public void destroyItem(View arg0, int arg1, Object arg2) {
//				((ViewPager) arg0).removeView(pageview.get(arg1));
//			}
//
//			// 返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
//			public Object instantiateItem(View arg0, int arg1) {
//				((ViewPager) arg0).addView(pageview.get(arg1));
//
//				return pageview.get(arg1);
//			}
//
//		};
//		viewPager.setAdapter(mPagerAdapter);
//		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int arg0) {
//				switchViewpager(arg0);
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//			}
//		});
//	}
//
//	private void switchViewpager(int position) {
//		int wdian = context.getResources().getIdentifier("wdian", "drawable", context.getPackageName());
//		int dian = context.getResources().getIdentifier("dian", "drawable", context.getPackageName());
//		if (position == 0) {
//			ll_fujian.setVisibility(View.VISIBLE);
//			ll_communication.setVisibility(View.GONE);
//			iv_wdian.setImageResource(wdian);
//			iv_dian.setImageResource(dian);
//		} else if (position == 1) {
//			ll_fujian.setVisibility(View.GONE);
//			ll_communication.setVisibility(View.VISIBLE);
//			iv_wdian.setImageResource(dian);
//			iv_dian.setImageResource(wdian);
//		}
//
//	}
//
//	/**
//	 * 判断后台发出的下载成功广播是否是当前界面中的附件信息
//	 * 
//	 * @param fileSDName
//	 *            保存在sd卡的名称
//	 * @return true-成功 false-不成功
//	 */
//	public boolean isFileDownloaded(String fileSDName) {
//		if (attachOnes != null && attachOnes.size() > 0) {
//			for (AttachOne attachOne : attachOnes) {
//				String fileName = attachOne.getAttachOneSaveName() + "." + attachOne.getAttachOneType();
//				if (fileName.equals(fileSDName)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	public void btnBack(View v) {
//		this.finish();
//	}
//
//	// 查看进度
//	/*
//	 * public void btnLookJD(View v) { Intent intent = new Intent(this,
//	 * SynManageActivity.class); this.startActivity(intent); }
//	 */
//	// 同步书籍xml和image包
//	long lastClick;
//	private TextView tv_bookname;
//	private TextView tv_author;
//	private ImageView iv_wdian;
//	private ImageView iv_dian;
//
//	public void btnRead(View v) {
//		if (System.currentTimeMillis() - lastClick <= 1000) {
//			Log.i("onclick", "您点击的太快了");
//			return;
//		}
//
//		lastClick = System.currentTimeMillis();
//		if (ActivityUtils.epubIsExistAndRead(bookId, bookSaveName)) {
//			ActivityUtils.unLock(bookId, bookSaveName + ".epub", bookSaveName + ".epub" + "copy.epub");
//			Intent intent = new Intent(BookSynActivity.this, HomeActivity.class);
//			intent.putExtra("bookPath", bookSaveName + ".epub" + "copy.epub");
//			intent.putExtra("bookId", bookId);
//			intent.putExtra("bookName", bookName);
//			intent.putExtra("author", bookVO.getBookAuthor());
//			startActivity(intent);
//			return;
//		} else if (ActivityUtils.isExistAndRead(bookId)) {
//			ActivityUtils.unLock(bookId, bookId + ".xml", bookId + "copy.xml");
//			Intent intent = new Intent(BookSynActivity.this, BookReadActivity.class);
//			intent.putExtra("bookPath", ActivityUtils.getSDPath(bookId) + "/" + bookId + "copy.xml");
//			intent.putExtra("imagePath", ActivityUtils.getSDPath(bookId) + "/images/");
//			intent.putExtra("bookName", bookVO.getBookName());
//
//			startActivity(intent);
//		} else {
//			ActivityUtils.showToastForFail(context, "您还没有下载哦！");
//		}
//
//	}
//
//	public void btnSyn(View v) {
//		if (System.currentTimeMillis() - lastClick <= 1000) {
//			Log.i("onclick", "您点击的太快了");
//			return;
//		}
//
//		lastClick = System.currentTimeMillis();
//		if (ActivityUtils.epubIsExistAndRead(bookId, bookSaveName) || ActivityUtils.isExistAndRead(bookId)) {
//			ActivityUtils.showToast(this, "请不要重复下载");
//			return;
//		}
//		attachOne = new AttachOne();
//		attachOne.setAttachOneId(bookId);
//		attachOne.setAttachOneType("zip");
//
//		if (downloadManager.isDownloading(attachOne)) {
//			ActivityUtils.showToast(this, "文件正在同步，请稍等");
//			return;
//		}
//		if (flag_download == -1 && ActivityUtils.isNetworkAvailable(context)) {
//			if (bookVO != null && bookVO.getBookImgPath() != null && bookVO.getBookSaveName() != null) {
//				if (!ActivityUtils.NetSwitch(context, Boolean.parseBoolean(SPUtility.getSPString(this, "switchKey")))) {
//					ActivityUtils.showToastForFail(this, "请在有WIFI的情况下下载");
//					return;
//				}
//				downLoadXml();
//				downLoadZip();
//				downLoadEpub();
//			}
//		}
//
//	}
//
//	// 下载书籍的xml模块
//	public void downLoadXml() {
//		String path = null;
//
//		Log.i("BookSynActivity", "BookImgPath" + bookVO.getBookImgPath());
//		Log.i("BookSynActivity", "SavaName" + bookVO.getBookSaveName());
//		path = Preferences.FILE_DOWNLOAD_URL + bookVO.getBookSavePath() + File.separator + bookVO.getBookSaveName() + ".xml";
//
//		Log.i("BookSynActivity", "downLoadXml" + path);
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			ActivityUtils.showToast(this, "加入同步列表,请稍候....");
//			Intent intent = new Intent(this, TestService.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			Bundle bundle = new Bundle();
//			DocInfo d = new DocInfo();
//			d.setUrl(path);
//			d.setDirectoty(false);
//			d.setBookId(bookId);
//			d.setName(bookId + ".xml");
//			d.setBookName(bookVO.getBookName());
//			bundle.putSerializable("info", d);
//			intent.putExtra("bundle", bundle);
//			startService(intent);
//			flag_download = 0;
//		} else {
//			ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡");
//		}
//
//	}
//
//	// 下载书籍的图片包模块
//	public void downLoadZip() {
//		String path = Preferences.FILE_DOWNLOAD_URL + bookVO.getBookSavePath() + File.separator + bookVO.getBookSaveName() + ".zip";
//		Log.i("BookSynActivity", "downLoadZip" + path);
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			Intent intent = new Intent(this, TestService.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			Bundle bundle = new Bundle();
//			DocInfo d = new DocInfo();
//			d.setUrl(path);
//			d.setDirectoty(false);
//			d.setName(bookId + ".zip");
//			d.setBookId(bookId);
//			d.setBookName(bookVO.getBookName() + "(图片包)");
//			bundle.putSerializable("info", d);
//			intent.putExtra("bundle", bundle);
//			startService(intent);
//			flag_download = 0;
//		} else {
//			ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡");
//		}
//	}
//
//	/**
//	 * Title: downLoadEpub Description: 下载Epub文件
//	 */
//	public void downLoadEpub() {
//		String path = Preferences.FILE_DOWNLOAD_URL + bookVO.getBookSavePath() + File.separator + bookVO.getBookSaveName() + ".epub";
//		Log.i("BookSynActivity", "downLoadEpub" + path);
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			Intent intent = new Intent(this, TestService.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			Bundle bundle = new Bundle();
//			DocInfo d = new DocInfo();
//			d.setUrl(path);
//			d.setDirectoty(false);
//			d.setName(bookSaveName + ".epub");
//			d.setBookId(bookId);
//			d.setBookName(bookName);
//			bundle.putSerializable("info", d);
//			intent.putExtra("bundle", bundle);
//			startService(intent);
//			flag_download = 0;
//		} else {
//			ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡");
//		}
//	}
//
//	/**
//	 * 下载附件
//	 * 
//	 * @param url
//	 *            附件的服务器地址 附件要保存的名字
//	 * @param fileName
//	 *            附件要现实的名字
//	 */
//	public void downLoadFile(String url, String saveName, String fileName) {
//
//		String path = Preferences.FILE_DOWNLOAD_URL + url;
//		Log.i("BookSynActivity", "url=" + path);
//		Log.i("BookSynActivity", "saveName=" + saveName);
//		Log.i("BookSynActivity", "fileName=" + fileName);
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			Intent intent = new Intent(this, TestService.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			Bundle bundle = new Bundle();
//			DocInfo d = new DocInfo();
//			d.setUrl(path);
//			d.setDirectoty(false);
//			d.setName(saveName);
//			d.setBookId(bookId);
//			d.setBookName(fileName);
//			bundle.putSerializable("info", d);
//			intent.putExtra("bundle", bundle);
//			startService(intent);
//		} else {
//			ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡");
//		}
//	}
//
//	public void PictureReadBook(View paramView) {
//		if (ActivityUtils.epubIsExistAndRead(bookId, bookSaveName)) {
//			ActivityUtils.unLock(bookId, bookSaveName + ".epub", bookSaveName + ".epub" + "copy.epub");
//			Intent intent = new Intent(BookSynActivity.this, HomeActivity.class);
//			intent.putExtra("bookPath", bookSaveName + ".epub" + "copy.epub");
//			intent.putExtra("bookId", bookId);
//			intent.putExtra("bookName", bookName);
//			intent.putExtra("author", bookVO.getBookAuthor());
//			startActivity(intent);
//			return;
//		} else if (ActivityUtils.isExistAndRead(bookId)) {
//			ActivityUtils.unLock(bookId, bookId + ".xml", bookId + "copy.xml");
//			Intent intent = new Intent(BookSynActivity.this, BookReadActivity.class);
//			intent.putExtra("bookPath", ActivityUtils.getSDPath(bookId) + "/" + bookId + "copy.xml");
//			intent.putExtra("imagePath", ActivityUtils.getSDPath(bookId) + "/images/");
//			intent.putExtra("bookName", bookVO.getBookName());
//			startActivity(intent);
//		} else {
//			if (!ActivityUtils.isNetworkAvailable(context)) {
//				return;
//			}
//			AttachOne attach = new AttachOne();
//			attach.setAttachOneId(bookId);
//			attach.setAttachOneType("zip");
//			if (downloadManager.isDownloading(attach)) {
//				ActivityUtils.showToast(this, "文件正在同步，请稍等");
//				return;
//			}
//			if (flag_download == -1) {
//				if (bookVO != null && bookVO.getBookImgPath() != null && bookVO.getBookSaveName() != null) {
//					downLoadXml();
//					downLoadZip();
//					downLoadEpub();
//				}
//				return;
//			}
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		DialogUtils.dismissMyDialog();
//		if (ActivityUtils.isExistAndRead(bookId) || ActivityUtils.epubIsExistAndRead(bookId, bookSaveName)) {
//			iv_point.setVisibility(View.GONE);
//		}
//		adapte_fujian.notifyDataSetChanged();
//		super.onResume();
//	}
//
//	/**
//	 * Title: btnReadBook Description: 图书阅读按钮
//	 * 
//	 * @param v
//	 */
//	public void btnReadBook(View v) {
//		if (ActivityUtils.isExistAndRead(bookId)) {
//			ActivityUtils.unLock(bookId, bookId + ".xml", bookId + "copy.xml");
//			Intent intent = new Intent(BookSynActivity.this, BookReadActivity.class);
//			intent.putExtra("bookPath", ActivityUtils.getSDPath(bookId) + "/" + bookId + "copy.xml");
//			intent.putExtra("imagePath", ActivityUtils.getSDPath(bookId) + "/images/");
//			intent.putExtra("bookName", bookVO.getBookName());
//			startActivity(intent);
//		} else {
//			ActivityUtils.showToast(BookSynActivity.this, "文件还未同步,请同步后观看!");
//		}
//	}
//
//	public void btnFileClick(View v) {
//		ActivityUtils.showToast(this, "已点击");
//	}
//
//	private void getBookIntroduction() {
//
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("bookId", bookId);
//		Log.i("BookSynActivity", "bookId" + this.bookId);
//		bookVO = unSerializeBookVO(this.bookId);
//		if ((this.bookVO == null) && (!ActivityUtils.isNetworkAvailable(this.context))) {
//			return;
//		}
//		if (this.bookVO != null) {
//
//			updataBook();
//			return;
//		}
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
//				if (errorMsg != null) {// 获取数据出现异常
//					addTextViewForError("加载失败," + errorMsg);
//
//				} else {
//					BookJson bookJson = bookController.getModel();
//					if (null == bookJson) {
//						addTextViewForError("加载失败," + "返回空数据");
//						return;
//					} else {
//						if (bookJson.getCode().equals(MyPreferences.SUCCESS) && null != bookJson.getBookVO()) {
//							bookVO = bookJson.getBookVO();
//							// 将bookVo序列化到本地
//							serializeBooKVO(bookVO);
//							updataBook();
//
//							return;
//						} else if (bookJson.getCode().equals(MyPreferences.FAIL)) {
//							addTextViewForError("加载失败," + bookJson.getCodeInfo());
//							return;
//						}
//					}
//				}
//
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
//					String str = BookSynActivity.this.bookController.getBookIntroduction(param);
//					return str;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//		}).execute(params);
//	}
//
//	private void getAttachOne() {
//
//		Map<String, String> params = new HashMap<String, String>();
//		// params.put("userId", UserUtil.getInstance(this).getUserId());
//		params.put("bookId", bookId);
//		params.put("systemType", "android");
//		Log.i("BookSynActivity", "bookId" + this.bookId);
//		if ((this.attachOne == null) && (!ActivityUtils.isNetworkAvailable(this.context))) {
//
//			return;
//		}
//		if (this.attachOne != null) {
//
//			updataBook();
//			return;
//		}
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
//				if (errorMsg != null) {// 获取数据出现异常
//					addTextViewForError("加载失败," + errorMsg);
//
//				} else {
//					BookJson bookJson = bookController.getModel();
//					SerializableUtil.serializeBookJson(context, bookId, bookJson);
//					if (null == bookJson) {
//						addTextViewForError("加载失败," + "返回空数据");
//						return;
//					} else {
//						if (bookJson.getCode().equals(MyPreferences.SUCCESS) && null != bookJson.getAttachOnes()) {
//							attachOnes = bookJson.getAttachOnes();
//							if (attachOnes != null) {
//								for (AttachOne attachOne : attachOnes) {
//									DbUtils db = DbUtils.create(context, "attach_one.db");
//									try {
//
//										db.save(attachOne);
//									} catch (DbException e) {
//										e.printStackTrace();
//									}
//								}
//							}
//
//							updataBook();
//
//							return;
//						} else if (bookJson.getCode().equals(MyPreferences.FAIL)) {
//							addTextViewForError("加载失败," + bookJson.getCodeInfo());
//							return;
//						}
//					}
//				}
//
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
//					String str = BookSynActivity.this.bookController.getAttachOne(param);
//					return str;
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
//	 * 将BookVO对象序列化到本地
//	 */
//	protected void serializeBooKVO(BookVO bookVO) {
//		try {
//			File target = new File(getCacheDir(), bookVO.getBookId());//
//			ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(target, false));
//			oo.writeObject(bookVO);
//			System.out.println(BookSynActivity.class.getName() + "=====bookVO对象序列化成功！");
//			oo.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/*
//	 * 反序列化BooKVO
//	 */
//	protected BookVO unSerializeBookVO(String bookId) {
//		BookVO bv = null;
//		try {
//			File source = new File(getCacheDir(), bookId);
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(source));
//			bv = (BookVO) ois.readObject();
//			System.out.println(BookSynActivity.class.getName() + "=====bookVO对象反序列化成功！");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return bv;
//	}
//
//	/**
//	 * 更新界面
//	 */
//	public void updataBook() {
//		gv_fujian.setAdapter(adapte_fujian);
//		if (ActivityUtils.isExistAndRead(bookId)) {
//			// btn_syn.setImageResource(player);
//		}
//		if (null != bookVO.getBookName()) {
//			tv_bookname.setText(bookVO.getBookName());
//		}
//		if (null != bookVO.getBookAuthor()) {
//			tv_author.setText(bookVO.getBookAuthor());
//			SPUtility.putSPString(context, "author" + bookVO.getBookId(), bookVO.getBookAuthor());
//		}
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
//	}
//
//	abstract class MyOnLongClickListener implements OnLongClickListener, OnTouchListener {
//	};
//
//	class FileAttachAdapter extends BaseAdapter {
//		LayoutInflater inflater;
//		View view;
//
//		public FileAttachAdapter(Context context) {
//
//			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//		}
//
//		public int getCount() {
//			return attachOnes.size();
//		}
//
//		public Object getItem(int position) {
//			return position;
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		/**
//		 * 把当前item对应的view对象返回回去
//		 */
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			if (convertView != null) {
//				view = convertView;
//			} else {
//				view = inflater.inflate(R.layout.fujian_item, null);
//			}
//
//			RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl_fujian);
//			MyOnLongClickListener longClick = new MyOnLongClickListener() {
//
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					if (event.getAction() == MotionEvent.ACTION_UP) {
//						tv_header_title.setText("图书详情");
//					}
//					return false;
//				}
//
//				@Override
//				public boolean onLongClick(View v) {
//					tv_header_title.setText(attachOnes.get(position).getAttachOneName());
//					DialogUtils.showMyDialog(context, MyPreferences.SHOW_CONFIRM_DIALOG, attachOnes.get(position).getAttachOneName(), "您要分享此文件给好友吗？", new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							DialogUtils.dismissMyDialog();
//							if (TextUtils.isEmpty(attachOnes.get(position).getShareUrl())) {
//								ActivityUtils.showToastForFail(BookSynActivity.this, "此文件不能分享！");
//								return;
//							}
//							ActivityUtils.ShareLink(BookSynActivity.this, attachOnes.get(position).getShareUrl());
//						}
//
//					});
//					return true;
//				}
//			};
//			rl.setOnLongClickListener(longClick);
//			rl.setOnTouchListener(longClick);
//			// 点击书籍阅读
//			rl.setOnClickListener(new OnClickListener() {
//				long lastClick;
//
//				@Override
//				public void onClick(View v) {
//					if (System.currentTimeMillis() - lastClick <= 1000) {
//						Log.i("onclick", "您点击的太快了");
//						return;
//					}
//					lastClick = System.currentTimeMillis();
//					Log.i("attachOnes", attachOnes.get(position).toString());
//
//					if (attachOnes.get(position).getAttachOneIspackage() == 0) {
//						if (!ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneSaveName() + "." + attachOnes.get(position).getAttachOneType()) && !ActivityUtils
//								.isExistByName(bookId, attachOnes.get(position).getAttachOneName())) {
//
//							if (!ActivityUtils.NetSwitch(BookSynActivity.this, Boolean.parseBoolean(SPUtility.getSPString(BookSynActivity.this, "switchKey")))) {
//								ActivityUtils.showToastForFail(BookSynActivity.this, "请在有WIFI的情况下下载");
//								return;
//							}
//							DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", "该文件未同步，是否开始同步？", new OnClickListener() {
//								@Override
//								public void onClick(View v) {
//
//									ActivityUtils.showToast(BookSynActivity.this, "加入下载列表");
//									downLoad(position);
//									DialogUtils.dismissMyDialog();
//								}
//							});
//						} else {
//
//							DataBaseHelper helper = new DataBaseHelper(context);
//							List<DocInfo> infos = helper.getInfo2(bookId);
//							if (info != null) {
//
//								if (attachOnes.get(position).getAttachOneName().equals(info.getBookName())) {
//									if (info.getStatus() == DataBaseFiledParams.LOADING) {
//										downloadManager.pause(info);
//										info.setStatus(DataBaseFiledParams.PAUSING);
//									} else if (info.getStatus() == DataBaseFiledParams.PAUSING) {
//										downloadManager.startForActivity(info);
//										info.setStatus(DataBaseFiledParams.LOADING);
//									}
//
//								}
//
//							}
//							for (DocInfo docInfo : infos) {
//								if (attachOnes.get(position).getAttachOneName().equals(docInfo.getBookName())) {
//
//									if (docInfo.getStatus() == DataBaseFiledParams.PAUSING) {
//										downloadManager.startForActivity(docInfo);
//										docInfo.setStatus(DataBaseFiledParams.LOADING);
//									} else if (docInfo.getStatus() == DataBaseFiledParams.WAITING) {
//
//									} else if (docInfo.getStatus() == DataBaseFiledParams.FAILED) {
//
//									}
//
//								}
//							}
//							adapte_fujian.notifyDataSetChanged();
//							// 文件是否正在同步
//							if (downloadManager.isDownloading(attachOnes.get(position))) {
//								// ActivityUtils.showToast(BookSynActivity.this,
//								// "该文件正在同步请稍后。");
//
//							} else {
//								Log.i("BookSynActivity", "name=" + attachOnes.get(position).getAttachOneName());
//								if (ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName())) {
//									readFlash(position);
//								} else {
//									Log.i("BookSynActivity", "name=" + attachOnes.get(position).getAttachOneSaveName());
//									unLock(position);
//								}
//							}
//						}
//					} else if (attachOnes.get(position).getAttachOneIspackage() == 2) {
//						String url = attachOnes.get(position).getAttachOneSaveName();
//						ActivityUtils.readURL(context, bookId, url);
//
//					} else {
//						Intent intent = new Intent(BookSynActivity.this, BookAttachActivity.class);
//						intent.putExtra("bookId", bookId);
//						intent.putExtra("attachOneId", attachOnes.get(position).getAttachOneId());
//						intent.putExtra("fileName", attachOnes.get(position).getAttachOneSaveName());
//						intent.putExtra("attachName", attachOnes.get(position).getAttachOneName());
//						BookJson bookJson = SerializableUtil.unSerializeBookJson(context, bookId);
//						DbUtils db = DbUtils.create(BookSynActivity.this, "attach_two.db");
//						if (!ActivityUtils.isNetworkAvailable(context)) {
//							try {
//								List<AttachTwo> attachTwos = db.findAll(Selector.from(AttachTwo.class).where("attachOneId", "=", attachOnes.get(position).getAttachOneId()));
//								if (attachTwos != null && attachTwos.size() != 0) {
//									startActivity(intent);
//									return;
//								}
//							} catch (DbException e) {
//								e.printStackTrace();
//							}
//							ActivityUtils.showToast(context, "请联网加载哦！");
//							return;
//						} else {
//							startActivity(intent);
//						}
//					}
//				}
//
//			});
//			// 设置书的封面
//			ImageView iv = (ImageView) view.findViewById(R.id.iv_fujian);
//			iv.setBackgroundResource(ActivityUtils.getFilePackageImageId(context, attachOnes.get(position).getAttachOneIspackage(), attachOnes.get(position).getAttachOneType()));
//			// 修饰文件包
//			ImageView iv_point_fujian = (ImageView) view.findViewById(R.id.iv_point_fujian);
//
//			if (attachOnes.get(position).getAttachOneIspackage() == 0) {
//				if (ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneSaveName() + "." + attachOnes.get(position).getAttachOneType()) || ActivityUtils
//						.isExistByName(bookId, attachOnes.get(position).getAttachOneName())) {
//					iv_point_fujian.setVisibility(View.GONE);
//				} else {
//					iv_point_fujian.setVisibility(View.VISIBLE);
//				}
//			} else {
//				iv_point_fujian.setVisibility(View.GONE);
//
//			}
//			// 附件名称
//			TextView tv_fujian = (TextView) view.findViewById(R.id.tv_fujian);
//			tv_fujian.setText(StringUtil.subString(attachOnes.get(position).getAttachOneName(), 7, "..."));
//			TextView tv_download = (TextView) view.findViewById(R.id.tv_download);
//
//			if (info != null) {
//
//				if (attachOnes.get(position).getAttachOneName().equals(info.getBookName())) {
//					getStateSetView(tv_download, info);
//				}
//
//			}
//			DataBaseHelper helper = new DataBaseHelper(context);
//			List<DocInfo> infos = helper.getInfo2(bookId);
//			for (DocInfo docInfo : infos) {
//				if (attachOnes.get(position).getAttachOneName().equals(docInfo.getBookName())) {
//					getStateSetView(tv_download, docInfo);
//				}
//			}
//			return view;
//
//		}
//
//	}
//
//	public void getStateSetView(TextView tv_download, DocInfo docInfo) {
//		if (docInfo.getStatus() == DataBaseFiledParams.PAUSING) {
//			tv_download.setText("暂停");
//		} else if (docInfo.getStatus() == DataBaseFiledParams.WAITING) {
//			tv_download.setText("等待中");
//		} else if (docInfo.getStatus() == DataBaseFiledParams.FAILED) {
//			tv_download.setText("服务器忙");
//		} else {
//			tv_download.setText(docInfo.getDownloadProgress() + "%");
//			if (tv_download.getText().toString().trim().equals("100%")) {
//				tv_download.setText("");
//			}
//		}
//	}
//
//	private void unLock(final int position) {
//		DialogUtils.showMyDialog(context, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
//		unLockFile(attachOnes.get(position).getAttachOneSaveName() + "." + attachOnes.get(position).getAttachOneType(), attachOnes.get(position).getAttachOneSaveName() + "_copy." + attachOnes
//				.get(position).getAttachOneType(), attachOnes.get(position).getAttachOneType(), attachOnes.get(position).getAttachOneName());
//	}
//
//	private void readFlash(final int position) {
//		String fileName = attachOnes.get(position).getAttachOneName() + "/Index.html";
//		ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName());
//		ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName() + ".zip");
//		Log.i("BookSynActivity", fileName);
//
//		ActivityUtils.readFile(context, bookId, fileName, "html", bookName);
//	}
//
//	private void downLoad(final int position) {
//		downLoadFile(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName(), attachOnes.get(position).getAttachOneSaveName() + "." + attachOnes.get(position)
//				.getAttachOneType(), attachOnes.get(position).getAttachOneName());
//		Log.i("BookSynActivity", attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName());
//		Log.i("BookSynActivity", attachOnes.get(position).getAttachOneSaveName() + "." + attachOnes.get(position).getAttachOneType());
//		Log.i("BookSynActivity", attachOnes.get(position).getAttachOneSaveName());
//	}
//
//	/**
//	 * Title: unLockFile Description: 解密文件
//	 * 
//	 * @param soureFileName
//	 *            接收到的原路径
//	 * @param saveFileName
//	 *            需要保存的路径
//	 * @param fileType
//	 *            文件类型
//	 * @param bookName
//	 *            书名
//	 */
//	public void unLockFile(final String soureFileName, final String saveFileName, final String fileType, final String bookName) {
//		DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载资源", null);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				ActivityUtils.unLock(bookId, soureFileName, saveFileName);
//				Message msg = new Message();
//				msg.what = 1;
//				Bundle bundle = new Bundle();
//				bundle.putString("fileName", saveFileName);
//				bundle.putString("fileType", fileType);
//				bundle.putString("bookName", bookName);
//				msg.setData(bundle);
//				mHandler.sendMessage(msg);
//			}
//		}).start();
//
//	}
//
//	/**
//	 * Title: deleteFileAttach Description: 删除解密后的文件
//	 */
//	public void deleteFileAttach() {
//		if (null != attachOnes && attachOnes.size() > 0) {
//			for (AttachOne attach : attachOnes) {
//				if (attach.getAttachOneIspackage() == 0) {
//					ActivityUtils.deleteBookFormSD(bookId, attach.getAttachOneSaveName() + "_copy." + attach.getAttachOneType());
//				}
//			}
//		}
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_HOME) {
//			// 删除解密后的文件
//			deleteFileAttach();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	/**
//	 * 图片加载第一次显示监听器
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
//
//		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
//
//		@Override
//		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//			if (loadedImage != null) {
//				ImageView imageView = (ImageView) view;
//				// 是否第一次显示
//				boolean firstDisplay = !displayedImages.contains(imageUri);
//				if (firstDisplay) {
//					// 图片淡入效果
//					FadeInBitmapDisplayer.animate(imageView, 500);
//					displayedImages.add(imageUri);
//				}
//			}
//		}
//	}
//
//	/**
//	 * Title: btnCode Description: 二维码扫描
//	 * 
//	 * @param v
//	 */
//	public void btnCode(View v) {
//		Intent intent = new Intent();
//		intent.setClass(this, MipcaActivityCapture.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivityForResult(intent, MyPreferences.SCANNIN_GREQUEST_CODE);
//
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//			case MyPreferences.SCANNIN_GREQUEST_CODE:
//				if (resultCode == RESULT_OK) {
//					// 显示扫描到的内容
//					Bundle bundle = data.getExtras();
//					String resultstr = bundle.getString("result");
//					// 显示扫描到的内容
//					ActivityUtils.readFileForJiaoda(BookSynActivity.this, bundle.getString("result"));
//				}
//				break;
//		}
//	}
//
//	public void btn_search(View v) {
//		Intent intent = new Intent(this, SearchActivity.class);
//		intent.putExtra("bookId", bookId);
//		intent.putExtra("image_url", image_url_str);
//		intent.putExtra("bookname", bookName);
//		Bundle bundle = new Bundle();
//		if (this.bookVO != null) {
//			bundle.putSerializable("bookVO", bookVO);
//			intent.putExtra("bundle", bundle);
//			startActivity(intent);
//		}
//	}
//
//	public void btn_bookInfo(View v) {
//		Intent intent = new Intent(this, BookDescriptionActivity.class);
//		intent.putExtra("bookId", bookId);
//		intent.putExtra("image_url", image_url_str);
//		intent.putExtra("bookname", bookName);
//		Bundle bundle = new Bundle();
//		if (this.bookVO != null) {
//			bundle.putSerializable("bookVO", bookVO);
//			intent.putExtra("bundle", bundle);
//			startActivity(intent);
//		}
//
//	}
//
//	public void author_communication(View v) {
//		Intent intent = new Intent(this, AuthorCommunicationActivity.class);
//		intent.putExtra("bookId", bookId);
//		intent.putExtra("image_url", image_url_str);
//		intent.putExtra("bookname", bookName);
//		Bundle bundle = new Bundle();
//		if (this.bookVO != null) {
//			bundle.putSerializable("bookVO", bookVO);
//			intent.putExtra("bundle", bundle);
//			startActivity(intent);
//		}
//
//	}
//
//	public void reader_communication(View v) {
//		Intent intent = new Intent(this, ReaderCommunicationActivity.class);
//		intent.putExtra("bookId", bookId);
//		intent.putExtra("image_url", image_url_str);
//		intent.putExtra("bookname", bookName);
//		Bundle bundle = new Bundle();
//		if (this.bookVO != null) {
//			bundle.putSerializable("bookVO", bookVO);
//			intent.putExtra("bundle", bundle);
//			startActivity(intent);
//		}
//	}
//
//	public void online_video(View v) {
//
//	}
//
//	@Override
//	protected void onDestroy() {
//
//		if (null != broadcastReciver) {
//			this.unregisterReceiver(broadcastReciver);
//		}
//		deleteFileAttach();
//		// ActivityUtils.deleteBookFormSD(bookId, bookId+"copy.xml");
//		getApplicationContext().removeActivity(this);
//		super.onDestroy();
//	}
//}
