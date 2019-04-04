package com.jiayue.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.GoodsSettingBean;
import com.jiayue.rest.OnRefreshAdapterListener;
import com.jiayue.util.ActivityUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import cn.jiguang.net.HttpUtils;

/**
 * Created by BAO on 2016-08-09.
 */
public class GoodsSettingAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<GoodsSettingBean> list;
    private final String TAG = getClass().getSimpleName();
    private OnRefreshAdapterListener listener;
    private int courseId;

    public GoodsSettingAdapter(Context context, List<GoodsSettingBean> list,int courseId,OnRefreshAdapterListener listener) {
        this.context = context;
        this.list = list;
        this.courseId = courseId;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    public List<GoodsSettingBean> getList() {
        return list;
    }

    public void setList(List<GoodsSettingBean> list) {
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
            convertView = inflater.inflate(R.layout.item_good_setting,null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.sum = (TextView) convertView.findViewById(R.id.sum);
            holder.btn_send = (Button) convertView.findViewById(R.id.btn_send);
            holder.btn_cancel = (Button) convertView.findViewById(R.id.btn_cancel);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsSettingBean bean = list.get(position);

        Log.d(TAG,"name=="+bean.getGoodsName());
        holder.title.setText(bean.getGoodsName());
        holder.price.setText("¥"+bean.getPrice());
        holder.sum.setText(bean.getCount()+"");
        showView(bean.getStatus(),holder.btn_send,holder.btn_cancel);

        holder.btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean.getStatus() == 1)
                    return;
                
                RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_SEND);
        		params.addQueryStringParameter("courseId", courseId+"");
                params.addQueryStringParameter("goodId", bean.getGoodId()+"");

                x.http().post(params, new Callback.CommonCallback<String>(){
                    @Override
                    public void onSuccess(String s) {
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<Bean>() {
                        }.getType();
                        Bean bean1 = gson.fromJson(s, type);

                        if (bean1 != null && bean1.getCode().equals("SUCCESS")) {
                            listener.onRefreshAdapter();
                        } else {
                            ActivityUtils.showToastForFail(context, "推送失败");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        ActivityUtils.showToastForFail(context, "推送失败");
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

        holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean.getStatus() == 0)
                    return;
                
                RequestParams params = new RequestParams(Preferences.ZHIBO_GOODS_CANCEL);
        		params.addQueryStringParameter("courseId", courseId+"");
                params.addQueryStringParameter("goodId", bean.getGoodId()+"");

        		HttpUtils http = new HttpUtils();
        		x.http().post(params, new Callback.CommonCallback<String>(){
                    @Override
                    public void onSuccess(String s) {
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<Bean>() {
                        }.getType();
                        Bean bean1 = gson.fromJson(s, type);

                        if(bean1!=null&&bean1.getCode().equals("SUCCESS")){
                            listener.onRefreshAdapter();
                        }else{
                            ActivityUtils.showToastForFail(context, "收回失败");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        ActivityUtils.showToastForFail(context, "收回失败");
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

    //0未推送 1已推送-----显示收回为绿色
    private void showView(int status,Button btn_send,Button btn_cancel) {
		int btn = context.getResources().getIdentifier("btn", "drawable", context.getPackageName());
		int kuang2 = context.getResources().getIdentifier("kuang2", "drawable", context.getPackageName());
        btn_send.setBackgroundResource(status == 0?btn:kuang2);
        btn_cancel.setBackgroundResource(status == 1?btn:kuang2); 
        btn_send.setTextColor(status == 0?context.getResources().getColor(R.color.white):context.getResources().getColor(R.color.background));
        btn_cancel.setTextColor(status == 1?context.getResources().getColor(R.color.white):context.getResources().getColor(R.color.background));
    }


    private class ViewHolder{
        private TextView title,price,sum;
        private Button btn_send,btn_cancel;
    }


}
