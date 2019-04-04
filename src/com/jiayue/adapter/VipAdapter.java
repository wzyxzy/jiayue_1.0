package com.jiayue.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.dto.base.VipBean;

/**
 * Created by BAO on 2016-09-28.
 */
public class VipAdapter extends BaseAdapter {

    private Context context;
    private final String DEFAULE = "无相应许可";
    private List<VipBean.Data> list = null;
    private LayoutInflater inflater;

    public VipAdapter(Context context, List<VipBean.Data> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public List<VipBean.Data> getList() {
        return list;
    }

    public void setList(List<VipBean.Data> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null||list.size() ==0?1:list.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_vip,null);
            holder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(list == null||list.size() ==0){
            holder.tv.setText(DEFAULE);
        }else {
            holder.tv.setText(list.get(position).getName());
        }
        return convertView;
    }

    private class ViewHolder{
        TextView tv;
    }
}
