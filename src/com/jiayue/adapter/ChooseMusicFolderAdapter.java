package com.jiayue.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.BookAttachActivity;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.AttachOne;
import com.jiayue.dto.base.AttachTwo;
import com.jiayue.dto.base.AttachTwoBean;
import com.jiayue.model.UserUtil;
import com.jiayue.sortlistview.ChildListView;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.MyPreferences;

import org.w3c.dom.Text;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChooseMusicFolderAdapter extends WZYBaseAdapter<AttachOne> {
    private final Context context;

    public ChooseMusicFolderAdapter(List<AttachOne> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
        this.context = context;
    }

    @Override
    public void bindData(ViewHolder holder, AttachOne attachOne, int indexPostion) {
        List<AttachTwo> attachTwosCache = new ArrayList<>();
        ChildListView listView = (ChildListView) holder.getView(R.id.music_folder_list);
        TextView textView = (TextView) holder.getView(R.id.title);
        textView.setText(attachOne.getAttachOneName());
        ChooseMusicTwoAdapter chooseMusicTwoAdapter = new ChooseMusicTwoAdapter(attachTwosCache, context, R.layout.item_music_check);
        listView.setAdapter(chooseMusicTwoAdapter);


    }



}
