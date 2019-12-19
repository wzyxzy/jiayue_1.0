package com.jiayue.adapter;

import android.content.Context;
import android.widget.CheckBox;

import com.jiayue.R;
import com.jiayue.dto.base.AttachOne;
import com.jiayue.dto.base.AttachTwo;

import java.util.List;

public class ChooseMusicTwoAdapter extends WZYBaseAdapter<AttachTwo> {
    public ChooseMusicTwoAdapter(List<AttachTwo> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, AttachTwo attachTwo, int indexPostion) {

        CheckBox checkBox = (CheckBox) holder.getView(R.id.checkbox_music);
        checkBox.setText(attachTwo.getAttachTwoName());
    }
}
