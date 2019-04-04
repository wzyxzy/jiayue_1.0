package com.jiayue.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.RecommendBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by BAO on 2018-07-18.
 */

public class ZhiYueAdapter extends BaseQuickAdapter<RecommendBean.Data.OrdinaryList, BaseViewHolder> {

    private Context context;
    private DisplayImageOptions options;

    public ZhiYueAdapter(Context context, int layoutId, List<RecommendBean.Data.OrdinaryList> OrdinaryList) {
        super(layoutId, OrdinaryList);
        this.context = context;
        int default_img = context.getResources().getIdentifier("default_img", "drawable", context.getPackageName());
        options = new DisplayImageOptions.Builder().showStubImage(default_img)
                // 设置图片下载期间显示的图片
                .showImageForEmptyUri(default_img)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(default_img)
                // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendBean.Data.OrdinaryList item) {
        String image_url = Preferences.IMAGE_HTTP_LOCATION + item.getCoverPath();
        ImageLoader.getInstance().displayImage(image_url,(ImageView)helper.getView(R.id.imageView8),options);

        helper.setText(R.id.text1,item.getContent());
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
        String geff = df.format(item.getAttachOnePrice());//返回的是String类型的
        helper.setText(R.id.text2,"¥"+geff);
    }

}
