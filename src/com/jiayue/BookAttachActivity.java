package com.jiayue;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.constants.Preferences;
import com.jiayue.download.TestService;
import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.Utils.DownloadManager.DownloadListener;
import com.jiayue.download2.db.DataBaseFiledParams;
import com.jiayue.download2.db.DataBaseHelper;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.dto.base.AttachOne;
import com.jiayue.dto.base.AttachTwo;
import com.jiayue.dto.base.AttachTwoBean;
import com.jiayue.dto.base.AudioItem;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.OrderBean;
import com.jiayue.dto.base.PaperNumBean;
import com.jiayue.model.UserUtil;
import com.jiayue.service.ZipService;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.MyFilter;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.jiayue.view.SwpipeListViewOnScrollListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipException;

import static com.jiayue.util.DialogUtils.dismissMyDialog;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class BookAttachActivity extends BaseActivity implements OnRefreshListener {
    ImageButton btn_lookjd;
    String bookId;
    AttachTwoAdapter adapter;
    String attachOneId;
    private String bookName;
    private String image_url_str;
    AttachTwo attachTwo;
    DownloadManager downloadManager;
    List<AttachTwo> attachTwos;

    private ListView listview;
    private final String TAG = getClass().getSimpleName();

    BroadcastReceiver broadcastReceiver;

    private final int CMD_MP3 = 0x03;
    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    cancleDialog();
                    ActivityUtils.readFile(BookAttachActivity.this, bookId, msg.getData().getString("fileName"), msg.getData().getString("fileType"), msg.getData().getString("bookName"));
                    break;
                case CMD_MP3:
                    cancleDialog();
                    Intent intent = new Intent(BookAttachActivity.this, MediaPlayerActivity.class);
                    intent.putParcelableArrayListExtra("list", msg.getData().getParcelableArrayList("list"));
                    intent.putExtra("index", msg.getData().getInt("index"));
                    // intent.putExtra("bookId",
                    // msg.getData().getString("bookId"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private TextView tv_header_title;
    private String attachName;
    private DbManager db;
    private SwipeRefreshLayout refresh_view;
    private int attachOneIsPay = -1;
    private float attachOneTotalPrice = -1f;
    private View header;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().addActivity(this);
        downloadManager = DownloadManager.getInstance(this);
        setContentView(R.layout.book_attach);
        db = MyDbUtils.getTwoAttachDb(this);
        initView();
        initDownload(this);
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
                // Log.d(TAG,
                // "----callback--------progress=="+info.getDownloadProgress());
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
                docInfo = info;
                if (info.getName().length() > 4) {
                    String substring = info.getName().substring(info.getName().length() - 4);
                    if (!substring.equalsIgnoreCase(".xml")) {
                        ActivityUtils.showToastForSuccess(BookAttachActivity.this, info.getBookName() + "下载完成");
                    }
                }
                if (docInfo.getName().endsWith(info.getBookId() + ".zip") || docInfo.getName().endsWith(".lrc")) {
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
                    }.start();
                }
            }

            public void unZip(DocInfo info) throws ZipException, IOException {
                String bookName = info.getName();
                if (bookName.endsWith(info.getBookId() + ".zip")) {
                    String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
                    ZipService.unzip(file, ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
                    ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
                }
//                else if (bookName.endsWith(".zip")) {
//                    Log.i("BookSynActivity", "bookName=" + bookName);
//                    String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
//                    Log.i("BookSynActivity", "file=" + file);
//                    ActivityUtils.unLock(info.getBookId(), bookName, bookName + "copy.zip");
//                    ZipService.unzip(file + "copy.zip", ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
//                    ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName() + "copy.zip");
//                    ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
//                }
                else if (bookName.endsWith(".lrc")) {
                    Log.i("BookSynActivity", "bookName=" + bookName);
                    ActivityUtils.unLock(info.getBookId(), bookName, bookName + "_copy.lrc");
                }
            }
        });
    }

//    private DocInfo info;

    public void initView() {
        refresh_view = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        bookId = getIntent().getStringExtra("bookId");
        attachOneId = getIntent().getStringExtra("attachOneId");
        attachName = getIntent().getStringExtra("attachName");
        bookName = getIntent().getStringExtra("bookName");
        image_url_str = getIntent().getStringExtra("image_url_str");


        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText(attachName);
        btn_lookjd = (ImageButton) findViewById(R.id.btn_header_right);
        btn_lookjd.setVisibility(View.VISIBLE);
        btn_lookjd.setBackgroundResource(getResources().getIdentifier("booksyn_syn", "drawable", getPackageName()));
        btn_lookjd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookAttachActivity.this, SynManageActivity.class);
                startActivityForResult(intent, 1000);
            }
        });
        try {
            attachTwos = db.selector(AttachTwo.class).where("attachOneId", "=", attachOneId).and("bookId", "=", bookId).findAll();
            //从本地数据库获取是否文件夹付费记录
            DbManager dbManager_attachone = MyDbUtils.getOneAttachDb(this);
            AttachOne attachOne = dbManager_attachone.selector(AttachOne.class).where("attachOneId", "=", attachOneId).and("bookId", "=", bookId).findFirst();
            attachOneIsPay = attachOne.getAttachOneIsPay();
            attachOneTotalPrice = new BigDecimal(Float.toString(attachOne.getAttachOneTotalPrice())).floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }


        listview = (ListView) findViewById(R.id.listview);
        listview.setOnScrollListener(new SwpipeListViewOnScrollListener(refresh_view));
        header = LayoutInflater.from(this).inflate(R.layout.header_attach, null);
        listview.addHeaderView(header);

        adapter = new AttachTwoAdapter(this);
        listview.setAdapter(adapter);
        if (ActivityUtils.isNetworkAvailable(this)) {
            getAttachTwo();
        } else {
            updataContent();
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.test")) {
                    String flag = intent.getStringExtra("flag");
                    Bundle bundle = intent.getBundleExtra("bundle");
                    DocInfo info = (DocInfo) bundle.getSerializable("info");
                    if (flag.equals("success") && isFileDownloaded(info.getName()) && null != adapter) {
                        Log.i("info", info.getName());
//                        if (!ActivityUtils.isExistByName(bookId, info.getBookName())) {
//                            try {
//                                unZip(info);
//                            } catch (ZipException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
                        adapter.notifyDataSetChanged();
                        listview.invalidateViews();
                    }

                    if (flag.equals("update")) {
                        // Log.d(TAG,
                        // "----broadcast--------progress=="+info.getDownloadProgress());
                        adapter.notifyDataSetChanged();
                        System.out.println("info.getDownloadProgress" + info.getDownloadProgress());
                        System.out.println("info.getDownloadProgress finish");
                        return;
                    }

                } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    if (null != intent.getStringExtra("reason") && intent.getStringExtra("reason").equals("homekey")) {
                        deleteAttachOne();
                        return;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.test");
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(broadcastReceiver, filter);
    }

//    public void unZip(DocInfo info) throws ZipException, IOException {
//        String bookName = info.getName();
//        if (bookName.endsWith(info.getBookId() + ".zip")) {
//            String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
//            ZipService.unzip(file, ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
//            ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName());
//        } else if (bookName.endsWith(".zip")) {
//            Log.i("BookSynActivity", "bookName=" + bookName);
//            String file = ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath() + "/" + bookName;
//            Log.i("BookSynActivity", "file=" + file);
//            ActivityUtils.unLock(info.getBookId(), bookName, bookName + "copy.zip");
//            ZipService.unzip(file + "copy.zip", ActivityUtils.getSDPath(info.getBookId()).getAbsolutePath());
//            ActivityUtils.deleteBookFormSD(info.getBookId(), info.getName() + "copy.zip");
//        }
//    }


    public void btnBack(View v) {
        this.finish();
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
            ActivityUtils.showToastForFail(BookAttachActivity.this, "未检测到SD卡,或未赋予应用存储权限");
        }
    }

    private String getPath(String url) {
        String path = url + "&userId=" + UserUtil.getInstance(BookAttachActivity.this).getUserId() + "&bookId=" + bookId;
        return path;
    }

    @Override
    protected void onResume() {
        dismissMyDialog();
        super.onResume();
    }

    private void getAttachTwo() {
        RequestParams params = new RequestParams();
        params.setUri(Preferences.FILE_DOWNLOAD_ATTACH_URL2);
        params.addQueryStringParameter("attachOneId", attachOneId);
        params.addQueryStringParameter("systemType", "android");
        params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
        Log.i("BookSynActivity", "bookId" + this.bookId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<AttachTwoBean>() {
                }.getType();
                AttachTwoBean bookJson = gson.fromJson(s, type);

                if (bookJson.getCode().equals(MyPreferences.SUCCESS) && null != bookJson.getData()) {
                    attachTwos = bookJson.getData().getAttachTwoList();
                    if (attachTwos != null) {
                        try {
                            List<AttachTwo> list = db.selector(AttachTwo.class).where("attachOneId", "=", attachTwos.get(0).getAttachOneId()).and("bookId", "=", bookId).findAll();
                            if (list != null && list.size() > 0) {
                                db.delete(list);
                                db.save(attachTwos);
                            } else {
                                db.save(attachTwos);
                            }
                        } catch (DbException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                    attachOneIsPay = bookJson.getData().getAttachOneIsPay();
                    attachOneTotalPrice = new BigDecimal(Float.toString(bookJson.getData().getAttachOneTotalPrice())).floatValue();
                    try {
                        DbManager dbManager_attachone = MyDbUtils.getOneAttachDb(BookAttachActivity.this);
                        AttachOne attachOne = dbManager_attachone.selector(AttachOne.class).where("attachOneId", "=", attachOneId).and("bookId", "=", bookId).findFirst();
                        attachOne.setAttachOneIsPay(attachOneIsPay);
                        attachOne.setAttachOneTotalPrice(attachOneTotalPrice);
                        dbManager_attachone.delete(attachOne);
                        dbManager_attachone.save(attachOne);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    Log.i("BookAttachActivity", attachTwos.toString());
                    updataContent();

                    return;
                } else if (bookJson.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToast(BookAttachActivity.this, "加载失败," + bookJson.getCodeInfo());
                    return;
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(BookAttachActivity.this, "加载失败,请检查网络！");
                updataContent();
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
     * 更新界面
     */
    public void updataContent() {
        if (attachOneIsPay == 1) {
            ((LinearLayout) header.findViewById(R.id.header_layout)).setVisibility(View.VISIBLE);
            ((TextView) header.findViewById(R.id.text1)).setText(attachName);
            DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
            String geff = df.format(attachOneTotalPrice);//返回的是String类型的
            ((TextView) header.findViewById(R.id.text2)).setText("¥" + geff);
            ((Button) header.findViewById(R.id.button5)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBuyDialog();
                }
            });
        } else {
            listview.removeHeaderView(header);
        }
        adapter.notifyDataSetChanged();
        if (getIntent().getBooleanExtra("isZhiYurJoin", false))
            dismissMyDialog();
    }

    @Override
    protected void onDestroy() {
        if (null != broadcastReceiver) {
            this.unregisterReceiver(broadcastReceiver);
        }
        UMShareAPI.get(this).release();
        deleteAttachOne();
        btn_lookjd.setVisibility(View.GONE);
        getApplicationContext().removeActivity(this);
        super.onDestroy();
    }

    /**
     * 删除解密后的文件
     */
    public void deleteAttachOne() {
        if (null != attachTwo && attachTwos.size() > 0) {
            for (AttachTwo attachTwo : attachTwos) {
                ActivityUtils.deleteBookFormSD(bookId, attachTwo.getAttachTwoId() + "_copy." + attachTwo.getAttachTwoType());
            }
        }
    }

    abstract class MyOnLongClickListener implements OnLongClickListener, OnTouchListener {
    }

    ;

    /**
     * Title: AttachTwoAdapter Description: 附件2适配器 Company: btpd
     *
     * @author Ping Wang
     * @date 2015-8-24
     */
    class AttachTwoAdapter extends BaseAdapter {
        LayoutInflater inflater;
        View view;
        String new_name = "";
        private String[] myPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,};

        public AttachTwoAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public int getCount() {
            return attachTwos == null ? 0 : attachTwos.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.item_fujian2, null);
            }
            final String fujianName = attachTwos.get(position).getAttachTwoName() + "." + attachTwos.get(position).getAttachTwoType();
            new_name = attachTwos.get(position).getAttachTwoName();
            if (new_name.startsWith("图")) {
                new_name = new_name.replace("图", "pic");
            }

            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl_fujian);

            MyOnLongClickListener longClick = new MyOnLongClickListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        tv_header_title.setText(attachName);
                    }
                    return false;
                }

                @Override
                public boolean onLongClick(View v) {
                    tv_header_title.setText(attachTwos.get(position).getAttachTwoName());
                    if (TextUtils.isEmpty(attachTwos.get(position).getShareUrl()) || attachTwos.get(position).getIsSendAtta2().equals("0")) {
                        ActivityUtils.showToastForFail(BookAttachActivity.this, "此文件不能分享！");

                    } else {
                        DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, attachTwos.get(position).getAttachTwoName(), "您要分享此文件给好友吗？", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dismissMyDialog();
                                // ActivityUtils.ShareLink(BookAttachActivity.this,
                                // attachTwos.get(position).getShareUrl());

                                UMWeb web = new UMWeb(attachTwos.get(position).getShareUrl());
                                web.setTitle(attachTwos.get(position).getAttachTwoName());// 标题
                                int ic_id = getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
                                UMImage thumb = new UMImage(BookAttachActivity.this, ic_id);
                                thumb.compressStyle = UMImage.CompressStyle.SCALE;// 大小压缩，默认为大小压缩，适合普通很大的图
                                thumb.compressStyle = UMImage.CompressStyle.QUALITY;// 质量压缩，适合长图的分享
                                thumb.compressFormat = Bitmap.CompressFormat.PNG;// 用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
                                web.setThumb(thumb); // 缩略图
                                web.setDescription("(分享来自加阅)");// 描述

                                new ShareAction(BookAttachActivity.this).withText("")
                                        .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA).withMedia(web)
                                        .setCallback(umShareListener).open();
                            }
                        });
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
                    if (System.currentTimeMillis() - lastClick <= 1000) {
                        Log.i("onclick", "您点击的太快了");
                        return;
                    }
                    if (attachTwos.get(position).getIsPay() == 1)
                        return;

                    lastClick = System.currentTimeMillis();
                    btn_lookjd.setVisibility(View.VISIBLE);

                    new_name = attachTwos.get(position).getAttachTwoName();
                    if (new_name.startsWith("图")) {
                        new_name = new_name.replace("图", "pic");
                    }
                    if (!ActivityUtils.isExistByName(bookId, attachTwos.get(position).getAttachTwoSaveName()) && !ActivityUtils.isExistByName(bookId, attachTwos.get(position).getAttachTwoName()) && !ActivityUtils
                            .isExistByName(bookId, new_name) && !ActivityUtils.isExistByName(bookId, attachTwos.get(position).getAttachTwoName() + "." + attachTwos.get(position).getAttachTwoType())) {
                        if (!ActivityUtils.NetSwitch(BookAttachActivity.this, Boolean.parseBoolean(SPUtility.getSPString(BookAttachActivity.this, "switchKey")))) {
                            ActivityUtils.showToastForFail(BookAttachActivity.this, "请在有WIFI的情况下下载");
                            return;
                        }
                        if (TextUtils.isEmpty(attachTwos.get(position).getShareUrl()) || attachTwos.get(position).getIsSendAtta2().equals("0")) {
//                            DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "文件同步", "该文件未同步，是否开始同步？", new OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
                            Boolean isAllGranted = checkPermissionAllGranted(myPermissions);
                            if (!isAllGranted) {
                                openAppDetails();
                                return;
                            }
                            ActivityUtils.showToast(BookAttachActivity.this, "加入同步列表");
                            if (attachTwos.get(position).getAttachTwoType().endsWith("lrc")) {
                                downLoadFile(attachTwos.get(position).getAttachTwoPath() + attachTwos.get(position).getAttachTwoSaveName(), attachTwos.get(position).getAttachTwoName() + "." + attachTwos
                                        .get(position).getAttachTwoType(), fujianName);
                            } else {
                                downLoadFile(attachTwos.get(position).getAttachTwoPath() + attachTwos.get(position).getAttachTwoSaveName(), attachTwos.get(position).getAttachTwoSaveName(), fujianName);
                            }
//                                    dismissMyDialog();
//                                }
//                            });
                        } else {
                            // String s1 = "暂不支持%s格式文件，";
                            // String s3 = String.format(s1,
                            // attachTwos.get(position).getAttachTwoType());
                            // SpannableString s4 = new
                            // SpannableString("可下载用其他应用打开或返回长按文件分享至电脑打开。");
                            // s4.setSpan(new ForegroundColorSpan(Color.BLUE),
                            // 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                            String content = "长按此文件可分享给您的好友，是否开始同步？";
                            DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_CONFIRM_DIALOG, "分享&同步", content, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityUtils.showToast(BookAttachActivity.this, "加入同步列表");
                                    if (attachTwos.get(position).getAttachTwoType().endsWith("lrc")) {
                                        downLoadFile(attachTwos.get(position).getAttachTwoPath() + attachTwos.get(position).getAttachTwoSaveName(), attachTwos.get(position).getAttachTwoName() + "." + attachTwos
                                                .get(position).getAttachTwoType(), fujianName);
                                    } else {
                                        downLoadFile(attachTwos.get(position).getAttachTwoPath() + attachTwos.get(position).getAttachTwoSaveName(), attachTwos.get(position).getAttachTwoSaveName(), fujianName);
                                    }
                                    dismissMyDialog();
                                }
                            });
                        }

                    } else {
                        DataBaseHelper helper = new DataBaseHelper(getBaseContext());
                        List<DocInfo> infos = helper.getInfo2(bookId);
//                        if (info != null) {
//
//                            if (fujianName.equals(info.getBookName())) {
//                                if (info.getStatus() == DataBaseFiledParams.LOADING) {
//                                    downloadManager.pause(info);
//                                    info.setStatus(DataBaseFiledParams.PAUSING);
//                                } else if (info.getStatus() == DataBaseFiledParams.PAUSING) {
//                                    downloadManager.startForActivity(info);
//                                    info.setStatus(DataBaseFiledParams.LOADING);
//                                }
//                            }
//
//                        }
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

                        adapter.notifyDataSetChanged();

                        // 文件是否正在同步
                        if (downloadManager.isDownloading(attachTwos.get(position))) {
                            ActivityUtils.showToast(BookAttachActivity.this, "该文件正在同步请稍后。");
                        } else {
                            if (attachTwos.get(position).getAttachTwoType().equals("testPaper")) {//试卷
                                Log.d(TAG, "testpaper");
                                showPaperfrequency(attachTwos.get(position).getAttachTwoId(), attachTwos.get(position).getAttachTwoName(), attachTwos.get(position).getPrice(), position);
                            } else if (ActivityUtils.isExistByName(bookId, attachTwos.get(position).getAttachTwoName()) || ActivityUtils.isExistByName(bookId, new_name)) {
                                if (attachTwos.get(position).getAttachTwoType().equals("frame")) {
                                    String filepath = "file:///android_asset/keyFrame_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                                    ActivityUtils.readURL(BookAttachActivity.this, bookId, filepath, true);
                                } else if (attachTwos.get(position).getAttachTwoType().equals("skeleton")) {
                                    String filepath = "file:///android_asset/animation_dae/index.html?model=" + "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".dae";
                                    ActivityUtils.readURL(BookAttachActivity.this, bookId, filepath, true);
                                } else if (attachTwos.get(position).getAttachTwoType().equals("blender")) {
                                    String filepath = "file://" + ActivityUtils.getSDPath(bookId).getAbsolutePath() + File.separator + new_name + File.separator + new_name + ".html?no_social";
                                    ActivityUtils.readURL(BookAttachActivity.this, bookId, filepath, true);
                                } else {
                                    String fileName = attachTwos.get(position).getAttachTwoName() + "/Index.html";
                                    ActivityUtils.deleteBookFormSD(bookId, attachTwos.get(position).getAttachTwoSaveName());
                                    ActivityUtils.deleteBookFormSD(bookId, attachTwos.get(position).getAttachTwoSaveName() + ".zip");
                                    ActivityUtils.readFile(BookAttachActivity.this, bookId, fileName, "html", attachTwos.get(position).getAttachTwoName());
                                }
                            } else {
                                try {
                                    unLock(position);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
            String type = attachTwos.get(position).getAttachTwoType();
            String saveName = attachTwos.get(position).getAttachTwoSaveName();
            // 设置书的封面
            ImageView iv = (ImageView) view.findViewById(R.id.iv_fujian);

            TextView tv_download = (TextView) view.findViewById(R.id.tv_baifen);
            tv_download.setText("");

            DataBaseHelper helper = new DataBaseHelper(getBaseContext());
            List<DocInfo> infos = helper.getInfo2(bookId);
            for (
                    DocInfo docInfo : infos)

            {
                if (fujianName.equals(docInfo.getBookName()) && saveName.equals(docInfo.getName())) {
                    getStateSetView(tv_download, docInfo);
                }
            }
            if (type != null && (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("gif")))

            {
                if (ActivityUtils.isExistByName(bookId, saveName)) {
                    ActivityUtils.unLock(bookId, saveName, "copy_" + saveName);
                    iv.setBackground(Drawable.createFromPath(ActivityUtils.getSDPath(bookId) + File.separator + "copy_" + saveName));
                    // android.view.ViewGroup.LayoutParams layoutParams =
                    // iv.getLayoutParams();
                    // layoutParams.width =
                    // DensityUtil.dip2px(BookAttachActivity.this, 37);
                    // layoutParams.height =
                    // DensityUtil.dip2px(BookAttachActivity.this, 37);
                    // iv.setLayoutParams(layoutParams);

                } else {
                    iv.setBackgroundResource(ActivityUtils
                            .getFilePackageImageId(BookAttachActivity.this, attachTwos.get(position).getAttachTwoIspackage(), attachTwos.get(position).getAttachTwoType()));
                }
            } else

            {
                if (attachTwos.get(position).getAttachTwoType().equals("zip")) {
                    iv.setBackgroundResource(ActivityUtils.getFileImageId(BookAttachActivity.this, "html"));
                } else {
                    iv.setBackgroundResource(ActivityUtils.getFileImageId(BookAttachActivity.this, attachTwos.get(position).getAttachTwoType()));
                    // android.view.ViewGroup.LayoutParams layoutParams =
                    // iv.getLayoutParams();
                    // layoutParams.width =
                    // DensityUtil.dip2px(BookAttachActivity.this, 37);
                    // layoutParams.height =
                    // DensityUtil.dip2px(BookAttachActivity.this, 37);
                    // iv.setLayoutParams(layoutParams);

                }
            }

            // 附件名称
            TextView tv_fujian = (TextView) view.findViewById(R.id.tv_fujian);
            tv_fujian.setText(attachTwos.get(position).

                    getAttachTwoName());
            LinearLayout layout_download = (LinearLayout) view.findViewById(R.id.layout_download);
            LinearLayout layout_buy = (LinearLayout) view.findViewById(R.id.layout_buy);
            if (attachTwos.get(position).

                    getIsPay() == 0)

            {

                layout_download.setVisibility(View.VISIBLE);
                layout_buy.setVisibility(View.GONE);
                // 修饰文件包
                ImageView iv_point_fujian = (ImageView) view.findViewById(R.id.iv_point_fujian);
                if (ActivityUtils.isExistByName(bookId, attachTwos.get(position).getAttachTwoSaveName()) || ActivityUtils.isExistByName(bookId, attachTwos.get(position).getAttachTwoName()) || ActivityUtils
                        .isExistByName(bookId, new_name) || ActivityUtils.isExistByName(bookId, attachTwos.get(position).getAttachTwoName() + "." + attachTwos.get(position).getAttachTwoType())) {
                    iv_point_fujian.setVisibility(View.GONE);
                } else {
                    iv_point_fujian.setVisibility(View.VISIBLE);
                }
                // 设置是否可以分享
                ImageView iv_share = (ImageView) view.findViewById(R.id.imageView1);
                if (TextUtils.isEmpty(attachTwos.get(position).getShareUrl()) || attachTwos.get(position).getIsSendAtta2().equals("0"))
                    iv_share.setVisibility(View.GONE);
                else
                    iv_share.setVisibility(View.VISIBLE);
            } else

            {
                layout_download.setVisibility(View.GONE);
                layout_buy.setVisibility(View.VISIBLE);
                layout_buy.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (attachTwos.get(position).getAttachTwoType().equals("testPaper")) {
                            DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_BUY_DIALOG, "", "请先点击购买解锁本资源！", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    DialogUtils.dismissMyDialog();
                                }

                            });
                        } else {
                            DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_BUY_DIALOG, "", "确认购买解锁本资源？", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    DialogUtils.dismissMyDialog();
                                    getOrder(attachTwos.get(position).getAttachTwoId(), attachTwos.get(position).getAttachTwoName(),
                                            new BigDecimal(Float.toString(attachTwos.get(position).getPrice())).floatValue(), "2");
                                }

                            });
                        }
                    }
                });
            }
            return view;

        }

    }

    //试卷次数
    private void showPaperfrequency(final String attachTwoId, final String attachName, final float attachPrice, final int position) {
        DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        RequestParams params = new RequestParams();
        params.setUri(Preferences.GET_TESTPAPER_NUMBER);
        params.addQueryStringParameter("attachId", attachTwoId);
        params.addQueryStringParameter("userId", UserUtil.getInstance(BookAttachActivity.this).getUserId());
        params.addQueryStringParameter("attachType", "2");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<PaperNumBean>() {
                }.getType();
                PaperNumBean bean = gson.fromJson(s, type);
                dismissMyDialog();
                if (bean.getCode().equals(MyPreferences.SUCCESS)) {
                    if (bean.getData().getResult().equals("true")) {
                        if (bean.getData().getNumber() == -1) {//免费的试卷
                            try {
                                unLock(position);
                            } catch (Exception e) {
                                Log.d(TAG, e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            showCustomDialog("您的试题使用次数还有" + bean.getData().getNumber() + "次，是否进行学习？", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancleDialog();
                                    removePaperNum(attachTwoId, position);
                                }
                            });
                        }
                    } else if (bean.getData().getResult().equals("false")) {
                        showCustomDialog("您的试题使用次数还有" + bean.getData().getNumber() + "次，是否购买学习？", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancleDialog();
//                                getOrder(attachTwoId, attachName, attachPrice,"2");//单独购买次数
                                getOrder(attachOneId, attachName, attachOneTotalPrice, "1");// 整体购买次数
                            }
                        });
                    }
                } else if (bean.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToastForFail(BookAttachActivity.this, "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(BookAttachActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {
                dismissMyDialog();
            }

            @Override
            public void onFinished() {
                dismissMyDialog();
            }
        });
    }

    Dialog dialog;

    private void showCustomDialog(String content, OnClickListener onClickListener) {
        dialog = new Dialog(this, R.style.my_dialog);
        dialog.setContentView(R.layout.dialog_choose_buy);
        Button button12 = (Button) dialog.findViewById(R.id.btn_right);
        Button button13 = (Button) dialog.findViewById(R.id.btn_left);
        TextView textView05 = (TextView) dialog.findViewById(R.id.message);
        textView05.setText(content);
        button12.setOnClickListener(onClickListener);
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleDialog();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showWaitDialog(String message) {
        dialog = new Dialog(this, R.style.my_dialog);
        dialog.setContentView(R.layout.progress);
        int tv_progess = getResources().getIdentifier("tv_progess", "id", getPackageName());
        TextView textView = (TextView) dialog.findViewById(tv_progess);
        textView.setText(Html.fromHtml(message));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void cancleDialog() {
        if (null != dialog) {
            dialog.cancel();
            dialog = null;
        }
    }

    private void removePaperNum(String attachTwoId, final int position) {
        RequestParams params = new RequestParams();
        params.setUri(Preferences.REMOVE_TESTPAPER_NUMBER);
        params.addQueryStringParameter("attachId", attachTwoId);
        params.addQueryStringParameter("userId", UserUtil.getInstance(BookAttachActivity.this).getUserId());
        params.addQueryStringParameter("attachType", "2");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);
//                DialogUtils.dismissMyDialog();
                if (bean.getCode().equals(MyPreferences.SUCCESS)) {
                    try {
                        unLock(position);
                    } catch (Exception e) {
                        Log.d(TAG + "!!!!!", e.getMessage());
                        e.printStackTrace();
                    }
                } else if (bean.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToastForFail(BookAttachActivity.this, "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(BookAttachActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {
                dismissMyDialog();
            }

            @Override
            public void onFinished() {
                dismissMyDialog();
            }
        });
    }

    private void showBuyDialog() {
        DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_BUY_DIALOG, "", "购买本套试题每份试题可使用10次，是否进行学习？", new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismissMyDialog();
//                                getOrder(attachTwos.get(position).getAttachTwoId(), attachTwos.get(position).getAttachTwoName(), attachTwos.get(position).getPrice());
                getOrder(attachOneId, attachName, attachOneTotalPrice, "1");

            }

        });
    }

    private void getOrder(String attachId, final String attachName, final float price, String attachType) {
        DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        RequestParams params = new RequestParams();
        params.setUri(Preferences.GET_ATTACHORDER);
        params.addQueryStringParameter("attachId", attachId);
        params.addQueryStringParameter("systemType", "android");
        params.addQueryStringParameter("userId", UserUtil.getInstance(BookAttachActivity.this).getUserId());
        params.addQueryStringParameter("attachType", attachType);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<OrderBean>() {
                }.getType();
                OrderBean bean = gson.fromJson(s, type);
                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(MyPreferences.SUCCESS) && null != bean.getData()) {
                    Bundle b = new Bundle();
                    b.putString("bookId", bookId);
                    b.putString("bookName", bookName);
                    b.putString("image_url_str", image_url_str);
                    b.putString("attachName", attachName);
                    b.putFloat("price", price);
                    b.putString("ordercode", bean.getData().getAttachCode());
                    Intent intent = new Intent(BookAttachActivity.this, PayChooseActivity.class);
                    intent.putExtra("data", b);
                    startActivityForResult(intent, 0);
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToastForFail(BookAttachActivity.this, "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(BookAttachActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {
                dismissMyDialog();
            }

            @Override
            public void onFinished() {
                dismissMyDialog();
            }
        });
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
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("加阅下载需要访问 “外部存储器”，请到 “设置 -> 应用权限” 中授予！");
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

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            // 分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);

            Toast.makeText(BookAttachActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(BookAttachActivity.this, platform + " 分享失败啦" + "----" + t.getMessage(), Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            // Toast.makeText(BookAttachActivity.this,platform + " 分享取消了",
            // Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000)
            adapter.notifyDataSetChanged();
        else if (resultCode == PayChooseActivity.PAY_SUCCESS)
            getAttachTwo();
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
     * 判断后台发出的下载成功广播是否是当前界面中的附件信息
     *
     * @param fileSDName 保存在sd卡的名称
     * @return true-成功 false-不成功
     */
    public boolean isFileDownloaded(String fileSDName) {
        if (attachTwos != null && attachTwos.size() > 0) {
            for (AttachTwo attachTwo : attachTwos) {
                String fileName = attachTwo.getAttachTwoSaveName();
                if (!TextUtils.isEmpty(fileName) && fileName.equals(fileSDName)) {
                    return true;
                }
            }
        }
        return false;
    }

    ArrayList<AttachTwo> list_down;// 同目录下已下载的MP3文件
    ArrayList<AudioItem> list_result;
    int index = 0;// 歌曲播放的位置

    private void unLock(final int position) throws Exception {
        showWaitDialog("正在加载中，请稍后...");
        if (attachTwos.get(position).getAttachTwoType().equalsIgnoreCase("mp3")
        /* ||attachTwos.get(position).getAttachTwoType().equalsIgnoreCase("wav") */) {
            List<AttachTwo> list_mp3 = new ArrayList<AttachTwo>();// 同目录下全部MP3文件
            for (AttachTwo bean : attachTwos) {
                if (bean.getAttachTwoType() != null && bean.getAttachTwoType().equalsIgnoreCase("mp3") || bean.getAttachTwoType().equalsIgnoreCase("wav"))
                    list_mp3.add(bean);
            }
            list_down = new ArrayList<AttachTwo>();// 同目录下已下载的MP3文件
            String[] str1 = ActivityUtils.getSDPath(bookId).list(new MyFilter("mp3"));// 所有已下载的MP3文件
            String[] str2 = ActivityUtils.getSDPath(bookId).list(new MyFilter("wav"));// 所有已下载的wav文件
            int strLen1 = str1.length;// 保存第一个数组长度
            int strLen2 = str2.length;// 保存第二个数组长度
            str1 = Arrays.copyOf(str1, strLen1 + strLen2);// 扩容
            System.arraycopy(str2, 0, str1, strLen1, strLen2);// 将第二个数组与第一个数组合并

            if (str1 != null && str1.length != 0) {
                for (int i = 0; i < list_mp3.size(); i++) {
                    for (int j = 0; j < str1.length; j++) {
                        if (list_mp3.get(i).getAttachTwoSaveName().equals(str1[j])) {
                            list_down.add(list_mp3.get(i));
                            break;
                        }
                    }
                }
            }
            list_result = new ArrayList<AudioItem>();
            if (list_down.size() != 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < list_down.size(); i++) {
                            if (list_down.get(i).getAttachTwoId().equals(attachTwos.get(position).getAttachTwoId())) {// 确认位置
                                index = i;
                            }
                            ActivityUtils.unLock(bookId, list_down.get(i).getAttachTwoSaveName(), "copy_" + list_down.get(i).getAttachTwoSaveName());
                            AudioItem item = new AudioItem();
                            item.setData(ActivityUtils.getSDPath(bookId) + File.separator + "copy_" + list_down.get(i).getAttachTwoSaveName());
                            item.setTitle(list_down.get(i).getAttachTwoName());
                            list_result.add(item);
                        }
                        Message msg = new Message();
                        msg.what = CMD_MP3;
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("list", list_result);
                        bundle.putInt("index", index);
                        bundle.putString("bookId", bookId);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        } else if (attachTwos.get(position).getAttachTwoType().equalsIgnoreCase("zip") || attachTwos.get(position).getAttachTwoType().equalsIgnoreCase("testPaper")) {
            final String name = attachTwos.get(position).getAttachTwoSaveName();
            final String file = ActivityUtils.getSDPath(bookId).getAbsolutePath() + "/" + name;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ActivityUtils.unLock(bookId, name, name + "copy.zip");
                    try {
                        ZipService.unzip(file + "copy.zip", ActivityUtils.getSDPath(bookId).getAbsolutePath(), new ZipService.onZipSuccess() {
                            @Override
                            public void onZipSuccess() {
                                ActivityUtils.deleteBookFormSD(file + "copy.zip");
                                if (attachTwos.get(position).getAttachTwoType().equalsIgnoreCase("zip"))
                                    ActivityUtils.deleteBookFormSD(file);
                                String fileName = attachTwos.get(position).getAttachTwoName() + (attachTwos.get(position).getAttachTwoType().equalsIgnoreCase("zip") ? "/Index.html" : "/index.html");
                                String type = attachTwos.get(position).getAttachTwoType().equalsIgnoreCase("zip") ? "html" : "testPaper";
                                ActivityUtils.readFile(BookAttachActivity.this, bookId, fileName, type, attachTwos.get(position).getAttachTwoName());
                                cancleDialog();
                            }
                        });

                    } catch (Exception e) {
                        Log.d(TAG + "!!!!!", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            unLockFile(attachTwos.get(position).getAttachTwoSaveName(), "copy_" + attachTwos.get(position).getAttachTwoSaveName(), attachTwos.get(position).getAttachTwoType(), attachTwos
                    .get(position).getAttachTwoName());
        }
    }

    public void unLockFile(final String soureFileName, final String saveFileName, final String fileType, final String bookName) {
        DialogUtils.showMyDialog(BookAttachActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载资源", null);
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

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                getAttachTwo();
                adapter.notifyDataSetChanged();
                refresh_view.setRefreshing(false);
            }
        }, 2000);

    }
}
