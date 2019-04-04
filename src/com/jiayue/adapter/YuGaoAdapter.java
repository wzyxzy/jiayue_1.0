package com.jiayue.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.dto.base.YuGaoChildBean;

public class YuGaoAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private Map<Integer,Boolean> selectmap = new HashMap<>();
	private List<YuGaoChildBean> list = new ArrayList<>();
	
	
	public YuGaoAdapter(Context context, Map<Integer, Boolean> selectmap, List<YuGaoChildBean> list) {
		super();
		this.context = context;
		this.selectmap = selectmap;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	
	

	public Map<Integer, Boolean> getSelectmap() {
		return selectmap;
	}



	public void setSelectmap(Map<Integer, Boolean> selectmap) {
		this.selectmap = selectmap;
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list == null?0:list.size();
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
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_yugao_dingyue,null);
			holder.box = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.tv = (TextView) convertView.findViewById(R.id.textView1);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(selectmap.containsKey(position)){
			holder.box.setChecked(true);
		}else {
			holder.box.setChecked(false);
		}
		holder.tv.setText(list.get(position).getCourseName());
		
		return convertView;
	}

	private class ViewHolder {
		private CheckBox box;
		private TextView tv;
	}
}
