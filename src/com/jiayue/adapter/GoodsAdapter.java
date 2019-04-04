package com.jiayue.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.GoodsBean;
import com.jiayue.dto.base.GoodsBuyBean;
import com.jiayue.dto.base.GoodsDetailBean;
import com.jiayue.model.UserUtil;
import com.jiayue.rest.LastOrNextListener;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.GoodsDetailUtils;
import com.jiayue.util.MyPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by BAO on 2016-08-09.
 */
public class GoodsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<GoodsBean> list;
    private DisplayImageOptions options;
    private final String TAG = getClass().getSimpleName();
    private LastOrNextListener listener;

    public GoodsAdapter(Context context,List<GoodsBean> list,LastOrNextListener listener) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.listener = listener;
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

    public List<GoodsBean> getList() {
        return list;
    }

    public void setList(List<GoodsBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_goods,null);
            holder.iv = (ImageView) convertView.findViewById(R.id.imageView8);
            holder.title = (TextView) convertView.findViewById(R.id.text1);
            holder.price = (TextView) convertView.findViewById(R.id.text2);
            holder.content = (TextView) convertView.findViewById(R.id.text3);
            holder.btn_buy = (Button) convertView.findViewById(R.id.button5);
//            holder.more = (ImageButton) convertView.findViewById(R.id.button6);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsBean bean = list.get(position);
        String url = Preferences.IMAGE_HTTP_LOCATION + bean.getPhotoUrl();
        ImageLoader.getInstance().displayImage(url, holder.iv, options);

        holder.title.setText(bean.getGoodsName());
        holder.price.setText("¥"+bean.getPrice());
        String content = "简介:"+bean.getGoodDisc();
        if(content.length()>25){
            content = content.substring(0,24)+"...";
        }
        holder.content.setText(content);
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showMyDialog(context, MyPreferences.SHOW_GOODS_CONTENT,null,bean.getGoodDisc(),null);
            }
        });
//        holder.more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogUtils.showMyDialog(context, MyPreferences.SHOW_GOODS_CONTENT,null,bean.getGoodDisc(),null);
//            }
//        });

        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogUtils.showMyDialog(context,MyPreferences.SHOW_PROGRESS_DIALOG,null,"加载中...",null);
            	
            	RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_BUY);
        		params.addQueryStringParameter("user.userId", UserUtil.getInstance(context).getUserId());
                params.addQueryStringParameter("livingGoods.goodId", bean.getGoodId()+"");
                params.addQueryStringParameter("totalPrice", bean.getPrice()+"");

                x.http().post(params, new Callback.CommonCallback<String>(){
                    @Override
                    public void onSuccess(String s) {
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<GoodsBuyBean>() {
                        }.getType();
                        GoodsBuyBean bean1 = gson.fromJson(s, type);

                        if (bean1 != null && bean1.getCode().equals("SUCCESS")) {
                            GoodsDetailBean goodsbean = GoodsDetailUtils.getInstance(context).getBean();
                            Log.d(TAG, "url========" + bean.getPhotoUrl());
                            goodsbean.setPhotourl(bean.getPhotoUrl());
                            goodsbean.setCode(bean1.getData().getCode());
                            goodsbean.setPrice(bean.getPrice());
                            goodsbean.setTitle(bean.getGoodsName());
                            goodsbean.setContent(bean.getGoodDisc());
                            GoodsDetailUtils.getInstance(context).setBean(goodsbean);
                            Log.d(TAG, "url========" + GoodsDetailUtils.getInstance(context).getBean().getPhotourl());
                            listener.next();
                        } else {
                            ActivityUtils.showToastForFail(context, "获取信息失败");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        ActivityUtils.showToastForFail(context, "获取信息失败");
                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });

        return convertView;
    }


    private class ViewHolder{
        private ImageView iv;
        private TextView title,price,content;
//        private ImageButton more;
        private Button btn_buy;
    }


}
