package com.jiayue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.MusicListAdapter;
import com.jiayue.adapter.MusicListAdapter2;
import com.jiayue.adapter.ScanAdapter;
import com.jiayue.chat.login.InitBusiness;
import com.jiayue.chat.login.LoginBusiness;
import com.jiayue.chat.login.TlsBusiness;
import com.jiayue.chat.presenter.MessageEvent;
import com.jiayue.constants.Preferences;
import com.jiayue.download.TestService;
import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.Utils.DownloadManager.DownloadListener;
import com.jiayue.download2.db.DataBaseFiledParams;
import com.jiayue.download2.db.DataBaseHelper;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.dto.base.AttachOne;
import com.jiayue.dto.base.AttachOneBean;
import com.jiayue.dto.base.AttachTwo;
import com.jiayue.dto.base.AudioItem;
import com.jiayue.dto.base.BookDetailBean;
import com.jiayue.dto.base.BookLiveAllBean;
import com.jiayue.dto.base.BookLiveBean;
import com.jiayue.dto.base.BookVO;
import com.jiayue.dto.base.MusicListBean;
import com.jiayue.dto.base.OrderBean;
import com.jiayue.dto.base.PhotoBean;
import com.jiayue.dto.base.SiftBean;
import com.jiayue.dto.base.SiftVO;
import com.jiayue.dto.base.ZhiboInfoBean;
import com.jiayue.model.MusicList;
import com.jiayue.model.UserUtil;
import com.jiayue.service.MusicPlayerService;
import com.jiayue.service.ZipService;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.MyFilter;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.jiayue.view.PercentCircle;
import com.jiayue.view.SwpipeListViewOnScrollListener;
import com.jiayue.view.camera.AutoFocusActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.skytree.epubtest.HomeActivity;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupManager;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipException;


@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BookSynActivity extends BaseActivity implements OnRefreshListener, TIMCallBack {
    Context context;
    LinearLayout ll_fujian;
    AttachOne attachOne;//书籍xml
    private AttachOne attachOne2;//书籍epub
    BookVO bookVO;
    String bookId;
    String bookName;
    ImageView btn_syn;
    FileAttachAdapter adapte_fujian;
    DisplayImageOptions options;
    int flag_download = -1;
    PercentCircle iv_point;
    List<AttachOne> attachOnes;
    ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    BroadcastReceiver broadcastReciver;
    DownloadManager downloadManager;
    private String image_url_str;
    private TextView tv_header_title;
    //    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private int iv_bookId;
    private ImageView iv_book;
    private ImageView book_download;
    private String bookSaveName;
    //    private LinearLayout ll_communication;
    long lastClick;
    private TextView tv_bookname;
    private TextView tv_author;
    //    private ImageView iv_wdian;
//    private ImageView iv_dian;
    private ImageButton btn_header_right;
    //    private TextView tv_loading;
    private TextView tv_size;
    private SwipeRefreshLayout refresh_view;
    //    private DocInfo info;
    private final String TAG = getClass().getSimpleName();
    // private TextView tv_price;
    // private LinearLayout mLayout_Shop, mLayout_Down;
    // private boolean isShop = false;// 是否已经付费
    // private Button mBtn_Shop;
    private TextView tv_progress;
    //    private ImageView iv_read;
    private BookLiveBean livebean;
    private LinearLayout btn_download;
//    private ImageView btn_read;
    private ListView listview;

    private final int CMD_DOWNLOAD = 0x02;
    private final int CMD_MP3 = 0x03;
    private final String TEMP = "";
    private String groupId, webUrl;
    ZhiboInfoBean zhibobean;
    boolean isZhiYurJoin;//是否是从知阅入口进入

    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DialogUtils.dismissMyDialog();
                    ActivityUtils.readFile(BookSynActivity.this, bookId, msg.getData().getString("fileName"), msg.getData().getString("fileType"), msg.getData().getString("bookName"));
                    break;
                case CMD_DOWNLOAD:
                    imageDownload((SiftBean) msg.obj);
                    break;
                case CMD_MP3:
                    DialogUtils.dismissMyDialog();
                    Intent intent = new Intent(context, MediaPlayerActivity.class);
                    intent.putParcelableArrayListExtra("list", msg.getData().getParcelableArrayList("list"));
                    intent.putExtra("index", msg.getData().getInt("index"));
                    // intent.putExtra("bookId",
                    // msg.getData().getString("bookId"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
            return false;
        }

    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().addActivity(this);
        setContentView(R.layout.book_syn);
        context = this;
        downloadManager = DownloadManager.getInstance(this);
        // 默认一张图片
        int cover_normal = getResources().getIdentifier("cover_normal", "drawable", getPackageName());
        options = new DisplayImageOptions.Builder().showStubImage(cover_normal)
                // 设置图片下载期间显示的图片
                .showImageForEmptyUri(cover_normal)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(cover_normal)
                // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();


        bookId = getIntent().getStringExtra("bookId");
        bookName = getIntent().getStringExtra("bookName");
        image_url_str = getIntent().getStringExtra("image_url");
        isZhiYurJoin = getIntent().getBooleanExtra("isZhiYurJoin", false);
        Log.d(TAG, "bookId===" + bookId + "----bookName==" + bookName + "---image_url_str=" + image_url_str);

        DbManager db = MyDbUtils.getBookVoDb(this);
        try {
            BookVO bookVO = db.selector(BookVO.class).where("book_id", "=", bookId).findFirst();
            if (bookVO != null) {
                bookSaveName = bookVO.getBookSaveName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bookVO != null && !ActivityUtils.isNetworkAvailable(context))
            ActivityUtils.showToast(this.context, "请联网更新后查看哦！");

        DbManager db1 = MyDbUtils.getOneAttachDb(this);
        try {
            attachOnes = db1.selector(AttachOne.class).where("bookId", "=", bookId).findAll();
        } catch (Exception e) {
            // TODO: handle exception
        }
        initView();
        initData();
        initDownload(this);

    }

    @Override
    public Intent getIntent() {
        return super.getIntent();
    }

    // 获取识别文件信息
    private void getImageDownload() {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();
        params.setUri(Preferences.BOOK_IAMGE_DOWNLOAD);
        params.addQueryStringParameter("bookId", bookId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<SiftBean>() {
                }.getType();
                SiftBean bean = gson.fromJson(s, type);
                Log.d(TAG, "getImageDownload info ====" + s);
                if (bean != null && bean.getData() != null && bean.getCode().equals("SUCCESS")) {
                    if (TextUtils.isEmpty(bean.getData().getZipSavePath()))
                        return;
                    File file = new File(ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId);
                    if (!file.exists()) {
                        Log.d(TAG, "lib bookid file is not exit");
                        mHandler.sendMessage(mHandler.obtainMessage(CMD_DOWNLOAD, bean));
                        return;
                    }
                    DbManager db = MyDbUtils.getSiftVoDb(BookSynActivity.this);
                    SiftVO vo = null;
                    try {
                        vo = db.selector(SiftVO.class).where("bookId", "=", bookId).findFirst();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (vo != null && vo.getTime() == bean.getData().getLastTime().getTime()) {
                        Log.d(TAG, "time is same!!!!!!!!!!!");
                        return;
                    } else {
                        mHandler.sendMessage(mHandler.obtainMessage(CMD_DOWNLOAD, bean));
                    }
                } else {

                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    // sift文件下载接口
    private void imageDownload(final SiftBean siftbean) {
        // TODO Auto-generated method stub
        final String path = ActivityUtils.getSDLibPath().getAbsolutePath();
        String url = getPath(Preferences.FILE_DOWNLOAD_URL + siftbean.getData().getZipSavePath());

        RequestParams params = new RequestParams(url);
        // 自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
        params.setSaveFilePath(path);
        // 自动为文件命名
        params.setAutoRename(true);
        params.setAutoResume(true);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "imageDownload onFailure------" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }

            // 网络请求之前回调
            @Override
            public void onWaiting() {
            }

            // 网络请求开始的时候回调
            @Override
            public void onStarted() {
            }

            // 下载的时候不断回调的方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                // 当前进度和文件总大小
                Log.i("JAVA", "current：" + current + "，total：" + total);
            }

            @Override
            public void onSuccess(File file) {
                // TODO Auto-generated method stub
                Log.d(TAG, "imageDownload onSuccess");
                try {
                    ActivityUtils.deletesiftfile(ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId);// 删除原有sift文件夹
                    ZipService.unzip(file.getAbsolutePath(), ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId);// 解压
                    Log.d(TAG, "responseInfo.result.getAbsolutePath()=" + file.getAbsolutePath());
                    ActivityUtils.deletesiftfile(file.getAbsolutePath());// 删除
                    DbManager db = MyDbUtils.getSiftVoDb(BookSynActivity.this);
                    db.delete(SiftVO.class, WhereBuilder.b("bookId", "=", bookId));// 删除原数据库
                    List<SiftBean.Data.Correspondence> beans = siftbean.getData().getCorrespondence();
                    for (SiftBean.Data.Correspondence bean : beans) {
                        SiftVO vo = new SiftVO();
                        vo.setBookId(bookId);
                        vo.setTime(siftbean.getData().getLastTime().getTime());
                        vo.setSiftName(bean.getSiftName());
                        vo.setSiftType(bean.getSiftType());
                        vo.setAttachFlag(bean.getAttachFlag());
                        vo.setImageName(bean.getImageName());
                        db.save(vo);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {
        attachOne = new AttachOne();
        attachOne.setAttachOneId(bookId);
        attachOne.setAttachOneType("zip");
        attachOne2 = new AttachOne();
        attachOne2.setAttachOneSaveName(bookSaveName);
        attachOne2.setAttachOneType("epub");

    }

    /**
     * 设置download监听
     *
     * @param cotext
     */
    public void initDownload(Context cotext) {
        downloadManager.removeDownloadListener();
        downloadManager.addDownloadListener(new DownloadListener() {

            private DocInfo docInfo;

            @Override
            public void onUpdateProgress(DocInfo info) {
                // Log.d(TAG, "----callback--------progress==" +
                // info.getDownloadProgress());
                Intent intent = new Intent();
                intent.setAction("android.intent.action.test");// action与接收器相同
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", info);
                intent.putExtra("bundle", bundle);
                intent.putExtra("flag", "update");
                sendBroadcast(intent);
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
                // 正在播放音乐
                if (SPUtility.getSPString(BookSynActivity.this, "isPlay").equals("true")) {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(MusicPlayerService.TYPE_Customer);
                    Intent mIntent = new Intent("com.jiayue.startservice");
                    mIntent.setPackage(getPackageName());
                    stopService(mIntent);
                }
                docInfo = info;
                String substring = info.getName().substring(info.getName().length() - 4);
                new Thread() {
                    public void run() {

                        try {
                            unZip(docInfo);
                        } catch (ZipException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
                if (!substring.equalsIgnoreCase(".xml")) {
                    ActivityUtils.showToastForSuccess(BookSynActivity.this, info.getBookName() + "下载完成");
                }
            }

            public void unZip(DocInfo info) throws ZipException, IOException {
                Log.d(TAG, "unZipunZipunZipunZipunZipunZipunZipunZip----------" + info.getName());
                String bookName = info.getName();
                if (bookName.endsWith(info.getBookId() + ".zip")) {
                    String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
                    ZipService.unzip(file, ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
                    ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
                } else if (bookName.endsWith(".zip")) {
                    Log.i("BookSynActivity", "bookName=" + bookName);
                    String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
                    Log.i("BookSynActivity", "file=" + file);
                    ActivityUtils.unLock(info.getBookId(), bookName, bookName + "copy.zip");
                    ZipService.unzip(file + "copy.zip", ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
                    ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName() + "copy.zip");
                    ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
                } else if (bookName.endsWith(".lrc")) {
                    Log.i("BookSynActivity", "bookName=" + bookName);
                    ActivityUtils.unLock(info.getBookId(), bookName, bookName + "_copy.lrc");
                }
            }
        });
    }

    public void initView() {
        Log.d(TAG, "11111111111");
        refresh_view = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        btn_download = (LinearLayout) findViewById(R.id.btn_download);
//        btn_read = (ImageView) findViewById(R.id.btn_read);
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);

//        tv_loading = (TextView) findViewById(R.id.tv_loading);
//        iv_read = (ImageView) findViewById(R.id.iv_read);

        tv_header_title.setText("图书详情");
        btn_header_right = (ImageButton) findViewById(R.id.btn_header_right);
        btn_header_right.setVisibility(View.VISIBLE);

        listview = (ListView) findViewById(R.id.listview);
        listview.setOnScrollListener(new SwpipeListViewOnScrollListener(refresh_view));

        ll_fujian = (LinearLayout) findViewById(R.id.ll_fujian);
//        ll_communication = (LinearLayout) findViewById(R.id.ll_communication);

        adapte_fujian = new FileAttachAdapter(this);
        listview.setAdapter(adapte_fujian);

        tv_bookname = (TextView) findViewById(R.id.tv_bookname);
        tv_bookname.setText(TextUtils.isEmpty(bookName) ? "" : bookName);
        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_size = (TextView) findViewById(R.id.tv_size);

        // tv_price = (TextView) findViewById(R.id.tv_price);
        // mLayout_Down = (LinearLayout) findViewById(R.id.layout_download);
        // mLayout_Shop = (LinearLayout) findViewById(R.id.layout_shop);
        btn_header_right.setBackgroundResource(getResources().getIdentifier("booksyn_syn", "drawable", getPackageName()));
        btn_header_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // if (isShop) {
                Intent intent = new Intent(BookSynActivity.this, SynManageActivity.class);
                startActivityForResult(intent, 1000);
                // } else {
                // Intent intent = new Intent(MyPreferences.MAIN_BROADCAST);
                // intent.putExtra("menu", 3);
                // sendBroadcast(intent);
                // finish();
                // }
            }
        });
        // mBtn_Shop = (Button) findViewById(R.id.btn_shop);
        tv_progress = (TextView) findViewById(R.id.tv_progress);

//        iv_bookId = getResources().getIdentifier("iv_book", "id", getPackageName());
        iv_book = (ImageView) this.findViewById(R.id.iv_book);
        book_download = (ImageView) this.findViewById(R.id.book_download);
        imageLoader.displayImage(image_url_str, iv_book, options);
        iv_point = (PercentCircle) findViewById(R.id.iv_point);
//        iv_wdian = (ImageView) findViewById(R.id.iv_wdian);
//        iv_dian = (ImageView) findViewById(R.id.iv_dian);
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        loadViewPager();
        try {
            getBookIntroduction();
            getAttachOne();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e========" + e.getLocalizedMessage());
        }

        broadcastReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.test")) {
                    String flag = intent.getStringExtra("flag");
                    Bundle bundle = intent.getBundleExtra("bundle");
                    DocInfo info = (DocInfo) bundle.getSerializable("info");
                    if (info.getName().equals(bookId + ".zip") || info.getName().equals(bookSaveName + ".epub")) {
                        DownloadManager manager = DownloadManager.getInstance(BookSynActivity.this);
                        if (flag.equals("success")) {
                            // ActivityUtils.showToastForSuccess(BookSynActivity.this,
                            // "下载完成");
                            flag_download = 1;
                            iv_point.setVisibility(View.GONE);
                            book_download.setVisibility(View.GONE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                iv_book.setForeground(null);
                            }
                            iv_book.setAlpha(1f);
//                            tv_loading.setText("悦读");
                            int booksyn_read_button = getResources().getIdentifier("finish_download", "drawable", getPackageName());
//                            btn_read.setImageResource(booksyn_read_button);
//                            iv_read.setBackgroundResource(booksyn_read_button);
                            tv_progress.setText("");
//                            btn_read.setClickable(true);
                        }
                        if (flag.equals("update")) {
                            iv_point.setVisibility(View.VISIBLE);
                            book_download.setVisibility(View.GONE);
//                            tv_progress.setText(info.getDownloadProgress() + "%");
                            iv_point.setTargetPercent(100);
//                            if (tv_progress.getText().toString().trim().equals("100%")) {
//                                tv_progress.setText("");
////                                btn_read.setClickable(false);
//                            }
                        }
                    }
                    if (flag.equals("success") && isFileDownloaded(info.getName())) {
//                        iv_point.setVisibility(View.GONE);
//                        book_download.setVisibility(View.GONE);
                        adapte_fujian.notifyDataSetChanged();
                        listview.invalidateViews();
                    }

                    if (flag.equals("update")) {
                        // Log.d(TAG, "----broadcast--------progress==" +
                        // info.getDownloadProgress());
                        adapte_fujian.notifyDataSetChanged();
                        System.out.println("info.getDownloadProgress" + info.getDownloadProgress());
                        System.out.println("info.getDownloadProgress finish");
                        return;
                    }

                } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    if (null != intent.getStringExtra("reason") && intent.getStringExtra("reason").equals("homekey")) {
                        deleteFileAttach();
                        return;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.test");
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(broadcastReciver, filter);
    }

    /**
     * Title: loadViewPager Description: 加载viewpager
     */

//    private void loadViewPager() {
//
//        // 查找布局文件用LayoutInflater.inflate
//        LayoutInflater inflater = getLayoutInflater();
//        View view1 = inflater.inflate(R.layout.item01, null);
//        View view2 = inflater.inflate(R.layout.item02, null);
//        pageview = new ArrayList<View>();
//        pageview.add(view1);
//        pageview.add(view2);
//        PagerAdapter mPagerAdapter = new PagerAdapter() {
//
//            // 获取当前窗体界面数
//            @Override
//            public int getCount() {
//                return pageview.size();
//            }
//
//            // 断是否由对象生成界面
//            @Override
//            public boolean isViewFromObject(View arg0, Object arg1) {
//                return arg0 == arg1;
//            }
//
//            // 是从ViewGroup中移出当前View
//            @Override
//            public void destroyItem(View arg0, int arg1, Object arg2) {
//                ((ViewPager) arg0).removeView(pageview.get(arg1));
//            }
//
//            // 返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
//            @Override
//            public Object instantiateItem(View arg0, int arg1) {
//                ((ViewPager) arg0).addView(pageview.get(arg1));
//                return pageview.get(arg1);
//            }
//
//        };
//        viewPager.setAdapter(mPagerAdapter);
//        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int arg0) {
//                switchViewpager(arg0);
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//            }
//        });
//    }

//    private void switchViewpager(int position) {
//        int wdian = context.getResources().getIdentifier("wdian", "drawable", context.getPackageName());
//        int dian = context.getResources().getIdentifier("dian", "drawable", context.getPackageName());
//        if (position == 0) {
//            ll_fujian.setVisibility(View.VISIBLE);
//            ll_communication.setVisibility(View.GONE);
//            iv_wdian.setImageResource(wdian);
//            iv_dian.setImageResource(dian);
//        } else if (position == 1) {
//            ll_fujian.setVisibility(View.GONE);
//            ll_communication.setVisibility(View.VISIBLE);
//            iv_wdian.setImageResource(dian);
//            iv_dian.setImageResource(wdian);
//        }
//    }

    /**
     * 判断后台发出的下载成功广播是否是当前界面中的附件信息
     *
     * @param fileSDName 保存在sd卡的名称
     * @return true-成功 false-不成功
     */
    public boolean isFileDownloaded(String fileSDName) {
        if (attachOnes != null && attachOnes.size() > 0) {
            for (AttachOne attachOne : attachOnes) {
                String fileName = attachOne.getAttachOneSaveName();
                if (!TextUtils.isEmpty(fileName) && fileName.equals(fileSDName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void btnBack(View v) {
        setResult(MyPreferences.FRESH_ADAPTER);
        this.finish();
    }

    public void btnScan(View v) {
        permission(MY_PERMISSION_REQUEST_CODE, 0);
    }

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private static final int MY_PERMISSION_REQUEST_CODE_2 = 20000;
    private String[] myPermissions = new String[]{Manifest.permission.CAMERA,};
    private String[] myPermissions2 = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int temp_position = 0;

    private void permission(int code, int position) {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = false;
        temp_position = position;
        if (code == MY_PERMISSION_REQUEST_CODE)
            isAllGranted = checkPermissionAllGranted(myPermissions);
        else
            isAllGranted = checkPermissionAllGranted(myPermissions2);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            doBackup(code, position);
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        if (code == MY_PERMISSION_REQUEST_CODE)
            ActivityCompat.requestPermissions(this, myPermissions, MY_PERMISSION_REQUEST_CODE);
        else
            ActivityCompat.requestPermissions(this, myPermissions2, MY_PERMISSION_REQUEST_CODE_2);

    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE || requestCode == MY_PERMISSION_REQUEST_CODE_2) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                doBackup(requestCode, temp_position);

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 第 4 步: 后续操作
     */
    private void doBackup(int code, int position) {
        // 本文主旨是讲解如果动态申请权限, 具体备份代码不再展示, 就假装备份一下
        if (code == MY_PERMISSION_REQUEST_CODE) {
            Intent it = new Intent(BookSynActivity.this, AutoFocusActivity.class);// 跳转到相机Activity
            it.putExtra(AutoFocusActivity.BOOKID, bookId);
            startActivityForResult(it, 102);
            String result = TextUtils.isEmpty(SPUtility.getSPString(this, "switchKey")) ? "false" : SPUtility.getSPString(this, "switchKey");
            if (ActivityUtils.NetSwitch(this, Boolean.parseBoolean(result))) {
                getImageDownload();
            }
        } else {
            ActivityUtils.showToast(BookSynActivity.this, "加入同步列表");
            downLoad(position);
            DialogUtils.dismissMyDialog();
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("M+Book需要访问 “相机” 和 “外部存储器”，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * <p>
     * Title
     * </p>
     * : btnRead Description: 阅读
     *
     * @param v
     */
    @SuppressLint("NewApi")
    public void btnRead(View v) throws Exception {

        if (System.currentTimeMillis() - lastClick <= 1000) {
            Log.i("onclick", "您点击的太快了");
            return;
        }
        lastClick = System.currentTimeMillis();
        // 正在下载
        if (downloadManager.isDownloading(attachOne) || downloadManager.isDownloading(attachOne2)) {
            ActivityUtils.showToast(this, "文件正在同步，请稍等");
            return;
        }

        if (ActivityUtils.epubIsExistAndRead(bookId, bookSaveName) || ActivityUtils.isExistAndRead(bookId)) {
            if (ActivityUtils.epubIsExistAndRead(bookId, bookSaveName)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(BookSynActivity.this)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("M+Book读此书籍需要允许‘修改系统设置’权限");
                        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                        return;
                    }
                }
                DialogUtils.showMyDialog(context, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在解析中...", null);
                ActivityUtils.unLock(bookId, bookSaveName + ".epub", bookSaveName + ".epub" + "copy.epub");
                Intent intent = new Intent(BookSynActivity.this, HomeActivity.class);
                intent.putExtra("bookPath", bookSaveName + ".epub" + "copy.epub");
                intent.putExtra("bookId", bookId);
                intent.putExtra("bookName", bookName);
                if (bookVO != null && !TextUtils.isEmpty(bookVO.getBookAuthor()))
                    intent.putExtra("author", bookVO.getBookAuthor());

                startActivity(intent);
                return;
            } else if (ActivityUtils.isExistAndRead(bookId)) {
                ActivityUtils.unLock(bookId, bookId + ".xml", bookId + "copy.xml");
                Intent intent = new Intent(BookSynActivity.this, BookReadActivity.class);
                intent.putExtra("bookPath", ActivityUtils.getSDPath(bookId) + "/" + bookId + "copy.xml");
                intent.putExtra("imagePath", ActivityUtils.getSDPath(bookId) + "/images/");
                if (bookVO != null && !TextUtils.isEmpty(bookVO.getBookName()))
                    intent.putExtra("bookName", bookVO.getBookName());

                startActivity(intent);
            }
            return;
        }
        if (!unConnectedShowDialog())
            return;

        if (flag_download == -1 && ActivityUtils.isNetworkAvailable(context)) {
            if (bookVO != null && bookVO.getBookImgPath() != null && bookVO.getBookSaveName() != null) {
                if (!ActivityUtils.NetSwitch(context, Boolean.parseBoolean(SPUtility.getSPString(this, "switchKey")))) {
                    ActivityUtils.showToastForFail(this, "请在有WIFI的情况下下载");
                    return;
                }
                downLoadXml();
                downLoadZip();
                downLoadEpub();
            }
        }

    }

    /**
     * <p>
     * Title
     * </p>
     * : unConnectedShowDialog Description: 未连接网络对话框
     */
    private boolean unConnectedShowDialog() {
        boolean isNet = true;
        if (!ActivityUtils.isNetworkAvailable(context)) {
            ActivityUtils.showToast(context, "无法连接网络，下载失败");
            isNet = false;
        }
        return isNet;
    }

    // 下载书籍的xml模块
    public void downLoadXml() {
        String path = null;

        Log.i("BookSynActivity", "BookImgPath" + bookVO.getBookImgPath());
        Log.i("BookSynActivity", "SavaName" + bookVO.getBookSaveName());
        path = getPath(Preferences.FILE_DOWNLOAD_URL + bookVO.getBookSavePath() + File.separator + bookVO.getBookSaveName() + ".xml");
        Log.i("BookSynActivity", "downLoadXml" + path);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ActivityUtils.showToast(this, "加入同步列表,请稍候....");
            Intent intent = new Intent(this, TestService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            DocInfo d = new DocInfo();
            d.setUrl(path);
            d.setDirectoty(false);
            d.setBookId(bookId);
            d.setName(bookId + ".xml");
            d.setBookName(bookVO.getBookName());
            bundle.putSerializable("info", d);
            intent.putExtra("bundle", bundle);
            startService(intent);
            flag_download = 0;
        } else {
            ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡");
        }
    }

    // 下载书籍的图片包模块
    public void downLoadZip() {
        String path = getPath(Preferences.FILE_DOWNLOAD_URL + bookVO.getBookSavePath() + File.separator + bookVO.getBookSaveName() + ".zip");
        Log.i("BookSynActivity", "downLoadZip" + path);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(this, TestService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            DocInfo d = new DocInfo();
            d.setUrl(path);
            d.setDirectoty(false);
            d.setName(bookId + ".zip");
            d.setBookId(bookId);
            d.setBookName(bookVO.getBookName() + "(图片包)");
            bundle.putSerializable("info", d);
            intent.putExtra("bundle", bundle);
            startService(intent);
            flag_download = 0;
        } else {
            ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡");
        }
    }

    /**
     * <p>
     * Title
     * </p>
     * : downLoadEpub Description: 下载Epub模块
     */
    public void downLoadEpub() {
        String path = getPath(Preferences.FILE_DOWNLOAD_URL + bookVO.getBookSavePath() + File.separator + bookVO.getBookSaveName() + ".epub");
        Log.i("BookSynActivity", "downLoadEpub" + path);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(this, TestService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            DocInfo d = new DocInfo();
            d.setUrl(path);
            d.setDirectoty(false);
            d.setName(bookSaveName + ".epub");
            d.setBookId(bookId);
            d.setBookName(bookName);
            bundle.putSerializable("info", d);
            intent.putExtra("bundle", bundle);
            startService(intent);
            flag_download = 0;
        } else {
            ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡");
        }
    }

    /**
     * 下载附件
     *
     * @param url      附件的服务器地址
     * @param saveName 附件要保存的名字
     * @param fileName 附件要现实的名字
     */
    public void downLoadFile(String url, String saveName, String fileName) {

        String path = getPath(Preferences.FILE_DOWNLOAD_URL + url);
        Log.i("BookSynActivity", "url=" + path);
        Log.i("BookSynActivity", "saveName=" + saveName);
        Log.i("BookSynActivity", "fileName=" + fileName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(this, TestService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            DocInfo d = new DocInfo();
            d.setUrl(path);
            d.setDirectoty(false);
            d.setName(saveName);
            d.setBookId(bookId);
            d.setBookName(fileName);
            bundle.putSerializable("info", d);
            intent.putExtra("bundle", bundle);
            startService(intent);
        } else {
            ActivityUtils.showToastForFail(BookSynActivity.this, "未检测到SD卡,或未赋予应用存储权限");
        }
    }

    private String getPath(String url) {
        String path = url + "&userId=" + UserUtil.getInstance(context).getUserId() + "&bookId=" + bookId;
        Log.d(TAG, "download path===========" + path);
        return path;
    }

    /**
     * <p>
     * Title
     * </p>
     * : PictureReadBook Description: 点击图片进行下载和阅读
     *
     * @param paramView
     */
    public void PictureReadBook(View paramView) {
        if (ActivityUtils.epubIsExistAndRead(bookId, bookSaveName)) {
            ActivityUtils.unLock(bookId, bookSaveName + ".epub", bookSaveName + ".epub" + "copy.epub");
            Intent intent = new Intent(BookSynActivity.this, HomeActivity.class);
            intent.putExtra("bookPath", bookSaveName + ".epub" + "copy.epub");
            intent.putExtra("bookId", bookId);
            intent.putExtra("bookName", bookName);
            if (bookVO != null && !TextUtils.isEmpty(bookVO.getBookAuthor()))
                intent.putExtra("author", bookVO.getBookAuthor());
            startActivity(intent);
            return;
        } else if (ActivityUtils.isExistAndRead(bookId)) {
            ActivityUtils.unLock(bookId, bookId + ".xml", bookId + "copy.xml");
            Intent intent = new Intent(BookSynActivity.this, BookReadActivity.class);
            intent.putExtra("bookPath", ActivityUtils.getSDPath(bookId) + "/" + bookId + "copy.xml");
            intent.putExtra("imagePath", ActivityUtils.getSDPath(bookId) + "/images/");
            if (bookVO != null && !TextUtils.isEmpty(bookVO.getBookName()))
                intent.putExtra("bookName", bookVO.getBookName());
            startActivity(intent);
        } else {
            if (!ActivityUtils.isNetworkAvailable(context)) {
                unConnectedShowDialog();
                return;
            }

            if (downloadManager.isDownloading(attachOne) || downloadManager.isDownloading(attachOne2)) {
                ActivityUtils.showToast(this, "文件正在同步，请稍等");
                return;
            }
            if (flag_download == -1) {
                if (bookVO != null && bookVO.getBookImgPath() != null && bookVO.getBookSaveName() != null) {
                    downLoadXml();
                    downLoadZip();
                    downLoadEpub();
                }
                return;
            }
        }
    }

    /*
     * Title: onResume Description: 为了更新小红点的标识和设置下载按钮的文字
     *
     * @see com.jiayue.BaseActivity#onResume()
     */
    @Override
    protected void onResume() {
        if (ActivityUtils.isExistAndRead(bookId) || ActivityUtils.epubIsExistAndRead(bookId, bookSaveName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                iv_book.setForeground(null);
            }
            iv_book.setAlpha(1f);
            book_download.setVisibility(View.GONE);
//            tv_loading.setText("悦读");
//            int booksyn_read_button = getResources().getIdentifier("finish_download", "drawable", getPackageName());
//            btn_read.setImageResource(booksyn_read_button);
//            iv_read.setBackgroundResource(booksyn_read_button);
        } else {
            book_download.setVisibility(View.VISIBLE);
            iv_book.setAlpha(0.4f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                iv_book.setForeground(new ColorDrawable(0xFF000000));
            }
//            tv_loading.setText("下载");
//            int booksyn_download_button = getResources().getIdentifier("no_download", "drawable", getPackageName());
//            btn_read.setImageResource(booksyn_download_button);
//            iv_read.setBackgroundResource(booksyn_download_button);
        }
        // try {
        // BookVO bookVO =
        // db.findFirst(Selector.from(BookVO.class).where("book_id", "=",
        // bookId));
        // if (bookVO == null) {
        // Log.d(TAG, "=======================bookVO == nullbookVO == null");
        // } else {
        // Log.d(TAG, "=======================bookVO name" +
        // bookVO.getBookName());
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // getBookIntroduction();

        super.onResume();
    }

    /**
     * <p>
     * Title
     * </p>
     * : getBookIntroduction Description: 网络获取BookVO对象
     */
    private void getBookIntroduction() {
        // 请求直播数据
        RequestParams params1 = new RequestParams();
        params1.setUri(Preferences.LIVE_JUDGE);
        params1.addQueryStringParameter("bookId", bookId);
        params1.addQueryStringParameter("userId", UserUtil.getInstance(BookSynActivity.this).getUserId());
        Log.d(TAG, "getBookIntroduction bookid=" + bookId);
        x.http().post(params1, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<BookLiveAllBean>() {
                }.getType();
                BookLiveAllBean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    if (bean.getData() != null && bean.getData().size() != 0) {
                        BookLiveBean bb = bean.getData().get(0);
                        if (bb.getLivePression() == 1) {
                            livebean = bean.getData().get(0);
                            btn_download.setClickable(true);
//                            btn_download.setBackgroundResource(R.drawable.booksyn_btn_selector);
                            InitBusiness.start(BookSynActivity.this, 0);
                            TlsBusiness.init(BookSynActivity.this);

                            btn_download.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
                                    final String id = UserUtil.getInstance(BookSynActivity.this).getUserId();

                                    RequestParams params = new RequestParams();
                                    params.setUri(Preferences.GET_ZHIBO_INFO);
                                    params.addQueryStringParameter("courseId", livebean.getCourseId() + "");
                                    params.addQueryStringParameter("userId", id + "");
                                    groupId = livebean.getGroupId();
                                    webUrl = livebean.getUrl();
                                    x.http().post(params, new CommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            Gson gson = new Gson();
                                            java.lang.reflect.Type type = new TypeToken<ZhiboInfoBean>() {
                                            }.getType();
                                            zhibobean = gson.fromJson(s, type);
                                            if (zhibobean != null && zhibobean.getData() != null && zhibobean.getCode().equals("SUCCESS")) {

                                                LoginBusiness.loginIm(UserUtil.getInstance(BookSynActivity.this).getUserName(), zhibobean.getData().getUserSig(), BookSynActivity.this);
                                            } else {
                                                ActivityUtils.showToast(context, getString(R.string.zhiboshibai));
                                            }
                                            DialogUtils.dismissMyDialog();
                                        }

                                        @Override
                                        public void onError(Throwable throwable, boolean b) {

                                        }

                                        @Override
                                        public void onCancelled(CancelledException e) {

                                        }

                                        @Override
                                        public void onFinished() {

                                        }
                                    });
                                }
                            });
                        }
                    }
                } else {
                    ActivityUtils.showToastForFail(BookSynActivity.this, "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

        bookVO = unSerializeBookVO(this.bookId);
        if ((this.bookVO == null) && (!ActivityUtils.isNetworkAvailable(this.context))) {
            return;
        }
        if (this.bookVO != null && !TextUtils.isEmpty(bookVO.getBookName()) && !ActivityUtils.isNetworkAvailable(this.context)) {
            updataBook();
            return;
        }

        RequestParams params = new RequestParams();
        params.setUri(Preferences.A_BOOK_URL);
        params.addQueryStringParameter("bookId", bookId);
        params.addQueryStringParameter("userId", UserUtil.getInstance(BookSynActivity.this).getUserId());
        Log.i("BookSynActivity", "bookId" + this.bookId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<BookDetailBean>() {
                }.getType();
                BookDetailBean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    if (bean.getData() != null) {
                        bookVO = bean.getData();
                        serializeBooKVO(bookVO);
                        updataBook();
                    }
                } else {
                    ActivityUtils.showToastForFail(BookSynActivity.this, "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(BookSynActivity.this, "信息获取失败");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * <p>
     * Title
     * </p>
     * : getAttachOne Description: 获取一级附件
     */
    private void getAttachOne() {
        if (ActivityUtils.isNetworkAvailable(this.context)) {
            getNetAttachOne();
        } else {
            updataBook();
        }

    }

    // 从网络获取数据
    public void getNetAttachOne() {

        RequestParams params = new RequestParams();
        params.setUri(Preferences.FILE_DOWNLOAD_ATTACH_URL1);
        params.addQueryStringParameter("bookId", bookId);
        params.addQueryStringParameter("systemType", "android");
        params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());

        Log.i("BookSynActivity", "bookId" + this.bookId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "attachone---------=" + s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<AttachOneBean>() {
                }.getType();
                AttachOneBean bean = gson.fromJson(s, type);

                if (bean.getCode() != null && bean.getCode().equals(MyPreferences.SUCCESS) && null != bean.getData()) {
                    attachOnes = bean.getData();
                    if (attachOnes != null) {
                        DbManager db = MyDbUtils.getOneAttachDb(context);
                        try {
                            List<AttachOne> list = db.selector(AttachOne.class).where("bookId", "=", bookId).findAll();
                            if (list != null && list.size() > 0) {
                                db.delete(list);
                                db.save(attachOnes);
                            } else {
                                db.save(attachOnes);
                            }
                        } catch (DbException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    updataBook();
                    return;
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToastForFail(context, "加载失败," + bean.getCodeInfo());
                    return;
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(context, "加载失败,请检查网络！");
                updataBook();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 将BookVO对象序列化到本地
     */
    protected void serializeBooKVO(BookVO bookVO) {
        ObjectOutputStream oo = null;
        try {
            File target = new File(getCacheDir(), bookVO.getBookId());//
            oo = new ObjectOutputStream(new FileOutputStream(target, false));
            oo.writeObject(bookVO);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oo != null) {
                try {
                    oo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 反序列化BooKVO
     */
    protected BookVO unSerializeBookVO(String bookId) {
        BookVO bv = null;
        ObjectInputStream ois = null;
        try {
            File source = new File(getCacheDir(), bookId);
            ois = new ObjectInputStream(new FileInputStream(source));
            bv = (BookVO) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bv;
    }


    /**
     * <p>
     * Title
     * </p>
     * : updataBook Description: 更新界面
     */
    public void updataBook() {
        adapte_fujian.notifyDataSetChanged();

        if (bookVO == null)
            return;
        if (null != bookVO.getBookSize()) {
            tv_size.setText(ActivityUtils.bytes2kb(Long.parseLong(bookVO.getBookSize())));
        }
        if (null != bookVO.getBookAuthor()) {
            tv_author.setText(bookVO.getBookAuthor());
            SPUtility.putSPString(context, "author" + bookVO.getBookId(), bookVO.getBookAuthor());
        }

        if (isZhiYurJoin) {
            isZhiYurJoin = false;
            Intent intent = new Intent(BookSynActivity.this, BookAttachActivity.class);
            intent.putExtra("bookId", bookId);
            intent.putExtra("attachOneId", getIntent().getIntExtra("attachOneId", -1) + "");
            intent.putExtra("attachName", getIntent().getStringExtra("attachName"));
            intent.putExtra("bookName", bookName);
            intent.putExtra("image_url_str", image_url_str);
            intent.putExtra("price", getIntent().getFloatExtra("price", 0f));
            intent.putExtra("isZhiYurJoin", isZhiYurJoin);
            startActivity(intent);
        }
    }

    public void choose_all(View view) {
        Intent intent = new Intent(BookSynActivity.this, ChooseMusicAll.class);
        intent.putParcelableArrayListExtra("attachOnes", (ArrayList<? extends Parcelable>) attachOnes);
        startActivity(intent);

    }

    abstract class MyOnLongClickListener implements OnLongClickListener, OnTouchListener {
    }

    ;

    /**
     * itle: FileAttachAdapter Description: 附件适配器 Company: btpd
     * get
     *
     * @author Ping Wang
     * @date 2015-11-13
     */
    class FileAttachAdapter extends BaseAdapter {
        LayoutInflater inflater;
        View view;
        String new_name = "";

        public FileAttachAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if (attachOnes != null && attachOnes.size() > 0)
                return attachOnes.size();
            return 0;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        /**
         * 把当前item对应的view对象返回回去
         */
        public View getView(final int position, final View convertView, ViewGroup parent) {
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.item_fujian2, null);
            }
            final String fujianName = attachOnes.get(position).getAttachOneName() + "." + attachOnes.get(position).getAttachOneType();
            new_name = attachOnes.get(position).getAttachOneName();
            if (new_name.startsWith("图")) {
                new_name = new_name.replace("图", "pic");
            }

            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl_fujian);

            MyOnLongClickListener longClick = new MyOnLongClickListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        tv_header_title.setText("图书详情");
                    }
                    return false;
                }

                @Override
                public boolean onLongClick(View v) {
                    tv_header_title.setText(attachOnes.get(position).getAttachOneName());
                    Log.d(TAG, "------url===" + attachOnes.get(position).getShareUrl() + "--------issend===" + attachOnes.get(position).getShareUrl());
                    if (TextUtils.isEmpty(attachOnes.get(position).getShareUrl()) || attachOnes.get(position).getIsSendAtta1().equals("0")) {
                        ActivityUtils.showToastForFail(BookSynActivity.this, "此文件不能分享！");
                    } else {
                        shareAttachFile(position);
                    }
                    return true;
                }
            };
            rl.setOnLongClickListener(longClick);
            rl.setOnTouchListener(longClick);
            // 点击书籍阅读
            rl.setOnClickListener(new OnClickListener() {
                long lastClick;

                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() - lastClick <= 1000)
                        return;

                    if (attachOnes.get(position).getIsPay() == 1)
                        return;
                    lastClick = System.currentTimeMillis();
                    Log.i("attachOnes", attachOnes.get(position).toString());
                    new_name = attachOnes.get(position).getAttachOneName();
                    if (new_name.startsWith("图")) {
                        new_name = new_name.replace("图", "pic");
                    }
                    if (attachOnes.get(position).getAttachOneIspackage() == 0) {

                        if (!ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneSaveName()) && !ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName()) && !ActivityUtils
                                .isExistByName(bookId, new_name) && !ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName() + "." + attachOnes.get(position)
                                .getAttachOneType())) {
                            if (!ActivityUtils.NetSwitch(BookSynActivity.this, Boolean.parseBoolean(SPUtility.getSPString(BookSynActivity.this, "switchKey")))) {
                                ActivityUtils.showToastForFail(BookSynActivity.this, "请在有WIFI的情况下下载");
                                return;
                            }
                            unConnectedShowDialog();
                            if (ActivityUtils.isNetworkAvailable(context)) {
                                if (TextUtils.isEmpty(attachOnes.get(position).getShareUrl()) || attachOnes.get(position).getIsSendAtta1().equals("0")) {
//                                    DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", "该文件未同步，是否开始同步？", new OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
                                    permission(MY_PERMISSION_REQUEST_CODE_2, position);

//                                        }
//                                    });
                                } else {
                                    String content = "长按此文件可分享给您的好友，是否直接同步？";
                                    DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "分享&同步", content, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityUtils.showToast(BookSynActivity.this, "加入同步列表");
                                            if (attachOnes.get(position).getAttachOneType().endsWith("lrc")) {
                                                downLoadFile(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName(), TEMP + attachOnes.get(position)
                                                        .getAttachOneName() + "." + attachOnes.get(position).getAttachOneType(), fujianName);
                                            } else {
                                                downLoadFile(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName(), TEMP + attachOnes.get(position)
                                                        .getAttachOneSaveName(), fujianName);
                                            }
                                            DialogUtils.dismissMyDialog();
                                        }
                                    });
                                }
                            }

                        } else {
                            DataBaseHelper helper = new DataBaseHelper(context);
                            List<DocInfo> infos = helper.getInfo2(bookId);
                            for (DocInfo docInfo : infos) {
                                if (fujianName.equals(docInfo.getBookName())) {
                                    if (docInfo.getStatus() == DataBaseFiledParams.LOADING) {
                                        docInfo.setStatus(DataBaseFiledParams.PAUSING);
                                        downloadManager.pause(docInfo);
                                    } else if (docInfo.getStatus() == DataBaseFiledParams.PAUSING) {
                                        docInfo.setStatus(DataBaseFiledParams.LOADING);
                                        downloadManager.startForActivity(docInfo);
                                    } else if (docInfo.getStatus() == DataBaseFiledParams.WAITING) {
                                        docInfo.setStatus(DataBaseFiledParams.LOADING);
                                        downloadManager.startForActivity(docInfo);
                                    } else if (docInfo.getStatus() == DataBaseFiledParams.FAILED) {
                                        docInfo.setStatus(DataBaseFiledParams.LOADING);
                                        downloadManager.startForActivity(docInfo);
                                    }
                                }
                            }

                            adapte_fujian.notifyDataSetChanged();

                            // 文件是否正在同步
                            if (downloadManager.isDownloading(attachOnes.get(position))) {
                                ActivityUtils.showToast(BookSynActivity.this, "该文件正在同步请稍后。");
                            } else {
                                if (ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName()) || ActivityUtils.isExistByName(bookId, new_name)) {
                                    if (attachOnes.get(position).getAttachOneType().equals("zip")) {
                                        readFlash(position);
                                    } else {
                                        Log.d(TAG, "tttttttt type==" + attachOnes.get(position).getAttachOneType());
                                        if (attachOnes.get(position).getAttachOneType().equals("frame")) {
                                            String filepath = "file:///android_asset/keyFrame_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                                            ActivityUtils.readURL(context, bookId, filepath, true);
                                        } else if (attachOnes.get(position).getAttachOneType().equals("skeleton")) {
                                            String filepath = "file:///android_asset/animation_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                                            ActivityUtils.readURL(context, bookId, filepath, true);
                                        } else if (attachOnes.get(position).getAttachOneType().equals("blender")) {
                                            String filepath = "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".html?no_social";
                                            ActivityUtils.readURL(context, bookId, filepath, true);
                                        }
                                    }
                                } else {
                                    try {
                                        unLock(position);
                                    } catch (Exception e) {
//                                        Looper.prepare();
                                        e.printStackTrace();
                                        Toast.makeText(getApplication(), "文件损坏，请重新下载！", Toast.LENGTH_LONG).show();
                                        ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName());
                                        ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName() + ".zip");
                                        DialogUtils.dismissMyDialog();

//                                        Looper.loop();
                                    }
                                }
                            }
                        }
                    } else if (attachOnes.get(position).getAttachOneIspackage() == 2) {
                        String url = attachOnes.get(position).getAttachOneSaveName();
                        ActivityUtils.readURL(context, bookId, url, false);
                    } else {
                        Intent intent = new Intent(BookSynActivity.this, BookAttachActivity.class);
                        intent.putExtra("bookId", bookId);
                        intent.putExtra("attachOneId", attachOnes.get(position).getAttachOneId());
                        intent.putExtra("attachName", attachOnes.get(position).getAttachOneName());
                        intent.putExtra("bookName", bookName);
                        intent.putExtra("image_url_str", image_url_str);
                        startActivity(intent);
                    }
                }

            });
            // 设置书的封面
            final ImageView iv = (ImageView) view.findViewById(R.id.iv_fujian);
            final String type = attachOnes.get(position).getAttachOneType();
            final String saveName = attachOnes.get(position).getAttachOneSaveName();

            TextView tv_download = (TextView) view.findViewById(R.id.tv_baifen);
            tv_download.setText("");
//            if (info != null) {
//                if (fujianName.equals(info.getBookName())&&saveName.equals(info.getName())) {
//                    getStateSetView(tv_download, info);
//                }
//
//            } else {
            DataBaseHelper helper = new DataBaseHelper(context);
            List<DocInfo> infos = helper.getInfo2(bookId);
            for (DocInfo docInfo : infos) {
                if (fujianName.equals(docInfo.getBookName()) && saveName.equals(docInfo.getName())) {
                    getStateSetView(tv_download, docInfo);
                }
            }
//            }

            if (type != null && (type.equalsIgnoreCase("jpg") | type.equalsIgnoreCase("png") || type.equalsIgnoreCase("gif"))) {
                if (ActivityUtils.isExistByName(bookId, saveName)) {
                    ActivityUtils.unLock(bookId, saveName, "copy_" + saveName);
                    iv.setBackground(Drawable.createFromPath(ActivityUtils.getSDPath(bookId) + File.separator + "copy_" + saveName));
                } else {
                    iv.setBackgroundResource(ActivityUtils.getFilePackageImageId(context, attachOnes.get(position).getAttachOneIspackage(), attachOnes.get(position).getAttachOneType()));
                }
            } else {
                iv.setBackgroundResource(ActivityUtils.getFilePackageImageId(context, attachOnes.get(position).getAttachOneIspackage(), attachOnes.get(position).getAttachOneType(), attachOnes
                        .get(position).getAttachOneName()));
            }
            // 附件名称
            TextView tv_fujian = (TextView) view.findViewById(R.id.tv_fujian);
            tv_fujian.setText(attachOnes.get(position).getAttachOneName());

            LinearLayout layout_buy = ((LinearLayout) view.findViewById(R.id.layout_buy));
            final LinearLayout layout_download = ((LinearLayout) view.findViewById(R.id.layout_download));
            if (attachOnes.get(position).getIsPay() == 0) {
                if (attachOnes.get(position).getAttachOneIspackage() == 0) {
                    if ((ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneSaveName()) || ActivityUtils.isExistByName(bookId, attachOnes.get(position)
                            .getAttachOneName()) || ActivityUtils.isExistByName(bookId, new_name) || ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName() + "." + attachOnes
                            .get(position).getAttachOneType()))) {
                        tv_fujian.setTextColor(context.getResources().getColor(R.color.background));
                    } else {
                        tv_fujian.setTextColor(0xff333333);
                    }

                } else {
                    tv_fujian.setTextColor(0xff333333);
                }
                if (attachOnes.get(position).getAttachOneIspackage() == 1 || type == null || TextUtils.isEmpty(type) || (type.equals("ulr") || type.equals("html"))) {
                    layout_download.setVisibility(View.GONE);
//                    tv_fujian.setTextColor(0xff36d5d3);
                } else {
                    layout_download.setVisibility(View.VISIBLE);
//                    tv_fujian.setTextColor(0xff333333);
                }
                layout_buy.setVisibility(View.GONE);
                layout_download.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupWindow popupWindow = new PopupWindow(context);

                        View view = LayoutInflater.from(context).inflate(R.layout.layout_popup_window_menu, null);

                        //下载
                        TextView menuItem1 = view.findViewById(R.id.tv_option1);
                        menuItem1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (System.currentTimeMillis() - lastClick <= 1000)
                                    return;

                                if (attachOnes.get(position).getIsPay() == 1)
                                    return;
                                lastClick = System.currentTimeMillis();
                                Log.i("attachOnes", attachOnes.get(position).toString());
                                new_name = attachOnes.get(position).getAttachOneName();
                                if (new_name.startsWith("图")) {
                                    new_name = new_name.replace("图", "pic");
                                }
                                if (attachOnes.get(position).getAttachOneIspackage() == 0) {

                                    if (!ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneSaveName()) && !ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName()) && !ActivityUtils
                                            .isExistByName(bookId, new_name) && !ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName() + "." + attachOnes.get(position)
                                            .getAttachOneType())) {
                                        if (!ActivityUtils.NetSwitch(BookSynActivity.this, Boolean.parseBoolean(SPUtility.getSPString(BookSynActivity.this, "switchKey")))) {
                                            ActivityUtils.showToastForFail(BookSynActivity.this, "请在有WIFI的情况下下载");
                                            return;
                                        }
                                        unConnectedShowDialog();
                                        if (ActivityUtils.isNetworkAvailable(context)) {
                                            if (TextUtils.isEmpty(attachOnes.get(position).getShareUrl()) || attachOnes.get(position).getIsSendAtta1().equals("0")) {
//                                    DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", "该文件未同步，是否开始同步？", new OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
                                                permission(MY_PERMISSION_REQUEST_CODE_2, position);

//                                        }
//                                    });
                                            } else {
                                                String content = "长按此文件可分享给您的好友，是否直接同步？";
                                                DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "分享&同步", content, new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ActivityUtils.showToast(BookSynActivity.this, "加入同步列表");
                                                        if (attachOnes.get(position).getAttachOneType().endsWith("lrc")) {
                                                            downLoadFile(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName(), TEMP + attachOnes.get(position)
                                                                    .getAttachOneName() + "." + attachOnes.get(position).getAttachOneType(), fujianName);
                                                        } else {
                                                            downLoadFile(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName(), TEMP + attachOnes.get(position)
                                                                    .getAttachOneSaveName(), fujianName);
                                                        }
                                                        DialogUtils.dismissMyDialog();
                                                    }
                                                });
                                            }
                                        }

                                    } else {
                                        DataBaseHelper helper = new DataBaseHelper(context);
                                        List<DocInfo> infos = helper.getInfo2(bookId);
                                        for (DocInfo docInfo : infos) {
                                            if (fujianName.equals(docInfo.getBookName())) {
                                                if (docInfo.getStatus() == DataBaseFiledParams.LOADING) {
                                                    docInfo.setStatus(DataBaseFiledParams.PAUSING);
                                                    downloadManager.pause(docInfo);
                                                } else if (docInfo.getStatus() == DataBaseFiledParams.PAUSING) {
                                                    docInfo.setStatus(DataBaseFiledParams.LOADING);
                                                    downloadManager.startForActivity(docInfo);
                                                } else if (docInfo.getStatus() == DataBaseFiledParams.WAITING) {
                                                    docInfo.setStatus(DataBaseFiledParams.LOADING);
                                                    downloadManager.startForActivity(docInfo);
                                                } else if (docInfo.getStatus() == DataBaseFiledParams.FAILED) {
                                                    docInfo.setStatus(DataBaseFiledParams.LOADING);
                                                    downloadManager.startForActivity(docInfo);
                                                }
                                            }
                                        }

                                        adapte_fujian.notifyDataSetChanged();

                                        // 文件是否正在同步
                                        if (downloadManager.isDownloading(attachOnes.get(position))) {
                                            ActivityUtils.showToast(BookSynActivity.this, "该文件正在同步请稍后。");
                                        } else {
                                            ActivityUtils.showToast(BookSynActivity.this, "该文件已经下载完毕，点击条目即可打开使用。");
                                        }
                                    }
                                }
                                popupWindow.dismiss();
                            }
                        });

                        //分享
                        TextView menuItem2 = view.findViewById(R.id.tv_option2);
                        menuItem2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (TextUtils.isEmpty(attachOnes.get(position).getShareUrl()) || attachOnes.get(position).getIsSendAtta1().equals("0")) {
                                    ActivityUtils.showToastForFail(BookSynActivity.this, "此文件不能分享！");
                                } else {
                                    shareAttachFile(position);
                                }
//                                Toast.makeText(context, "Option 2", Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();
                            }
                        });

                        //删除
                        TextView menuItem3 = view.findViewById(R.id.tv_option3);
                        menuItem3.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onClick(View view) {
                                DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_BUY_DIALOG, "删除文件", String.format("确定要删除文件\"%s\"吗？", attachOnes.get(position).getAttachOneName()), new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        DialogUtils.dismissMyDialog();
                                        ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName());
                                        ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName() + ".zip");
                                        ActivityUtils.showToastForFail(BookSynActivity.this, String.format("文件\"%s\"删除完毕！", attachOnes.get(position).getAttachOneName()));
                                    }

                                });

                                popupWindow.dismiss();
                            }
                        });

                        //加入歌单
                        TextView menuItem4 = view.findViewById(R.id.tv_option4);
                        menuItem4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                List<MusicList> musicLists = null;
                                final DbManager db = MyDbUtils.getMusicDb(BookSynActivity.this);
                                try {
                                    musicLists = db.findAll(MusicList.class);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                if (musicLists == null || musicLists.size() == 0) {
                                    ActivityUtils.showToast(BookSynActivity.this, "您还没有创建歌单!");
                                    return;
                                }
                                final MusicListAdapter2 musicListAdapter = new MusicListAdapter2(musicLists, BookSynActivity.this, R.layout.item_music_list, false);
                                final ListView listView = new ListView(BookSynActivity.this);
                                listView.setPadding(60, 96, 60, 96);
                                listView.setAdapter(musicListAdapter);
                                final androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(BookSynActivity.this).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();

                                final List<MusicList> finalMusicLists = musicLists;
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, final int position1, long id) {
                                        new androidx.appcompat.app.AlertDialog.Builder(BookSynActivity.this)
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .setTitle("加入歌单")
                                                .setMessage(String.format("您是否将%s加入到%s歌单中？", attachOnes.get(position).getAttachOneName(), finalMusicLists.get(position1).getName()))
                                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finalMusicLists.get(position1).setMusic_num(finalMusicLists.get(position1).getMusic_num() + 1);
                                                        try {
                                                            db.update(finalMusicLists);
                                                        } catch (DbException e) {
                                                            e.printStackTrace();
                                                        }
                                                        musicListAdapter.notifyDataSetChanged();
                                                        MusicListBean musicListBean = new MusicListBean();
                                                        musicListBean.setList_id(finalMusicLists.get(position1).getId());
                                                        musicListBean.setList_name(finalMusicLists.get(position1).getName());
                                                        musicListBean.setMusic_name(attachOnes.get(position).getAttachOneName());
                                                        musicListBean.setNow_path(ActivityUtils.getSDPath(bookId) + File.separator + "copy_" + attachOnes.get(position).getAttachOneSaveName());
                                                        musicListBean.setOld_path(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName());
                                                        musicListBean.setBook_id(bookId);
                                                        musicListBean.setSave_name(attachOnes.get(position).getAttachOneSaveName());
                                                        try {
                                                            db.save(musicListBean);
                                                        } catch (DbException e) {
                                                            e.printStackTrace();
                                                        }


                                                        alertDialog.dismiss();
                                                        dialog.dismiss();
                                                    }
                                                }).setNegativeButton("取消", null).create().show();
                                    }
                                });
                                alertDialog.setView(listView);
                                alertDialog.setTitle("收藏到歌单");
                                alertDialog.show();

                                popupWindow.dismiss();
                            }
                        });

                        popupWindow.setContentView(view);
                        popupWindow.setWidth(250);
                        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                        popupWindow.setTouchable(true);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.showAsDropDown(v);
                        if (!popupWindow.isShowing()) {
                            popupWindow.showAtLocation(layout_download, Gravity.CENTER, 0, 0);
                        }
                        if (attachOnes.get(position).getAttachOneIspackage() == 0) {
                            if ((ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneSaveName()) || ActivityUtils.isExistByName(bookId, attachOnes.get(position)
                                    .getAttachOneName()) || ActivityUtils.isExistByName(bookId, new_name) || ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName() + "." + attachOnes
                                    .get(position).getAttachOneType()))) {
                                menuItem1.setVisibility(View.GONE);
                                menuItem3.setVisibility(View.VISIBLE);
                            } else {
                                menuItem1.setVisibility(View.VISIBLE);
                                menuItem3.setVisibility(View.GONE);
                            }

                        } else {
                            menuItem1.setVisibility(View.GONE);
                            menuItem3.setVisibility(View.GONE);
                        }
                        if (TextUtils.isEmpty(attachOnes.get(position).getShareUrl()) || attachOnes.get(position).getIsSendAtta1().equals("0"))
                            menuItem2.setVisibility(View.GONE);
                        else
                            menuItem2.setVisibility(View.VISIBLE);

                        if (type != null && (type.equalsIgnoreCase("mp3") | type.equalsIgnoreCase("wav")))
                            menuItem4.setVisibility(View.VISIBLE);
                        else
                            menuItem4.setVisibility(View.GONE);

                    }
                });

                // 下载
//                ImageView iv_point_fujian = (ImageView) view.findViewById(R.id.iv_point_fujian);
//                if (attachOnes.get(position).getAttachOneIspackage() == 0) {
//                    iv_point_fujian.setVisibility((ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneSaveName()) || ActivityUtils.isExistByName(bookId, attachOnes.get(position)
//                            .getAttachOneName()) || ActivityUtils.isExistByName(bookId, new_name) || ActivityUtils.isExistByName(bookId, attachOnes.get(position).getAttachOneName() + "." + attachOnes
//                            .get(position).getAttachOneType())) ? View.INVISIBLE : View.VISIBLE);
//                } else {
//                    iv_point_fujian.setVisibility(View.INVISIBLE);
//                }

                // 设置是否可以分享
//                ImageView iv_share = (ImageView) view.findViewById(R.id.imageView1);
//                if (TextUtils.isEmpty(attachOnes.get(position).getShareUrl()) || attachOnes.get(position).getIsSendAtta1().equals("0"))
//                    iv_share.setVisibility(View.INVISIBLE);
//                else
//                    iv_share.setVisibility(View.VISIBLE);
            } else {
                layout_download.setVisibility(View.GONE);
                layout_buy.setVisibility(View.VISIBLE);
                layout_buy.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_BUY_DIALOG, "", "", new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                DialogUtils.dismissMyDialog();
                                getOrder(attachOnes.get(position).getAttachOneId(), attachOnes.get(position).getAttachOneName(), new BigDecimal(Float.toString(attachOnes.get(position).getPrice())).floatValue());
                            }

                        });
                    }
                });
            }

//            layout_download.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                    menu.add(Menu.NONE, 0, 0, "删除");
//                    menu.add(Menu.NONE, 1, 0, "分享");
//                }
//            });
//
            return view;
        }
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item){
//        //关键代码在这里
//        AdapterView.AdapterContextMenuInfo menuInfo;
//        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
//        switch (item.getItemId()){
//            case 0:
//                //点击第一个菜单项要做的事，如获取点击listview的位置
//                Toast.makeText(BookSynActivity.this, String.valueOf(menuInfo.position), Toast.LENGTH_LONG).show();
//                break;
//            case 1:
//                //点击第二个菜单项要做的事，如获取点击的数据
//                Toast.makeText(BookSynActivity.this, ""+attachOnes.get(menuInfo.position), Toast.LENGTH_LONG).show();
//                break;
//        }
//        return super.onContextItemSelected(item);
//    }


    private void getOrder(String attachId, final String attachName, final float price) {
        DialogUtils.showMyDialog(context, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        RequestParams params = new RequestParams();
        params.setUri(Preferences.GET_ATTACHORDER);
        params.addQueryStringParameter("attachId", attachId);
        params.addQueryStringParameter("systemType", "android");
        params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
        params.addQueryStringParameter("attachType", "1");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "attachone---------=" + s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<OrderBean>() {
                }.getType();
                OrderBean bean = gson.fromJson(s, type);

                DialogUtils.dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(MyPreferences.SUCCESS) && null != bean.getData()) {
                    Bundle b = new Bundle();
                    b.putString("bookId", bookId);
                    b.putString("bookName", bookName);
                    b.putString("image_url_str", image_url_str);
                    b.putString("attachName", attachName);
                    b.putFloat("price", price);
                    b.putString("ordercode", bean.getData().getAttachCode());
                    Intent intent = new Intent(BookSynActivity.this, PayChooseActivity.class);
                    intent.putExtra("data", b);
                    startActivityForResult(intent, 0);
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToastForFail(context, "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(context, "加载失败,请检查网络!");
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onFinished() {
                DialogUtils.dismissMyDialog();
            }
        });
    }

    public void getStateSetView(TextView tv_download, DocInfo docInfo) {
        if (docInfo.getStatus() == DataBaseFiledParams.PAUSING) {
            tv_download.setText("暂停");
        } else if (docInfo.getStatus() == DataBaseFiledParams.WAITING) {
            tv_download.setText("等待中");
        } else if (docInfo.getStatus() == DataBaseFiledParams.FAILED) {
            tv_download.setText("服务器忙");
        } else {
            tv_download.setText(docInfo.getDownloadProgress() + "%");
            if (tv_download.getText().toString().trim().equals("100%")) {
                tv_download.setText("");
            }
        }
    }

    /**
     * <p>
     * Title
     * </p>
     * : shareAttachFile Description: 文件分享
     *
     * @param position
     */
    private void shareAttachFile(final int position) {
        DialogUtils.showMyDialog(context, MyPreferences.SHOW_CONFIRM_DIALOG, attachOnes.get(position).getAttachOneName(), "您要分享此文件给好友吗？", new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.dismissMyDialog();
                // ActivityUtils.ShareLink(BookSynActivity.this,
                // attachOnes.get(position).getShareUrl());

                UMWeb web = new UMWeb(attachOnes.get(position).getShareUrl());
                web.setTitle(attachOnes.get(position).getAttachOneName());// 标题
                int ic_id = getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
                UMImage thumb = new UMImage(BookSynActivity.this, ic_id);
                thumb.compressStyle = UMImage.CompressStyle.SCALE;// 大小压缩，默认为大小压缩，适合普通很大的图
                thumb.compressStyle = UMImage.CompressStyle.QUALITY;// 质量压缩，适合长图的分享
                thumb.compressFormat = Bitmap.CompressFormat.PNG;// 用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
                web.setThumb(thumb); // 缩略图
                web.setDescription("(分享来自M+Book)");// 描述

                new ShareAction(BookSynActivity.this).withText("")
                        .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA).withMedia(web)
                        .setCallback(umShareListener).open();
            }
        });
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            // 分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            Toast.makeText(BookSynActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(BookSynActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            // Toast.makeText(BookSynActivity.this,platform + " 分享取消了",
            // Toast.LENGTH_SHORT).show();
        }
    };
    ArrayList<AttachOne> list_down;// 同目录下已下载的MP3文件
    ArrayList<AudioItem> list_result;
    int index = 0;// 歌曲播放的位置

    private void unLock(final int position) throws Exception {
        String path = SPUtility.getSPString(this, "mPath");
        String savePath = "copy_" + attachOnes.get(position).getAttachOneSaveName();
        Log.d(TAG, "pathpath=" + path + "-----------" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + "/" + savePath);
        if (TextUtils.isEmpty(path) && path.equals(ActivityUtils.getSDPath(bookId).getAbsolutePath() + "/" + savePath)) {
            Log.d(TAG, "pathpath=" + path + "-----------" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + "/" + savePath);
//            Intent intent = new Intent(this, MediaPlayerActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
            return;
        }
        DialogUtils.showMyDialog(context, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);

        if (attachOnes.get(position).getAttachOneType().equalsIgnoreCase("mp3") || attachOnes.get(position).getAttachOneType().equalsIgnoreCase("wav")) {
            List<AttachOne> list_mp3 = new ArrayList<AttachOne>();// 同目录下全部MP3文件
            for (AttachOne bean : attachOnes) {
                if (bean.getAttachOneType() != null && (bean.getAttachOneType().equalsIgnoreCase("mp3") || bean.getAttachOneType().equalsIgnoreCase("wav")))
                    list_mp3.add(bean);
            }
            list_down = new ArrayList<AttachOne>();// 同目录下已下载的MP3文件
            String[] str1 = ActivityUtils.getSDPath(bookId).list(new MyFilter("mp3"));// 所有已下载的MP3文件
            String[] str2 = ActivityUtils.getSDPath(bookId).list(new MyFilter("wav"));// 所有已下载的wav文件
            int strLen1 = str1.length;// 保存第一个数组长度
            int strLen2 = str2.length;// 保存第二个数组长度
            str1 = Arrays.copyOf(str1, strLen1 + strLen2);// 扩容
            System.arraycopy(str2, 0, str1, strLen1, strLen2);// 将第二个数组与第一个数组合并

            if (str1 != null && str1.length != 0) {
                for (int i = 0; i < list_mp3.size(); i++) {
                    for (int j = 0; j < str1.length; j++) {
                        if (list_mp3.get(i).getAttachOneSaveName().equals(str1[j])) {
                            list_down.add(list_mp3.get(i));
                            break;
                        }
                    }
                }
            }
            Log.d(TAG, "list_down size=" + list_down.size());
            list_result = new ArrayList<AudioItem>();
            if (list_down.size() != 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < list_down.size(); i++) {
                            if (list_down.get(i).getAttachOneId().equals(attachOnes.get(position).getAttachOneId())) {// 确认位置
                                index = i;
                            }

                        }
                        ActivityUtils.unLock(bookId, list_down.get(index).getAttachOneSaveName(), "copy_" + list_down.get(index).getAttachOneSaveName());
                        AudioItem item = new AudioItem();
                        item.setData(ActivityUtils.getSDPath(bookId) + File.separator + "copy_" + list_down.get(index).getAttachOneSaveName());
                        Log.d(TAG, "name====" + list_down.get(index).getAttachOneSaveName());
                        item.setOldData(list_down.get(index).getAttachOnePath() + list_down.get(index).getAttachOneSaveName());
                        item.setBookId(bookId);
                        item.setArtist(list_down.get(index).getAttachOneSaveName());
                        item.setTitle(list_down.get(index).getAttachOneName());
                        list_result.add(item);
                        Message msg = new Message();
                        msg.what = CMD_MP3;
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("list", list_result);
                        bundle.putInt("index", 0);
                        bundle.putString("bookId", bookId);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        } else if (attachOnes.get(position).getAttachOneType().equalsIgnoreCase("zip")) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String name = attachOnes.get(position).getAttachOneSaveName();
                    String file = ActivityUtils.getSDPath(bookId).getAbsolutePath() + "/" + name;
                    Log.i("BookSynActivity", "file=" + file);
                    ActivityUtils.unLock(bookId, name, name + "copy.zip");
                    try {
                        ZipService.unzip(file + "copy.zip", ActivityUtils.getSDPath(bookId).getAbsolutePath());
                    } catch (ZipException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    readFlash(position);
                    DialogUtils.dismissMyDialog();
                }
            }).start();


        } else {
             unLockFile(attachOnes.get(position).getAttachOneSaveName(), "copy_" + attachOnes.get(position).getAttachOneSaveName(), attachOnes.get(position).getAttachOneType(), attachOnes
                    .get(position).getAttachOneName());
        }
    }

    private void readFlash(final int position) {
        String fileName = attachOnes.get(position).getAttachOneName() + "/Index.html";
        ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName());
        ActivityUtils.deleteBookFormSD(bookId, attachOnes.get(position).getAttachOneSaveName() + ".zip");
        ActivityUtils.readFile(context, bookId, fileName, "html", bookName);
    }

    private void downLoad(final int position) {
        if (attachOnes.get(position).getAttachOneType().endsWith("lrc")) {
            downLoadFile(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName(), TEMP + attachOnes.get(position).getAttachOneName() + "." + attachOnes
                    .get(position).getAttachOneType(), attachOnes.get(position).getAttachOneName() + "." + attachOnes.get(position).getAttachOneType());
        } else {
            downLoadFile(attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName(), TEMP + attachOnes.get(position).getAttachOneSaveName(), attachOnes
                    .get(position).getAttachOneName() + "." + attachOnes.get(position).getAttachOneType());
        }
        Log.i("BookSynActivity", attachOnes.get(position).getAttachOnePath() + attachOnes.get(position).getAttachOneSaveName());
        Log.i("BookSynActivity", attachOnes.get(position).getAttachOneSaveName() + "." + attachOnes.get(position).getAttachOneType());
        Log.i("BookSynActivity", attachOnes.get(position).getAttachOneSaveName());
    }

    /**
     * Title: unLockFile Description: 解密文件
     *
     * @param soureFileName 接收到的原路径
     * @param saveFileName  需要保存的路径
     * @param fileType      文件类型
     * @param bookName      书名
     */
    public void unLockFile(final String soureFileName, final String saveFileName, final String fileType, final String bookName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.unLock(bookId, soureFileName, saveFileName);
                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putString("fileName", saveFileName);
                bundle.putString("fileType", fileType);
                bundle.putString("bookName", bookName);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    /**
     * Title: deleteFileAttach Description: 删除解密后的文件
     */
    public void deleteFileAttach() {
        if (null != attachOnes && attachOnes.size() > 0) {
            for (AttachOne attach : attachOnes) {
                if (attach.getAttachOneIspackage() == 0) {
                    ActivityUtils.deleteBookFormSD(bookId, attach.getAttachOneSaveName() + "_copy." + attach.getAttachOneType());
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // 删除解密后的文件
            deleteFileAttach();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(MyPreferences.FRESH_ADAPTER);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 图片加载第一次显示监听器
     *
     * @author Administrator
     */
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public final int CMD_PHOTO = 888;
    public final int CMD_BUY = 999;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case CMD_PHOTO:
                try {
                    ArrayList<PhotoBean.Data> result = data.getParcelableArrayListExtra("flags");
                    if (result != null && result.size() != 0) {
                        if (result.size() == 1) {
                            if (result.get(0).getAttachType().equals(AutoFocusActivity.SHITU))
                                Search(result.get(0).getAttachName(), true);
                            else
                                Search(result.get(0).getAttachName(), false);
                        } else {
                            showResultDialog(result);
                        }
                    } else {
                        ActivityUtils.showToast(BookSynActivity.this, "未查询到相关信息");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "responseInfo=====" + e.getMessage());
                    ActivityUtils.showToast(BookSynActivity.this, "未查询到相关信息");
                }
                break;
            case 1000:
                adapte_fujian.notifyDataSetChanged();
                break;
            case PayChooseActivity.PAY_SUCCESS:
                getNetAttachOne();
                break;
        }
    }

    AttachOne attachOne_Search = null;

    public void Search(String id, boolean isShiTu) {
        DbManager db = MyDbUtils.getOneAttachDb(this);
        StringBuffer sb = null;
        try {
            if (isShiTu)
                attachOne_Search = db.selector(AttachOne.class).where("attach_flag", "=", id).and("bookId", "=", bookId).findFirst();
            else {
                sb = new StringBuffer(id);
                sb.insert(8, "-");
                sb.insert(13, "-");
                sb.insert(18, "-");
                sb.insert(23, "-");
                Log.d(TAG, "resultString====StringBuffer sb = " + sb.toString());
                attachOne_Search = db.selector(AttachOne.class).where("attachOneSaveName", "like", "%" + sb.toString() + "%").and("bookId", "=", bookId).findFirst();
            }
            if (attachOne_Search != null && attachOne_Search.getAttachOneType() != null && attachOne_Search.getAttachOneSaveName() != null) {
                final String attachOneSaveName = attachOne_Search.getAttachOneSaveName();
                final String attachOneName = attachOne_Search.getAttachOneName();
                final String attachOneType = attachOne_Search.getAttachOneType();
                final String path = attachOne_Search.getAttachOnePath();
                String new_name = attachOne_Search.getAttachOneName();
                if (new_name.startsWith("图")) {
                    new_name = new_name.replace("图", "pic");
                }
                Log.i("SearchActivity", "attach=" + attachOneName + "." + attachOneType + "|attachSaveName=" + attachOneSaveName + "------path=" + path);
                if (attachOneType.equalsIgnoreCase("zip") && ActivityUtils.isExistByName(bookId, attachOneName)) {// flash
                    String fileName = attachOneName + File.separator + "Index.html";
                    ActivityUtils.readFile(this, bookId, fileName, "html", bookName);
                } else if (ActivityUtils.isExistByName(bookId, new_name)) {// 3d
                    String filepath = "";
                    if (attachOneType.equals("frame")) {
                        filepath = "file:///android_asset/keyFrame_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                    } else if (attachOneType.equals("skeleton")) {
                        filepath = "file:///android_asset/animation_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                    } else if (attachOneType.equals("blender")) {
                        filepath = "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".html?no_social";
                    }
                    ActivityUtils.readURL(this, bookId, filepath, true);
                } else {
                    if (!ActivityUtils.isExistByName(bookId, attachOneSaveName)) {

                        DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", attachOneName + ",该文件未同步，是否开始同步？", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityUtils.showToast(BookSynActivity.this, "加入下载列表");
                                if (attachOneType.endsWith("lrc")) {
                                    downLoadFile(path + attachOneSaveName, TEMP + attachOneName + "." + attachOneType, attachOneName + "." + attachOneType);
                                } else {
                                    downLoadFile(path + attachOneSaveName, TEMP + attachOneSaveName, attachOneName + "." + attachOneType);
                                }
                                DialogUtils.dismissMyDialog();
                            }
                        });

                    } else {
                        DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
                        unLockFile(attachOneSaveName, "copy_" + attachOneSaveName, attachOneType, attachOneName);
                    }
                }
            } else {
                if (isShiTu)
                    Search2(id, isShiTu);
                else
                    Search2(sb.toString(), isShiTu);
            }
        } catch (Exception e) {
            if (isShiTu)
                Search2(id, isShiTu);
            else
                Search2(sb.toString(), isShiTu);
            e.printStackTrace();
        }

    }

    AttachTwo attachTwo_Search;

    private void Search2(String id, boolean isShiTu) {
        DbManager db2 = MyDbUtils.getTwoAttachDb(this);
        try {
            if (isShiTu)
                attachTwo_Search = db2.selector(AttachTwo.class).where("attchtwo_flag", "=", id).and("bookId", "=", bookId).findFirst();
            else {
                Log.d(TAG, "resultString====StringBuffer sb2222 = " + id);
                attachTwo_Search = db2.selector(AttachTwo.class).where("attachTwoSaveName", "like", "%" + id + "%").and("bookId", "=", bookId).findFirst();
            }
            if (attachTwo_Search != null) {
                final String attachTwoSaveName = attachTwo_Search.getAttachTwoSaveName();
                final String attachTwoName = attachTwo_Search.getAttachTwoName();
                final String attachTwoType = attachTwo_Search.getAttachTwoType();
                final String path = attachTwo_Search.getAttachTwoPath();
                String new_name = attachTwo_Search.getAttachTwoName();
                if (new_name.startsWith("图")) {
                    new_name = new_name.replace("图", "pic");
                }
                Log.i("SearchActivity", "attach=" + attachTwoName + "." + attachTwoType + "|attachSaveName=" + attachTwoSaveName);
                if (attachTwoType.equalsIgnoreCase("zip") && ActivityUtils.isExistByName(bookId, attachTwoName)) {
                    String fileName = attachTwoName + File.separator + "Index.html";
                    ActivityUtils.readFile(this, bookId, fileName, "html", bookName);
                } else if (ActivityUtils.isExistByName(bookId, new_name)) {
                    String filepath = "";
                    if (attachTwoType.equals("frame")) {
                        filepath = "file:///android_asset/keyFrame_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                    } else if (attachTwoType.equals("skeleton")) {
                        filepath = "file:///android_asset/animation_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                    } else if (attachTwoType.equals("blender")) {
                        filepath = "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".html?no_social";
                    }
                    ActivityUtils.readURL(this, bookId, filepath, true);
                } else {
                    if (!ActivityUtils.isExistByName(bookId, attachTwoSaveName)) {
                        DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", attachTwoName + ",该文件未同步，是否开始同步？", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityUtils.showToast(BookSynActivity.this, "加入下载列表");
                                if (attachTwoType.endsWith("lrc")) {
                                    downLoadFile(path + attachTwoSaveName, TEMP + attachTwoName + "." + attachTwoType, attachTwoName + "." + attachTwoType);
                                } else {
                                    downLoadFile(path + attachTwoSaveName, TEMP + attachTwoSaveName, attachTwoName + "." + attachTwoType);
                                }
                                DialogUtils.dismissMyDialog();
                            }
                        });
                        // ActivityUtils.showToastForFail(this,
                        // attachTwoName+"下载之后您就可以观看了呦!");
                        // btn_confirm.setBackgroundResource(search_btn_confirm);
                    } else {
                        DialogUtils.showMyDialog(BookSynActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
                        unLockFile(attachTwoSaveName, "copy_" + attachTwoSaveName, attachTwoType, attachTwoName);
                    }
                }
            } else {
                ActivityUtils.showToast(this, "很遗憾，您搜索的资源不存在!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ActivityUtils.showToast(this, "很遗憾，您搜索的资源不存在!");
        }
    }

    private void showResultDialog(final ArrayList<PhotoBean.Data> result) {
        Dialog dialog = new Dialog(this, R.style.my_dialog);
        dialog.setContentView(R.layout.dialog_scanresult);
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        ScanAdapter adapter = new ScanAdapter(this, result, bookId);
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        // R.layout.item_dialog_scan, R.id.text, ss);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (System.currentTimeMillis() - lastClick <= 1000) {
                    Log.i("onclick", "您点击的太快了");
                    return;
                }
                lastClick = System.currentTimeMillis();
                Search(result.get(position).getAttachName(), true);
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public void btn_search(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("image_url", image_url_str);
        intent.putExtra("bookname", bookName);
        Bundle bundle = new Bundle();
        if (this.bookVO != null) {
            bundle.putSerializable("bookVO", bookVO);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    public void btn_bookInfo(View v) {
        Intent intent = new Intent(this, BookDescriptionActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("image_url", image_url_str);
        intent.putExtra("bookname", bookName);
        Bundle bundle = new Bundle();
        if (this.bookVO != null) {
            bundle.putSerializable("bookVO", bookVO);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    public void author_communication(View v) {
        if (!ActivityUtils.isNetworkAvailable(context)) {
            ActivityUtils.showToast(context, "无法连接网络，加载载失败");
            return;
        }
        Intent intent = new Intent(this, AuthorCommunicationActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("image_url", image_url_str);
        intent.putExtra("bookname", bookName);
        Bundle bundle = new Bundle();
        if (this.bookVO != null) {
            bundle.putSerializable("bookVO", bookVO);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }

    }

    public void reader_communication(View v) {
        if (!ActivityUtils.isNetworkAvailable(context)) {
            ActivityUtils.showToast(context, "无法连接网络，加载载失败");
            return;
        }
        Intent intent = new Intent(this, ReaderCommunicationActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("image_url", image_url_str);
        intent.putExtra("bookname", bookName);
        Bundle bundle = new Bundle();
        if (this.bookVO != null) {
            bundle.putSerializable("bookVO", bookVO);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    public void online_video(View v) {

    }

    @Override
    protected void onDestroy() {

        if (null != broadcastReciver) {
            this.unregisterReceiver(broadcastReciver);
        }
        deleteFileAttach();

        // ActivityUtils.deleteBookFormSD(bookId, bookId+"copy.xml");
        getApplicationContext().removeActivity(this);
        setResult(MyPreferences.FRESH_ADAPTER);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                getNetAttachOne();
                refresh_view.setRefreshing(false);
            }
        }, 2000);

    }

    @Override
    public void onError(int arg0, String arg1) {
        // TODO Auto-generated method stub
        Log.e(TAG, "login disconnected   code===" + arg0 + "------desc=" + arg1);
        DialogUtils.dismissMyDialog();
    }

    @Override
    public void onSuccess() {
        // TODO Auto-generated method stub
        // 初始化消息监听
        MessageEvent.getInstance();

        TIMGroupManager.getInstance().applyJoinGroup(groupId, "some reason", new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                // 接口返回了错误码code和错误描述desc，可用于原因
                // 错误码code列表请参见错误码表
                Log.e(TAG, "disconnected   code===" + code + "------desc=" + desc);
                // 已添加
                if (code == 10013) {
                    TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i(TAG, "quit group");
                            DialogUtils.dismissMyDialog();
                        }

                        @Override
                        public void onError(int arg0, String arg1) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "disconnected   code===" + arg0 + "------desc=" + arg1);
                            DialogUtils.dismissMyDialog();
                        }
                    });
                }
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "join group");

                Intent intent = new Intent(BookSynActivity.this, PlayActivity.class);
                intent.putExtra("bean", zhibobean);
                intent.putExtra("identify", groupId);
                intent.putExtra("type", TIMConversationType.Group);
                intent.putExtra("weburl", webUrl);
                startActivity(intent);
                DialogUtils.dismissMyDialog();
            }
        });
    }

}
