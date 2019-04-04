package com.jiayue.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.GoodsDetailBean;
import com.jiayue.rest.LastOrNextListener;
import com.jiayue.util.GoodsDetailUtils;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Good4 extends Fragment implements View.OnClickListener {

    private View mRootView;
    private LastOrNextListener listener;
    private ImageView photo;
    private TextView title, price,price_all,tv_fapiao,tv_address,tv_namephone,tv_beizhu;
    private Button btn_last, btn_pay;
    private DisplayImageOptions options;
    private LinearLayout layout_fapiao,layout_beizhu;

    public void setLastOrNextListener(LastOrNextListener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fagment_goods4, null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        photo = (ImageView) mRootView.findViewById(R.id.imageView8);
        title = (TextView) mRootView.findViewById(R.id.text1);
        price = (TextView) mRootView.findViewById(R.id.text2);
        price_all = (TextView) mRootView.findViewById(R.id.text3);
        btn_last = (Button) mRootView.findViewById(R.id.button7);
        btn_pay = (Button) mRootView.findViewById(R.id.button8);
        btn_last.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        layout_fapiao = (LinearLayout) mRootView.findViewById(R.id.layout_fapiao);
        tv_fapiao = (TextView) mRootView.findViewById(R.id.tv_fapiao);
        tv_namephone = (TextView) mRootView.findViewById(R.id.tv_namephone);
        tv_address = (TextView) mRootView.findViewById(R.id.tv_address);
        tv_beizhu = (TextView) mRootView.findViewById(R.id.tv_beizhu);
        layout_beizhu = (LinearLayout) mRootView.findViewById(R.id.layout_beizhu);

        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();

		int default_img = getActivity().getResources().getIdentifier("default_img", "drawable", getActivity().getPackageName());
        options = new DisplayImageOptions.Builder().showStubImage(default_img)
                // 设置图片下载期间显示的图片
                .showImageForEmptyUri(default_img)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(default_img)
                // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();

        String url = Preferences.IMAGE_HTTP_LOCATION + bean.getPhotourl();
        ImageLoader.getInstance().displayImage(url, photo, options);
        title.setText(bean.getTitle());
        price.setText("¥" + bean.getPrice());
        price_all.setText("¥" + bean.getPrice());

    }

    public void refreshFragment4() {
        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
        if(bean.isFapiao()){
            layout_fapiao.setVisibility(View.VISIBLE);
            tv_fapiao.setText(bean.getFapiao());
        }else
            layout_fapiao.setVisibility(View.GONE);

        if(TextUtils.isEmpty(bean.getBeizhu()))
            layout_beizhu.setVisibility(View.GONE);
        else{
            layout_beizhu.setVisibility(View.VISIBLE);
            tv_beizhu.setText(bean.getBeizhu());
        }
        tv_address.setText(bean.getAddress());
        tv_namephone.setText(bean.getName()+" "+bean.getTelephone());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button7:
                listener.last();
                break;
            case R.id.button8:
                listener.charge();
                break;
        }
    }
}
