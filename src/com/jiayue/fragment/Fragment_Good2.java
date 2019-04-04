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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.GoodsDetailBean;
import com.jiayue.rest.LastOrNextListener;
import com.jiayue.rest.OnRefreshAdapterListener;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.GoodsDetailUtils;
import com.jiayue.util.MyPreferences;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Good2 extends Fragment implements OnRefreshAdapterListener, View.OnClickListener {

    private View mRootView;
    private LastOrNextListener listener;
    private ImageView photo;
    private TextView title, price, address;
    private ImageButton more;
    private Button btn_last, btn_next;
    private DisplayImageOptions options;

    public void setLastOrNextListener(LastOrNextListener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fagment_goods2, null);
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
        address = (TextView) mRootView.findViewById(R.id.address);
        more = (ImageButton) mRootView.findViewById(R.id.button6);
        btn_last = (Button) mRootView.findViewById(R.id.button7);
        btn_next = (Button) mRootView.findViewById(R.id.button8);
        btn_last.setOnClickListener(this);
        btn_next.setOnClickListener(this);


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

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
                String msg = bean.getAddress() + "@@" + bean.getName() + "@@" + bean.getTelephone();
                DialogUtils.showGoodsDialog(getActivity(), MyPreferences.SHOW_GOODS_ADDRESS, msg, Fragment_Good2.this);
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
                String msg = bean.getAddress() + "@@" + bean.getName() + "@@" + bean.getTelephone();
                DialogUtils.showGoodsDialog(getActivity(), MyPreferences.SHOW_GOODS_ADDRESS, msg, Fragment_Good2.this);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void refreshFragment2() {
        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
        String url = Preferences.IMAGE_HTTP_LOCATION + bean.getPhotourl();
        ImageLoader.getInstance().displayImage(url, photo, options);
        title.setText(bean.getTitle());
        price.setText("¥" + bean.getPrice());
        address.setText(bean.getAddress());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRefreshAdapter() {
        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
        address.setText(bean.getAddress());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button7:
                listener.last();
                break;
            case R.id.button8:
                GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
                if (TextUtils.isEmpty(bean.getAddress()))
                    ActivityUtils.showToastForFail(getActivity(), "地址等信息不可为空");
                else
                    listener.next();
                break;
        }
    }
}
