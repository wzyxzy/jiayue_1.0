package com.jiayue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.adapter.GoodsSettingAdapter;
import com.jiayue.adapter.MyViewPagerAdapter;
import com.jiayue.chat.ChatAdapter;
import com.jiayue.chat.ImagePreviewActivity;
import com.jiayue.chat.ListPickerDialog;
import com.jiayue.chat.ProfileSummaryAdapter;
import com.jiayue.chat.customer.ChatInput;
import com.jiayue.chat.customer.VoiceSendingView;
import com.jiayue.chat.model.CustomMessage;
import com.jiayue.chat.model.GroupMemberProfile;
import com.jiayue.chat.model.ImageMessage;
import com.jiayue.chat.model.Message;
import com.jiayue.chat.model.MessageFactory;
import com.jiayue.chat.model.ProfileSummary;
import com.jiayue.chat.model.TextMessage;
import com.jiayue.chat.model.VideoMessage;
import com.jiayue.chat.model.VoiceMessage;
import com.jiayue.chat.presenter.ChatPresenter;
import com.jiayue.chat.presenter.ChatView;
import com.jiayue.chat.util.FileUtil;
import com.jiayue.chat.util.MediaUtil;
import com.jiayue.chat.util.RecorderUtil;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.GoodsAddressAllBean;
import com.jiayue.dto.base.GoodsDetailBean;
import com.jiayue.dto.base.GoodsFapiaoAllBean;
import com.jiayue.dto.base.GoodsSettingListBean;
import com.jiayue.dto.base.OrderBean;
import com.jiayue.dto.base.ZhiboInfoBean;
import com.jiayue.fragment.Fragment_Good1;
import com.jiayue.fragment.Fragment_Good2;
import com.jiayue.fragment.Fragment_Good3;
import com.jiayue.fragment.Fragment_Good4;
import com.jiayue.fragment.Fragment_Good5;
import com.jiayue.model.UserUtil;
import com.jiayue.rest.LastOrNextListener;
import com.jiayue.rest.OnRefreshAdapterListener;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DensityUtil;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.GoodsDetailUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.jiayue.view.CustomViewpager;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import cn.jiguang.net.HttpUtils;

public class PlayActivity extends FragmentActivity implements OnClickListener, LastOrNextListener, OnRefreshAdapterListener, ITXLivePlayListener, ChatView, TIMValueCallBack<List<TIMGroupMemberInfo>> {
    private final String TAG = "PlayActivity";
    private TextView title;
    private LinearLayout layout_title, layout, layout_viewpager, layout_webchat;
    private LinearLayout layout_listview;
    private ZhiboInfoBean bean;
    private WebView mWebView;

    // private SwipeRefreshLayout refresh_view;
    private Button btn_all, btn_goods, btn_setting, btn_quiet;
    private Context context = this;

    private final int CHOOSE_CHAT = 0;
    private final int CHOOSE_GOODS = 1;
    private final int CHOOSE_SETTING = 2;
    private final int CHOOSE_QUIET = 3;

    private int choose = CHOOSE_CHAT;

    private CustomViewpager viewpager;
    private MyViewPagerAdapter myAdapter;

    private GoodsSettingAdapter settingAdapter;
    private LinearLayout headview;
    private Fragment f1, f2, f3, f4, f5;
    private ListView listView;
    private String webUrl;

    private List<Message> messageList = new ArrayList<Message>();
    private ChatAdapter adapter;
    private ListView listView_chat;
    private ChatPresenter presenter;
    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int IMAGE_PREVIEW = 400;
    private Uri fileUri;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;

    ProfileSummaryAdapter quietAdapter;
    List<ProfileSummary> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 界面唤醒状态
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        setContentView(R.layout.activity_play);

        bean = (ZhiboInfoBean) getIntent().getSerializableExtra("bean");
        webUrl = getIntent().getStringExtra("weburl");

        if (bean == null || bean.getCode().equals("FAIL") || bean.getData() == null) {
            ActivityUtils.showToastForFail(this, "直播信息获取失败！");
            finish();
        }
        getDefaultData();
        initChat();
        initVideo();
        initViewPager();
        initSetting();
        initQuiet();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
			permission();
        } else {
            //当前系统小于6.0，直接调用拍照
          doBackup();
        }
    }
    
    private static final int MY_PERMISSION_REQUEST_CODE = 245;
	private String[] myPermissions = new String[] {
            Manifest.permission.RECORD_AUDIO
    };
	
	private void permission(){
		/**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
        		myPermissions);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
        	doBackup();
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                this,
                myPermissions,
                MY_PERMISSION_REQUEST_CODE
        );
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
    @SuppressLint("NewApi")
	@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
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
                doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 第 4 步: 后续操作
     */
    private void doBackup() {
        // 本文主旨是讲解如果动态申请权限, 具体备份代码不再展示, 就假装备份一下
    }
    
    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("M+Book需要访问 “麦克风”，请到 “设置 -> 应用权限” 中授予！");
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

    private final int CMD_REFRESH = 0x01;
    private final int CMD_TISHI = 0x02;
    private final int CMD_XIADAN = 0x03;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CMD_TISHI:
                    DialogUtils.dismissMyDialog();
                    ActivityUtils.showToastForFail(PlayActivity.this, "抱歉，未获取到资源，请退出后重试，谢谢");
                    break;
                case CMD_XIADAN:
                    viewpager.setCurrentItem(0);
                    break;
                default:
                    break;
            }
        }
    };

    private void initChatRoom() {
        // TODO Auto-generated method stub
        identify = getIntent().getStringExtra("identify");
        final TIMConversationType type = (TIMConversationType) getIntent().getSerializableExtra("type");
        presenter = new ChatPresenter(this, identify, type);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);
        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        listView_chat = (ListView) findViewById(R.id.list);
        listView_chat.setAdapter(adapter);
        listView_chat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView_chat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        listView_chat.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    // 如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView_chat);
        // TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
        // switch (type) {
        // case C2C:
        // title.setMoreImg(R.drawable.btn_person);
        // if (FriendshipInfo.getInstance().isFriend(identify)){
        // title.setMoreImgAction(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
        // intent.putExtra("identify", identify);
        // startActivity(intent);
        // }
        // });
        // FriendProfile profile =
        // FriendshipInfo.getInstance().getProfile(identify);
        // title.setTitleText(profile == null ? identify : profile.getName());
        // }else{
        // title.setMoreImgAction(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // Intent person = new
        // Intent(ChatActivity.this,AddFriendActivity.class);
        // person.putExtra("id",identify);
        // person.putExtra("name",identify);
        // startActivity(person);
        // }
        // });
        // title.setTitleText(identify);
        // }
        // break;
        // case Group:
        // title.setMoreImg(R.drawable.btn_group);
        // title.setMoreImgAction(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // Intent intent = new Intent(ChatActivity.this,
        // GroupProfileActivity.class);
        // intent.putExtra("identify", identify);
        // startActivity(intent);
        // }
        // });
        // title.setTitleText(GroupInfo.getInstance().getGroupName(identify));
        // break;
        //
        // }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        presenter.start();
    }

    private void initChat() {
        // TODO Auto-generated method stub
        btn_all = (Button) findViewById(R.id.button2);
        btn_all.setOnClickListener(this);

        layout_webchat = (LinearLayout) findViewById(R.id.layout_webchat);
        // mWebView = (WebView) findViewById(R.id.webchat);

        // initWebChat();
        initChatRoom();
    }

    // private void initWebChat() {
    // WebSettings ws = mWebView.getSettings();
    // ws.setJavaScriptEnabled(true);
    // ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
    //
    // // 是否允许缩放
    // ws.setBuiltInZoomControls(true);
    // ws.setSupportZoom(true);
    // mWebView.setWebChromeClient(new MyWebChromeClient());
    //
    // mWebView.setWebViewClient(new MyWebViewClient(this));
    //
    // mWebView.setDownloadListener(new DownloadListener() {
    // @Override
    // public void onDownloadStart(String url, String userAgent, String
    // contentDisposition, String mimetype, long contentLength) {
    // if (url != null && url.startsWith("http://"))
    // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    // }
    // });
    // String groudId = getIntent().getStringExtra("groudId");
    // Log.d(TAG, "groudId===" + groudId);
    // groudId = groudId.replace("#", "%23");
    // String url =
    // "http://101.200.179.47:8080/AVChatRoom/AVChatRoomAction?UserId=" +
    // UserUtil.getInstance(this).getUserName() + "&groupId=" + groudId +
    // "&expert=" + getIntent()
    // .getStringExtra("expert");
    // //
    // http:182.92.103.51:8080/AVChatRoom/AVChatRoomAction?UserId=王亮&groupId=@TGS%23aF7XTEEEI&expert=成长123
    // // for(int i = 0;i<40;i++){
    // mWebView.loadUrl(url);
    // // }
    // Log.d(TAG, url);
    // }

    private void initViewPager() {
        // refresh_view = (SwipeRefreshLayout)
        // findViewById(R.id.swipeRefreshLayout);
        // refresh_view.setOnRefreshListener(this);
        // refresh_view.setColorSchemeResources(android.R.color.holo_blue_light,
        // android.R.color.holo_red_light, android.R.color.holo_orange_light,
        // android.R.color.holo_green_light);
        btn_goods = (Button) findViewById(R.id.button3);
        btn_goods.setOnClickListener(this);
        layout_viewpager = (LinearLayout) findViewById(R.id.layout_viewpager);
        viewpager = (CustomViewpager) findViewById(R.id.viewpager);

        f1 = new Fragment_Good1();
        ((Fragment_Good1) f1).setLastOrNextListener(this);
        Bundle b = new Bundle();
        b.putInt("courseId", bean.getData().getCourseId());
        f1.setArguments(b);
        f2 = new Fragment_Good2();
        ((Fragment_Good2) f2).setLastOrNextListener(this);
        f3 = new Fragment_Good3();
        ((Fragment_Good3) f3).setLastOrNextListener(this);
        f4 = new Fragment_Good4();
        ((Fragment_Good4) f4).setLastOrNextListener(this);
        f5 = new Fragment_Good5();
        ((Fragment_Good5) f5).setLastOrNextListener(this);
        List<Fragment> list = new LinkedList<>();
        list.add(f1);
        list.add(f2);
        list.add(f3);
        list.add(f4);
        list.add(f5);
        myAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), list);
        viewpager.setAdapter(myAdapter);


    }

    private void initSetting() {
        headview = (LinearLayout) findViewById(R.id.head);
        listView = (ListView) findViewById(R.id.listview);
        btn_setting = (Button) findViewById(R.id.button4);
        btn_setting.setOnClickListener(this);
        layout_listview = (LinearLayout) findViewById(R.id.layout_listview);

        Log.d(TAG, "getUserStatus===" + UserUtil.getInstance(this).getUserStatus());
        if (UserUtil.getInstance(this).getUserStatus() != 2)
            btn_setting.setVisibility(View.GONE);
        else {
            btn_setting.setVisibility(View.VISIBLE);

            settingAdapter = new GoodsSettingAdapter(this, null, bean.getData().getCourseId(), this);
        }
    }

    /***********************
     * 禁言
     *************************/
    private void initQuiet() {
        btn_quiet = (Button) findViewById(R.id.button5);
        btn_quiet.setOnClickListener(this);

        if (bean.getData().getAdmin() == 1) {
            btn_quiet.setVisibility(View.VISIBLE);

            quietingOpt = new String[]{getString(R.string.group_member_quiet_cancel)};
            quietOpt = new String[]{getString(R.string.group_member_quiet_ten_min),
                    getString(R.string.group_member_quiet_one_hour),
                    getString(R.string.group_member_quiet_one_day),
            };

            quietAdapter = new ProfileSummaryAdapter(this, R.layout.item_profile_summary, list);
        } else {
            btn_quiet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        list.clear();
        if (timGroupMemberInfos == null) return;
        for (TIMGroupMemberInfo item : timGroupMemberInfos) {
            list.add(new GroupMemberProfile(item));
        }
        quietAdapter.notifyDataSetChanged();
    }

    private String[] quietingOpt;
    private String[] quietOpt;
    private long[] quietTimeOpt = new long[]{600, 3600, 24 * 3600};
    private GroupMemberProfile profile;

    private String[] getQuietOption() {
        if (!isQuiet()) {
            return quietOpt;
        } else {
            return quietingOpt;
        }
    }

    private long getQuietTime(int which) {
        if (!isQuiet()) {
            return quietTimeOpt[which];
        }
        return 0;
    }

    private boolean isQuiet() {
        if (profile == null) return false;
        return profile.getQuietTime() != 0 && profile.getQuietTime() > Calendar.getInstance().getTimeInMillis() / 1000;
    }

    /***********************
     * 禁言
     *************************/
    // 获取默认数据并保存
    private void getDefaultData() {

        RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_GETADDRESS);
        params.addQueryStringParameter("user.userId", UserUtil.getInstance(this).getUserId());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<GoodsAddressAllBean>() {
                }.getType();
                GoodsAddressAllBean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    GoodsDetailBean detail = GoodsDetailUtils.getInstance(PlayActivity.this).getBean();
                    if (!TextUtils.isEmpty(bean.getData().getAddress()))
                        detail.setAddress(bean.getData().getAddress());
                    // else
                    // detail.setAddress(UserUtil.getInstance(PlayActivity.this).getAddress1());
                    if (!TextUtils.isEmpty(bean.getData().getName()))
                        detail.setName(bean.getData().getName());
                    // else
                    // detail.setName(UserUtil.getInstance(PlayActivity.this).getParents());
                    if (!TextUtils.isEmpty(bean.getData().getTelephone()))
                        detail.setTelephone(bean.getData().getTelephone());
                    else
                        detail.setTelephone(UserUtil.getInstance(PlayActivity.this).getUserName());
                    if (!TextUtils.isEmpty(bean.getData().getId() + ""))
                        detail.setReceiverId(bean.getData().getId());

                    GoodsDetailUtils.getInstance(PlayActivity.this).setBean(detail);
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

        RequestParams params1 = new RequestParams(Preferences.ZHIBO_GOODS_GETFAPIAO);
        params1.addQueryStringParameter("user.userId", UserUtil.getInstance(this).getUserId());

        x.http().post(params1, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<GoodsFapiaoAllBean>() {
                }.getType();
                GoodsFapiaoAllBean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    GoodsDetailBean detail = GoodsDetailUtils.getInstance(PlayActivity.this).getBean();
                    if (!TextUtils.isEmpty(bean.getData().getId() + ""))
                        detail.setFapiaoId(bean.getData().getId());
                    if (!TextUtils.isEmpty(bean.getData().getInvoiceTitle()))
                        detail.setFapiao(bean.getData().getInvoiceTitle());

                    GoodsDetailUtils.getInstance(PlayActivity.this).setBean(detail);
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

    boolean isLoadMore = false;

    // time=0为全部数据
    // private void loadData(long time) {
    // isLoadMore = false;
    // RequestParams params = new RequestParams();
    // params.put("courseId", bean.getData().getCourseId());
    // // params.put("userId", userId);
    // if (time != 0) {
    // isLoadMore = true;
    // params.put("time", list_all.get(list_all.size() -
    // 1).getAddTime().getTime());
    // Log.d(TAG, "time==========" + list_all.get(list_all.size() -
    // 1).getAddTime().getTime());
    // }
    //
    // // Log.d(TAG, Preferences.GET_CHATLIST + "&courseId=" +
    // bean.getData().getCourseId());
    //
    // HttpUtil.post(Preferences.GET_CHATLIST, params, new
    // AsyncHttpResponseHandler() {
    //
    // @Override
    // public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
    // // TODO Auto-generated method stub
    // String s = new String(arg2);
    // Log.d(TAG, s);
    // Gson gson = new Gson();
    // java.lang.reflect.Type type = new TypeToken<ChatAllBean>() {
    // }.getType();
    // chatbean = gson.fromJson(s, type);
    //
    // mHandler.sendMessage(mHandler.obtainMessage(CMD_REFRESH, isLoadMore));
    // isRefresh = false;
    // }
    //
    // @Override
    // public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable
    // arg3) {
    // // TODO Auto-generated method stub
    // ActivityUtils.showToastForFail(context, "获取信息失败");
    // isRefresh = false;
    // // if(refresh_view.isRefreshing())
    // // refresh_view.setRefreshing(false);
    // }
    // });
    // }

    private void loadSettingData() {

        RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_SETTINGLIST);
        params.addQueryStringParameter("user.userId", UserUtil.getInstance(this).getUserId());
        params.addQueryStringParameter("courseId", bean.getData().getCourseId() + "");

        HttpUtils http = new HttpUtils();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<GoodsSettingListBean>() {
                }.getType();
                GoodsSettingListBean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    settingAdapter.setList(bean.getData());
                    settingAdapter.notifyDataSetChanged();
                } else {
                    ActivityUtils.showToastForFail(context, "获取信息失败");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(context, "获取信息失败");
                isRefresh = false;
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /***********************************************
     * video
     ********************************************************************/

    private String mRtmpUrl;

    private TXLivePlayer mLivePlayer = null;
    private boolean mVideoPlay;
    private TXCloudVideoView mPlayerView;
    private ImageView mLoadingView;
    private Button mBtnPlay;
    private Button mBtnRenderRotation;
    private Button mBtnShare;
    private SeekBar mSeekBar;
    private TextView mTextDuration;
    private TextView mTextStart;
    static final int ACTIVITY_TYPE_PUBLISH = 1;
    static final int ACTIVITY_TYPE_LIVE_PLAY = 2;
    static final int ACTIVITY_TYPE_VOD_PLAY = 3;

    protected int mActivityType = ACTIVITY_TYPE_LIVE_PLAY;

    private static final int CACHE_STRATEGY_FAST = 1; // 极速
    private static final int CACHE_STRATEGY_SMOOTH = 2; // 流畅
    private static final int CACHE_STRATEGY_AUTO = 3; // 自动

    private static final int CACHE_TIME_FAST = 1;
    private static final int CACHE_TIME_SMOOTH = 5;

    private static final int CACHE_TIME_AUTO_MIN = 5;
    private static final int CACHE_TIME_AUTO_MAX = 10;

    private int mCacheStrategy = 0;

    private int mCurrentRenderMode;
    private int mCurrentRenderRotation;

    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    private boolean mVideoPause = false;
    private int mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private TXLivePlayConfig mPlayConfig;
    private long mStartPlayTS = 0;

    private LinearLayout layout_hidden;


    int play_pause;
    int play_start;

    private void initVideo() {
        // TODO Auto-generated method stub
        title = (TextView) findViewById(R.id.title);
        layout_title = (LinearLayout) findViewById(R.id.layout_title);
        layout = (LinearLayout) findViewById(R.id.layout);
        title.setText(bean.getData().getCourseName());
        layout_hidden = (LinearLayout) findViewById(R.id.layout_hidden);
        mPlayerView = (TXCloudVideoView) findViewById(R.id.video_view);
        mLoadingView = (ImageView) findViewById(R.id.loadingImageView);
        layout = (LinearLayout) findViewById(R.id.layout);

        play_pause = getResources().getIdentifier("play_pause", "drawable", getPackageName());
        play_start = getResources().getIdentifier("play_start", "drawable", getPackageName());

        mVideoPlay = false;

        if (mLivePlayer == null) {
            mLivePlayer = new TXLivePlayer(this);
        }

        mBtnPlay = (Button) findViewById(R.id.btnPlay);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mBtnShare = (Button) findViewById(R.id.btnShare);

        mCurrentRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;

        mPlayConfig = new TXLivePlayConfig();

        mRtmpUrl = "";

        mTextDuration = (TextView) findViewById(R.id.duration);
        mTextStart = (TextView) findViewById(R.id.play_start);
        mTextDuration.setTextColor(Color.rgb(255, 255, 255));
        mTextStart.setTextColor(Color.rgb(255, 255, 255));

        this.setCacheStrategy(CACHE_STRATEGY_AUTO);

        View progressGroup = findViewById(R.id.play_progress);

        if (ActivityUtils.isNetworkAvailable(this)) {
            if (ActivityUtils.is3rd(this) && Boolean.parseBoolean(SPUtility.getSPString(this, "switchKey"))) {

            } else if (ActivityUtils.is3rd(this) && !Boolean.parseBoolean(SPUtility.getSPString(this, "switchKey"))) {
                DialogUtils.showMyDialog(this, MyPreferences.SHOW_ERROR_DIALOG, "网络未连接", "请在wifi状态下观看，或在设置中设置3G/4G网络", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });
            } else {

            }
        } else {
            DialogUtils.showMyDialog(this, MyPreferences.SHOW_ERROR_DIALOG, "网络不给力", "请检查网络！", new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
        }

        Log.e(TAG, "getRtmp_downstream_address====" + bean.getData().getRtmp_downstream_address());
        if (bean.getData() != null && !TextUtils.isEmpty(bean.getData().getRtmp_downstream_address())) {
            mRtmpUrl = bean.getData().getRtmp_downstream_address();
        } else {
            ActivityUtils.showToastForFail(PlayActivity.this, "抱歉，未获取到资源，请退出后重试，谢谢");
            return;
        }

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click playbtn isplay:" + mVideoPlay + " ispause:" + mVideoPause + " playtype:" + mPlayType);
                if (mVideoPlay) {
                    if (mPlayType == TXLivePlayer.PLAY_TYPE_VOD_FLV || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_HLS || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_MP4) {
                        if (mVideoPause) {
                            mLivePlayer.resume();
                            mBtnPlay.setBackgroundResource(play_pause);
                        } else {
                            mLivePlayer.pause();
                            mBtnPlay.setBackgroundResource(play_start);
                        }
                        mVideoPause = !mVideoPause;

                    } else {
                        stopPlayRtmp();
                        mVideoPlay = !mVideoPlay;
                    }

                } else {
                    if (startPlayRtmp()) {
                        mVideoPlay = !mVideoPlay;
                    }
                }
            }
        });

        // 分享网络播放器
        mBtnShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                UMWeb web = new UMWeb(webUrl);
                web.setTitle(bean.getData().getCourseName());// 标题
				int ic_id = getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
                UMImage thumb = new UMImage(PlayActivity.this, ic_id);
                thumb.compressStyle = UMImage.CompressStyle.SCALE;// 大小压缩，默认为大小压缩，适合普通很大的图
                thumb.compressStyle = UMImage.CompressStyle.QUALITY;// 质量压缩，适合长图的分享
                thumb.compressFormat = Bitmap.CompressFormat.PNG;// 用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
                web.setThumb(thumb); // 缩略图
                web.setDescription(bean.getData().getCourseDisc());// 描述

                new ShareAction(PlayActivity.this).withText("")
                        .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA).withMedia(web)
                        .setCallback(umShareListener).open();
            }
        });

        // 横屏|竖屏
        // mBtnRenderRotation = (Button) findViewById(R.id.btnOrientation);
        // mBtnRenderRotation.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // if (mLivePlayer == null) {
        // return;
        // }
        //
        // if (mCurrentRenderRotation ==
        // TXLiveConstants.RENDER_ROTATION_PORTRAIT) {
        // mBtnRenderRotation.setBackgroundResource(R.drawable.portrait);
        // mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_LANDSCAPE;
        // mPlayerView.setLayoutParams(new
        // RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        // } else if (mCurrentRenderRotation ==
        // TXLiveConstants.RENDER_ROTATION_LANDSCAPE) {
        // mBtnRenderRotation.setBackgroundResource(R.drawable.landscape);
        // mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;
        // mPlayerView.setLayoutParams(new
        // RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,260*3));
        // }
        //
        // mLivePlayer.setRenderRotation(mCurrentRenderRotation);
        // }
        // });

        // //平铺模式
        // mBtnRenderMode = (Button) view.findViewById(R.id.btnRenderMode);
        // mBtnRenderMode.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // if (mLivePlayer == null) {
        // return;
        // }
        //
        // if (mCurrentRenderMode ==
        // TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN) {
        // mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        // mBtnRenderMode.setBackgroundResource(R.drawable.fill_mode);
        // mCurrentRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
        // } else if (mCurrentRenderMode ==
        // TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) {
        // mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        // mBtnRenderMode.setBackgroundResource(R.drawable.adjust_mode);
        // mCurrentRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
        // }
        // }
        // });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                mTextStart.setText(String.format("%02d:%02d", progress / 60, progress % 60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mLivePlayer != null) {
                    mLivePlayer.seek(seekBar.getProgress());
                }
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });

        // 直播不需要进度条，点播不需要缓存策略
        if (mActivityType == ACTIVITY_TYPE_LIVE_PLAY) {
            progressGroup.setVisibility(View.INVISIBLE);
        } else if (mActivityType == ACTIVITY_TYPE_VOD_PLAY) {
        }

        if (mVideoPlay) {
            if (mPlayType == TXLivePlayer.PLAY_TYPE_VOD_FLV || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_HLS || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_MP4) {
                if (mVideoPause) {
                    mLivePlayer.resume();
                    mBtnPlay.setBackgroundResource(play_pause);
                } else {
                    mLivePlayer.pause();
                    mBtnPlay.setBackgroundResource(play_start);
                }
                mVideoPause = !mVideoPause;

            } else {
                stopPlayRtmp();
                mVideoPlay = !mVideoPlay;
            }

        } else {
            if (startPlayRtmp()) {
                mVideoPlay = !mVideoPlay;
            }
        }

        // 进入记录
        record(LIVE_ENTER);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            // 分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);

            Toast.makeText(PlayActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(PlayActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
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

    public void btnBack(View view) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                layout_title.setVisibility(View.VISIBLE);
                layout.setPadding(DensityUtil.dip2px(PlayActivity.this, 13), DensityUtil.dip2px(PlayActivity.this, 7), DensityUtil.dip2px(PlayActivity.this, 13), DensityUtil
                        .dip2px(PlayActivity.this, 7));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);

        // 退出聊天界面时输入框有内容，保存草稿
        if (input.getText().length() > 0) {
            TextMessage message = new TextMessage(input.getText());
            presenter.saveDraft(message.getMessage());
        } else {
            presenter.saveDraft(null);
        }
        // RefreshEvent.getInstance().onRefresh();
        presenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    public void setCacheStrategy(int nCacheStrategy) {
        if (mCacheStrategy == nCacheStrategy)
            return;
        mCacheStrategy = nCacheStrategy;

        switch (nCacheStrategy) {
            case CACHE_STRATEGY_FAST:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_SMOOTH:
                mPlayConfig.setAutoAdjustCacheTime(false);
                mPlayConfig.setCacheTime(CACHE_TIME_SMOOTH);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_AUTO:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_AUTO_MAX);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_AUTO_MIN);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            default:
                break;
        }
    }

    public void onConfigurationChanged(Configuration paramConfiguration) { // 响应系统方向改变
        if (paramConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            layout_title.setVisibility(View.GONE);
            layout_hidden.setVisibility(View.GONE);
            layout.setPadding(0, 0, 0, 0);
            mPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            layout_title.setVisibility(View.VISIBLE);
            layout_hidden.setVisibility(View.VISIBLE);
            layout.setPadding(DensityUtil.dip2px(PlayActivity.this, 13), DensityUtil.dip2px(PlayActivity.this, 7), DensityUtil.dip2px(PlayActivity.this, 13), DensityUtil.dip2px(PlayActivity.this, 7));
            mPlayerView.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 180)));
        }
        super.onConfigurationChanged(paramConfiguration); // 必有，否则运行会出现异常
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
        }
        if (mPlayerView != null) {
            mPlayerView.onDestroy();
        }
        // 记录退出
        record(LIVE_EXIT);
        presenter.stop();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPlayType == TXLivePlayer.PLAY_TYPE_VOD_FLV || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_HLS || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_MP4) {
            if (mLivePlayer != null) {
                mLivePlayer.pause();
            }
        } else {
            stopPlayRtmp();
        }

        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        if (mVideoPlay && !mVideoPause) {
            if (mPlayType == TXLivePlayer.PLAY_TYPE_VOD_FLV || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_HLS || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_MP4) {
                if (mLivePlayer != null) {
                    mLivePlayer.resume();
                }
            } else {
                startPlayRtmp();
            }
        }

        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
    }

    private boolean checkPlayUrl(final String playUrl) {
        Log.d(TAG, "playUrl===" + playUrl);
        if (TextUtils.isEmpty(playUrl) || (!playUrl.startsWith("http://") && !playUrl.startsWith("https://") && !playUrl.startsWith("rtmp://"))) {
            Toast.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (mActivityType) {
            case ACTIVITY_TYPE_LIVE_PLAY: {
                if (playUrl.startsWith("rtmp://")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
                } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".flv")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
                } else {
                    Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            break;
            case ACTIVITY_TYPE_VOD_PLAY: {
                if (playUrl.startsWith("http://") || playUrl.startsWith("https://")) {
                    if (playUrl.contains(".flv")) {
                        mPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
                    } else if (playUrl.contains(".m3u8")) {
                        mPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
                    } else if (playUrl.toLowerCase().contains(".mp4")) {
                        mPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
                    } else {
                        Toast.makeText(getApplicationContext(), "播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            break;
            default:
                Toast.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
                return false;
        }
        return true;
    }

    private boolean startPlayRtmp() {
        String playUrl = mRtmpUrl;

        if (!checkPlayUrl(playUrl)) {
            return false;
        }

        int[] ver = TXLivePlayer.getSDKVersion();
        // if (ver != null && ver.length >= 3) {
        // mLogMsg.append(String.format("rtmp sdk version:%d.%d.%d ", ver[0],
        // ver[1], ver[2]));
        // mLogViewEvent.setText(mLogMsg);
        // }
        mBtnPlay.setBackgroundResource(play_pause);

        mLivePlayer.setPlayerView(mPlayerView);
        mLivePlayer.setPlayListener(this);

        // 硬件加速在1080p解码场景下效果显著，但细节之处并不如想象的那么美好：
        // (1) 只有 4.3 以上android系统才支持
        // (2) 兼容性我们目前还仅过了小米华为等常见机型，故这里的返回值您先不要太当真
        // mLivePlayer.enableHardwareDecode(mHWDecode);
        mLivePlayer.setRenderRotation(mCurrentRenderRotation);
        mLivePlayer.setRenderMode(mCurrentRenderMode);
        // 设置播放器缓存策略
        // 这里将播放器的策略设置为自动调整，调整的范围设定为1到4s，您也可以通过setCacheTime将播放器策略设置为采用
        // 固定缓存时间。如果您什么都不调用，播放器将采用默认的策略（默认策略为自动调整，调整范围为1到4s）
        // mLivePlayer.setCacheTime(5);
        mLivePlayer.setConfig(mPlayConfig);

        int result = mLivePlayer.startPlay(playUrl, mPlayType); // result返回值：0
        // success; -1
        // empty url; -2
        // invalid url;
        // -3 invalid
        // playType;
        if (result == -2) {
            Toast.makeText(getApplicationContext(), "非腾讯云链接地址，若要放开限制，请联系腾讯云商务团队", Toast.LENGTH_SHORT).show();
        }
        if (result != 0) {
            mBtnPlay.setBackgroundResource(play_start);
            return false;
        }

        // mLivePlayer.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);

        startLoadingAnimation();

        mStartPlayTS = System.currentTimeMillis();
        return true;
    }

    private void stopPlayRtmp() {
        mBtnPlay.setBackgroundResource(play_start);
        stopLoadingAnimation();
        if (mLivePlayer != null) {
            mLivePlayer.setPlayListener(null);
            mLivePlayer.stopPlay(true);
        }
    }

    @Override
    public void onNetStatus(Bundle status) {
        Log.d(TAG, "Current status: " + status.toString());
        // if (mLivePlayer != null){
        // mLivePlayer.onLogRecord("[net state]:\n"+str+"\n");
        // }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            stopLoadingAnimation();
            Log.d("AutoMonitor", "PlayFirstRender,cost=" + (System.currentTimeMillis() - mStartPlayTS));
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
            long curTS = System.currentTimeMillis();

            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
            }
            if (mTextStart != null) {
                mTextStart.setText(String.format("%02d:%02d", progress / 60, progress % 60));
            }
            if (mTextDuration != null) {
                mTextDuration.setText(String.format("%02d:%02d", duration / 60, duration % 60));
            }
            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
            return;
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlayRtmp();
            mVideoPlay = false;
            mVideoPause = false;
            if (mTextStart != null) {
                mTextStart.setText("00:00");
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                DialogUtils.showMyDialog(context, MyPreferences.SHOW_SUCCESS_DIALOG, "aaaaaaa", "ceshiceshi", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        DialogUtils.dismissMyDialog();
                    }
                });
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {
            startLoadingAnimation();
        }

        if (event < 0) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            stopLoadingAnimation();
        }
    }

    private void startLoadingAnimation() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
            ((AnimationDrawable) mLoadingView.getDrawable()).start();
        }
    }

    private void stopLoadingAnimation() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
            ((AnimationDrawable) mLoadingView.getDrawable()).stop();
        }
    }

    // 记录直播时长
    // enter 0是进入 1是退出
    private final int LIVE_ENTER = 0;
    private final int LIVE_EXIT = 1;

    private void record(int enter) {
        RequestParams params = new RequestParams(Preferences.LIVE_RECORD);
        params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
        params.addQueryStringParameter("courseId", bean.getData().getCourseId() + "");
        params.addQueryStringParameter("playType", enter + "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    Log.d(TAG, "LIVE_RECORD SUCCESS");
                } else {
                    Log.d(TAG, "LIVE_RECORD FAIL");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.d(TAG, "LIVE_RECORD FAIL");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**********************************************************************************************************************/

    long lastClick = 0;
    boolean isRefresh = false;
    boolean isFirstFragment1 = true;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button2:
                if (choose == CHOOSE_CHAT)
                    return;
                choose = CHOOSE_CHAT;
                changeView(choose);
                break;
            case R.id.button3:
                if (choose == CHOOSE_GOODS)
                    return;
                choose = CHOOSE_GOODS;
                changeView(choose);
                if (isFirstFragment1) {
                    isFirstFragment1 = false;
                } else {
                    ((Fragment_Good1) f1).refreshFragment1();
                }
                back();
                break;
            case R.id.button4:
                if (choose == CHOOSE_SETTING)
                    return;
                choose = CHOOSE_SETTING;
                changeView(choose);
                listView.setAdapter(settingAdapter);
                loadSettingData();
                listView.setOnItemClickListener(null);
                break;
            case R.id.button5:
                if (choose == CHOOSE_QUIET)
                    return;
                choose = CHOOSE_QUIET;
                changeView(choose);
                listView.setAdapter(quietAdapter);
                TIMGroupManager.getInstance().getGroupMembers(identify, this);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        profile = (GroupMemberProfile) list.get(position);
                        new ListPickerDialog().show(getQuietOption(), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                TIMGroupManager.getInstance().modifyGroupMemberInfoSetSilence(identify, profile.getIdentify(), getQuietTime(which),
                                        new TIMCallBack() {
                                            @Override
                                            public void onError(int i, String s) {
                                                Toast.makeText(context, /*getString(R.string.group_member_quiet_err)*/s, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onSuccess() {
//                                                if (getQuietTime(which) == 0) {
//                                                    setQuiet.setContent("");
//                                                } else {
//                                                    setQuiet.setContent(getString(R.string.group_member_quiet_ing));
//                                                }
                                                profile.setQuietTime(getQuietTime(which) + Calendar.getInstance().getTimeInMillis() / 1000);
                                            }
                                        });
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    private void changeView(int choose) {
        btn_all.setBackground(getResources().getDrawable(choose == CHOOSE_CHAT ? R.drawable.green_btn : R.color.transparent));
        btn_goods.setBackground(getResources().getDrawable(choose == CHOOSE_GOODS ? R.drawable.green_btn : R.color.transparent));
        btn_setting.setBackground(getResources().getDrawable(choose == CHOOSE_SETTING ? R.drawable.green_btn : R.color.transparent));
        btn_quiet.setBackground(getResources().getDrawable(choose == CHOOSE_QUIET ? R.drawable.green_btn : R.color.transparent));

        btn_all.setTextColor(getResources().getColor(choose == CHOOSE_CHAT ? R.color.white : R.color.zhibo_black2));
        btn_goods.setTextColor(getResources().getColor(choose == CHOOSE_GOODS ? R.color.white : R.color.zhibo_black2));
        btn_setting.setTextColor(getResources().getColor(choose == CHOOSE_SETTING ? R.color.white : R.color.zhibo_black2));
        btn_quiet.setTextColor(getResources().getColor(choose == CHOOSE_QUIET ? R.color.white : R.color.zhibo_black2));

        layout_webchat.setVisibility(choose == CHOOSE_CHAT ? View.VISIBLE : View.GONE);
        layout_listview.setVisibility(choose == CHOOSE_SETTING || choose == CHOOSE_QUIET ? View.VISIBLE : View.GONE);
        layout_viewpager.setVisibility(choose == CHOOSE_GOODS ? View.VISIBLE : View.GONE);

        headview.setVisibility(choose == CHOOSE_SETTING ? View.VISIBLE : View.GONE);
    }

    // private ArrayList<ChatBean> list_all = new ArrayList<>();
    // private ArrayList<ChatBean> list_mine = new ArrayList<>();

    // 商品上下步选择 支付接口
    @Override
    public void last() {
        if (viewpager.getCurrentItem() - 1 == 1)
            ((Fragment_Good2) f2).refreshFragment2();
        viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
    }

    @Override
    public void next() {
        if (viewpager.getCurrentItem() + 1 == 1)
            ((Fragment_Good2) f2).refreshFragment2();
        if (viewpager.getCurrentItem() + 1 == 3)
            ((Fragment_Good4) f4).refreshFragment4();
        viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
    }

    @Override
    public void charge() {
        final GoodsDetailBean bean = GoodsDetailUtils.getInstance(this).getBean();

        RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_PAY);
        params.addQueryStringParameter("code", bean.getCode());
        if (bean.isFapiao()) {
            params.addQueryStringParameter("invoice.id", bean.getFapiaoId() + "");
            params.addQueryStringParameter("invoice.invoiceTitle", bean.getFapiao());
        } else {
            params.addQueryStringParameter("invoice.id", "");
            params.addQueryStringParameter("invoice.invoiceTitle", "");
        }
        params.addQueryStringParameter("receiverInfo.id", bean.getReceiverId() + "");
        params.addQueryStringParameter("receiverInfo.name", bean.getName());
        params.addQueryStringParameter("receiverInfo.address", bean.getAddress());
        params.addQueryStringParameter("receiverInfo.telephone", bean.getTelephone());
        params.addQueryStringParameter("comment", bean.getBeizhu());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<OrderBean>() {
                }.getType();
                OrderBean bean1 = gson.fromJson(s, type);

                if (bean1 != null && bean1.getCode().equals("SUCCESS")) {
                    // Intent intent = new Intent(PlayActivity.this,
                    // PayChooseActivity.class);
                    // Bundle b = new Bundle();
                    // b.putString(PaySubmitActivity.ORDER_PRICE,
                    // bean1.getData().getTotalPrice());
                    // b.putString(PaySubmitActivity.ORDER_CODE,
                    // bean1.getData().getOrderCode());
                    // b.putString(PaySubmitActivity.ORDER_URL,
                    // bean1.getData().getNotify_url());
                    // b.putString(PaySubmitActivity.ORDER_NAME,
                    // bean.getTitle());
                    // b.putString(PaySubmitActivity.ORDER_BODY, "益智玩具");
                    // intent.putExtra(PaySubmitActivity.ORDER, b);
                    // startActivityForResult(intent, 99);
                    // ActivityUtils.showToastForSuccess(context,"已成功下单");
                    // mHandler.sendEmptyMessageDelayed(CMD_XIADAN,1000);
                    viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
                } else {
                    ActivityUtils.showToastForFail(context, "订单提交失败");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(PlayActivity.this, "订单提交失败");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void back() {
        viewpager.setCurrentItem(0);
        GoodsDetailBean bean = GoodsDetailUtils.getInstance(this).getBean();
        bean.setBeizhu("");
        GoodsDetailUtils.getInstance(this).setBean(bean);
    }

    @Override
    public void onRefreshAdapter() {
        loadSettingData();
    }

    // ///////////////////////////////////////////////////////聊天室

    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (mMessage instanceof CustomMessage) {
                    CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
                    switch (messageType) {
                        case TYPING:
                            // TemplateTitle title = (TemplateTitle)
                            // findViewById(R.id.chat_title);
                            // title.setTitleText(getString(R.string.chat_typing));
                            // handler.removeCallbacks(resetTitle);
                            // handler.postDelayed(resetTitle,3000);
                            break;
                        default:
                            break;
                    }
                } else {
                    if (messageList.size() == 0) {
                        mMessage.setHasTime(null);
                    } else {
                        mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    listView_chat.setSelection(adapter.getCount() - 1);
                }
            }
        }

    }

    /**
     * 显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                continue;
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING || ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID))
                continue;
            ++newMsgNum;
            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                messageList.add(0, mMessage);
            } else {
                mMessage.setHasTime(null);
                messageList.add(0, mMessage);
            }
        }
        adapter.notifyDataSetChanged();
        listView_chat.setSelection(newMsgNum);
    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList) {
            if (msg.getMessage().getMsgUniqueId() == id) {
                switch (code) {
                    case 80001:
                        // 发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        Message message = new TextMessage(input.getText());
        presenter.sendMessage(message.getMessage());
        input.setText("");
    }

    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage());
        }
    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C) {
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()) {
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        }
        if (message instanceof ImageMessage) {
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                break;
            case 3:
                message.save();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && fileUri != null) {
                showImagePreview(fileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()));
            }

        }
//        else if (requestCode == FILE_CODE) {
//            if (resultCode == RESULT_OK) {
//                sendFile(FileUtil.getFilePath(this, data.getData()));
//            }
//        } 
        else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists() && file.length() > 0) {
                    if (file.length() > 1024 * 1024 * 10) {
                        Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                    } else {
                        Message message = new ImageMessage(path, isOri);
                        presenter.sendMessage(message.getMessage());
                    }
                } else {
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path) {
//        if (path == null) return;
//        File file = new File(path);
//        if (file.exists()){
//            if (file.length() > 1024 * 1024 * 10){
//                Toast.makeText(this, getString(R.string.chat_file_too_large),Toast.LENGTH_SHORT).show();
//            }else{
//                Message message = new FileMessage(path);
//                presenter.sendMessage(message.getMessage());
//            }
//        }else{
//            Toast.makeText(this, getString(R.string.chat_file_not_exist),Toast.LENGTH_SHORT).show();
//        }

    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
//            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
//            title.setTitleText(titleStr);
        }
    };

    private void sendImage(String path) {
        if (path == null)
            return;
        File file = new File(path);
        if (file.exists()) {
            if (file.length() > 1024 * 1024 * 10) {
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                Message message = new ImageMessage(path);
                presenter.sendMessage(message.getMessage());
            }
        } else {
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void sendFile() {
        // TODO Auto-generated method stub

    }
}
