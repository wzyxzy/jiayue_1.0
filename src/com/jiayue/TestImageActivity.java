package com.jiayue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class TestImageActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		ImageView view = (ImageView) findViewById(R.id.imageView1);
		// String path = getIntent().getStringExtra("path");
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inJustDecodeBounds = true; // 先获取原大小
		// // scanBitmap = BitmapFactory.decodeFile(path, options);
		// view.setImageBitmap(BitmapFactory.decodeFile(path, options));

		Intent intent = getIntent();
		if (intent != null) {
			byte[] bis = intent.getByteArrayExtra("bitmap");
			Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
			view.setImageBitmap(bitmap);
		}
	}
}
