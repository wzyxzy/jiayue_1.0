package com.jiayue.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.dto.base.AudioItem;

public class AudioAdapter extends BaseAdapter {

	private Context context;
	private List<AudioItem> list;
	private int index;
    private LayoutInflater inflater;

    public AudioAdapter(Context context, List<AudioItem> list, int index) {
		super();
		this.context = context;
		this.list = list;
		this.index = index;
		inflater = LayoutInflater.from(context);
	}
	
	public List<AudioItem> getList() {
		return list;
	}

	public void setList(List<AudioItem> list) {
		this.list = list;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list!=null?list.size():0;
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
            convertView = inflater.inflate(R.layout.item_audio,null);
            holder.num = (TextView) convertView.findViewById(R.id.num);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        AudioItem item = list.get(position);
        if(index == position){
        	holder.num.setText("");
        	int bofangjian = context.getResources().getIdentifier("bofangjian", "drawable", context.getPackageName());
        	holder.num.setBackgroundResource(bofangjian);
        	holder.name.setTextColor(context.getResources().getColor(R.color.audio_red));
        }else {
        	holder.num.setText((position+1)+"");
        	holder.num.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        	holder.num.setTextColor(context.getResources().getColor(R.color.audio_black));
        	holder.name.setTextColor(context.getResources().getColor(R.color.audio_black));
		}
        holder.name.setText(item.getTitle());
		return convertView;
	}

	private class ViewHolder{
        private TextView num,name;
    }
}
