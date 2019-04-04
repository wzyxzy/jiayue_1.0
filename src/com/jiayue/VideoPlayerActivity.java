package com.jiayue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.jiayue.service.MusicPlayerService;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.SPUtility;


public class VideoPlayerActivity extends Activity {
	protected static final int PROGRESS = 1;
	protected static final int MESSAGEDELAYED = 2;
	private LinearLayout videoplayer_loading;
	private LinearLayout videoplayer_buffer;
	private boolean isBufferEnd = false;
	private VideoView vv;
	private String path = "";
	private String bookName;
	private MediaController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_vitamioplayer);
		if (getIntent().getStringExtra("filePath") != null && (!getIntent().getStringExtra("filePath").equals(""))) {
			path = getIntent().getStringExtra("filePath");
			Log.i("path", path);
		}
		if (getIntent().getStringExtra("bookName") != null) {
			bookName = getIntent().getStringExtra("bookName");

		}
		// 正在播放音乐
		if (SPUtility.getSPString(this, "isPlay").equals("true")) {
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(MusicPlayerService.TYPE_Customer);
			stopService(new Intent("com.jiayue.startservice"));
		}
		initView();
		controller = new MediaController(this);
//		controller.setFileName(bookName);
		setData();
		setOnListener();

		vv.setMediaController(controller);
		controller.setMediaPlayer(vv);
		vv.requestFocus();
//		vv.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);

//		vv.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				toggleMediaControlsVisiblity();
//			}
//
//		});
	}

//	private void toggleMediaControlsVisiblity() {
//		// TODO Auto-generated method stub
//		if (controller.isShowing())
//			controller.hide();
//		else
//			controller.show();
//	}

	private void initView() {
		vv = (VideoView) findViewById(R.id.vv);
		videoplayer_loading = (LinearLayout) findViewById(R.id.videoplayer_loading);
		videoplayer_buffer = (LinearLayout) findViewById(R.id.videoplayer_buffer);
	}

	private void setData() {
		vv.setVideoPath(path);
	}

	/**
	 * 设置各种监听
	 */
	private void setOnListener() {
		vv.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// 当这个方法回调的时候，就可以去播放
				vv.start();
				// 隐藏缓存等待页面
				videoplayer_loading.setVisibility(View.GONE);
				isBufferEnd = true;
//				toggleMediaControlsVisiblity();
			}
		});
		vv.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// 返回
				// finish();
			}
		});

		// 设置监听视频卡
		vv.setOnInfoListener(new OnInfoListener() {

			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				switch (what) {
					case MediaPlayer.MEDIA_INFO_BUFFERING_START:// 视频播放卡和视频拖动卡开始
						videoplayer_buffer.setVisibility(View.VISIBLE);
						isBufferEnd = false;

						break;

					case MediaPlayer.MEDIA_INFO_BUFFERING_END:// 视频播放卡和视频拖动卡结束:
						videoplayer_buffer.setVisibility(View.GONE);
						isBufferEnd = true;
						break;
				}
				return true;
			}
		});

//		vv.setOnSeekCompleteListener(new OnSeekCompleteListener() {
//
//			@Override
//			public void onSeekComplete(MediaPlayer mp) {
//				if (!isBufferEnd) {
//					videoplayer_buffer.setVisibility(View.GONE);
//				}
//
//			}
//		});

		// 设置监听播放出错
		vv.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// 处理的事情：一般处理-提示用户播放出错
				// 1.不支持的视频格式-跳转到万能播放器里面去
				// 2.下载的视频文件中中间有空白-播放器无法解决
				// 3.播放网络视频-中途没有网络-没有网络的时候提前提示-重新播放
				new AlertDialog.Builder(VideoPlayerActivity.this).setMessage("视频播放失败").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();// 退出播放器
					}
				}).setCancelable(false).show();
				return true;
			}
		});
	}

	@Override
	protected void onStop() {
		if (vv != null) {
			vv.stopPlayback();
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (controller.isShowing())
				controller.hide();
			else{
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		ActivityUtils.deleteBookFormSD(path);
		if (vv != null) {
			vv.stopPlayback();
		}

		super.onDestroy();
	}
}
