package com.jiayue.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.ZhiboListBean;
import com.jiayue.util.TimeFormate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ZhiboAdapter extends BaseAdapter {

	private Context context;
	private List<ZhiboListBean> list;
	private DisplayImageOptions options;
	private LayoutInflater inflater;
	private ViewHolder holder;

	public ZhiboAdapter(Context context, List<ZhiboListBean> list) {
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

	public List<ZhiboListBean> getList() {
		return list;
	}

	public void setList(List<ZhiboListBean> list) {
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

	@SuppressLint("SimpleDateFormat") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_zhibo, null);
			holder = new ViewHolder();
			holder.bg = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.title = (TextView) convertView.findViewById(R.id.textView1);
			holder.status = (TextView) convertView.findViewById(R.id.textView2);
			holder.describe = (TextView) convertView.findViewById(R.id.textView3);
			holder.isfree = (ImageView) convertView.findViewById(R.id.imageView);
			holder.dingyue = (ImageView) convertView.findViewById(R.id.iv_dingyue);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ZhiboListBean bean = list.get(position);
		String url = Preferences.IMAGE_HTTP_LOCATION + bean.getLphotoUrl();
		ImageLoader.getInstance().displayImage(url, holder.bg, options);

		holder.title.setText(bean.getCourseName());
//		if (bean.getChannel_status().equals("1")) {
			holder.status.setText(context.getString(R.string.zhengzaizhibo));
//		} else {
//			Date date = new Date(bean.getStartTime().getTime());
//			SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
//			String str = sdf.format(date) + " 播出";
//			holder.status.setText(str);
//		}
		holder.describe.setText(bean.getCourseDisc());
		if(bean.getIsFree() != 0)
			holder.isfree.setVisibility(View.GONE);
		else
			holder.isfree.setVisibility(View.VISIBLE);

		if(bean.isPay())
			holder.dingyue.setBackgroundResource(R.drawable.yiding);
		else
			holder.dingyue.setBackgroundResource(R.drawable.weidingyue);
//		String min = bean.getStartTime().getMinutes()>9?bean.getStartTime().getMinutes()+"":"0"+bean.getStartTime().getMinutes();
//		holder.time.setText(bean.getStartTime().getHours()+":"+min);
		holder.time.setText(TimeFormate.getlongtime(bean.getStartTime().getTime(),bean.getEndTime().getTime()));
		return convertView;
	}

	private class ViewHolder {
		private TextView title, describe, status,time;
		private ImageView bg,isfree,dingyue;
	}

}
