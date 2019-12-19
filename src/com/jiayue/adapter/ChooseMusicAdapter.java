package com.jiayue.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.jiayue.R;
import com.jiayue.dto.base.AttachCache;
import com.jiayue.dto.base.AttachOne;

import java.util.List;

public class ChooseMusicAdapter extends WZYBaseAdapter<AttachCache> {
    public ChooseMusicAdapter(List<AttachCache> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, AttachCache attachCache, int indexPostion) {
        CheckBox checkBox = (CheckBox) holder.getView(R.id.checkbox_music);
        checkBox.setText(attachCache.getAttachName());
        checkBox.setChecked(attachCache.isChoose());
        if (attachCache.getAttachIspackage() == 1) {
            checkBox.setTextSize(16);
            checkBox.setTextColor(Color.rgb(0x66, 0x66, 0x66));
            setMargins(checkBox, 40, 0, 0, 0);
            checkBox.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            checkBox.setTextSize(15);
            checkBox.setTextColor(Color.rgb(0x7f, 0x7f, 0x7f));
            setMargins(checkBox, 150, 0, 0, 0);
            checkBox.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        attachCache.setChoose(checkBox.isChecked());
    }


    private static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
