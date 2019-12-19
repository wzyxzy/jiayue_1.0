package com.jiayue;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jiayue.adapter.AudioAdapter;
import com.jiayue.adapter.MusicListAdapter;
import com.jiayue.adapter.MusicListAdapter2;
import com.jiayue.download2.Utils.DownloadManager;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.dto.base.AudioItem;
import com.jiayue.dto.base.BookVO;
import com.jiayue.dto.base.MusicListBean;
import com.jiayue.lrcview.DefaultLrcParser;
import com.jiayue.lrcview.LrcRow;
import com.jiayue.lrcview.LrcView;
import com.jiayue.model.MusicList;
import com.jiayue.service.IMusicPlayerService;
import com.jiayue.service.MusicPlayerService;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DensityUtil;
import com.jiayue.util.LyricUtils;
import com.jiayue.util.MusicUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.SPUtility;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MediaPlayerActivity extends AppCompatActivity {

    private String path = "";
    private String bookName;
    private TextView start_time;
    private SeekBar seekBar_audio;
    private TextView end_time;
    private LinearLayout btn_pre;
    private ImageView bac;
    private ImageButton btn_play;
    private LinearLayout btn_next;
    private LinearLayout btn_list;
    private TextView tv_header_title;
    private ImageButton btn_header_right;
    private int mediaplayer_pause;
    private int mediaplayer_play;
    protected static final int PROGRESS = 1;
    protected static final int LYRIC_SHOW = 2;

    private LyricUtils lyricUtils;


//    private LrcView mLrcView;
    /**
     * 服务代理对象
     */
    private com.jiayue.service.IMusicPlayerService service;
    /**
     * 在音频列表中播放那一个
     */
    private int currentPosition;

    private MyBroadcastReceiver receiver;
    private boolean isStoped = false;
    /**
     * true:表示进入这个Activity状态栏进来 flase:播放列表进来
     */
    private boolean from_notification;
    private boolean isBack;
    private MusicUtils utils;
    private List<AudioItem> audioList = new ArrayList<AudioItem>();
    private int currentIndex;
    private Dialog dialog;
    private AudioAdapter adapter;
    private LinearLayout btn_status;
    private ImageButton iv_mode;
    int mode_one;
    int mode_all;
    int mode_random;
    int mode = MusicPlayerService.REPEATE_NORMAL;
    private MusicListAdapter musicListAdapter;
    private MusicListAdapter2 musicListAdapter2;
    private ListView music_list;
    private List<MusicList> musicLists;
    private List<MusicListBean> musicListBeans;
    private List<MusicListBean> musicListBeansCache;
    private DbManager db;

    BroadcastReceiver broadcastReciver;
    DownloadManager downloadManager;


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS:
                    // 得到当前播放进度
                    try {
                        int currentPosition = service.getCurrentPositon();
//                        mLrcView.seekTo(currentPosition, true, true);
                        seekBar_audio.setProgress(currentPosition);
                        seekBar_audio.setMax(service.getDuration());
                        start_time.setText(utils.stringForTime(service.getCurrentPositon()));
                        end_time.setText(utils.stringForTime(service.getDuration()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!isStoped) {
                        handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    }

                    break;
                case LYRIC_SHOW:
                    // int currentTime;
                    // try {
                    // currentTime = service.getCurrentPositon();
                    // // mLrcView.setShowNextLyric(currentTime);
                    // } catch (RemoteException e) {
                    // e.printStackTrace();
                    // }
                    if (!isStoped) {
                        handler.removeMessages(LYRIC_SHOW);
                        // showLyric();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载解码器
        // if (!Vitamio.isInitialized(this))
        // return;

        setContentView(R.layout.activity_music_player);

//		if (getIntent().getStringExtra("filePath") != null && (!getIntent().getStringExtra("filePath").equals(""))) {
//			path = getIntent().getStringExtra("filePath");
//			SPUtility.putSPString(this, "mPath", path);
//		} else {
//			path = SPUtility.getSPString(this, "mPath");// 下拉栏进去
//		}

//		if (getIntent().getStringExtra("bookId") != null) {
//			bookId = getIntent().getStringExtra("bookId");
//			SPUtility.putSPString(this, "bookId", bookId);
//		} else {
//			bookId = SPUtility.getSPString(this, "bookId");
//		}
        initView();
        startService();
        getData();
        setOnlistener();
        initDownload();

    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // int getmCurRow = mLrcView.getmCurRow();
            switch (v.getId()) {
                case R.id.btn_pre:
                    // mLrcView.setmCurRow(getmCurRow-1);
                    try {
                        service.pre();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case R.id.btn_next:

                    // mLrcView.setmCurRow(getmCurRow+1);
                    try {
                        service.next();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case R.id.btn_play:// 播放和暂停

                    if (service != null) {
                        try {
                            if (service.isPlaying()) {
                                service.pause();
                            } else {
                                service.play();
                            }
                            setPlayOrPauseStatus();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_list:
//                    showDialog();
                    addMusicToList();
                    break;
                default:
                    break;
            }

        }
    };


    /**
     * 设置download监听
     *
     * @param
     */
    public void initDownload() {


        broadcastReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.test")) {
                    String flag = intent.getStringExtra("flag");
                    Bundle bundle = intent.getBundleExtra("bundle");
                    DocInfo info = (DocInfo) bundle.getSerializable("info");
//                    ActivityUtils.showToastForSuccess(MediaPlayerActivity.this, info.getBookName() + "下载完成");
                    switch (flag) {
                        case "success":
                            Log.d("audioaudioaudio", info.toString());
                            ActivityUtils.unLock(info.getBookId(), info.getName(), "copy_" + info.getName());
//                            try {
//                                Thread.sleep(2000);

//                            currentIndex=;
                            try {
                                service.openAudio(service.getAudioPosition());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
//
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            audioLists.get(currentPosition).setData(ActivityUtils.getSDPath(audioLists.get(currentPosition).getBookId()) + File.separator + "copy_" + audioLists.get(currentPosition).getArtist());
                            break;
                        case "update":
                            break;
                        case "failed":
                            ActivityUtils.showToastForFail(MediaPlayerActivity.this, "下载失败，请检查网络！");
                            try {
                                service.next();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    if (null != intent.getStringExtra("reason") && intent.getStringExtra("reason").equals("homekey")) {
//                        deleteAttachOne();
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


    public void showDialog() {
        dialog = new Dialog(this, R.style.my_dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.list_bottom, null);
        ListView listView = (ListView) dialogView.findViewById(R.id.listview);
        try {
            adapter = new AudioAdapter(this, service.getAudioList(), service.getAudioPosition());
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                try {
                    service.openAudio(position);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                adapter.setIndex(position);
                adapter.notifyDataSetChanged();
            }
        });
        dialog.setContentView(dialogView);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 300));
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.mystyle); // 添加动画
        dialogWindow.setAttributes(lp);

        dialog.show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (!isBack) {
            setPlayOrPauseStatus();

//            try {
//                musicLists = db.findAll(MusicList.class);
//            } catch (DbException e) {
//                e.printStackTrace();
//            }
//            musicListAdapter.notifyDataSetChanged();

        }

    }

    private void getData() {
        utils = new MusicUtils();
        lyricUtils = new LyricUtils();
        currentPosition = getIntent().getIntExtra("currentPosition", 0);
        from_notification = getIntent().getBooleanExtra("from_notification", false);
        // 注册广播
        IntentFilter filter = new IntentFilter();
        receiver = new MyBroadcastReceiver();
        filter.addAction(MusicPlayerService.PREPARED_MESSAGE);
        filter.addAction(MusicPlayerService.PREPARED_CLOSE);
        filter.addAction(MusicPlayerService.PREPARED_STATUSA);
        registerReceiver(receiver, filter);
    }

    private boolean isMore200 = false;

    private void addNowMusic(AudioItem audioItem) {
//        bac.setBackground();
        Glide
                .with(MediaPlayerActivity.this)
                .load(SPUtility.getSPString(MediaPlayerActivity.this, audioItem.getBookId()))
                .centerCrop()
                .placeholder(R.drawable.cover_normal)
                .into(bac);
        MusicListBean musicListBean = new MusicListBean();
        musicListBean.setMusic_name(audioItem.getTitle());
        musicListBean.setNow_path(audioItem.getData());
        musicListBean.setOld_path(audioItem.getOldData());
        musicListBean.setBook_id(audioItem.getBookId());
//            ActivityUtils.showToast(this,audioItem.getBookId());
        musicListBean.setSave_name(audioItem.getArtist());
        musicListBean.setList_id("1");
        musicListBean.setList_name("最近播放");
//        MusicList musicList=new MusicList();
        if (isMore200) {
            ActivityUtils.showToast(this, "最近播放最多存储200首音乐，请将不需要的音乐删除！");
            return;
        }
        try {
            List<MusicListBean> all = db.selector(MusicListBean.class).where("list_id", "=", "1").and("save_name", "=", musicListBean.getSave_name()).findAll();
            if (all != null && all.size() > 0) {
                db.deleteById(MusicListBean.class, all.get(0).getId());
            }
            db.save(musicListBean);


            List<MusicListBean> musicListBeans = db.selector(MusicListBean.class).where("list_id", "=", "1").findAll();
            isMore200 = musicListBeans.size() >= 200;
            musicLists.get(0).setMusic_num(musicListBeans.size());

            db.update(musicLists);
        } catch (DbException e) {
            e.printStackTrace();
        }

        musicListAdapter.notifyDataSetChanged();
    }

    /**
     * 绑定服务
     */

    private void startService() {
        Bundle extras = new Bundle();
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.putExtras(extras);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 设置按钮监听
     */
    private void setOnlistener() {
        btn_play.setOnClickListener(mOnClickListener);
        btn_next.setOnClickListener(mOnClickListener);
        btn_pre.setOnClickListener(mOnClickListener);
        btn_list.setOnClickListener(mOnClickListener);
        seekBar_audio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        service.seeKTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 设置播放和暂停按钮状态
     */
    protected void setPlayOrPauseStatus() {
        if (service != null) {
            try {
                if (service.isPlaying()) {
                    // 暂停
                    btn_play.setBackgroundResource(mediaplayer_pause);
                } else {
                    // 播放
                    btn_play.setBackgroundResource(mediaplayer_play);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void createMusicList(View view) {
        final EditText et = new EditText(MediaPlayerActivity.this);
        et.setPadding(40, 40, 40, 40);
        et.setHint("请输入歌单名称");
        new AlertDialog.Builder(this)
                .setView(et)
                .setTitle("新建歌单")

                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        musicLists.add(new MusicList(et.getText().toString(), musicLists.size() + 1 + "", 0));
                        try {
                            db.saveOrUpdate(musicLists);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        musicListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicPlayerService.PREPARED_MESSAGE)) {
                // 音频是播起来了
                setViewStatus();

                //加入到最近播放
                try {
                    if (service.getAudioList() != null && service.getAudioList().size() != 0)
                        addNowMusic(service.getAudioItem());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(MusicPlayerService.PREPARED_CLOSE)) {
                finish();
            } else if (intent.getAction().equals(MusicPlayerService.PREPARED_STATUSA)) {
                setPlayOrPauseStatus();

            }
        }
    }

    private void addMusicToList() {
        final ListView listView = new ListView(MediaPlayerActivity.this);
        listView.setPadding(60, 96, 60, 96);
        musicListAdapter2 = new MusicListAdapter2(musicLists, this, R.layout.item_music_list, false);

        listView.setAdapter(musicListAdapter2);
        final AlertDialog alertDialog = new AlertDialog.Builder(MediaPlayerActivity.this).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {
                    ActivityUtils.showToast(MediaPlayerActivity.this, "不能加入最近播放歌单！");
                    return;
                }
                new AlertDialog.Builder(MediaPlayerActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("加入歌单")
                        .setMessage(String.format("您是否将%s加入到%s歌单中？", audioList.get(currentIndex).getTitle(), musicLists.get(position).getName()))
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicLists.get(position).setMusic_num(musicLists.get(position).getMusic_num() + 1);
                                try {
                                    db.update(musicLists);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                musicListAdapter.notifyDataSetChanged();
                                MusicListBean musicListBean = new MusicListBean();
                                musicListBean.setList_id(musicLists.get(position).getId());
                                musicListBean.setList_name(musicLists.get(position).getName());
                                musicListBean.setMusic_name(audioList.get(currentIndex).getTitle());
                                musicListBean.setNow_path(audioList.get(currentIndex).getData());
                                musicListBean.setOld_path(audioList.get(currentIndex).getOldData());
                                musicListBean.setBook_id(audioList.get(currentIndex).getBookId());
                                musicListBean.setSave_name(audioList.get(currentIndex).getArtist());
                                musicListBeans.add(musicListBean);
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
    }


    private void initView() {
        db = MyDbUtils.getMusicDb(MediaPlayerActivity.this);
        try {
            musicLists = db.findAll(MusicList.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (musicLists == null || musicLists.size() == 0) {
            musicLists = new ArrayList<>();
            musicLists.add(new MusicList("最近播放", "1", 0));
            try {
                db.save(musicLists);
            } catch (DbException e) {
                e.printStackTrace();
            }

        }

//        try {
//            db.delete(BookVO.class);
//            db.save(books);
//
//        } catch (DbException e) {
//            e.printStackTrace();
//        }

        musicListAdapter = new MusicListAdapter(musicLists, this, R.layout.item_music_list, false);
        music_list = findViewById(R.id.music_list);
        music_list.setAdapter(musicListAdapter);
        audioList = getIntent().getParcelableArrayListExtra("list");
        if (audioList == null || audioList.size() == 0) {
            audioList = new ArrayList<AudioItem>();
        }
        currentIndex = getIntent().getIntExtra("index", 0);
        try {
            path = audioList.get(currentIndex).getData();
        } catch (Exception e) {
            // TODO: handle exception
        }
        musicListBeans = new ArrayList<>();
//        try {
//            db.delete(MusicListBean.class, WhereBuilder.b("list_id", "=", "1"));
//        } catch (DbException e) {
//            e.printStackTrace();
//        }

//        musicLists.get(0).setMusic_num(musicLists.size() + audioList.size());
//        musicListAdapter.notifyDataSetChanged();
        try {
            musicListBeans = db.findAll(MusicListBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
//        musicListAdapter.set
        music_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isBack = false;
                Intent intent = new Intent(MediaPlayerActivity.this, MusicListActivity.class);
                intent.putExtra("Id", musicLists.get(position).getId());
                intent.putExtra("name", musicLists.get(position).getName());
                try {
                    if (service.getAudioList() != null && service.getAudioList().size() > 0)
                        intent.putExtra("position", service.getAudioItem().getArtist());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, 22);
            }
        });

        music_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {
                    ActivityUtils.showToast(MediaPlayerActivity.this, "不能删除最近播放歌单！");
                    return true;
                }
                new AlertDialog.Builder(MediaPlayerActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("删除歌单")
                        .setMessage(String.format("您是否将%s歌单删除？", musicLists.get(position).getName()))
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    db.deleteById(MusicList.class, musicLists.get(position).getId());
                                    db.delete(MusicListBean.class, WhereBuilder.b("list_id", "=", musicLists.get(position).getId()));
//                                    musicLists = db.findAll(MusicList.class);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                musicLists.remove(position);
                                musicListAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                return true;
            }
        });
        start_time = (TextView) findViewById(R.id.start_time);
        seekBar_audio = (SeekBar) findViewById(R.id.seekBar_audio);
        end_time = (TextView) findViewById(R.id.end_time);
        btn_pre = (LinearLayout) findViewById(R.id.btn_pre);
        bac = (ImageView) findViewById(R.id.bac);
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_list = (LinearLayout) findViewById(R.id.btn_list);
        mediaplayer_pause = getResources().getIdentifier("mediaplayer_pause", "drawable", getPackageName());
        mediaplayer_play = getResources().getIdentifier("mediaplayer_play", "drawable", getPackageName());
        btn_next = (LinearLayout) findViewById(R.id.btn_next);
//        mLrcView = (LrcView) findViewById(R.id.show_lyric_view);
//        mLrcView.setOnSeekToListener(new com.jiayue.lrcview.LrcView.OnSeekToListener() {
//
//            @Override
//            public void onSeekTo(int progress) {
//                try {
//                    service.seeKTo(progress);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText("音乐播放器");
//        btn_header_right = (ImageButton) findViewById(R.id.btn_header_right);
//        btn_header_right.setVisibility(View.VISIBLE);

//        btn_header_right.setBackgroundResource(getResources().getIdentifier("mediaplayer_hide", "drawable", getPackageName()));
//        btn_header_right.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                moveTaskToBack(true);
//            }
//        });

        btn_status = (LinearLayout) findViewById(R.id.btn_status);
        iv_mode = (ImageButton) findViewById(R.id.play_status);
        mode_one = getResources().getIdentifier("mode_one", "drawable", getPackageName());
        mode_all = getResources().getIdentifier("mode_all", "drawable", getPackageName());
        mode_random = getResources().getIdentifier("mode_random", "drawable", getPackageName());
        btn_status.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    service.setPlayMode(service.getPlayMode() + 1);
                    showPlayMode();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        isBack = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 22 && resultCode == 33) {
            assert data != null;
            long id = data.getLongExtra("music_id", 0);
            String list_id = data.getStringExtra("list_id");
            musicListBeansCache = new ArrayList<>();
            try {
                musicListBeansCache = db.selector(MusicListBean.class).where("list_id", "=", list_id).findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }
            for (MusicList musicList : musicLists) {
                if (musicList.getId().equalsIgnoreCase(list_id)) {
                    musicList.setPlaying(true);
                    musicList.setMusic_num(musicListBeansCache.size());
                } else {
                    musicList.setPlaying(false);
                }
            }
            if (list_id.equalsIgnoreCase("1")) {
                Collections.reverse(musicListBeansCache);
            }

//            musicListAdapter.notifyDataSetChanged();
            audioList.clear();
            if (musicListBeansCache != null && musicListBeansCache.size() != 0) {
                for (int i = 0; i < musicListBeansCache.size(); i++) {
                    if (musicListBeansCache.get(i).getId() == id)
                        currentIndex = i;
                    AudioItem audioItem = new AudioItem();
                    audioItem.setTitle(musicListBeansCache.get(i).getMusic_name());
                    audioItem.setData(musicListBeansCache.get(i).getNow_path());
                    audioItem.setOldData(musicListBeansCache.get(i).getOld_path());
                    audioItem.setArtist(musicListBeansCache.get(i).getSave_name());
                    audioItem.setBookId(musicListBeansCache.get(i).getBook_id());
                    audioList.add(audioItem);
//                    musicListBeansCache.get(i).setList_id("1");
//                    musicListBeansCache.get(i).setList_name("最近播放");
                }


//                if (!list_id.equalsIgnoreCase("1")) {
//
//                    try {
//                        db.delete(MusicListBean.class, WhereBuilder.b("list_id", "=", "1"));
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//                    musicLists.get(0).setMusic_num(musicListBeansCache.size());
//                    try {
//                        db.save(musicListBeansCache);
//                        db.update(musicLists);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//                    musicListAdapter.notifyDataSetChanged();
//                }

//                unbindService(conn);
                try {
                    // 下拉栏、歌曲相同
//                    service.pause();
                    service.setAudioList(audioList);
                    service.openAudio(currentIndex);
                    isBack = true;
                    btn_play.setBackgroundResource(mediaplayer_pause);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == 22 && resultCode == 44) {
            String list_id = data.getStringExtra("list_id");
            musicListBeansCache = new ArrayList<>();
            try {
                musicListBeansCache = db.selector(MusicListBean.class).where("list_id", "=", list_id).findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (musicListBeansCache == null || musicLists == null || musicLists.size() == 0 || musicListBeansCache.size() == 0)
                return;
            for (MusicList musicList : musicLists) {
                if (musicList.getId().equalsIgnoreCase(list_id)) {
                    musicList.setMusic_num(musicListBeansCache.size());
                }
            }
            musicListAdapter.notifyDataSetChanged();
        }
    }

    private void showPlayMode() {

        try {
            mode = service.getPlayMode();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (mode) {
            case MusicPlayerService.RANDOM_ALL:
                iv_mode.setBackgroundResource(mode_random);
                break;
            case MusicPlayerService.REPEATE_NORMAL:
                iv_mode.setBackgroundResource(mode_all);
                break;
            case MusicPlayerService.REPEATE_CURRENT:
                iv_mode.setBackgroundResource(mode_one);
                break;
            default:
                iv_mode.setBackgroundResource(mode_all);
                break;
        }
    }


//    private void showLyric() {
//
//        try {
//            String lrcpath = service.getAudioPath();// mnt/sdcard/video/beijingbeijing.mp3
//            Log.d("mediaplayer", "lrcpath==" + lrcpath);
//            if (!TextUtils.isEmpty(lrcpath)) {
//                lrcpath = lrcpath.substring(0, lrcpath.lastIndexOf("/")) + "/" + service.getAudioName() + ".lrc_copy.lrc";// mnt/sdcard/video/beijingbeijing
//            }
//            Log.d("mediaplayer", "lrcpath2==" + lrcpath);
//            File file = new File(lrcpath);
//            if (!file.exists()) {
//                file = new File(lrcpath + ".txt");
//            }
//            // Log.d("mediaplayer","file=="+file.getPath());
//            // lyricUtils.readLyricFile(file);
//            // mLrcView.setLyrics(lyricUtils.getLyrics());
//            mLrcView.setLrcRows((List<LrcRow>) getLrcRows(file));
//            if (lyricUtils.isExistLyric()) {
//                handler.sendEmptyMessage(LYRIC_SHOW);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 获取歌词List集合
     *
     * @return
     */
    private List<LrcRow> getLrcRows(File file) {
        List<LrcRow> rows = new ArrayList<LrcRow>();
        String line = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), getCharset(file)));
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
            rows = DefaultLrcParser.getIstance().getLrcRows(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rows;
    }

    /**
     * 判断文件编码
     *
     * @param file 文件
     * @return 编码：GBK,UTF-8,UTF-16LE
     */
    public String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis = null;
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                // int loc = 0;
                while ((read = bis.read()) != -1) {
                    // loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return charset;
    }

    public void setViewStatus() {
        Log.d("setViewStatus", "setViewStatussetViewStatussetViewStatus");
        try {
            // tv_artist.setText(service.getArtistName());
            tv_header_title.setText(service.getAudioName());
            // 设置seeKBar的总长度
            seekBar_audio.setMax(service.getDuration());

            start_time.setText(utils.stringForTime(service.getCurrentPositon()));
            end_time.setText(utils.stringForTime(service.getDuration()));
            handler.sendEmptyMessage(PROGRESS);
//            showLyric();

            if (dialog != null && dialog.isShowing()) {
                adapter.setIndex(service.getAudioPosition());
                adapter.notifyDataSetChanged();
            }
            // setPlayModeButtonStatus();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    private ServiceConnection conn = new ServiceConnection() {

        // 当取消绑定服务成功后，会回调这个方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;

        }

        // 当绑定服务成功后，会回调--
        @Override
        public void onServiceConnected(ComponentName name, IBinder ibinder) {
            // 代表服务
            service = IMusicPlayerService.Stub.asInterface(ibinder);
            try {
                // 下拉栏、歌曲相同
                if (!from_notification && !TextUtils.isEmpty(path) && !service.getAudioPath().equals(path)) {
                    Log.d("audioaudioaudio", "!from_notification!from_notification");
                    service.setAudioList(audioList);
                    service.openAudio(currentIndex);
                } else {
                    service.notifyChange(MusicPlayerService.PREPARED_MESSAGE);
                    setPlayOrPauseStatus();
                }
                showPlayMode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    };


    public void btnBack(View v) {
        // if (service != null) {
        // try {
        // service.pause();
        // } catch (RemoteException e) {
        // e.printStackTrace();
        // }
        // }
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // if (service != null) {
        // try {
        // service.pause();
        // } catch (RemoteException e) {
        // e.printStackTrace();
        // }
        // }
    }

    @Override
    public void onBackPressed() {
        // if (service != null) {
        // try {
        // service.pause();
        // } catch (RemoteException e) {
        // e.printStackTrace();
        // }
        // }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        // if (service != null) {
        // try {
        // service.pause();
        // } catch (RemoteException e) {
        // e.printStackTrace();
        // }
        // }

        if (null != broadcastReciver) {
            this.unregisterReceiver(broadcastReciver);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        unbindService(conn);

        // isStoped = true;
        // ActivityUtils.deleteBookFormSD(path);
        super.onDestroy();
    }
}
