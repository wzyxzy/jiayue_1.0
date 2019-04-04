package com.jiayue.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.DianPlayActivity;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.WangQiDataBean;
import com.jiayue.dto.base.WangqiUrlBean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.jiguang.net.HttpUtils;

public class WangqiAdapter extends BaseAdapter {

	private Context context;
	private List<WangQiDataBean> list;
	private DisplayImageOptions options;
	private LayoutInflater inflater;
	private ViewHolder holder;
	//保存打开的选项
	private static HashMap<Integer, Boolean> selectedMap;

	public WangqiAdapter(Context context, List<WangQiDataBean> list) {
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
		selectedMap = new HashMap<Integer, Boolean>();
	}

	public void reverse(){
		if(list!=null&&list.size()!=0){
			Collections.reverse(list);
			selectedMap.clear();
		}
	}
	public List<WangQiDataBean> getList() {
		return list;
	}

	public void setList(List<WangQiDataBean> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_wangqi, null);
			holder = new ViewHolder();
			holder.bg = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.title = (TextView) convertView.findViewById(R.id.textView1);
			holder.btn_shujia = (LinearLayout) convertView.findViewById(R.id.shujia);
			holder.describe = (TextView) convertView.findViewById(R.id.textView3);
			holder.show = (ImageView) convertView.findViewById(R.id.show);
			holder.show_layout = (LinearLayout) convertView.findViewById(R.id.show_layout);
			holder.time = (TextView) convertView.findViewById(R.id.textView2);
			holder.teacher = (TextView) convertView.findViewById(R.id.textView4);
			holder.creat_time = (TextView) convertView.findViewById(R.id.textView5);
			holder.keyword = (TextView) convertView.findViewById(R.id.textView6);
			holder.level = (TextView) convertView.findViewById(R.id.textView7);
			holder.btn_show = (LinearLayout) convertView.findViewById(R.id.btn_show);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final WangQiDataBean bean = list.get(position);
		ImageLoader.getInstance().displayImage(bean.getImageUrl(), holder.bg, options);
		
		holder.bg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogUtils.showMyDialog(context, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在查找中...", null);
				
				RequestParams params = new RequestParams(Preferences.GET_VIDEO_URL);
        		params.addQueryStringParameter("fileId", bean.getFileId());

        		HttpUtils http = new HttpUtils();
				x.http().post(params, new Callback.CommonCallback<String>(){
					@Override
					public void onSuccess(String s) {
						Gson gson = new Gson();
						java.lang.reflect.Type type = new TypeToken<WangqiUrlBean>() {
						}.getType();
						WangqiUrlBean urlbean = gson.fromJson(s, type);
						if (urlbean != null && urlbean.getCode().equals("SUCCESS")) {
							Intent intent = new Intent(context, DianPlayActivity.class);
							intent.putExtra("bean", bean);
							intent.putExtra("urlbean", urlbean);
							context.startActivity(intent);
						} else {
							ActivityUtils.showToastForFail(context, urlbean.getCodeInfo());
						}
						DialogUtils.dismissMyDialog();
					}

					@Override
					public void onError(Throwable throwable, boolean b) {
						ActivityUtils.showToastForFail(context, "获取失败");
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
		});

		holder.title.setText(bean.getFileName());
		holder.time.setText(bean.getDutation());
		holder.btn_shujia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if(bean.getAvailable() == 0) {
//                    ActivityUtils.showToastForFail(context, "您没有权限");
//                    return;
//                }
                DialogUtils.showMyDialog(context, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在查找中...", null);
                
                RequestParams params = new RequestParams(Preferences.ADDBOOK_URL);
        		params.addQueryStringParameter("userId", UserUtil.getInstance(context).getUserId());
                params.addQueryStringParameter("code", bean.getShelfEncode());
        		x.http().post( params, new Callback.CommonCallback<String>(){
					@Override
					public void onSuccess(String s) {
						Gson gson = new Gson();
						java.lang.reflect.Type type = new TypeToken<Bean>() {
						}.getType();
						Bean bean = gson.fromJson(s, type);
						if (bean != null && bean.getCode().equals("SUCCESS")) {
							ActivityUtils.showToastForSuccess(context, bean.getCodeInfo());
						} else {
							ActivityUtils.showToastForFail(context, bean.getCodeInfo());
						}
						DialogUtils.dismissMyDialog();
					}

					@Override
					public void onError(Throwable throwable, boolean b) {
						ActivityUtils.showToastForFail(context, "添加失败");
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
		});
		holder.btn_show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selectedMap.containsKey(position)){
					selectedMap.remove(position);
					notifyDataSetChanged();
				}else {
					selectedMap.put(position, true);
					notifyDataSetChanged();
				}
			}
		});
		
		if(selectedMap.containsKey(position)){
    		int show2 = context.getResources().getIdentifier("show2", "drawable", context.getPackageName());
			holder.show.setImageResource(show2);
			holder.show_layout.setVisibility(View.VISIBLE);
			holder.describe.setSingleLine(false);
			holder.describe.setText(context.getString(R.string.jianjie)+bean.getContent());
			holder.teacher.setText(context.getString(R.string.teacher)+bean.getAuthor());
			holder.creat_time.setText(context.getString(R.string.shijian)+bean.getCreateTime());
			holder.keyword.setText(context.getString(R.string.guanjianci)+bean.getKeyword());
			holder.level.setText(context.getString(R.string.jieduan)+bean.getVipType());
		}else{
    		int show = context.getResources().getIdentifier("show", "drawable", context.getPackageName());
			holder.show.setImageResource(show);
			holder.show_layout.setVisibility(View.GONE);
			holder.describe.setSingleLine(true);
			holder.describe.setText(context.getString(R.string.jianjie)+bean.getContent());
		}
		return convertView;
	}
	
	private void showLayout(ViewHolder holder,WangQiDataBean bean,boolean isShow,boolean isAnim){
		if(isAnim)
//			Animator a = AnimationUtils.loadAnimation(context, R.anim.rotate180);
//			holder.show.setAnimation(a);
		if(isShow){
			holder.show_layout.setVisibility(View.VISIBLE);
			holder.describe.setSingleLine(false);
			holder.describe.setText(context.getString(R.string.jianjie)+bean.getContent());
			holder.teacher.setText(context.getString(R.string.teacher)+bean.getAuthor());
			holder.creat_time.setText(context.getString(R.string.shijian)+bean.getCreateTime());
			holder.keyword.setText(context.getString(R.string.guanjianci)+bean.getKeyword());
			holder.level.setText(context.getString(R.string.jieduan)+bean.getVipType());
		}else {
			holder.show_layout.setVisibility(View.GONE);
			holder.describe.setSingleLine(true);
			holder.describe.setText(context.getString(R.string.jianjie)+bean.getContent());
		}
	}

	private class ViewHolder {
		private TextView title, describe, teacher,time,creat_time,keyword,level;
		private ImageView bg,show;
		private LinearLayout btn_shujia,show_layout,btn_show;
	}

}
