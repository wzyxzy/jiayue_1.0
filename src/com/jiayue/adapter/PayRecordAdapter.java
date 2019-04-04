package com.jiayue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.dto.base.BookVO;
import com.jiayue.dto.base.RecordBean;
import com.jiayue.util.MyDbUtils;

import org.xutils.DbManager;

import java.util.List;

/**
 * Created by BAO on 2016-09-28.
 */
public class PayRecordAdapter extends BaseAdapter {

    private Context context;
    private final String DEFAULE = "无相应许可";
    private List<RecordBean.Data> list = null;
    private LayoutInflater inflater;

    public PayRecordAdapter(Context context, List<RecordBean.Data> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public List<RecordBean.Data> getList() {
        return list;
    }

    public void setList(List<RecordBean.Data> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null || list.size() == 0 ? 0 : list.size();
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_record, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecordBean.Data.AttachList attachList = list.get(position).getAttachList().get(0);
        if (attachList != null) {
            DbManager db = MyDbUtils.getBookVoDb(context);
            try {
                BookVO bookVO = db.selector(BookVO.class).where("book_id", "=", attachList.getGroupId()).findFirst();
                if (bookVO != null) {
                    if(attachList.getGroupList().size()>1){
                        holder.name.setText("《"+bookVO.getBookName()+"》|"+attachList.getGroupList().get(0).getAttachName()+"......");
                    }else{
                        holder.name.setText("《"+bookVO.getBookName()+"》|"+attachList.getGroupList().get(0).getAttachName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RecordBean.Data.PayTime paytime = list.get(position).getPayTime();
            if (paytime != null) {
                String hours = paytime.getHours()<10?("0"+paytime.getHours()):paytime.getHours()+"";
                String minutes = paytime.getMinutes()<10?("0"+paytime.getMinutes()):paytime.getMinutes()+"";
                String seconds = paytime.getSeconds()<10?("0"+paytime.getSeconds()):paytime.getSeconds()+"";
                String time = (paytime.getYear()-100+2000)+"-"+(paytime.getMonth()+1)+"-"+paytime.getDate()+" "+hours+":"+minutes+":"+seconds;
                holder.time.setText(time);
            }
            holder.price.setText("¥"+list.get(position).getRealPrice());
        }
        return convertView;
    }

    private class ViewHolder {
        TextView name, time, price;
    }
}
