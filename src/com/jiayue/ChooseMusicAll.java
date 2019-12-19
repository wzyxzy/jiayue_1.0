package com.jiayue;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jiayue.adapter.ChooseMusicAdapter;
import com.jiayue.adapter.ChooseMusicFolderAdapter;
import com.jiayue.adapter.MusicListAdapter;
import com.jiayue.adapter.MusicListAdapter2;
import com.jiayue.constants.Preferences;
import com.jiayue.download.TestService;
import com.jiayue.download2.entity.DocInfo;
import com.jiayue.dto.base.AttachCache;
import com.jiayue.dto.base.AttachOne;
import com.jiayue.dto.base.AttachTwo;
import com.jiayue.dto.base.AttachTwoBean;
import com.jiayue.dto.base.MusicListBean;
import com.jiayue.model.MusicList;
import com.jiayue.model.UserUtil;
import com.jiayue.service.MusicPlayerService;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.MyPreferences;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChooseMusicAll extends AppCompatActivity implements View.OnClickListener {

    private ListView all_music;
    private List<AttachOne> attachOnes;
    private List<AttachOne> attachOnesMusic;
    private List<AttachTwo> attachTwosMusic;
    private List<AttachOne> attachOnesFolder;
    private List<AttachCache> attachCaches;
    private ChooseMusicAdapter chooseMusicAdapter;
    private ChooseMusicFolderAdapter chooseMusicFolderAdapter;
    private LinearLayout ll_header;
    private TextView tv_header_title;
    private ImageButton btn_header_right;
    private TextView choose_all;
    private TextView add_music;
    private TextView download_all;
    private boolean isChooseAll;
    private MusicListAdapter2 musicListAdapter;
    private List<MusicList> musicLists;
    //    private List<MusicListBean> musicListBeans;
//    private List<MusicListBean> musicListBeansCache;
    private DbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_music_all);
        initView();
    }

    private void initView() {
        all_music = (ListView) findViewById(R.id.all_music);
//        mine_music = (ListView) findViewById(R.id.mine_music);
        attachOnesFolder = new ArrayList<>();
        attachOnesMusic = new ArrayList<>();
        attachCaches = new ArrayList<>();
        attachOnes = getIntent().getParcelableArrayListExtra("attachOnes");
        for (AttachOne attachOne : attachOnes) {
            if (attachOne.getAttachOneIspackage() == 1) {
                attachOnesFolder.add(attachOne);
            }
            if (TextUtils.isEmpty(attachOne.getAttachOneType()))
                continue;
            if (attachOne.getAttachOneType().equalsIgnoreCase("mp3") || attachOne.getAttachOneType().equalsIgnoreCase("wav")) {
                attachOnesMusic.add(attachOne);
            }
        }

        isChooseAll = false;


        for (AttachOne attachOne : attachOnesMusic) {
            AttachCache attachCache = new AttachCache();
            attachCache.setAttachId(attachOne.getAttachOneId());
            attachCache.setBookId(attachOne.getBookId());
            attachCache.setAttachIspackage(attachOne.getAttachOneIspackage());
            attachCache.setAttachName(attachOne.getAttachOneName());
            attachCache.setAttachPath(attachOne.getAttachOnePath());
            attachCache.setAttachSaveName(attachOne.getAttachOneSaveName());
            attachCache.setAttachType(attachOne.getAttachOneType());
            attachCache.setChoose(false);
//            attachCache.setPackageId(attachOne.getAttachOneId());
            attachCaches.add(attachCache);
        }

        chooseMusicAdapter = new ChooseMusicAdapter(attachCaches, this, R.layout.item_music_check);
        all_music.setAdapter(chooseMusicAdapter);
        all_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (attachCaches.get(position).getAttachIspackage() == 1) {
                    attachCaches.get(position).setChoose(!attachCaches.get(position).isChoose());
                    for (AttachCache attachCach : attachCaches) {
                        if (attachCach.getAttachId().equals(attachCaches.get(position).getAttachId())) {
                            attachCach.setChoose(attachCaches.get(position).isChoose());
                        }
                    }
                } else {
                    if (attachCaches.get(position).isChoose()) {//去掉文件夹全选标示
                        for (AttachCache attachCach : attachCaches) {
                            if (attachCach.getAttachIspackage() == 1 && attachCaches.get(position).getAttachId().equalsIgnoreCase(attachCach.getAttachId()) && attachCach.isChoose()) {
                                attachCach.setChoose(false);
                            }
                        }
                    }
                    attachCaches.get(position).setChoose(!attachCaches.get(position).isChoose());

                }
                chooseMusicAdapter.notifyDataSetChanged();
            }
        });
        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        ll_header.setOnClickListener(this);
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        tv_header_title.setText("多选");

        choose_all = (TextView) findViewById(R.id.choose_all);
        choose_all.setOnClickListener(this);
        add_music = (TextView) findViewById(R.id.add_music);
        add_music.setOnClickListener(this);
        download_all = (TextView) findViewById(R.id.download_all);
        download_all.setOnClickListener(this);

        if (attachOnesFolder != null && attachOnesFolder.size() > 0) {
            DialogUtils.showMyDialog(this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在加载音乐\n请稍后...", null);
            getAttachTwo(0);
        }
//        for (AttachOne attachOne : attachOnesFolder) {
//            getAttachTwo(attachOne);
//
//        }

        db = MyDbUtils.getMusicDb(ChooseMusicAll.this);
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
//        for (MusicList musicList : musicLists) {
//            if (musicList.getId().equalsIgnoreCase("1") && musicList.getName().equalsIgnoreCase("最近播放")) {
//                musicList.setName("新建歌单");
//            }
//        }

        musicListAdapter = new MusicListAdapter2(musicLists, this, R.layout.item_music_list, true);

//        try {
//            musicListBeans = db.findAll(MusicListBean.class);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_header:
                finish();
                break;

            case R.id.choose_all:
                isChooseAll = !isChooseAll;
                for (AttachCache attachCache : attachCaches) {
                    attachCache.setChoose(isChooseAll);
                }
                chooseMusicAdapter.notifyDataSetChanged();
//                Drawable drawable = isChooseAll ? getResources().getDrawable(R.drawable.choose_right) : getResources().getDrawable(R.drawable.choose_all);
//                choose_all.setCompoundDrawablesRelative(null, drawable, null, null);
                break;
            case R.id.add_music:

                addMusicToList();

                break;
            case R.id.download_all:
                new AlertDialog.Builder(ChooseMusicAll.this)
                        .setIcon(android.R.drawable.stat_sys_download)
                        .setTitle("下载音乐")
                        .setMessage("您是否要下载所有选择音乐？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (AttachCache attachCache : attachCaches) {
                                    if (attachCache.getAttachIspackage() == 0 && attachCache.isChoose()) {
                                        downLoadFile(attachCache.getAttachPath() + attachCache.getAttachSaveName(), attachCache.getAttachSaveName(), attachCache.getAttachName(), attachCache.getBookId());
                                    }
                                }
                                ActivityUtils.showToast(ChooseMusicAll.this, "加入下载同步列表完成！");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                break;
        }
    }

    /**
     * 下载附件
     *
     * @param url      附件的服务器地址
     * @param saveName 附件要保存的名字
     * @param fileName 附件要现实的名字
     */
    public void downLoadFile(String url, String saveName, String fileName, String bookId) {

        String path = Preferences.FILE_DOWNLOAD_URL + url + "&userId=" + UserUtil.getInstance(this).getUserId() + "&bookId=" + bookId;
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
            ActivityUtils.showToastForFail(ChooseMusicAll.this, "未检测到SD卡,或未赋予应用存储权限");
        }
    }

    private void createMusicList() {
        final EditText et = new EditText(ChooseMusicAll.this);
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

    private void addMusicToList() {
        final ListView listView = new ListView(ChooseMusicAll.this);
        listView.setPadding(60, 96, 60, 96);
        listView.setAdapter(musicListAdapter);
        final AlertDialog alertDialog = new AlertDialog.Builder(ChooseMusicAll.this).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {//新建歌单

                    createMusicList();
                } else
                    new AlertDialog.Builder(ChooseMusicAll.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("加入歌单")
                            .setMessage(String.format("您是否将所选歌曲加入到%s歌单中？", musicLists.get(position).getName()))
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (AttachCache attachCache : attachCaches) {
                                        if (attachCache.getAttachIspackage() == 0 && attachCache.isChoose()) {
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
                                            musicListBean.setMusic_name(attachCache.getAttachName());
                                            /**
                                             * item.setData(ActivityUtils.getSDPath(bookId) + File.separator + "copy_" + list_down.get(i).getAttachTwoSaveName());
                                             * item.setOldData(list_down.get(i).getAttachTwoPath() + list_down.get(i).getAttachTwoSaveName());
                                             *
                                             */
                                            musicListBean.setNow_path(ActivityUtils.getSDPath(attachCache.getBookId()) + File.separator + "copy_" + attachCache.getAttachSaveName());
                                            musicListBean.setOld_path(attachCache.getAttachPath() + attachCache.getAttachSaveName());
                                            musicListBean.setBook_id(attachCache.getBookId());
                                            musicListBean.setSave_name(attachCache.getAttachSaveName());
//                                        musicListBeans.add(musicListBean);
                                            try {
                                                db.save(musicListBean);
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }


                                    ActivityUtils.showToast(ChooseMusicAll.this, "添加完成");
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


    private void getAttachTwo(final int i) {
        RequestParams params = new RequestParams();
        params.setUri(Preferences.FILE_DOWNLOAD_ATTACH_URL2);
        params.addQueryStringParameter("attachOneId", attachOnesFolder.get(i).getAttachOneId());
        params.addQueryStringParameter("systemType", "android");
        params.addQueryStringParameter("userId", UserUtil.getInstance(ChooseMusicAll.this).getUserId());
//        Log.i("BookSynActivity", "bookId" + bookId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
//                java.lang.reflect.Type type = new TypeToken<AttachTwoBean>() {
//                }.getType();
                AttachTwoBean bookJson = gson.fromJson(s, AttachTwoBean.class);


                if (bookJson.getCode().equals(MyPreferences.SUCCESS) && null != bookJson.getData()) {
                    List<AttachTwo> attachTwos = bookJson.getData().getAttachTwoList();

                    if (attachTwos != null) {
                        attachTwosMusic = new ArrayList<>();
                        for (AttachTwo attachTwo : attachTwos) {

                            if (attachTwo.getAttachTwoType().equalsIgnoreCase("mp3") || attachTwo.getAttachTwoType().equalsIgnoreCase("wav")) {
                                attachTwosMusic.add(attachTwo);
                            }
                        }

                        if (attachTwosMusic.size() > 0) {
                            AttachCache attachCache = new AttachCache();
                            attachCache.setAttachIspackage(attachOnesFolder.get(i).getAttachOneIspackage());
                            attachCache.setAttachName(attachOnesFolder.get(i).getAttachOneName());
                            attachCache.setChoose(false);
                            attachCache.setAttachId(attachOnesFolder.get(i).getAttachOneId());
                            attachCaches.add(attachCache);
                            for (AttachTwo attachTwo : attachTwosMusic) {
                                AttachCache attachCacheTwo = new AttachCache();
                                attachCacheTwo.setAttachId(attachTwo.getAttachOneId());
                                attachCacheTwo.setBookId(attachTwo.getBookId());
                                attachCacheTwo.setAttachIspackage(attachTwo.getAttachTwoIspackage());
                                attachCacheTwo.setChoose(false);
//                                attachCacheTwo.setPackageId(attachOne.getAttachOneId());
                                attachCacheTwo.setAttachName(attachTwo.getAttachTwoName());
                                attachCacheTwo.setAttachPath(attachTwo.getAttachTwoPath());
                                attachCacheTwo.setAttachSaveName(attachTwo.getAttachTwoSaveName());
                                attachCacheTwo.setAttachType(attachTwo.getAttachTwoType());
                                attachCaches.add(attachCacheTwo);
                            }
                        }
                        if (i + 1 < attachOnesFolder.size()) {
                            getAttachTwo(i + 1);
                        } else {
                            ActivityUtils.showToast(ChooseMusicAll.this, "所有音乐加载完成！");
                            DialogUtils.dismissMyDialog();

                        }
//                        try {
//                            List<AttachTwo> list = db.selector(AttachTwo.class).where("attachOneId", "=", attachTwos.get(0).getAttachOneId()).and("bookId", "=", bookId).findAll();
//                            if (list != null && list.size() > 0) {
//                                db.delete(list);
//                                db.save(attachTwos);
//                            } else {
//                                db.save(attachTwos);
//                            }
//                        } catch (DbException e1) {
//                            // TODO Auto-generated catch block
//                            e1.printStackTrace();
//                        }
                    }
//                    attachOneIsPay = bookJson.getData().getAttachOneIsPay();
//                    attachOneTotalPrice = new BigDecimal(Float.toString(bookJson.getData().getAttachOneTotalPrice())).floatValue();
//                    try {
//                        DbManager dbManager_attachone = MyDbUtils.getOneAttachDb(BookAttachActivity.this);
//                        AttachOne attachOne = dbManager_attachone.selector(AttachOne.class).where("attachOneId", "=", attachOneId).and("bookId", "=", bookId).findFirst();
//                        attachOne.setAttachOneIsPay(attachOneIsPay);
//                        attachOne.setAttachOneTotalPrice(attachOneTotalPrice);
//                        dbManager_attachone.delete(attachOne);
//                        dbManager_attachone.save(attachOne);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i("BookAttachActivity", attachTwos.toString());
//                    updataContent();
//
//                    return;
                } else if (bookJson.getCode().equals(MyPreferences.FAIL)) {
                    ActivityUtils.showToast(ChooseMusicAll.this, "加载失败," + bookJson.getCodeInfo());
//                    return;
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(ChooseMusicAll.this, "加载失败,请检查网络！");
//                updataContent();
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                chooseMusicAdapter.notifyDataSetChanged();


            }
        });

    }
}
