package com.jiayue.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiayue.BookSynActivity;
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.dto.base.BookVO;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class BookAllAdapter extends BaseAdapter {

	private List<BookVO> list = new ArrayList<>();
	private int cancel_position;
	private Context context;
	private onListRefreshListener listener;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
	
	public BookAllAdapter(Context context, List<BookVO> list, int cancel_position) {
		super();
		this.context = context;
		this.list = list;
		this.cancel_position = cancel_position;
		
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
	}

	public void setonListRefreshListener(onListRefreshListener listener){
		this.listener = listener;
	}
	
	public List<BookVO> getList() {
		return list;
	}

	public void setList(List<BookVO> list) {
		this.list = list;
	}

	public int getCancel_position() {
		return cancel_position;
	}

	public void setCancel_position(int cancel_position) {
		this.cancel_position = cancel_position;
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
            convertView = inflater.inflate(R.layout.item_bookall,null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_book);
            holder.title = (TextView) convertView.findViewById(R.id.tv_book);
//            holder.layout = (LinearLayout) convertView.findViewById(R.id.layout_book);
            holder.btn = (Button) convertView.findViewById(R.id.btn_book);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final BookVO bean = list.get(position);
        String image_url = Preferences.IMAGE_HTTP_LOCATION + bean.getBookImgPath() + bean.getBookImg();
        ImageLoader.getInstance().displayImage(image_url, holder.iv, options);
        
        holder.title.setText(bean.getBookName());
        //已删除的书籍
        if(position<cancel_position){
        	holder.btn.setText("找回");
        	holder.btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					bookBack(bean.getBookId());
				}

			});
        	
//        	holder.layout.setClickable(false);
        }else {
        	holder.btn.setText("打开");
        	holder.btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openBook(bean);
				}

			});
        	
//        	holder.layout.setClickable(false);
//        	holder.layout.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
		}
        
		return convertView;
	}
	
	private void openBook(BookVO bean){
		Intent intent = new Intent(context, BookSynActivity.class);
		intent.putExtra("bookId", bean.getBookId());
		intent.putExtra("bookName", bean.getBookName());
		intent.putExtra("image_url", Preferences.IMAGE_HTTP_LOCATION + bean.getBookImgPath() + bean.getBookImg());
		intent.putExtra("bookSaveName", bean.getBookSaveName());
		context.startActivity(intent);
	}
	
	//找回书籍
	private void bookBack(String bookId) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams(Preferences.BOOK_BACK);
		params.addQueryStringParameter("userId", UserUtil.getInstance(context).getUserId());
        params.addQueryStringParameter("bookId", bookId);

		x.http().post(params,new Callback.CommonCallback<String>(){
			@Override
			public void onSuccess(String s) {
				Gson gson = new Gson();
				java.lang.reflect.Type type = new TypeToken<Bean>() {
				}.getType();
				Bean bean = gson.fromJson(s, type);

				if(bean!=null&&bean.getCode().equals("SUCCESS")){
					ActivityUtils.showToast(context, "书籍已成功找回");
					listener.onListRefresh();
				}else{
					ActivityUtils.showToastForFail(context, "书籍找回失败");
				}
			}

			@Override
			public void onError(Throwable throwable, boolean b) {
				ActivityUtils.showToastForFail(context, "书籍找回失败");
			}

			@Override
			public void onCancelled(CancelledException e) {

			}

			@Override
			public void onFinished() {

			}
		});
	}
	
	private class ViewHolder{
//		private LinearLayout layout;
        private ImageView iv;
        private TextView title;
        private Button btn;
    }

	public interface onListRefreshListener{
		void onListRefresh();
	}

}
