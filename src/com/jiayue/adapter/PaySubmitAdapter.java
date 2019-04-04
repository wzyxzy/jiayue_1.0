package com.jiayue.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.dto.base.BookVO;

public class PaySubmitAdapter extends BaseAdapter {

	private Context context;
	private List<BookVO> mList = new ArrayList<BookVO>();
	private LayoutInflater inflater;
	private final String TAG = getClass().getSimpleName();

	public PaySubmitAdapter(Context context, List<BookVO> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mList = list;
	}

	public List<BookVO> getmList() {
		return mList;
	}

	public void setmList(List<BookVO> mList) {
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
			holder.cb.setVisibility(View.GONE);
		return convertView;
	}

	class ViewHolder {
		CheckBox cb;
		ImageView cover;
		TextView tvName, tvAbstract, tvPrice;
	}

}
