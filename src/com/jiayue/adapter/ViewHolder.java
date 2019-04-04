package com.jiayue.adapter;

import java.net.Proxy.Type;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
//import android.support.v4.widget.SearchViewCompat.OnCloseListenerCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.skytree.epub.ClickListener;

public class ViewHolder {
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	private ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
	}

	/**
	 * 拿到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder) convertView.getTag();
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}
	public ViewHolder setIsChecked(int viewId ,Boolean bm){
		CheckBox checkbox=getView(viewId);
		checkbox.setChecked(bm);
		return this;
	}
	/**
	 * <p>
	 * Title
	 * </p>
	 * : setDisplay Description: 设置控件的显示
	 * 
	 * @param viewId
	 * @return
	 */
	public ViewHolder setDisplay(int viewId) {
		getView(viewId).setVisibility(View.VISIBLE);
		return this;
	}
	/**
	 * <p>
	 * Title
	 * </p>
	 * : setDisplay Description: 设置控件的隐藏
	 * 
	 * @param viewId
	 * @return
	 */
	public ViewHolder setHiddle(int... viewId) {
		for (int j = 0; j < viewId.length; j++) {
			getView(viewId[j]).setVisibility(View.GONE);
		}
		return this;
	}
	/**
	 * <p>
	 * Title
	 * </p>
	 * : setDisplay Description: 设置控件的隐藏
	 * 
	 * @param viewId
	 * @return
	 */
	public ViewHolder setInVisibility(int... viewId) {
		for (int j = 0; j < viewId.length; j++) {
			getView(viewId[j]).setVisibility(View.INVISIBLE);
		}
		return this;
	}

	/**
	 * 为按钮设置点击事件
	 * 
	 * @param viewId
	 * @param l
	 * @return
	 */
	public ViewHolder setOnClickListener(int viewId, OnClickListener l) {
		View view = getView(viewId);
		view.setOnClickListener(l);
		return this;
	}
	/**
	 * 为按钮设置长按点击事件
	 * 
	 * @param viewId
	 * @param l
	 * @return
	 */
	public ViewHolder setOnLongClickListener(int viewId, OnLongClickListener l) {
		View view = getView(viewId);
		view.setOnLongClickListener(l);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageByUrl(int viewId, String url) {
		ImageLoader.getInstance()
				.displayImage(url, (ImageView) getView(viewId));
		return this;
	}

	public int getPosition() {
		return mPosition;
	}

	

}