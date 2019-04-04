package com.jiayue.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.DingyueDataBean;
import com.jiayue.rest.OnRefreshAdapterListener;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.TimeFormate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import cn.jiguang.net.HttpUtils;

/**
 * Created by BAO on 2016-08-02.
 */
public class DingYueAdapter extends BaseAdapter {
    private Context context;
    private List<DingyueDataBean> list;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private ViewHolder holder = null;
//    private Map<Integer,Boolean> cancelmap = new HashMap<>();
    private OnRefreshAdapterListener onRefreshAdapterListener;
    public DingYueAdapter(Context context, List<DingyueDataBean> list,OnRefreshAdapterListener onRefreshAdapterListener) {
        this.context = context;
        this.list = list;
        this.onRefreshAdapterListener = onRefreshAdapterListener;
        inflater = LayoutInflater.from(context);
        int draw = context.getResources().getIdentifier("zhibo_default", "drawable", context.getPackageName());
        options = new DisplayImageOptions.Builder().showStubImage(draw)
                // 设置图片下载期间显示的图片
                .showImageForEmptyUri(draw)
                // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(draw)
                // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)
                // 设置下载的图片是否缓存在内存中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).build();
    }

    public List<DingyueDataBean> getList() {
        return list;
    }

    public void setList(List<DingyueDataBean> list) {
        this.list = list;
//        cancelmap.clear();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_mydingyue,null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.imageView6);
            holder.title = (TextView) convertView.findViewById(R.id.textView14);
            holder.expert = (TextView) convertView.findViewById(R.id.textView22);
            holder.time = (TextView) convertView.findViewById(R.id.textView16);
            holder.addtime = (TextView) convertView.findViewById(R.id.textView20);
            holder.btn = (Button) convertView.findViewById(R.id.button);
            holder.divider = (LinearLayout) convertView.findViewById(R.id.divider);
            holder.date = (TextView) convertView.findViewById(R.id.textView18);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        DingyueDataBean bean = list.get(position);
        String url = Preferences.IMAGE_HTTP_LOCATION + bean.getFphotoUrl();
        ImageLoader.getInstance().displayImage(url, holder.iv, options);

        holder.title.setText(bean.getCourseName());
        holder.expert.setText(bean.getExpert());
        String minutes_start = bean.getStartTime().getMinutes() > 9 ? bean.getStartTime().getMinutes() + "" : "0" + bean.getStartTime().getMinutes();
        String minutes_end = bean.getEndTime().getMinutes() > 9 ? bean.getEndTime().getMinutes() + "" : "0" + bean.getEndTime().getMinutes();
//        if(bean.getPid() == 0){
            String date = String
                    .format(context.getResources().getString(R.string.yugao_date), "20" + bean.getStartTime().getYear() % 100 + "/" + (bean.getStartTime().getMonth() + 1) + "/" + bean.getStartTime().getDate(), "20" + bean.getEndTime().getYear() % 100 + "/" + (bean.getEndTime()
                            .getMonth() + 1) + "/" + bean.getEndTime().getDate());
//            holder.time.setText(date);
//        }else{
//            holder.time.setText("20" + bean.getStartTime().getYear() % 100 + "-" + (bean.getStartTime().getMonth() + 1) + "-" + bean.getStartTime().getDate() +" "+bean.getStartTime().getHours() + ":" + minutes+"播出");
//        }

        holder.date.setText(date);
        holder.time.setText(TimeFormate.getDay(bean.getStartTime().getDay())+" "+bean.getStartTime().getHours() + ":" + minutes_start+"-"+bean.getEndTime().getHours() + ":" + minutes_end);

        String minutes_add = bean.getAddTime().getMinutes() > 9 ? bean.getAddTime().getMinutes() + "" : "0" + bean.getAddTime().getMinutes();
        holder.addtime.setText("20" + bean.getAddTime().getYear() % 100 + "-" + (bean.getAddTime().getMonth() + 1) + "-" + bean.getAddTime().getDate() +" "+bean.getAddTime().getHours() + ":" + minutes_add);
//        if(cancelmap.containsKey(position)){
//            holder.btn.setText("已取消");
//            holder.btn.setClickable(false);
//        }else{
//            holder.btn.setText("取消订阅");
//            holder.btn.setClickable(true);
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showMyDialog(context,MyPreferences.SHOW_CONFIRM_DIALOG,"确认取消订阅？","一经取消订阅无法找回，是否确认取消订阅？", new MyOnClickListener(position,holder.btn));
                }
            });
//        }
        if(position == list.size()-1)
            holder.divider.setVisibility(View.INVISIBLE);
        else
            holder.divider.setVisibility(View.VISIBLE);
        return convertView;
    }


    private class MyOnClickListener implements View.OnClickListener{

        private int position;
        private Button btn;

        public MyOnClickListener(int position, Button btn) {
            this.position = position;
            this.btn = btn;
        }

        @Override
        public void onClick(View v) {
            DialogUtils.showMyDialog(context, MyPreferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
            
            RequestParams params = new RequestParams(Preferences.CANCEL_DINGYUE);
            params.addQueryStringParameter("lcourseId", list.get(position).getCourseId()+"");
            params.addQueryStringParameter("corderId", list.get(position).getOrderId()+"");

    		HttpUtils http = new HttpUtils();
    		x.http().post(params, new Callback.CommonCallback<String>(){
                @Override
                public void onSuccess(String s) {
                    Gson gson = new Gson();
                    java.lang.reflect.Type type = new TypeToken<Bean>() {
                    }.getType();
                    Bean bean = gson.fromJson(s, type);
                    if (bean != null && bean.getCode().equals("SUCCESS")) {
//                        cancelmap.put(position,true);
                        ActivityUtils.showToastForSuccess(context,bean.getCodeInfo());
                        onRefreshAdapterListener.onRefreshAdapter();
//                        notifyDataSetChanged();
                    } else {
                        ActivityUtils.showToastForFail(context,bean.getCodeInfo());
                    }
                    DialogUtils.dismissMyDialog();
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ActivityUtils.showToastForFail(context,"加载失败");
                    DialogUtils.dismissMyDialog();
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private class ViewHolder{
        private ImageView iv;
        private TextView title,expert,time,addtime,date;
        private Button btn;
        private LinearLayout divider;
    }
}
