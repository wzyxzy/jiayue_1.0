package com.jiayue.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.PhotoBean;
import com.jiayue.util.ActivityUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

/**
 * Created by BAO on 2017-08-07.
 */

public class ScanAdapter extends BaseAdapter {

	private Context context;
	private List<PhotoBean.Data> list;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private String bookId;

	public ScanAdapter(Context context, List<PhotoBean.Data> list, String bookId) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
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
		this.bookId = bookId;
	}

	public List<PhotoBean.Data> getList() {
		return list;
	}

	public void setList(List<PhotoBean.Data> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list != null ? list.size() : 0;
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_dialog_scan, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.tv = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PhotoBean.Data bean = list.get(position);
		String url = "";
		String[] array = new String[] {};
		try {
			Log.d("SCANadapter", "bean.getImagePath()="+bean.getImagePath());
			array = bean.getImagePath().split("/");
			ImageLoader.getInstance()
			.displayImage("file://" + ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId + "/" + array[array.length - 1], holder.iv, options, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					// TODO Auto-generated method stub
					ImageLoader.getInstance().displayImage(Preferences.IMAGE_HTTP_LOCATION + bean.getImagePath(), (ImageView) view, options);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub

				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("SCANadapter", e.getMessage());
		}
		// if (ActivityUtils.isNetworkAvailable(context)) {
		// url = Preferences.IMAGE_HTTP_LOCATION + bean.getImagePath();
		// } else {
		// url = "file://" + ActivityUtils.getSDLibPath().getAbsolutePath() +
		// "/" + bookId + "/" + array[array.length - 1];
		// }
		// ImageLoader.getInstance().displayImage(url, holder.iv, options);
		holder.tv.setText(bean.getAttachName());
		return convertView;
	}

	private class ViewHolder {
		private ImageView iv;
		private TextView tv;
	}
}
