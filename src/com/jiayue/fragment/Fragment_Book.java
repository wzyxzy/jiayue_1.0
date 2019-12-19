package com.jiayue.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.BookAttachActivity;
import com.jiayue.BookSynActivity;
import com.jiayue.MediaPlayerActivity;
import com.jiayue.MipcaActivityCapture;
import com.jiayue.R;
import com.jiayue.adapter.BookAdapter;
import com.jiayue.adapter.DragViewAdapter;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.BookVO;
import com.jiayue.dto.base.BookVOBean;
import com.jiayue.dto.base.SiftBean;
import com.jiayue.dto.base.SiftVO;
import com.jiayue.model.UserUtil;
import com.jiayue.service.MusicPlayerService;
import com.jiayue.service.ZipService;
import com.jiayue.sortlistview.DragGridView;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DensityUtil;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.jiayue.view.DragSortGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Book extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View mRootView;

    public String image_url;
    int poisition;
    BookAdapter adapter;// 书架的适配器
    private DragViewAdapter dragViewAdapter;
    //	private ListView lv_books;// 书架的listView
    // private TextView tv_publishName;// 出版社名称
    private DragGridView gridView;
    private List<BookVO> books = new ArrayList<BookVO>();// 书籍列表数据
    private Handler mHandler;
    private int screenWidth;// 屏幕宽
    DisplayImageOptions options;
    String pageNo = "1";// 页码
    String pageSize = "10";// 每页
    String errorMsg;
    private ImageButton music;
    private ImageView btn_add_img;
    private GifImageView music_img;
    private LinearLayout ll_music;
    private final String TAG = getClass().getSimpleName();
    private SwipeRefreshLayout refresh_view;
    private boolean isFirst = true;
    private int index = 0;
    private List<BookVO> books_tmp = new ArrayList<BookVO>();// 书籍列表数据
    private final int CMD_DOWNLOAD = 0x03;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.book, null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int cover_normal = getResources().getIdentifier("cover_normal", "drawable", getActivity().getPackageName());

        options = new DisplayImageOptions.Builder().showStubImage(cover_normal)
                // 设置图片下载期间显示的图片
                .showImageForEmptyUri(cover_normal)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(cover_normal)
                // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
        // initTitle();
        initView();

    }

    /**
     * 初始化界面
     */
    public void initView() {
        screenWidth = ActivityUtils.getScreenWidth(getActivity());
        // gv_book = (GridView) findViewById(R.id.gv_main);

        refresh_view = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

//		lv_books = (ListView) mRootView.findViewById(R.id.xListView);
        gridView = mRootView.findViewById(R.id.gridview);
        adapter = new BookAdapter(books, getContext(), R.layout.a_book_list);
//        dragViewAdapter = new DragViewAdapter(books, getContext(), R.layout.a_book_list);
//        gridView.setDragModel(DragSortGridView.DRAG_BY_LONG_CLICK);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BookSynActivity.class);
                intent.putExtra("bookId", books.get(position).getBookId());
                intent.putExtra("bookName", books.get(position).getBookName());
                intent.putExtra("image_url", Preferences.IMAGE_HTTP_LOCATION + books.get(position).getBookImgPath() + books.get(position).getBookImg());
                intent.putExtra("bookSaveName", books.get(position).getBookSaveName());
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        });

//        gridView.setOnDragSelectListener(new DragSortGridView.OnDragSelectListener() {
//            @Override
//            public void onDragSelect(View mirror) {
//                refresh_view.setEnabled(false);
//            }
//
//            @Override
//            public void onPutDown(View itemView) {
//                refresh_view.setEnabled(true);
//                saveSort();
//            }
//        });
        gridView.setOnChangeListener(new DragGridView.OnChangeListener() {
            @Override
            public void onChange(int from, int to) {
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(books, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(books, i, i - 1);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onStartChange() {
                refresh_view.setEnabled(false);

            }

            @Override
            public void onEndChange() {
                refresh_view.setEnabled(true);
//                gridView.setFocusable(false);
//                gridView.setFocusableInTouchMode(false);
                saveSort();

            }
        });
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
//                Intent intent = new Intent(getActivity(), BookSynActivity.class);
//                intent.putExtra("bookId", books.get(i).getBookId());
//                intent.putExtra("bookName", books.get(i).getBookName());
//                intent.putExtra("image_url", image_url);
//                intent.putExtra("bookSaveName", books.get(i).getBookSaveName());
//                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
////                new MyAdapter.MyClick(books.get(i).getBookId(), books.get(i).getBookName(), image_url, books.get(i).getBookSaveName());
//            }
//        });
        // tv_publishName = (TextView) findViewById(R.id.tv_publishName);
//        music = (ImageButton) mRootView.findViewById(R.id.music);
        btn_add_img = (ImageView) mRootView.findViewById(R.id.btn_add_img);
        music_img = mRootView.findViewById(R.id.music_img);

//        if (MusicPlayerService.)
//        ll_music = (LinearLayout) mRootView.findViewById(R.id.ll_music);

        // Typeface face =
        // Typeface.createFromAsset(getAssets(),"fonts/chinese_travel_model.ttf");
        // tv_publishName.setTypeface(face);
        // }
        mHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        findBooks();
                        break;
                    case 2:
                        adapter.notifyDataSetChanged();
                        refresh_view.setRefreshing(false);
                        if (isFirst) {
                            String result = TextUtils.isEmpty(SPUtility.getSPString(getActivity(), "switchKey")) ? "false" : SPUtility.getSPString(getActivity(), "switchKey");
                            if (ActivityUtils.NetSwitch(getActivity(), Boolean.parseBoolean(result))) {
                                isFirst = false;
                                books_tmp = books;
                                try {
                                    singleDownload();
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }
                        }
                        break;
                    case CMD_DOWNLOAD:
                        try {
                            imageDownload((SiftBean) msg.getData().getSerializable("bean"), msg.getData().getString("id"));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        ((ImageView) mRootView.findViewById(R.id.btn_add_img)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 当前系统大于等于6.0
                    permission();
                } else {
                    // 当前系统小于6.0，直接调用拍照
                    doBackup();
                }
            }
        });
        (mRootView.findViewById(R.id.music_img)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getContext(), MediaPlayerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveSort() {
        StringBuilder bookSort = new StringBuilder();
        for (BookVO book : books) {
            bookSort.append(book.getBookId());
            bookSort.append(",");
        }
        RequestParams params = new RequestParams();
        params.setUri(Preferences.BOOK_LIST_SORT);
        params.addQueryStringParameter("userId", UserUtil.getInstance(getActivity()).getUserId());
        params.addQueryStringParameter("bookSortList", bookSort.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                Bean bean = gson.fromJson(s, Bean.class);
                if (!bean.getCode().equals("SUCCESS")) {
                    Toast.makeText(getContext(), bean.getCodeInfo(), Toast.LENGTH_SHORT).show();
                }
//                Log.e("s", s);
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

    private static final int MY_PERMISSION_REQUEST_CODE = 10;
    private String[] myPermissions = new String[]{Manifest.permission.CAMERA};

    private void permission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(myPermissions);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        Log.d(TAG, "isAllGranted==" + isAllGranted);
        if (isAllGranted) {
            doBackup();
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        Fragment_Book.this.requestPermissions(myPermissions, MY_PERMISSION_REQUEST_CODE);
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
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

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            Log.d(TAG, "isAllGranted222==" + isAllGranted);
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
        Intent intent = new Intent(getActivity(), MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, Activity.RESULT_FIRST_USER);

//		Intent intent = new Intent(getActivity(), WorkWebActivity.class);
//        startActivity(intent);
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("加阅需要访问 “相机”，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            mHandler.sendEmptyMessageDelayed(1, 500);
        } else if (resultCode == MyPreferences.FRESH_ADAPTER) {
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden)
            adapter.notifyDataSetChanged();
    }

    /**
     * 初始化图书列表数据
     */
    private void initBook() {
        if (ActivityUtils.isNetworkAvailable(getActivity()))
            findBooks();
        else {
            getNativeBooks();
        }
    }

    private void singleDownload() {
        Log.d(TAG, "position==========" + index + "------size =" + books_tmp.size());
        if (index >= books_tmp.size()) {
            index = 0;
            return;
        } else {
            if (books_tmp.get(index).getIsImage() == 1)//没有识别图片
                getImageDownload(books_tmp.get(index).getBookId());
        }
    }

    // 获取识别文件信息
    private void getImageDownload(final String bookId) {
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
                Log.d(TAG, "position==========getImageDownload info ====" + s);
                if (bean != null && bean.getData() != null && bean.getCode().equals("SUCCESS")) {
                    if (TextUtils.isEmpty(bean.getData().getZipSavePath())) {
                        index++;
                        singleDownload();
                        return;
                    }
                    File file = new File(ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId);
                    if (!file.exists()) {
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putSerializable("bean", bean);
                        b.putString("id", bookId);
                        msg.setData(b);
                        msg.what = CMD_DOWNLOAD;
                        mHandler.sendMessage(msg);
                        return;
                    }
                    DbManager db = MyDbUtils.getSiftVoDb(getActivity());
                    SiftVO vo = null;
                    try {
                        vo = db.selector(SiftVO.class).where("bookId", "=", bookId).findFirst();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (vo != null && vo.getTime() == bean.getData().getLastTime().getTime()) {
                        index++;
                        singleDownload();
                        return;
                    } else {
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putSerializable("bean", bean);
                        b.putString("id", bookId);
                        msg.setData(b);
                        msg.what = CMD_DOWNLOAD;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    index++;
                    singleDownload();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                index++;
                singleDownload();
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
    private void imageDownload(final SiftBean siftbean, final String bookId) throws Exception {
        // TODO Auto-generated method stub
        final String path = ActivityUtils.getSDLibPath().getAbsolutePath();
        String url = Preferences.FILE_DOWNLOAD_URL + siftbean.getData().getZipSavePath() + "&userId=" + UserUtil.getInstance(getActivity()).getUserId() + "&bookId=" + bookId;

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
                index++;
                singleDownload();
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
                try {
                    ActivityUtils.deletesiftfile(ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId);// 删除原有sift文件夹
                    ZipService.unzip(file.getAbsolutePath(), ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId);// 解压
                    Log.d(TAG, "responseInfo.result.getAbsolutePath()=" + file.getAbsolutePath());
                    ActivityUtils.deletesiftfile(file.getAbsolutePath());// 删除
                    DbManager db = MyDbUtils.getSiftVoDb(getActivity());
                    db.delete(SiftVO.class, WhereBuilder.b("bookId", "=", bookId));// 删除原数据库
                    List<SiftBean.Data.Correspondence> beans = siftbean.getData().getCorrespondence();
                    for (SiftBean.Data.Correspondence bean : beans) {
                        SiftVO vo = new SiftVO();
                        vo.setBookId(bookId);
                        vo.setTime(siftbean.getData().getLastTime().getTime());
                        vo.setConfidence(siftbean.getData().getConfidence());
                        vo.setSiftName(bean.getSiftName());
                        vo.setSiftType(bean.getSiftType());
                        vo.setAttachFlag(bean.getAttachFlag());
                        Log.d(TAG, "bookId=" + bookId + "---bean.getSiftName()=" + bean.getSiftName() + "----ImageName=" + bean.getImageName());
                        vo.setImageName(bean.getImageName());
                        db.save(vo);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                index++;
                singleDownload();
            }
        });
    }

    @Override
    public void onRefresh() {

        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        findBooks();
        // }
        // }).start();
    }

    /**
     * 书架adapter
     *
     * @author Administrator
     */
//    private class MyAdapter extends BaseAdapter {
//
//        private LayoutInflater inflater;
//        View view;
//
//        public MyAdapter(Context context) {
//            inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            if (books.size() <= 9) {
//                return 3;
//            } else {
//                return (books.size() - 1) / 3 + 1;
//            }
//        }
//
//        @Override
//        public Object getItem(int position) {
//
//            List<BookVO> items = new ArrayList<BookVO>();
//            items.add(books.get(position * 3));
//            items.add(books.get(position * 3 + 1));
//            items.add(books.get(position * 3 + 2));
//            return items;
//        }
//
//        @Override
//        public long getItemId(int position) {
//
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            view = inflater.inflate(R.layout.book_item, null);
//            LinearLayout ll_books_item = (LinearLayout) view.findViewById(R.id.ll_books);
//            for (int i = position * 3; i < position * 3 + 3 && i < books.size(); i++) {
//                View view_book = inflater.inflate(R.layout.a_book_list, null);
//                RelativeLayout rl_book = (RelativeLayout) view_book.findViewById(R.id.rl_book1);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((screenWidth - 50) / 3,
//                        /* (screenWidth - 50) / 3 * 220 / 161 */DensityUtil.dip2px(getActivity(), 120));
//                lp.setMargins(5, 0, 5, 0);
//                rl_book.setLayoutParams(lp);
//                // 是否显示云彩
//                ImageView iv_cloud1 = (ImageView) view_book.findViewById(R.id.iv_cloud1);
//                if (ActivityUtils.isExistAndRead(books.get(i).getBookId()) || ActivityUtils.epubIsExistAndRead(books.get(i).getBookId(), books.get(i).getBookSaveName())) {
//                    iv_cloud1.setVisibility(View.GONE);
//                } else {
//                    iv_cloud1.setVisibility(View.VISIBLE);
//                }
//                // 书籍封面获取
//                ImageView imageView01 = (ImageView) view_book.findViewById(R.id.iv_book1);
//                image_url = Preferences.IMAGE_HTTP_LOCATION + books.get(i).getBookImgPath() + books.get(i).getBookImg();
//
//                Log.i("图片地址", image_url);
//                String bookId = books.get(i).getBookId();
//                // String bookSaveName = books.get(i).getBookSaveName();
//                if (ActivityUtils.isNetworkAvailable(getActivity())) {
//                    SPUtility.putSPString(getActivity(), bookId, image_url);
//                }
//
//                if (ActivityUtils.isExistByName(books.get(i).getBookId(), books.get(i).getBookName() + ".jpg")) {
//                    ImageLoader.getInstance().displayImage("file://" + ActivityUtils.getSDPath(books.get(i).getBookId()) + File.separator + books.get(i).getBookName() + ".jpg", imageView01, options);
//                } else {
//                    ImageLoader.getInstance().displayImage(SPUtility.getSPString(getActivity(), bookId), imageView01, options);
//                }
//
//                Button btn_delete = (Button) view_book.findViewById(R.id.btn_delete1);
//                Button btn_cancel = (Button) view_book.findViewById(R.id.btn_cancel);
//                ll_books_item.addView(view_book);
//
//                rl_book.setOnClickListener(new MyClick(books.get(i).getBookId(), books.get(i).getBookName(), image_url, books.get(i).getBookSaveName()));
//
//                if (ActivityUtils.isNetworkAvailable(getActivity())) {
//
//                    rl_book.setOnLongClickListener(new MyLongClick(btn_delete,
//                            /* btn_cancel, */getActivity(), i));
//
//                }
//
//                // 判断是否可以直播
//                if (books.get(i).getIsPlay() == 0) {
//                    btn_cancel.setVisibility(View.INVISIBLE);
//                } else {
//                    btn_cancel.setVisibility(View.VISIBLE);
//                }
//
//            }
//            if (books.size() == 0 || (books.size() <= 6 && position > (books.size() - 1) / 3)) {
//                ll_books_item.setPadding(0, /*
//                         * (screenWidth - 50) / 3 * 198 /
//                         * 161
//                         */
//                        DensityUtil.dip2px(getActivity(), 110), 0, 0);
//                return view;
//            }
//            return view;
//        }
//
//        /**
//         * 点击事件
//         *
//         * @author Administrator
//         */
//        class MyClick implements OnClickListener {
//            // int ispackage;
//            String bookid;
//            String bookName;
//            String image_url;
//            String bookSaveName;
//
//            public MyClick(String bookid, String bookName) {
//                this.bookid = bookid;
//                this.bookName = bookName;
//
//            }
//
//            public MyClick(String bookId, String bookName, String image_url, String bookSaveName) {
//                this(bookId, bookName);
//                this.image_url = image_url;
//                this.bookSaveName = bookSaveName;
//            }
//
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), BookSynActivity.class);
//                intent.putExtra("bookId", bookid);
//                intent.putExtra("bookName", bookName);
//                intent.putExtra("image_url", image_url);
//                intent.putExtra("bookSaveName", bookSaveName);
//                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
//                // }
//            }
//
//        }
//
//        /**
//         * 长按
//         *
//         * @author Administrator 事件
//         */
//        class MyLongClick implements OnLongClickListener {
//            Button btn_delete;
//            // Button btn_cancel;
//            Context context;
//            int location;
//
//            public MyLongClick(Button btn_delete, /* Button btn_cancel, */
//                               Context context, int location) {
//                // this.btn_cancel = btn_cancel;
//                this.btn_delete = btn_delete;
//                this.context = context;
//                this.location = location;
//            }
//
//            @Override
//            public boolean onLongClick(View v) {
//                // btn_cancel.setVisibility(View.VISIBLE);
//                // btn_cancel.setOnClickListener(new OnClickListener() {
//
//                // @Override
//                // public void onClick(View v) {
//                // btn_delete.setVisibility(View.GONE);
//                // btn_cancel.setVisibility(View.GONE);
//                // }
//                // });
//                btn_delete.setVisibility(View.VISIBLE);
//                btn_delete.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        btn_delete.setVisibility(View.GONE);
//                        // btn_cancel.setVisibility(View.GONE);
//                        DialogUtils.showMyDialog(context, MyPreferences.SHOW_CONFIRM_DIALOG, "删除书籍", "你确定永久删除此书籍,删除后将不可恢复？", new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                DialogUtils.dismissMyDialog();
//                                deleteBook(location);
//                            }
//                        });
//                    }
//                });
//                return true;
//            }
//
//        }
//
//    }

    /**
     * 数据库获取本地书籍
     */
    private void getNativeBooks() {
        DbManager db = MyDbUtils.getBookVoDb(getActivity());
        List<BookVO> list = null;
        try {
            list = db.selector(BookVO.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            books.clear();
            books.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 查询书架列表
     */
    public void findBooks() {

        RequestParams params = new RequestParams(Preferences.BOOKLIST_URL);
        params.addQueryStringParameter("userId", UserUtil.getInstance(getActivity()).getUserId());
        params.addQueryStringParameter("pageNo", pageNo);
        params.addQueryStringParameter("pageSize", pageSize);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<BookVOBean>() {
                }.getType();
                BookVOBean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    ImageLoader.getInstance().clearDiscCache();
                    ImageLoader.getInstance().clearMemoryCache();

                    books.clear();
                    books.addAll(bean.getData());
                    Log.d(TAG, "----------books size=" + books.size());
//                    Collections.reverse(books);// 反向
                    updateBooksData();
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } else {
                    ActivityUtils.showToastForFail(getActivity(), "加载失败," + bean.getCodeInfo());
                    refresh_view.setRefreshing(false);
                    getNativeBooks();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToastForFail(getActivity(), "未连接网络");
                refresh_view.setRefreshing(false);
                getNativeBooks();
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
     * 删除图书
     *
     * @param location
     */
    public void deleteBook(final int location) {

        RequestParams params = new RequestParams(Preferences.DELETE_BOOK_URL);
        params.addQueryStringParameter("userId", UserUtil.getInstance(getActivity()).getUserId());
        params.addQueryStringParameter("bookId", books.get(location).getBookId());
        final String id = books.get(location).getBookId();

        DialogUtils.showMyDialog(getActivity(), MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在删除中...", null);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);
                DialogUtils.dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ActivityUtils.deletefile(id);
                        }
                    }).start();
                    books.remove(location);
//                    lv_books.postInvalidate();
                    if (books != null) {
                        updateBooksData();
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    DialogUtils.showMyDialog(getActivity(), MyPreferences.SHOW_ERROR_DIALOG, "删除失败", bean.getCodeInfo(), null);
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(getActivity(), "删除书籍失败");
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
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(SPUtility.getSPString(getContext(), "isPlay")) && SPUtility.getSPString(getContext(), "isPlay").equals("true")) {
            music_img.setImageResource(R.drawable.music_play2);
        } else {
            music_img.setImageResource(R.drawable.music_right);

        }
        initBook();
    }

    /**
     * 更新图书数据
     *
     * @param
     */
    public void updateBooksData() {

        if (books != null) {
            DbManager db = MyDbUtils.getBookVoDb(getActivity());

            try {
//                List<BookVO> bookVOS = db.findAll(BookVO.class);
//                for (int i = 0; i < books.size(); i++) {
//                    for (int i1 = 0; i1 < bookVOS.size(); i1++) {
//                        if (books.get(i).getBookId().equals(bookVOS.get(i1).getBookId())) {
//                            books.get(i).setSort(bookVOS.get(i1).getSort());
//                        }
//                    }
//                }
                db.delete(BookVO.class);
                db.save(books);
//                Collections.sort(books, new Comparator<BookVO>() {
//                    @Override
//                    public int compare(BookVO o1, BookVO o2) {
//                        return o1.getSort() - o2.getSort();
//                    }
//                });
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

}
