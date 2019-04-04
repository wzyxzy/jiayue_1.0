package com.jiayue.vr;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jiayue.R;
import com.jiayue.util.DialogUtils;
import com.jiayue.vr.seekbar.DiscreteSeekBar;
import com.snail.media.player.ISnailPlayer;
import com.snail.media.player.ISnailPlayer.EventType;
import com.snail.media.player.ISnailPlayer.ISnailPlayerErrorNotification;
import com.snail.media.player.ISnailPlayer.ISnailPlayerEventNotification;
import com.snail.media.player.ISnailPlayer.ISnailPlayerStateChangeNotification;

public class VRPlayActivity extends Activity /*implements OnClickListener*/{
	private static final String TAG = "VRPlayActivity";

	private SnailPlayerVideoView mVideoView;

	private ImageView mImageView_Back;
	private TextView mTextView_VideoUrl;

	private ImageView mImageView_PlayPause;
	private ImageView mImageView_Reload;

	private RelativeLayout mBufferingView;
	private TextView mTextViewBufferPercent;

	private final String mUrl = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
	private String mPlayUrl = "";

	private GestureDetector mGestureDetector = null;

	private static final int GESTURE_TYPE_NO = 0;
	private static final int GESTURE_TYPE_HRO = 1;
	private static final int GESTURE_TYPE_VER = 2;

	private static final int SNVR_PROJ_PLANE = 0;
	private static final int SNVR_PROJ_SPHERE = 1;
	private static final int SNVR_PROJ_DOME = 2;

	private static final int SNVR_VIDEO_SPLICE_FMT_2D = 0;
	private static final int SNVR_VIDEO_SPLICE_FMT_3D_SBS = 1;
	private static final int SNVR_VIDEO_SPLICE_FMT_3D_OVU = 2;

	public static final int SNVR_NAVIGATION_SENSOR = 0;
	public static final int SNVR_NAVIGATION_TOUCH = 1;
	public static final int SNVR_NAVIGATION_BOTH = 2;


    public static final int SCALE_05 = 0;
    public static final int SCALE_10 = 1;
    public static final int SCALE_20 = 2;

	public static final int SNVR_SINGLE_EYES_MODE = 0;
	public static final int SNVR_DOUBLE_EYES_MODE = 1;

	private int cur_gesture_type = GESTURE_TYPE_NO;
	private int cur_aspect_type = SnailPlayerVideoView.ASPECT_TYEP_AUTO_FIT;

	private boolean mIsPrepared = false;

	private boolean mIsVRMode = true;

	private boolean mIsLive = true;

	private boolean mShowing = true;

	private AudioManager mAudioManager;
	
    private static final int SHOW_PROGRESS = 2;

	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	/** 调节亮度和声音的控件 */
	private RelativeLayout mOperLayout;
	private ImageView mOperationBg;
	private TextView mOperTextView;

	// media_meta 信息展示页面
	private ImageView mImageView_MediaMeta;
	private TextView mTextView_MediaMeta;
	private String mMediaMeta;

	private ImageView mImageView_MediaInfo;
	private ImageView mImageView_ResetAngle;
	private TextView mTextView_MediaInfo;

    private TextView mErroText;

	// 网络处理
//	private SnailNetReceiver mNetReceiver;
//	private NetStateChangedListener mNetChangedListener;

	private RelativeLayout mLayoutPlayerControllerFull;

	private SeekBar mSeekBar;
	private TextView mCurrentTime;
	private TextView mEndTime;
    private int mDuration;
    private boolean mDragging;


	private int mFov;
	private int mProjectionType = VRPlayActivity.SNVR_PROJ_SPHERE;
	private int mVideoSpliceFormat = VRPlayActivity.SNVR_VIDEO_SPLICE_FMT_2D;
	private int mNavigationMode = VRPlayActivity.SNVR_NAVIGATION_SENSOR;
	private int mEyesMode = VRPlayActivity.SNVR_DOUBLE_EYES_MODE;

	private Button btn_ProjectionType,btn_VideoSpliceFormat,btn_EyesMode,btn_NavigationMode,btn_FORSET;
	private ImageView btn_back;
    private int mScale = SCALE_10;

	private int pausePostion;

    public static final int FOV_DEFAULT = 90;


    @SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG,"surfaceCreated");
		mPlayUrl = getIntent().getStringExtra(Definition.KEY_PLAY_URL);
		mFov = uiutils.getPreferenceKeyIntValue(getApplicationContext(),
				Definition.KEY_FOV, 90);
		mProjectionType = uiutils.getPreferenceKeyIntValue(
				getApplicationContext(), Definition.KEY_PROJECTIONTYPE,
				VRPlayActivity.SNVR_PROJ_PLANE);
		mVideoSpliceFormat = uiutils.getPreferenceKeyIntValue(
				getApplicationContext(), Definition.KEY_VIDEOSPLICEFORMAT,
				VRPlayActivity.SNVR_VIDEO_SPLICE_FMT_2D);
		 mNavigationMode = uiutils.getPreferenceKeyIntValue(
				 getApplicationContext(), Definition.KEY_SENSORMODE,
				 VRPlayActivity.SNVR_NAVIGATION_SENSOR);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_vrplay);
		
		
		getWindow().getDecorView().setSystemUiVisibility(
		            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
		            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		mVideoView = (SnailPlayerVideoView) findViewById(R.id.id_videoview);
		mVideoView.setVideoPlayerType(ISnailPlayer.PlayerType.PLAYER_TYPE_SNAIL_VR);
		mVideoView.setPlayFov(mFov);
		mVideoView.setProjectionType(mProjectionType);
		mVideoView.setNavigationmode(mNavigationMode);
		mVideoView.setVideoSpliceFormat(mVideoSpliceFormat);

		mVideoView.setScale(VRPlayActivity.SCALE_10);

        mErroText = (TextView)findViewById(R.id.txt_view_erro);

		mLayoutPlayerControllerFull = (RelativeLayout) findViewById(R.id.id_mediaplayer_controller);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle("Back");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
//		btn_ProjectionType = (Button) findViewById(R.id.projection_type);
//		btn_VideoSpliceFormat = (Button) findViewById(R.id.vsplice_format);
//		btn_EyesMode = (Button) findViewById(R.id.vn_eyesmode);
//		btn_NavigationMode = (Button) findViewById(R.id.sensor_mode);
//		btn_FORSET = (Button) findViewById(R.id.fov_set);
//		btn_back = (ImageView) findViewById(R.id.id_player_top_back_full);
//		btn_ProjectionType.setOnClickListener(this);
//		btn_VideoSpliceFormat.setOnClickListener(this);
//		btn_NavigationMode.setOnClickListener(this);
//		btn_EyesMode.setOnClickListener(this);
//		btn_FORSET.setOnClickListener(this);
//		btn_back.setOnClickListener(this);
//		initButton();
		

		mSeekBar = (SeekBar) findViewById(R.id.id_video_player_seekbar);
		mCurrentTime = (TextView) findViewById(R.id.id_video_player_current_time);
		mEndTime = (TextView) findViewById(R.id.id_video_player_total_time);
		mSeekBar.setThumbOffset(1);
		//mSeekBar.setMax(1000);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				 mVideoView.seekTo(seekBar.getProgress());
				 mHandler.removeMessages(SHOW_PROGRESS);
				 mDragging = false;
				 mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
				 mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (!fromUser)
	                return;
				//int newposition = (mDuration * progress) / 1000;
				String time = generateTime(progress);
				if (mCurrentTime != null)
					mCurrentTime.setText(time);
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
	            mHandler.removeMessages(SHOW_PROGRESS);
	            mDragging = true;
	            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
			}

		});



		mVideoView.setOnStatListener(new ISnailPlayerStateChangeNotification() {
			@Override
			public void notify(ISnailPlayer player, ISnailPlayer.State state) {

				if(state == ISnailPlayer.State.PLAYER_STARTED){

					mVideoView.start();
					int btn_selector_player_pause_big = getResources().getIdentifier("btn_selector_player_pause_big", "drawable", getPackageName());
					mImageView_PlayPause
							.setBackgroundResource(btn_selector_player_pause_big);
					mBufferingView.setVisibility(View.GONE);
					mIsPrepared = true;
					mDuration = mVideoView.getDuration();
					if (mDuration == 0) {
						mIsLive = true;
						mSeekBar.setEnabled(false);
					} else {
						mIsLive = false;
						mSeekBar.setEnabled(true);
					}
					mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);

					Log.d(TAG, "player duration :" + mDuration);

				}

			}
		});



		mVideoView.setOnEventListener(new ISnailPlayerEventNotification() {

			@Override
			public boolean notify(ISnailPlayer mp, ISnailPlayer.EventType what, int extra) {


				if(what == EventType.PLAYER_EVENT_BUFFERING){

					Log.i(TAG, "PLAYER_EVENT_BUFFERING");
					mBufferingView.setVisibility(View.VISIBLE);
				}else if(what == EventType.PLAYER_EVENT_BUFFERED){

					Log.i(TAG, "PLAYER_EVENT_BUFFERED");
					mBufferingView.setVisibility(View.GONE);
				}else if(what == EventType.PLAYER_EVENT_FINISHED){

					Log.d(TAG, "PLAYER_EVENT_FINISHED ");
					mIsPrepared = false;
					mBufferingView.setVisibility(View.GONE);
				}

				return true;
			}
		});

		mVideoView.setOnErrorListener(new ISnailPlayerErrorNotification() {

			@Override

			public void onError(ISnailPlayer mp, ISnailPlayer.ErrorType error, int extra) {

                mErroText.setText("error code:(" + error + "," + extra + ")");
                mErroText.setVisibility(View.VISIBLE);
//				Toast.makeText(PlayActivity.this,
//						"error code:(" + error + "," + extra + ")",
//						Toast.LENGTH_LONG).show();
				mBufferingView.setVisibility(View.GONE);
				mIsPrepared = false;
			}
		});
//
//		mVideoView
//				.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
//
//					@Override
//					public void onBufferingUpdate(IMediaPlayer mp, int percent) {
//						Log.i(TAG, "percent is:" + percent);
//						mTextViewBufferPercent.setText(percent + "%");
//					}
//				});
//
//		mVideoView.setOnCompletionListener(new OnCompletionListener() {
//			@Override
//			public void onCompletion(IMediaPlayer mp) {
//				mIsPrepared = false;
//				mBufferingView.setVisibility(View.GONE);
//			}
//		});
//

//
//		mVideoView
//				.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
//					@Override
//					public void onVideoSizeChanged(IMediaPlayer mp, int width,
//							int height, int sar_num, int sar_den) {
//						// TODO Auto-generated method stub
//					}
//				});

		mImageView_Back = (ImageView) findViewById(R.id.id_player_top_back_full);
		mImageView_Back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				VRPlayActivity.this.finish();
			}
		});

		mTextView_VideoUrl = (TextView) findViewById(R.id.id_textview_videourl);
		mTextView_VideoUrl.setSelected(true);

		mImageView_PlayPause = (ImageView) findViewById(R.id.id_imageview_play_pause_full);
		mImageView_PlayPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mVideoView.isPlaying()) {
					mVideoView.pause();
					int btn_selector_player_play_big = getResources().getIdentifier("btn_selector_player_play_big", "drawable", getPackageName());
					mImageView_PlayPause
							.setBackgroundResource(btn_selector_player_play_big);
				} else {
					mVideoView.start();
					int btn_selector_player_pause_big = getResources().getIdentifier("btn_selector_player_pause_big", "drawable", getPackageName());
					mImageView_PlayPause
							.setBackgroundResource(btn_selector_player_pause_big);
				}
			}
		});

		mImageView_Reload = (ImageView) findViewById(R.id.id_imageview_play_reload);
		mImageView_Reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mVideoView != null) {
					mVideoView.stopPlayback();
					mVideoView.setVideoPath(mPlayUrl);
					mBufferingView.setVisibility(View.VISIBLE);
				}
			}
		});

		mBufferingView = (RelativeLayout) findViewById(R.id.id_mediaplay_buffering_view);
		mTextViewBufferPercent = (TextView) findViewById(R.id.tv_buffering);
		mBufferingView.setVisibility(View.GONE);

		// 声音和亮度调节图标
		mOperLayout = (RelativeLayout) findViewById(R.id.layout_volume_bright_transparent);
		mOperLayout.setVisibility(View.GONE);
		mOperationBg = (ImageView) findViewById(R.id.video_player_voiceortranparent_img);
		mOperTextView = (TextView) findViewById(R.id.video_player_voiceortranparent_value);

		mGestureDetector = new GestureDetector(this, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				toggleMediaControlsVisiblity();
              //  if (mErroText.getVisibility() == View.VISIBLE)
               //     mErroText.setVisibility(View.GONE);
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				Log.i(TAG, "onScroll");

				float nFristX = e1.getX();
				float nFristY = e1.getY();

				// Log.i(TAG, " nFristX:" + nFristX + " nFristY:" + nFristY);

				int video_width = mVideoView.getWidth();
				int video_height = mVideoView.getHeight();
				Log.i(TAG, " video_width:" + video_width);

				float nCurrentX = e2.getRawX();
				float nCurrentY = e2.getRawY();

				int movePosX = (int) Math.abs(distanceX);
				int movePosY = (int) Math.abs(distanceY);

				// Log.i(TAG, "movePosX:" + movePosX + " movePosY:" + movePosY);

				if (mNavigationMode != SNVR_NAVIGATION_SENSOR) {
					float phi = distanceX * 360 / video_width;
					float theta = distanceY * mFov / video_height;
					mVideoView.setTouchInfo(phi, theta);

				} else {
					getGestureDirection(movePosX, movePosY);

					if (cur_gesture_type == GESTURE_TYPE_VER) {
						float _percent = (nFristY - nCurrentY) / video_height;

						if (nFristX > video_width / 2) {
							Log.i(TAG, "right");
							onVolumeSlide(_percent);
						} else {
							Log.i(TAG, "Left");
//							onBrightnessSlide(_percent);
						}
					} else if (cur_gesture_type == GESTURE_TYPE_HRO) {
						Log.i(TAG, "横向移动");
					}

				}

				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				return false;
			}
		});

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		mImageView_MediaMeta = (ImageView) findViewById(R.id.id_imageview_media_meta);
		mImageView_MediaMeta.setEnabled(false);
		mImageView_MediaMeta.setVisibility(View.GONE);
		mImageView_MediaMeta.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTextView_MediaMeta.getVisibility() != View.VISIBLE) {
					mTextView_MediaMeta.setText(mMediaMeta);
					mTextView_MediaMeta.setVisibility(View.VISIBLE);
				} else {
					mTextView_MediaMeta.setVisibility(View.GONE);
				}
			}
		});

		mImageView_MediaInfo = (ImageView) findViewById(R.id.id_imageview_media_info);
		mImageView_MediaInfo.setVisibility(View.GONE);
		mImageView_MediaInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTextView_MediaInfo.getVisibility() != View.VISIBLE) {
					mTextView_MediaInfo.setVisibility(View.VISIBLE);
				} else {
					mTextView_MediaInfo.setVisibility(View.GONE);
				}
			}
		});

		mImageView_ResetAngle = (ImageView) findViewById(R.id.id_imageview_resetAngle);
		mImageView_ResetAngle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVideoView.setOriginalAngle();
			}
		});
/*
		mNetReceiver = SnailNetReceiver.getInstance();
		mNetChangedListener = new NetStateChangedListener() {

			@Override
			public void onNetStateChanged(NetState netCode) {
				switch (netCode) {
				case NET_NO:
					// Log.i(Constants.LOG_TAG, "网络断了");
					Toast.makeText(PlayActivity.this,
							"network changed: no network available!",
							Toast.LENGTH_SHORT).show();
					break;
				case NET_2G:
					// Log.i(Constants.LOG_TAG, "2g网络");
					Toast.makeText(PlayActivity.this,
							"network changed: 2g network!", Toast.LENGTH_SHORT)
							.show();
					break;
				case NET_3G:
					// Log.i(Constants.LOG_TAG, "3g网络");
					Toast.makeText(PlayActivity.this,
							"network changed: 3g network!", Toast.LENGTH_SHORT)
							.show();
					break;
				case NET_4G:
					// Log.i(Constants.LOG_TAG, "4g网络");
					Toast.makeText(PlayActivity.this,
							"network changed: 4g network!", Toast.LENGTH_SHORT)
							.show();
					break;
				case NET_WIFI:
					// Log.i(Constants.LOG_TAG, "WIFI网络");
					Toast.makeText(PlayActivity.this,
							"network changed: wifi network!",
							Toast.LENGTH_SHORT).show();
					break;

				case NET_UNKNOWN:
					// Log.i(Constants.LOG_TAG, "未知网络");
					Toast.makeText(PlayActivity.this,
							"network changed: unknown network!",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					// Log.i(Constants.LOG_TAG, "不知道什么情况~>_<~");
					Toast.makeText(PlayActivity.this,
							"network changed: unknown conditions ???",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	*/	
		Log.d(TAG, "mPlayUrl=-"+mPlayUrl);
		mVideoView.setVideoPath(mPlayUrl);

	}

	private void initButton() {
		// TODO Auto-generated method stub
		
		if (SNVR_PROJ_PLANE == mProjectionType) {
			int vn_projection_plane = getResources().getIdentifier("vn_projection_plane", "drawable", getPackageName());
			btn_ProjectionType.setBackgroundResource(vn_projection_plane);
		} else if (SNVR_PROJ_SPHERE == mProjectionType) {
			int vn_projection_sphere = getResources().getIdentifier("vn_projection_sphere", "drawable", getPackageName());
			btn_ProjectionType.setBackgroundResource(vn_projection_sphere);
		} else if (SNVR_PROJ_DOME == mProjectionType) {
			int vn_projection_dome = getResources().getIdentifier("vn_projection_dome", "drawable", getPackageName());
			btn_ProjectionType.setBackgroundResource(vn_projection_dome);
		}

		if (SNVR_VIDEO_SPLICE_FMT_2D == mVideoSpliceFormat) {
			int vn_display_mono = getResources().getIdentifier("vn_display_mono", "drawable", getPackageName());
			btn_VideoSpliceFormat.setBackgroundResource(vn_display_mono);
		} else if (SNVR_VIDEO_SPLICE_FMT_3D_SBS == mVideoSpliceFormat) {
			int vn_display_side_by_side = getResources().getIdentifier("vn_display_side_by_side", "drawable", getPackageName());
			btn_VideoSpliceFormat.setBackgroundResource(vn_display_side_by_side);
		} else if (SNVR_VIDEO_SPLICE_FMT_3D_OVU == mVideoSpliceFormat) {
			int vn_display_over_under = getResources().getIdentifier("vn_display_over_under", "drawable", getPackageName());
			btn_VideoSpliceFormat.setBackgroundResource(vn_display_over_under);
		}

		if (SNVR_SINGLE_EYES_MODE == mEyesMode) {
			int vn_double_eye = getResources().getIdentifier("vn_double_eye", "drawable", getPackageName());
			btn_EyesMode.setBackgroundResource(vn_double_eye);
		} else if (SNVR_DOUBLE_EYES_MODE == mEyesMode) {
			int vn_double_eye_light = getResources().getIdentifier("vn_double_eye_light", "drawable", getPackageName());
			btn_EyesMode.setBackgroundResource(vn_double_eye_light);
		}

		if (SNVR_NAVIGATION_SENSOR == mNavigationMode) {
			int sensor = getResources().getIdentifier("sensor", "drawable", getPackageName());
			btn_NavigationMode.setBackgroundResource(sensor);
		} else if (SNVR_NAVIGATION_BOTH == mNavigationMode) {
			int both = getResources().getIdentifier("both", "drawable", getPackageName());
			btn_NavigationMode.setBackgroundResource(both);
		} else if (SNVR_NAVIGATION_TOUCH == mNavigationMode) {
			int touch = getResources().getIdentifier("touch", "drawable", getPackageName());
			btn_NavigationMode.setBackgroundResource(touch);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem menuItem = menu.findItem(R.id.projection_type);
		if (SNVR_PROJ_PLANE == mProjectionType) {
			int vn_projection_plane = getResources().getIdentifier("vn_projection_plane", "drawable", getPackageName());
			menuItem.setIcon(vn_projection_plane);
		} else if (SNVR_PROJ_SPHERE == mProjectionType) {
			int vn_projection_sphere = getResources().getIdentifier("vn_projection_sphere", "drawable", getPackageName());
			menuItem.setIcon(vn_projection_sphere);
		} else if (SNVR_PROJ_DOME == mProjectionType) {
			int vn_projection_dome = getResources().getIdentifier("vn_projection_dome", "drawable", getPackageName());
			menuItem.setIcon(vn_projection_dome);
		}

		menuItem = menu.findItem(R.id.vsplice_format);
		if (SNVR_VIDEO_SPLICE_FMT_2D == mVideoSpliceFormat) {
			int vn_display_mono = getResources().getIdentifier("vn_display_mono", "drawable", getPackageName());
			menuItem.setIcon(vn_display_mono);
		} else if (SNVR_VIDEO_SPLICE_FMT_3D_SBS == mVideoSpliceFormat) {
			int vn_display_side_by_side = getResources().getIdentifier("vn_display_side_by_side", "drawable", getPackageName());
			menuItem.setIcon(vn_display_side_by_side);
		} else if (SNVR_VIDEO_SPLICE_FMT_3D_OVU == mVideoSpliceFormat) {
			int vn_display_over_under = getResources().getIdentifier("vn_display_over_under", "drawable", getPackageName());
			menuItem.setIcon(vn_display_over_under);
		}

		menuItem = menu.findItem(R.id.vn_eyesmode);
		if (SNVR_SINGLE_EYES_MODE == mEyesMode) {
			int vn_double_eye = getResources().getIdentifier("vn_double_eye", "drawable", getPackageName());
			menuItem.setIcon(vn_double_eye);
		} else if (SNVR_DOUBLE_EYES_MODE == mEyesMode) {
			int vn_double_eye_light = getResources().getIdentifier("vn_double_eye_light", "drawable", getPackageName());
			menuItem.setIcon(vn_double_eye_light);
		}

		menuItem = menu.findItem(R.id.sensor_mode);
		if (SNVR_NAVIGATION_SENSOR == mNavigationMode) {
			int sensor = getResources().getIdentifier("sensor", "drawable", getPackageName());
			menuItem.setIcon(sensor);
		} else if (SNVR_NAVIGATION_BOTH == mNavigationMode) {
			int both = getResources().getIdentifier("both", "drawable", getPackageName());
			menuItem.setIcon(both);
		} else if (SNVR_NAVIGATION_TOUCH == mNavigationMode) {
			int touch = getResources().getIdentifier("touch", "drawable", getPackageName());
			menuItem.setIcon(touch);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		case R.id.projection_type:
			popProjectionTypeDialog(item);
			break;
		case R.id.vsplice_format:
			popDisplayModeDialog(item);
			break;
		case R.id.vn_eyesmode:
            popEyesModeDialog(item);
			break;
		case R.id.sensor_mode:
			popSensorModeDialog(item);
			break;
            case R.id.fov_set:
                popFovSetDialog(item);
                break;
			case R.id.scale_set:
				popScaleDialog(item);
				break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

    private void popFovSetDialog(final MenuItem item) {

            DialogUtils.showSelectFovDialog(this, new DiscreteSeekBar.OnProgressChangeListener() {
                        @Override
                        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
							if(fromUser) {
								mVideoView.setPlayFov(value);
							}

                        }

                        @Override
                        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
//                            mVideoView.setPlayFov(seekBar.getValue());
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int angle = (int) v.getTag();
                            mFov = angle;
                            mVideoView.setPlayFov(mFov);
                        }
                    }
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mVideoView.setPlayFov(mFov);
                        }
                    });

    }


	private void popScaleDialog(final MenuItem item) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(new String[] { "X0.5", "X1.0",
						"X2.0" }, mScale,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (0 == which) {
							int x_05 = getResources().getIdentifier("x_05", "drawable", getPackageName());
							item.setIcon(x_05);
                        } else if (1 == which) {
                			int x_10 = getResources().getIdentifier("x_10", "drawable", getPackageName());
							item.setIcon(x_10);
                        } else if (2 == which) {
                			int x_20 = getResources().getIdentifier("x_20", "drawable", getPackageName());
							item.setIcon(x_20);
						}
                        mScale = which;
						mVideoView.setScale(mScale);
						uiutils.setPreferenceKeyIntValue(
								getApplicationContext(),
								Definition.KEY_SCALE, mScale);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("Cancel", null);
		AlertDialog myDialog = builder.create();
		myDialog.setTitle("Select Scale Mode");
		myDialog.show();

	}

	private void popSensorModeDialog(final MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(new String[] { "Sensor", "Touch",
				/*"Double Sensor"*/ }, mNavigationMode,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (VRPlayActivity.SNVR_NAVIGATION_TOUCH == which) {
                			int touch = getResources().getIdentifier("touch", "drawable", getPackageName());
							item.setIcon(touch);
						} else if (VRPlayActivity.SNVR_NAVIGATION_BOTH == which) {
                			int both = getResources().getIdentifier("both", "drawable", getPackageName());
							item.setIcon(both);
						} else if (VRPlayActivity.SNVR_NAVIGATION_SENSOR == which) {
                			int sensor = getResources().getIdentifier("sensor", "drawable", getPackageName());
							item.setIcon(sensor);
						}
						mNavigationMode = which;
						mVideoView.setNavigationmode(mNavigationMode);
						uiutils.setPreferenceKeyIntValue(
								getApplicationContext(),
								Definition.KEY_SENSORMODE, mNavigationMode);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("Cancel", null);
		AlertDialog myDialog = builder.create();
		myDialog.setTitle("Select Sensor Mode");
		myDialog.show();

	}

	private void popEyesModeDialog(final MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(new String[] { "single eyes",
				"double eyes" }, mEyesMode,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (VRPlayActivity.SNVR_SINGLE_EYES_MODE == which) {
                			int vn_double_eye = getResources().getIdentifier("vn_double_eye", "drawable", getPackageName());
							item.setIcon(vn_double_eye);
						} else if (VRPlayActivity.SNVR_DOUBLE_EYES_MODE == which) {
                			int vn_double_eye_light = getResources().getIdentifier("vn_double_eye_light", "drawable", getPackageName());
							item.setIcon(vn_double_eye_light);
						}

						mEyesMode = which;
						mVideoView.setEyesMode(mEyesMode);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("Cancel", null);
		AlertDialog myDialog = builder.create();
		myDialog.setTitle("Select Eyes Mode");
		myDialog.show();

	}

	private void popProjectionTypeDialog(final MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(
				new String[] { "小星球", "360°"/*, "Dome" */}, mProjectionType,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (VRPlayActivity.SNVR_PROJ_PLANE == which) {
                			int vn_projection_plane = getResources().getIdentifier("vn_projection_plane", "drawable", getPackageName());
							item.setIcon(vn_projection_plane);
						} else if (VRPlayActivity.SNVR_PROJ_SPHERE == which) {
                			int vn_projection_sphere = getResources().getIdentifier("vn_projection_sphere", "drawable", getPackageName());
							item.setIcon(vn_projection_sphere);
						} else if (VRPlayActivity.SNVR_PROJ_DOME == which) {
                			int vn_projection_dome = getResources().getIdentifier("vn_projection_dome", "drawable", getPackageName());
							item.setIcon(vn_projection_dome);
						}

						mProjectionType = which;
						Log.d(TAG, "selected projection = " + mProjectionType);
						mVideoView.setProjectionType(mProjectionType);
						uiutils.setPreferenceKeyIntValue(
								getApplicationContext(),
								Definition.KEY_PROJECTIONTYPE, mProjectionType);
						dialog.dismiss();
					}
				});

		builder.setNegativeButton("Cancel", null);
		AlertDialog myDialog = builder.create();
		myDialog.setTitle("Select projection type");
		myDialog.show();
	}

	private void popDisplayModeDialog(final MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(new String[] { "2D", "3D-左右",
				/*"3D Over/Under" */}, mVideoSpliceFormat,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (VRPlayActivity.SNVR_VIDEO_SPLICE_FMT_2D == which) {
                			int vn_display_mono = getResources().getIdentifier("vn_display_mono", "drawable", getPackageName());
							item.setIcon(vn_display_mono);
						} else if (VRPlayActivity.SNVR_VIDEO_SPLICE_FMT_3D_SBS == which) {
                			int vn_display_side_by_side = getResources().getIdentifier("vn_display_side_by_side", "drawable", getPackageName());
							item.setIcon(vn_display_side_by_side);
						} else if (VRPlayActivity.SNVR_VIDEO_SPLICE_FMT_3D_OVU == which) {
                			int vn_display_over_under = getResources().getIdentifier("vn_display_over_under", "drawable", getPackageName());
							item.setIcon(vn_display_over_under);
						}
						mVideoSpliceFormat = which;
						mVideoView.setVideoSpliceFormat(mVideoSpliceFormat);
						uiutils.setPreferenceKeyIntValue(
								getApplicationContext(),
								Definition.KEY_VIDEOSPLICEFORMAT,
								mVideoSpliceFormat);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("Cancel", null);
		AlertDialog myDialog = builder.create();
		myDialog.setTitle("Select Format");
		myDialog.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
//		mBufferingView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
//		mNetReceiver.registNetBroadCast(this);
//		mNetReceiver.addNetStateChangeListener(mNetChangedListener);



		if(mVideoView != null){

			mVideoView.setPlayFov(mFov);
			mVideoView.setScale(mScale);

			if(mVideoView.IsSurfaceHolderValid()){

				mVideoView.resetUrl();
			}

		}

		super.onResume();
	}

	@Override
	protected void onPause() {
//		mNetReceiver.remoteNetStateChangeListener(mNetChangedListener);
//		mNetReceiver.unRegistNetBroadCast(this);
		Log.d(TAG,"onPause()");
//		pausePostion = mVideoView.getCurrentPosition();
//		mVideoView.pause();
		super.onPause();
	}

	@Override
	protected void onStop() {
//		mNetReceiver.remoteNetStateChangeListener(mNetChangedListener);
//		mNetReceiver.unRegistNetBroadCast(this);
		Log.d(TAG,"onPause()");
		pausePostion = mVideoView.getCurrentPosition();
		mVideoView.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        mHandler.removeMessages(SHOW_PROGRESS);
		if (mVideoView != null) {			
			mVideoView.stopPlayback();
			mVideoView = null;
		}		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;
		cur_gesture_type = GESTURE_TYPE_NO;
		mOperLayout.setVisibility(View.GONE);
	}

	private void getGestureDirection(float nX, float nY) {

		if (cur_gesture_type == GESTURE_TYPE_NO) {
			if (nX == 0) {
				if (nY > 15) {
					cur_gesture_type = GESTURE_TYPE_VER;
				}
			} else if (nY == 0) {
				if (nX > 5) {
					cur_gesture_type = GESTURE_TYPE_HRO;
				}
			} else {
				if (nX / nY > 3) {
					cur_gesture_type = GESTURE_TYPE_HRO;
				} else if (nY / nX > 3) {
					cur_gesture_type = GESTURE_TYPE_VER;
				}
			}
		}
	}

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;
			// 显示
			int video_player_voice = getResources().getIdentifier("video_player_voice", "drawable", getPackageName());
			mOperationBg.setImageResource(video_player_voice);
			mOperLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		int present = index * 100 / mMaxVolume;
		Log.i(TAG, "present is:" + present);
		mOperTextView.setText(String.valueOf(present) + "%");
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// 显示
			int video_player_bright = getResources().getIdentifier("video_player_bright", "drawable", getPackageName());
			mOperationBg.setImageResource(video_player_bright);
			mOperLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		int present = (int) (lpa.screenBrightness * 100);
		Log.i(TAG, "present is:" + present);
		mOperTextView.setText(String.valueOf(present) + "%");

	}

	private void toggleMediaControlsVisiblity() {
		if (mLayoutPlayerControllerFull.getVisibility() == View.VISIBLE) {
			mLayoutPlayerControllerFull.setVisibility(View.GONE);
			getActionBar().hide();
			mShowing = false;
            mHandler.removeMessages(SHOW_PROGRESS);
		} else {
			mLayoutPlayerControllerFull.setVisibility(View.VISIBLE);
			getActionBar().show();
			mShowing = true;
	        updatePausePlay();
	        mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
	}

	private static String generateTime(long position) {
		int totalSeconds = (int) (position / 1000);

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		if (hours > 0) {
			return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
					seconds).toString();
		} else {
			return String.format(Locale.US, "%02d:%02d", minutes, seconds)
					.toString();
		}
	}
	
    private void updatePausePlay() {
        if (mVideoView.isPlaying()) {
			int btn_selector_player_pause_big = getResources().getIdentifier("btn_selector_player_pause_big", "drawable", getPackageName());
            mImageView_PlayPause.setBackgroundResource(btn_selector_player_pause_big);
        }
        else {
			int btn_selector_player_play_big = getResources().getIdentifier("btn_selector_player_play_big", "drawable", getPackageName());
        	mImageView_PlayPause.setBackgroundResource(btn_selector_player_play_big);
        }
    }

	
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            if (msg.what == SHOW_PROGRESS) {
                pos = setProgress();
				mSeekBar.setMax(mVideoView.getDuration());
                if (!mDragging && mShowing) {
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    updatePausePlay();
                }
           
            }
        }
    };

	private long setProgress() {
		if (mVideoView == null || mDragging)
			return 0;

		int position = mVideoView.getCurrentPosition();
		int duration = mVideoView.getDuration();
		if (mSeekBar != null) {
			if (duration > 0) {
				//long pos = 1000L * position / duration;
				mSeekBar.setProgress(position);
			}
			int percent = mVideoView.getBufferPercentage();
			mSeekBar.setSecondaryProgress(percent);
		}

		mDuration = duration;

		if (mEndTime != null)
			mEndTime.setText(generateTime(mDuration));
		if (mCurrentTime != null)
			mCurrentTime.setText(generateTime(position));

		return position;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	        super.onWindowFocusChanged(hasFocus);
	    if (hasFocus) {
	        getWindow().getDecorView().setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	}

//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//			case R.id.id_player_top_back_full:
//				finish();
//				break;
//			case R.id.projection_type:
//				popProjectionTypeDialog(btn_ProjectionType);
//				break;
//			case R.id.vsplice_format:
//				popDisplayModeDialog(btn_VideoSpliceFormat);
//				break;
//			case R.id.vn_eyesmode:
//				popEyesModeDialog(btn_EyesMode);
//				break;
//			case R.id.sensor_mode:
//				popSensorModeDialog(btn_NavigationMode);
//				break;
//			case R.id.fov_set:
//				popFovSetDialog(btn_FORSET);
//				break;
////			case R.id.scale_set:
////				popScaleDialog(item);
////				break;
//			default:
//				break;
//		}
//	}
	

}
