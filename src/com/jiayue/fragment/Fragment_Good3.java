package com.jiayue.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.jiayue.rest.OnRefreshAdapterListener;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.GoodsDetailUtils;
import com.jiayue.util.MyPreferences;

/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Good3 extends Fragment implements View.OnClickListener ,OnRefreshAdapterListener{

    private View mRootView;
    private LastOrNextListener listener;
    private ImageView photo;
    private TextView title, price,fapiao;
    private ImageButton more;
    private Button btn_last, btn_pay;
    private DisplayImageOptions options;
    private CheckBox checkBox;
    private LinearLayout layout;
    private EditText et_beizhu;

    public void setLastOrNextListener(LastOrNextListener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fagment_goods3, null);
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
        more = (ImageButton) mRootView.findViewById(R.id.button6);
        btn_last = (Button) mRootView.findViewById(R.id.button7);
        btn_pay = (Button) mRootView.findViewById(R.id.button8);
        layout = (LinearLayout) mRootView.findViewById(R.id.layout2);
        checkBox = (CheckBox) mRootView.findViewById(R.id.checkBox);
        btn_last.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        fapiao = (TextView) mRootView.findViewById(R.id.fapiao);
        et_beizhu = (EditText) mRootView.findViewById(R.id.beizhu);



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

        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
        String url = Preferences.IMAGE_HTTP_LOCATION + bean.getPhotourl();
        ImageLoader.getInstance().displayImage(url, photo, options);
        title.setText(bean.getTitle());
        price.setText("¥" + bean.getPrice());
        et_beizhu.setText(bean.getBeizhu());


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    layout.setClickable(true);
            		int iv_more = getActivity().getResources().getIdentifier("more", "drawable", getActivity().getPackageName());
                    more.setBackgroundResource(iv_more);
                    GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
                    fapiao.setText(bean.getFapiao());
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
                            String msg = bean.getFapiao() + "@@" + price.getText().toString();
                            DialogUtils.showGoodsDialog(getActivity(), MyPreferences.SHOW_GOODS_FAPIAO, msg, Fragment_Good3.this);
                        }
                    });
                }else {
                    layout.setClickable(false);
            		int more_gray = getActivity().getResources().getIdentifier("more_gray", "drawable", getActivity().getPackageName());
                    more.setBackgroundResource(more_gray);
                    fapiao.setText("");
                }
            }
        });

//        more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
//                String msg = bean.getFapiao() + "@@" + price.getText().toString();
//                DialogUtils.showGoodsDialog(getActivity(), MyPreferences.SHOW_GOODS_FAPIAO, msg, null);
//            }
//        });
    }

    public void refreshFragment3() {
        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
        String url = Preferences.IMAGE_HTTP_LOCATION + bean.getPhotourl();
        ImageLoader.getInstance().displayImage(url, photo, options);
        title.setText(bean.getTitle());
        price.setText("¥" + bean.getPrice());
        fapiao.setText(bean.getFapiao());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
        switch (v.getId()) {
            case R.id.button7:
                bean.setBeizhu(et_beizhu.getText().toString());
                GoodsDetailUtils.getInstance(getActivity()).setBean(bean);
                listener.last();
                break;
            case R.id.button8:
                if(checkBox.isChecked()&& TextUtils.isEmpty(bean.getFapiao())){
                    ActivityUtils.showToastForFail(getActivity(), "发票信息不可为空");
                    return;
                }
                Log.d("Fragment3","isFapiao===="+checkBox.isChecked());
                bean.setisFapiao(checkBox.isChecked());
                bean.setBeizhu(et_beizhu.getText().toString());
                GoodsDetailUtils.getInstance(getActivity()).setBean(bean);
                listener.next();
                break;
        }
    }



    @Override
    public void onRefreshAdapter() {
        GoodsDetailBean bean = GoodsDetailUtils.getInstance(getActivity()).getBean();
        fapiao.setText(bean.getFapiao());
    }
}
