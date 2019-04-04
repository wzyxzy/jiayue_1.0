package com.jiayue;

import java.io.FileInputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiayue.util.ActivityUtils;

public class PictureActivity extends BaseActivity {
	private TextView tv_header_title;
	private FileInputStream fis;
	private LinearLayout layout;
	private String filepath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture_player);
		
		layout = (LinearLayout) findViewById(R.id.layout);
		tv_header_title = (TextView) findViewById(R.id.tv_header_title);
		tv_header_title.setText(getIntent().getStringExtra("bookName"));
		
		if (getIntent().getStringExtra("filePath") != null && (!getIntent().getStringExtra("filePath").equals(""))) {
			filepath = getIntent().getStringExtra("filePath");
			
			if(filepath.endsWith(".gif")||filepath.endsWith(".GIF")){
				setGifView();
			}else{
				setImageView();
			}
		}
	}

	private void setGifView() {
		// TODO Auto-generated method stub
		GifImageView picture = new GifImageView(this);
		picture.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		layout.addView(picture);
		try {
			GifDrawable gid = new GifDrawable(filepath);
			picture.setImageDrawable(gid);
//			final MediaController mediaController = new MediaController(this);
//			mediaController.setMediaPlayer((GifDrawable) picture_palyer.getDrawable());
//			mediaController.setAnchorView(picture_palyer);
//			mediaController.show();
			new PhotoViewAttacher(picture).update();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void setImageView() {
		// TODO Auto-generated method stub
		ImageView picture = new ImageView(this);
		picture.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		layout.addView(picture);
		try {
			fis = new FileInputStream(filepath);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			picture.setImageBitmap(bitmap);
			new PhotoViewAttacher(picture).update();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void btnBack(View v) {
		this.finish();
	}

	@Override
	protected void onDestroy() {
		Log.d("Picture----------", "---------picture path ===== " + filepath);
		ActivityUtils.deleteBookFormSD(filepath);
		super.onDestroy();
	}
}
