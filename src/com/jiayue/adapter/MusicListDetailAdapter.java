package com.jiayue.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.dto.base.MusicListBean;

import java.util.List;

public class MusicListDetailAdapter extends WZYBaseAdapter<MusicListBean> {

    private Context context;
    private String position;

    public MusicListDetailAdapter(List<MusicListBean> data, Context context, int layoutRes, String position) {
        super(data, context, layoutRes);
        this.context = context;
        this.position = position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void bindData(ViewHolder holder, MusicListBean musicListBean, int indexPostion) {

        TextView num_list = (TextView) holder.getView(R.id.num_list);
        num_list.setText(String.format("%d", indexPostion + 1));
        TextView name_text = (TextView) holder.getView(R.id.name_text);
        name_text.setText(musicListBean.getMusic_name());
        if (!TextUtils.isEmpty(position) && position.equalsIgnoreCase(musicListBean.getSave_name())) {
            num_list.setTextColor(context.getResources().getColor(R.color.background));
            name_text.setTextColor(context.getResources().getColor(R.color.background));
        } else {
            num_list.setTextColor(0xff333333);
            name_text.setTextColor(0xff333333);
        }
    }
}
