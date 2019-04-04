package com.jiayue.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiayue.R;
import com.jiayue.dto.base.RecommendBean;

import java.util.List;

/**
 * Created by BAO on 2018-07-18.
 */

public class ZhiYueDetailAdapter extends BaseQuickAdapter<RecommendBean.Data.AttachTwoList, BaseViewHolder> {

    private Context context;

    public ZhiYueDetailAdapter(Context context, int layoutId, List<RecommendBean.Data.AttachTwoList> attachTwoList) {
        super(layoutId, attachTwoList);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendBean.Data.AttachTwoList item) {
        helper.setText(R.id.name,item.getAttachTwoName());
    }

}
