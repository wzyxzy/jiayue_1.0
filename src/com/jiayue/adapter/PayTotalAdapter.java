package com.jiayue.adapter;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.dto.base.ShopListBean;

public class PayTotalAdapter extends BaseAdapter {

	private Context context;
	private List<ShopListBean> mList = new ArrayList<ShopListBean>();
	private LayoutInflater inflater;
	// 用来控制选中购买，保存未选中的
	private static HashMap<Integer, Boolean> selectedMap;
	//用来控制删除，保存删除的
	private static TreeMap<Integer, Boolean> cancelMap;
	private final String TAG = getClass().getSimpleName();
	//是否是删除模式
	private boolean isCancelMode = false;

	public PayTotalAdapter(Context context, List<ShopListBean> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mList = list;
		selectedMap = new HashMap<Integer, Boolean>();
		cancelMap = new TreeMap<Integer, Boolean>(new Comparator<Integer>() {

			@Override
			public int compare(Integer lhs, Integer rhs) {
				// TODO Auto-generated method stub
				return rhs.compareTo(lhs);//降序排列
			}
		});
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return selectedMap;
	}
	
	public static TreeMap<Integer, Boolean> getCancle() {
		return cancelMap;
	}

	public static void setCancelMap(TreeMap<Integer, Boolean> cancelMap) {
		PayTotalAdapter.cancelMap = cancelMap;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		PayTotalAdapter.selectedMap = isSelected;
	}
	
	public boolean isCancelMode() {
		return isCancelMode;
	}

	public void setCancelMode(boolean isCancelMode) {
		this.isCancelMode = isCancelMode;
		clearMap();
	}

	public void clearMap(){
		if(isCancelMode){
			selectedMap.clear();
		}else {
			cancelMap.clear();
		}
	}
	
	public void clearTreeMap(){
		cancelMap.clear();
	}

	public List<ShopListBean> getmList() {
		return mList;
	}

	public void setmList(List<ShopListBean> mList) {
		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		inflater = LayoutInflater.from(context);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_pay, null);
			holder = new ViewHolder();
			holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.cover = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.tvName = (TextView) convertView.findViewById(R.id.textView1);
			holder.tvAbstract = (TextView) convertView.findViewById(R.id.textView2);
			holder.tvPrice = (TextView) convertView.findViewById(R.id.textView3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(!isCancelMode){
			holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						// 取消选中的则剔除
						selectedMap.remove(position);
					} else {
						selectedMap.put(position, isChecked);
					}
				}
			});
			// 找到需要选中的条目
			if (selectedMap != null && selectedMap.containsKey(position)) {
				holder.cb.setChecked(selectedMap.get(position));
			} else {
				holder.cb.setChecked(true);
			}
		}else {
			holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						cancelMap.put(position, isChecked);
					} else {
						// 取消选中的则剔除
						cancelMap.remove(position);
					}
				}
			});
			// 找到需要选中的条目
			if (cancelMap != null && cancelMap.containsKey(position)) {
				holder.cb.setChecked(cancelMap.get(position));
			} else {
				holder.cb.setChecked(false);
			}
		}
		
		return convertView;
	}

	class ViewHolder {
		CheckBox cb;
		ImageView cover;
		TextView tvName, tvAbstract, tvPrice;
	}

}
