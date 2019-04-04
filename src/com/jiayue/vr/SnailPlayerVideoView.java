package com.jiayue.vr;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import com.jiayue.util.SPUtility;
import com.snail.media.player.ISnailPlayer;
import com.snail.media.player.ISnailPlayer.ISnailPlayerErrorNotification;
import com.snail.media.player.ISnailPlayer.ISnailPlayerEventNotification;
import com.snail.media.player.ISnailPlayer.ISnailPlayerStateChangeNotification;
import com.snail.media.player.ISnailPlayer.PlayerType;
import com.snail.media.player.SnailPlaybackInfo;
import com.snail.media.player.SnailPlayer;
import com.snail.media.player.pragma.DebugLog;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */
public class SnailPlayerVideoView extends SurfaceView implements
		MediaPlayerControl{
	private String TAG = "VideoViewEx";

	private Context mContext;

	// settable by the client
	private Uri mUri;
	private long mDuration;

	// All the stuff we need for playing and showing a video
	private SurfaceHolder mSurfaceHolder = null;
	private ISnailPlayer mMediaPlayer = null;
	private boolean mIsPrepared;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private MediaController mMediaController = null;
	private ISnailPlayerStateChangeNotification mOnStatListener;
	private ISnailPlayerEventNotification mOnEventListener;
	private ISnailPlayerErrorNotification mOnErrorListener;

	private int mSeekWhenPrepared;
	private int mBufferPercentage = 0;

	public static final int SOFT_VIDEO_DECODE = 0;
	public static final int HARD_VIDEO_DECODE = 1;

	public static Choreographer mChoreographer;

	private int mVideoDecodeMode = 1;
	private PlayerType mVideoPlayerType = PlayerType.PLAYER_TYPE_SNAIL_VR;

	public static final int ASPECT_TYEP_AUTO_FIT = 0;
	public static final int ASPECT_TYEP_16_9_FIT = 1;
	public static final int ASPECT_TYEP_4_3_FIT  = 2;
	public static final int ASPECT_TYEP_FULL_FIT = 3;
	
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

	private float auto_aspect_ratio;



	private double mScale = 1.0;
	private long mFov = 90;
	private int mProjectionType = ISnailPlayer.VRCtrlValue.VR_DP_PLANE;
	private int mVideoSpliceFormat = ISnailPlayer.VRCtrlValue.VR_SF_2D;
	private int mEyesMode = ISnailPlayer.VRCtrlValue.VR_DOUBLE_EYE;
	private int mNavigationMode = ISnailPlayer.VRCtrlValue.VR_NV_GYROSCAPE_TOUCH;

	private int mVideoSarNum;

	private int mVideoSarDen;
		    

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

	public void setPlayFov(int _fov) {
		mFov = (long) _fov;
        if (mMediaPlayer!=null)
		    mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SET_FOV, mFov);
	}

	public boolean IsSurfaceHolderValid() {

		if(mSurfaceHolder == null){
			return false;
		}

		return true;
	}

    public void setScale(int scale) {
		mScale = scale;
		double _scale;
        if (mMediaPlayer != null) {
            switch (scale) {
                case VRPlayActivity.SCALE_05:
                    _scale = 0.5;
                    break;
                case VRPlayActivity.SCALE_10:
                    _scale = 1.0;
                    break;
                case VRPlayActivity.SCALE_20:
                    _scale = 2.0;
                    break;
                default:
                    _scale = 1.0;
            }

			mScale = _scale;
            mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SET_SCALE_FACTOR, _scale);
        }
    }

	public void setProjectionType(int _projectionType) {
		mProjectionType =  _projectionType;

		if (mMediaPlayer != null) {
			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_DISPLAY_PROJECTION, mProjectionType);

		}
	}

	public void setVideoSpliceFormat(int _videoSpliceFormat) {
		mVideoSpliceFormat =  _videoSpliceFormat;
		if (mMediaPlayer != null) {
			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SPLICE_FORMAT, mVideoSpliceFormat);
		}
	}

	public void setEyesMode(int _eyesMode) {
		mEyesMode =  _eyesMode;

		if (mMediaPlayer != null) {
			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_EYE_MODE, mEyesMode);
		}

	}

	public void setVideoAspect(int _parent_width, int _parent_height,
			int aspect_mode) {
		LayoutParams lp = getLayoutParams();
		// View parent = (View)getParent();

		// int _parent_width ;
		// int _parent_height ;

		// _parent_width = parent.getMeasuredWidth();
		// _parent_height = parent.getMeasuredHeight();

		float aspect_ratio;
		switch (aspect_mode) {
		case ASPECT_TYEP_AUTO_FIT: {
			aspect_ratio = auto_aspect_ratio;
			if (_parent_height * aspect_ratio > _parent_width) {
				lp.height = (int) (_parent_width / aspect_ratio);
				lp.width = _parent_width;
			} else {
				lp.width = (int) (_parent_height * aspect_ratio);
				lp.height = _parent_height;
			}
		}
			break;
		case ASPECT_TYEP_16_9_FIT: {
			aspect_ratio = 16f / 9;
			if (_parent_height * aspect_ratio > _parent_width) {
				lp.height = (int) (_parent_width / aspect_ratio);
				lp.width = _parent_width;
			} else {
				lp.width = (int) (_parent_height * aspect_ratio);
				lp.height = _parent_height;
			}
		}
			break;
		case ASPECT_TYEP_4_3_FIT: {
			aspect_ratio = 4f / 3;
			if (_parent_height * aspect_ratio > _parent_width) {
				lp.height = (int) (_parent_width / aspect_ratio);
				lp.width = _parent_width;
			} else {
				lp.width = (int) (_parent_height * aspect_ratio);
				lp.height = _parent_height;
			}
		}
			break;
		case ASPECT_TYEP_FULL_FIT: {
			lp.height = LayoutParams.MATCH_PARENT;
			lp.width = LayoutParams.MATCH_PARENT;
		}
			break;
		default: {
			lp.height = LayoutParams.MATCH_PARENT;
			lp.width = LayoutParams.MATCH_PARENT;
		}
			break;
		}

		setLayoutParams(lp);
	}

	public void setVideoScaleSize() {
		LayoutParams lp = getLayoutParams();
		/*DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
		int windowWidth = disp.widthPixels, windowHeight = disp.heightPixels;
		int sarNum = mVideoSarNum;
		int sarDen = mVideoSarDen;
		float windowRatio = windowWidth / (float) windowHeight;

		Log.d(TAG, "before setVideoScaleSize(), mmVideoWidth:" +  mVideoWidth +" mVideoHeight:" + mVideoHeight );
		Log.d(TAG, "before setVideoScaleSize(), windowWidth:" + windowWidth + " windowHeight:" + windowHeight  );
		Log.d(TAG, "before setVideoScaleSize(), sarNum:" + sarNum + " sarDen:" + sarDen);
		Log.d(TAG, "before setVideoScaleSize(), lp.width:" + lp.width + " lp.height:" + lp.height);
		Log.d(TAG, "before setVideoScaleSize(), mSurfaceWidth:" + mSurfaceWidth + " mSurfaceHeight:" + mSurfaceHeight);*/

//
//		if (mVideoHeight > 0 && mVideoWidth > 0) {
//			float videoRatio = ((float) (mVideoWidth)) / mVideoHeight;
//			if (sarNum > 0 && sarDen > 0)
//				videoRatio = videoRatio * sarNum / sarDen;
//			mSurfaceHeight = mVideoHeight;
//			mSurfaceWidth = mVideoWidth;
//
//
//			boolean full = true;
//			lp.width = (full || windowRatio < videoRatio) ? windowWidth
//					: (int) (videoRatio * windowHeight);
//			lp.height = (full || windowRatio > videoRatio) ? windowHeight
//					: (int) (windowWidth / videoRatio);
//		}

		lp.width  = mSurfaceWidth;
		lp.height = mSurfaceHeight;;

		setLayoutParams(lp);
//		getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);

		Log.d(TAG, "after setVideoScaleSize(), lp.width:" + lp.width + " lp.height:" + lp.height);
		Log.d(TAG, "after setVideoScaleSize(), mSurfaceWidth:" + mSurfaceWidth + " mSurfaceHeight:" + mSurfaceHeight);

				
	}

	public void setOnStatListener(ISnailPlayerStateChangeNotification l) {
		mOnStatListener = l;
	}

	public void setOnEventListener(ISnailPlayerEventNotification l) {
		mOnEventListener = l;
	}


	public void setOnErrorListener(ISnailPlayerErrorNotification l) {
		mOnErrorListener = l;
	}



	public SnailPlayerVideoView(Context context) {
		super(context);
		mContext = context;
		initVideoView();
	}

	public SnailPlayerVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
		initVideoView();
	}

	public SnailPlayerVideoView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initVideoView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = desiredSize;
			break;

		case MeasureSpec.AT_MOST:
			/*
			 * Parent says we can be as big as we want, up to specSize. Don't be
			 * larger than specSize, and don't be larger than the max size
			 * imposed on ourselves.
			 */
			result = Math.min(desiredSize, specSize);
			break;

		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void initVideoView() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(mSHCallback);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        
		mChoreographer = Choreographer.getInstance();
		setVSync(true);
	}

	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri) {
		mUri = uri;
		mSeekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
	}

	public void stopPlayback() {
		if (mMediaPlayer != null) {
			Log.d(TAG, "stopPlayback");
			mMediaPlayer.stop();
			mMediaPlayer.shutdown();
			mMediaPlayer = null;
	
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
			if (SPUtility.getSPBoolean(mContext,Definition.LOG_SETTING,true))
				LogcatFileManager.getInstance().stopLogcatManager();
		}
		setVSync(false);
	}

	public void stop(){
		if (mMediaPlayer != null) {
			Log.d(TAG, "stop");
			mSeekWhenPrepared = getCurrentPosition();
			mMediaPlayer.stop();
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
		}
	}

	public void resetUrl(){
		if (mMediaPlayer != null) {
			Log.d(TAG, "stop");

			if (mUri != null) {

				mMediaPlayer.resetUrl(mUri.toString());

			}

		}
	}

	public void setVideoDecodeMode(int videodecode) {
		if (mMediaPlayer != null && mMediaPlayer.getStat() == ISnailPlayer.State.PLAYER_PLAYING) {
			if (videodecode != SOFT_VIDEO_DECODE
					&& videodecode != HARD_VIDEO_DECODE)
				return;
			if (videodecode != mVideoDecodeMode) {
				mVideoDecodeMode = videodecode;
				this.stopPlayback();
				this.openVideo();
			}
		}
	}

	public void setVideoPlayerType(ISnailPlayer.PlayerType playerType) {
		mVideoPlayerType = playerType;
	}

	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null) {
			// not ready for playback just yet, will try again later
			return;
		}

		AudioManager am = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		am.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);

		if (mMediaPlayer != null) {
			Log.d(TAG, "release");

			mMediaPlayer.stop();
			mMediaPlayer.shutdown();
			mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
		}
		{
			mMediaPlayer = SnailPlayer.createPlayer(mContext,mVideoPlayerType);

			if (SPUtility.getSPBoolean(mContext,Definition.LOG_SETTING,true)) {
				mMediaPlayer.setLogLevel(ISnailPlayer.LogLevel.PLAYER_LOG_DEBUG);
			}


			DebugLog.setLogFlag(DebugLog.DEBUG_LOG_FLAG_ALWAYS_PRINT | DebugLog.DEBUG_LOG_FLAG_APPEND_TIME | DebugLog.DEBUG_LOG_FLAG_APPEND_THREADID);

			String folderPath = null;

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "SnailVRPlayer/Logcat";
			} else {
				folderPath = mContext.getFilesDir().getAbsolutePath() + File.separator + "SnailVRPlayer/Logcat";
			}


			File folder = new File(folderPath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			if (!folder.isDirectory()) {
				Log.e(TAG,"The logcat folder path is not a directory: " + folderPath);
				//throw new IllegalArgumentException("The logcat folder path is not a directory: " + folderPath);
			}

			folderPath += "/snail-sdk-test.log";


			DebugLog.setLogFile(folderPath, 2*1024 * 1024);

			//	LogcatFileManager.getInstance().startLogcatManager(mContext);
			//}

			mMediaPlayer.setDecoderOption(ISnailPlayer.DecoderType.PLAYER_DECODER_HARDWARE);


			mMediaPlayer.setLoop(true);

			mMediaPlayer.setSnailPlayerStateChangeNotificationListener(mStatListener);
			mMediaPlayer.setSnailPlayerEventNotificationListener(mEventListener);
			mMediaPlayer.setSnailPlayerErrorNotificationListener(mErrorListener);

			mIsPrepared = false;
			Log.v(TAG, "reset duration to -1 in openVideo");
			mDuration = -1;


			mBufferPercentage = 0;

			if (mUri != null) {
				if(mMediaPlayer.setUrl(mUri.toString()) != 0){

					mCurrentState = STATE_ERROR;
					mTargetState = STATE_ERROR;
					return;
				}

			}
			// before set holder to set vr parameter


			mMediaPlayer.setView(mSurfaceHolder.getSurface());

			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_NAVIGATION_MODE, mNavigationMode);

			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_DISPLAY_PROJECTION, mProjectionType);

			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_EYE_MODE, mEyesMode);
			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SPLICE_FORMAT, mVideoSpliceFormat);
			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SET_FOV, mFov);
			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SET_SCALE_FACTOR, mScale);
			
			//mMediaPlayer.setPlayerOption(ISnailPlayer.SnailPlayerOption.PLAYER_OPT_CONNECT_TIMEOUT, 8);
			//mMediaPlayer.setPlayerOption(ISnailPlayer.SnailPlayerOption.PLAYER_OPT_LOW_LATENCY_MODE, 1);
			//mMediaPlayer.setPlayerOption(ISnailPlayer.SnailPlayerOption.PLAYER_OPT_1ST_CACHED_BUFFER_SIZE, 3000);
			//mMediaPlayer.setPlayerOption(ISnailPlayer.SnailPlayerOption.PLAYER_OPT_2ND_CACHED_BUFFER_SIZE, 8000);
			//mMediaPlayer.setPlayerOption(ISnailPlayer.SnailPlayerOption.PLAYER_OPT_RECONNECT_TIMES, 5);





			mMediaPlayer.start();
            mCurrentState = STATE_PREPARING;


			attachMediaController();
		}
	}

	public void setMediaController(MediaController controller) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		if (controller == null) {
			mMediaController = new MediaController(mContext);
		} else {
			mMediaController = controller;
		}
		attachMediaController();
	}

	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this
					.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(mIsPrepared);
		}
	}

	ISnailPlayerStateChangeNotification mStatListener = new ISnailPlayerStateChangeNotification() {
		@Override
		public void notify(ISnailPlayer mp, ISnailPlayer.State state) {
			if (state == ISnailPlayer.State.PLAYER_STARTED) {

				mCurrentState = STATE_PREPARED;
				mTargetState = STATE_PLAYING;
				mIsPrepared = true;
//				mVideoWidth = mp.getVideoWidth();
//				mVideoHeight = mp.getVideoHeight();
//				if (mVideoHeight != 0 && mVideoWidth != 0)
//					setVideoScaleSize();
				if (mSeekWhenPrepared != 0 && getDuration()!=0) {
					mMediaPlayer.seekTo(mSeekWhenPrepared);
					mSeekWhenPrepared = 0;
				}

			}
			if (mOnStatListener != null) {
				mOnStatListener.notify(mMediaPlayer, state);
			}
		}
	};


	ISnailPlayerEventNotification mEventListener = new ISnailPlayerEventNotification() {
		@Override
		public boolean notify(ISnailPlayer mp, ISnailPlayer.EventType event, int extra) {

			if (event == ISnailPlayer.EventType.PLAYER_EVENT_FINISHED) {

			}else if (event == ISnailPlayer.EventType.PLAYER_EVENT_BUFFER_UPDATE) {
				mBufferPercentage  = extra;
			}
			if (mOnEventListener != null) {
				mOnEventListener.notify(mMediaPlayer, event, extra);

			}

			return true;
		}
	};


	ISnailPlayerErrorNotification mErrorListener = new ISnailPlayerErrorNotification() {
		@Override
		public void onError(ISnailPlayer mp, ISnailPlayer.ErrorType error, int extra) {

			 mCurrentState = STATE_ERROR;
             mTargetState = STATE_ERROR;

			if (mOnErrorListener != null) {
				mOnErrorListener.onError(mMediaPlayer, error, extra);
			}

		}
	};



	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(TAG,"surfaceCreated");
			mSurfaceHolder = holder;
			//openVideo();
			if (mMediaPlayer!=null) {
				mMediaPlayer.setView(mSurfaceHolder.getSurface());


				//resetUrl();

				//mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SET_FOV, mFov);
				//mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_SET_SCALE_FACTOR, mScale);
				//start();

			} else {
				openVideo();
			}

		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			Log.d(TAG,"surfaceChanged");
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			setVideoScaleSize();
//			if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w
//					&& mVideoHeight == h) {
//
//				Log.d("VideoPlayExActivity", "mMediaPlayer.start()3");
//				if (mMediaPlayer.isPlaying())
//					mMediaPlayer.start();
//				if (mMediaController != null) {
//					mMediaController.show();
//				}
//			}
		}
		
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG,"surfaceDestroyed");
			// after we return from this we can't use the surface any more
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			if (mMediaPlayer != null) {
				mMediaPlayer.setView(null);
			}

		}
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (mIsPrepared && keyCode != KeyEvent.KEYCODE_BACK && keyCode !=
	 * KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
	 * keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL &&
	 * keyCode != KeyEvent.KEYCODE_ENDCALL && mMediaPlayer != null &&
	 * mMediaController != null) { if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
	 * || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) { if
	 * (mMediaPlayer.isPlaying()) { pause(); mMediaController.show(); } else {
	 * start(); mMediaController.hide(); } return true; } else if (keyCode ==
	 * KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) { pause();
	 * mMediaController.show(); } else { toggleMediaControlsVisiblity(); } }
	 * 
	 * return super.onKeyDown(keyCode, event); }
	 */

	private void toggleMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
	}

	public void start() {
		if (mMediaPlayer != null && mIsPrepared) {
			Log.d("VideoPlayExActivity", "mMediaPlayer.start()4");
			mMediaPlayer.resume();
            mCurrentState = STATE_PLAYING;
		}
	}

	public void pause() {
		if (mMediaPlayer != null && mIsPrepared) {
			if (mMediaPlayer.getStat() == ISnailPlayer.State.PLAYER_PLAYING) {
				mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
                mSeekWhenPrepared = getCurrentPosition();
			}
		}
	}

	public int getDuration() {
		if (mMediaPlayer != null && mIsPrepared) {
			if (mDuration > 0) {
				return (int) mDuration;
			}
			mDuration = mMediaPlayer.getDuration();
			return (int) mDuration;
		}
		mDuration = -1;
		return (int) mDuration;
	}

	public int getCurrentPosition() {
		if (mMediaPlayer != null && mIsPrepared) {
			long position = mMediaPlayer.getCurrentPosition();
			return (int) position;
		}
		return 0;
	}

	public void seekTo(int msec) {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.seekTo(msec);
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public boolean isPlaying() {
		if (mMediaPlayer != null && mIsPrepared) {

			ISnailPlayer.State st = mMediaPlayer.getStat();

			return (mMediaPlayer.getStat() == ISnailPlayer.State.PLAYER_PLAYING);
		}
		return false;
	}

	@Override
	public boolean canPause() {
		return false;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	@Override
	public int getBufferPercentage() {
		return mBufferPercentage;
	}

	public String getRateKbps() {
		float speed = 0.f;
		String rev = "0.00Mbps";
		if (mMediaPlayer != null) {
			SnailPlaybackInfo info = mMediaPlayer.getPlaybackInfo();
			if (info != null) {
				speed = info.downloadSpeed / 1000;
				DecimalFormat fmt = new DecimalFormat("0.00");
				if (speed > 1000) {
					rev = fmt.format(speed / 1000) + "Mbps";
				}else {
					rev = fmt.format(speed) + "Kbps";
				}
			}
		}
		return rev;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	public String getMediaInfo() {
		return "";
	}

	public String getVideoFPS() {

//		long _outputfps = mMediaPlayer.getValue(ISnailPlayer.ValueKey.PLAYER_VIDEO_OUTPUT_FPS);
//		long _decodefps = mMediaPlayer.getValue(ISnailPlayer.ValueKey.PLAYER_DECODER_FPS);
//
//		return "\nFPS:" + String.format("%d", _outputfps) + "/"
//				+ String.format("%d", _decodefps);

		return "";

	}

	public String getVideoCachedInfo() {

//		long _cached_duration = mMediaPlayer.getValue(ISnailPlayer.ValueKey.PLAYER_VIDEO_CACHEDDURATION);
//		long _cached_bytes = mMediaPlayer.getValue(ISnailPlayer.ValueKey.PLAYER_VIDEO_CACHEDBYTES);
//		return "\nV-Cache:"
//				+ String.format("%.2f", (double) _cached_duration / 1000)
//				+ " sec"
//				+ ";"
//				+ String.format("%.2f", (double) _cached_bytes * 8
//				/ (1024 * 1024)) + " MB";

		return "";
	}

	public String getAudioCachedInfo() {
//		long _cached_duration = mMediaPlayer.getValue(ISnailPlayer.ValueKey.PLAYER_AUDIO_CACHEDURATION);
//		long _cached_bytes = mMediaPlayer.getValue(ISnailPlayer.ValueKey.PLAYER_AUDIO_CACHEDBYTES);
//		return "\nA-Cache:"
//				+ String.format("%.2f", (double) _cached_duration / 1000)
//				+ " sec"
//				+ ";"
//				+ String.format("%.2f", (double) _cached_bytes * 8
//				/ (1024 * 1024)) + " MB";

		return "";
	}

	// public Bundle getMediaMeta() {
	// return mMediaPlayer.getMediaMeta();
	// }

	public void setVSync(boolean _open) {

	}


	public void setOriginalAngle() {
		if (mMediaPlayer != null) {

			mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_RESET_ANGLE,  (long)0);

		}
	}

	public void setNavigationmode(int _navigationMode) {
		mNavigationMode = _navigationMode;
		if (mMediaPlayer != null) {
			switch (_navigationMode) {
			case VRPlayActivity.SNVR_NAVIGATION_SENSOR: {

				mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_NAVIGATION_MODE, ISnailPlayer.VRCtrlValue.VR_NV_GYROSCAPE);
			}
				break;
			case VRPlayActivity.SNVR_NAVIGATION_TOUCH: {
				mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_NAVIGATION_MODE, ISnailPlayer.VRCtrlValue.VR_NV_TOUCH);
			}
				break;
			case VRPlayActivity.SNVR_NAVIGATION_BOTH: {
				mMediaPlayer.setVrOption(ISnailPlayer.VRCtrlKey.VR_NAVIGATION_MODE, ISnailPlayer.VRCtrlValue.VR_NV_GYROSCAPE_TOUCH);
			}
				break;

			default:
				break;
			}
		}

	}

	public void setTouchInfo(float nCurrentX, float nCurrentY) {


		if (mMediaPlayer != null) {

			mMediaPlayer.updateTouchVector(nCurrentX, nCurrentY);

		}

	}

}
