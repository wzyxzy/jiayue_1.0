package com.jiayue;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.jiayue.adapter.MusicListDetailAdapter;
import com.jiayue.dto.base.MusicListBean;
import com.jiayue.dto.base.SiftVO;
import com.jiayue.model.MusicList;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.MyDbUtils;

import org.xutils.DbManager;
import org.xutils.db.DbModelSelector;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.Collections;
import java.util.List;

public class MusicListActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_header;
    private TextView tv_header_title;
    private ImageButton btn_header_right;
    private ListView all_music_list;
    private String musicListId;
    private String musicListName;
    private String position;
    private DbManager db;
    private List<MusicListBean> musicListBeans;
    private MusicListDetailAdapter musicListDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        initView();
    }

    private void initView() {
        musicListId = getIntent().getStringExtra("Id");
        musicListName = getIntent().getStringExtra("name");
        position = getIntent().getStringExtra("position");
//        ActivityUtils.showToast(MusicListActivity.this, String.valueOf(position));
        db = MyDbUtils.getMusicDb(MusicListActivity.this);
        try {
            musicListBeans = db.selector(MusicListBean.class).where("list_id", "=", musicListId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (musicListId.equalsIgnoreCase("1")) {
            Collections.reverse(musicListBeans);
        }
        musicListDetailAdapter = new MusicListDetailAdapter(musicListBeans, this, R.layout.item_list_music, position);

        ll_header = (LinearLayout) findViewById(R.id.ll_header);
        tv_header_title = (TextView) findViewById(R.id.tv_header_title);
        btn_header_right = (ImageButton) findViewById(R.id.btn_header_right);
        all_music_list = (ListView) findViewById(R.id.all_music_list);
        tv_header_title.setText(musicListName);
        all_music_list.setAdapter(musicListDetailAdapter);
        ll_header.setOnClickListener(this);
        btn_header_right.setOnClickListener(this);
        all_music_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra("music_id", musicListBeans.get(position).getId());
                intent.putExtra("list_id", musicListBeans.get(position).getList_id());
                setResult(33, intent);
                finish();
            }
        });
        all_music_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //删除歌曲方法
                new AlertDialog.Builder(MusicListActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("删除音乐")
                        .setMessage(String.format("您是否将%s音乐删除？", musicListBeans.get(position).getMusic_name()))
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    db.deleteById(MusicListBean.class, musicListBeans.get(position).getId());
                                    musicListBeans = db.selector(MusicListBean.class).where("list_id", "=", musicListId).findAll();

//                                    db.delete(MusicListBean.class, WhereBuilder.b("list_id", "=", musicLists.get(position).getId()));
//                                    musicLists = db.findAll(MusicList.class);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }

                                musicListDetailAdapter.updateRes(musicListBeans);
                                ActivityUtils.showToast(MusicListActivity.this, "删除完毕！");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_header:
                Intent intent = new Intent();
                intent.putExtra("list_id", musicListId);
                setResult(44, intent);
                finish();

                break;
            case R.id.btn_header_right:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("list_id", musicListId);
        setResult(44, intent);
        finish();
    }
}
