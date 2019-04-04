package com.jiayue.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.YuGaoActivity;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.YuGaoDataBean;
import com.jiayue.util.TimeFormate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ZhiBoYuGaoAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<YuGaoDataBean> list = new ArrayList<>();
    private DisplayImageOptions options;

    public ZhiBoYuGaoAdapter(Context context, List<YuGaoDataBean> list) {
        super();
        this.context = context;
        this.list = list;
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

    public List<YuGaoDataBean> getList() {
        return list;
    }

    public void setList(List<YuGaoDataBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_yugao, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.textView1);
            holder.price = (TextView) convertView.findViewById(R.id.textView2);
            holder.content = (TextView) convertView.findViewById(R.id.textView4);
            holder.teacher = (TextView) convertView.findViewById(R.id.textView7);
            holder.teacher_content = (TextView) convertView.findViewById(R.id.textView6);
            holder.time = (TextView) convertView.findViewById(R.id.textView8);
            holder.bg = (ImageView) convertView.findViewById(R.id.imageView1);
            holder.share = (LinearLayout) convertView.findViewById(R.id.ll1);
            holder.comment = (LinearLayout) convertView.findViewById(R.id.ll2);
            holder.zan = (LinearLayout) convertView.findViewById(R.id.ll3);
            holder.btn = (Button) convertView.findViewById(R.id.button1);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
            holder.divider = (LinearLayout) convertView.findViewById(R.id.divider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        YuGaoDataBean bean = list.get(position);
        String url = Preferences.IMAGE_HTTP_LOCATION + bean.getCphotoUrl();
        ImageLoader.getInstance().displayImage(url, holder.bg, options);
        holder.title.setText(bean.getCourseName());
        if (bean.getPrice() == 0.0)
            holder.price.setText("免费");
        else
            holder.price.setText(String.format(context.getString(R.string.yugao_jiage), bean.getPrice() + ""));
        holder.content.setText(bean.getCourseDisc());
//        String content1;
//        if (bean.getExpertInfo().length() > 20) {
//            content1 = bean.getExpertInfo().subSequence(0, 20).toString() + "...";
//        } else {
//            content1 = bean.getExpertInfo();
//        }
        holder.teacher_content.setText(bean.getExpertInfo());
        holder.teacher.setText(bean.getExpert());
        String minutes = bean.getStartTime().getMinutes() > 9 ? bean.getStartTime().getMinutes() + "" : "0" + bean.getStartTime().getMinutes();
        String longtime = TimeFormate.getlongtime(bean.getStartTime().getTime(),bean.getEndTime().getTime());
        holder.time.setText(TimeFormate.getDay(bean.getStartTime().getDay())+bean.getStartTime().getHours() + ":" + minutes+" "+longtime);
        if (bean.isPay()) {
            holder.btn.setBackgroundResource(R.drawable.yidingyue);
            holder.btn.setClickable(false);
        } else {
            holder.btn.setBackgroundResource(R.drawable.dingyue);
            holder.btn.setClickable(true);
            holder.btn.setOnClickListener(new onMyClickListener(position));
        }
        holder.layout.setOnClickListener(new onMyClickListener(position));
        if (position == list.size() - 1)
            holder.divider.setVisibility(View.INVISIBLE);
        else
            holder.divider.setVisibility(View.VISIBLE);
        return convertView;
    }

    private class ViewHolder {
        private TextView title, price, content, teacher, teacher_content, time;
        private ImageView bg;
        private LinearLayout share, comment, zan;
        private Button btn;
        private LinearLayout layout, divider;
    }

    private class onMyClickListener implements OnClickListener {

        int position;

        public onMyClickListener(int position) {
            super();
            this.position = position;
        }


        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//            switch (v.getId()) {
//                case R.id.button1:
            Intent intent = new Intent(context, YuGaoActivity.class);
            intent.putExtra("bean", list.get(position));
            context.startActivity(intent);
//                    break;
//
//                default:
//                    break;
//            }
        }

    }

}
