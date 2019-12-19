package com.jiayue.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.model.MusicList;

import java.util.List;

public class MusicListAdapter extends WZYBaseAdapter<MusicList> {

    private final Context context;
    private final boolean isChoose;

    public MusicListAdapter(List<MusicList> data, Context context, int layoutRes, boolean isChoose) {
        super(data, context, layoutRes);
        this.context = context;
        this.isChoose = isChoose;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindData(ViewHolder holder, MusicList musicList, int indexPostion) {

        TextView name_music = (TextView) holder.getView(R.id.name_music_list);
        if (isChoose && musicList.getId().equalsIgnoreCase("1")) {
//            music_num.setText("");
            name_music.setText("创建歌单");

        } else {
            name_music.setText(musicList.getName());

        }
        TextView music_num = (TextView) holder.getView(R.id.music_num);
        if (isChoose && musicList.getId().equalsIgnoreCase("1")) {
//            music_num.setText("");
            music_num.setText("+");
            music_num.setTextColor(context.getResources().getColor(R.color.background));
        } else {
            music_num.setText("（共" + musicList.getMusic_num() + "首）");
            music_num.setTextColor(0xffdddddd);
        }

        if (musicList.isPlaying()) {
            name_music.setTextColor(context.getResources().getColor(R.color.background));
            music_num.setTextColor(context.getResources().getColor(R.color.background));
        } else {
            name_music.setTextColor(0xffffffff);
            music_num.setTextColor(0xffdddddd);
        }

    }
}
