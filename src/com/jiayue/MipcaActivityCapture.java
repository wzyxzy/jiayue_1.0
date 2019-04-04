package com.jiayue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Vector;

import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.Bean;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.UriUtils;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.decoding.RGBLuminanceSource;
import com.mining.app.zxing.view.ViewfinderView;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button mBtn_Local;
	private final int REQUEST_CODE = 0x01;
	private String photo_path;

	private final int CMD_RESULT = 0x02;
	private final int CMD_NORESULT = 0x03;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case CMD_RESULT:
					handleDecode((Result) msg.obj, null);
					break;
				case CMD_NORESULT:
					ActivityUtils.showToast(getApplicationContext(), "图片格式有误");
					break;
				default:
					break;
			}
		};
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getApplicationContext().addActivity(this);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		LinearLayout mButtonBack = (LinearLayout) findViewById(R.id.button_back);
		mButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MipcaActivityCapture.this.finish();

			}
		});

		mBtn_Local = (Button) findViewById(R.id.button_local);
		mBtn_Local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openPhotoAlbum();
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	/**
	 * 打开相册
	 */
	private void openPhotoAlbum() {
		Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
		if (Build.VERSION.SDK_INT < 19) {
			innerIntent.setAction(Intent.ACTION_GET_CONTENT);
		} else {
			innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		}
		innerIntent.setType("image/*");
		Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
		startActivityForResult(wrapperIntent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

				case REQUEST_CODE:

					String[] proj = { MediaStore.Images.Media.DATA };
					// 获取选中图片的路径
					Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);

					if (cursor.moveToFirst()) {
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						photo_path = cursor.getString(column_index);
						if (photo_path == null) {
							photo_path = UriUtils.getPath(getApplicationContext(), data.getData());
							Log.i("123path  Utils", photo_path);
						}
						Log.i("123path", photo_path);
					}

					cursor.close();

					new Thread(new Runnable() {

						@Override
						public void run() {

							Result result = scanningImage(photo_path);
							// String result = decode(photo_path);
							if (result == null) {
								mHandler.sendEmptyMessage(CMD_NORESULT);
							} else {
								Log.i("123result", result.toString());
								// Log.i("123result", result.getText());
								// 数据返回
								// String recode = recode(result.toString());
								// Intent data = new Intent();
								// data.putExtra("result", recode);
								// setResult(300, data);
								// finish();
								mHandler.sendMessage(mHandler.obtainMessage(CMD_RESULT, result));
							}
						}
					}).start();
					break;
			}
		}
	}

	private Bitmap scanBitmap;

	/**
	 * 解析二维码图片
	 * 
	 * @param path
	 * @return
	 */
	private Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		// DecodeHintType 和EncodeHintType
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小

		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);

		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {

			return reader.decode(bitmap1, hints);

		} catch (NotFoundException e) {

			e.printStackTrace();

		} catch (ChecksumException e) {

			e.printStackTrace();

		} catch (FormatException e) {

			e.printStackTrace();

		}

		return null;

	}

	/**
	 * 中文乱码处理
	 * 
	 * @param str
	 * @return
	 */
	private String recode(String str) {
		String formart = "";

		try {
			boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
			if (ISO) {
				formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
				Log.i("1234      ISO8859-1", formart);
			} else {
				formart = str;
				Log.i("1234      stringExtra", str);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formart;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		getApplicationContext().removeActivity(this);
		super.onDestroy();
	}

	/**
	 * ����ɨ����
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		// Intent intent=new Intent(this,TestImageActivity.class);
		// ByteArrayOutputStream baos=new ByteArrayOutputStream();
		// barcode.compress(Bitmap.CompressFormat.PNG, 100, baos);
		// byte [] bitmapByte =baos.toByteArray();
		// intent.putExtra("bitmap", bitmapByte);
		// startActivity(intent);

		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();

		// String resultString = recode(result.toString());

		// if (resultString.equals("")) {
		// Toast.makeText(MipcaActivityCapture.this, "Scan failed!",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// Intent resultIntent = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.putString("result", resultString);
		// // bundle.putParcelable("bitmap", barcode);
		// resultIntent.putExtras(bundle);
		// this.setResult(RESULT_OK, resultIntent);
		//
		// }

		if (TextUtils.isEmpty(resultString)) {
			Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		} else {
			RequestParams params = new RequestParams(Preferences.ADDBOOK_URL);
			params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
			params.addQueryStringParameter("code", resultString);

			DialogUtils.showMyDialog(MipcaActivityCapture.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "正在查找中...", null);
			x.http().post(params, new CommonCallback<String>() {
				@Override
				public void onSuccess(String s) {
					Gson gson = new Gson();
					java.lang.reflect.Type type = new TypeToken<Bean>() {
					}.getType();
					Bean bean = gson.fromJson(s, type);
					DialogUtils.dismissMyDialog();
					if (bean != null && bean.getCode().equals("SUCCESS")) {
						ActivityUtils.showToastForSuccess(MipcaActivityCapture.this, bean.getCodeInfo());
						// getApplicationContext().ADD_FLAG = 1;
						// getApplicationContext().notice =
						// bookJson.getNotice();
						MipcaActivityCapture.this.setResult(RESULT_OK);
						finish();
					} else {
						ActivityUtils.showToastForFail(MipcaActivityCapture.this, "添加书籍失败," + bean.getCodeInfo());
						finish();
					}
				}

				@Override
				public void onError(Throwable throwable, boolean b) {
					ActivityUtils.showToast(MipcaActivityCapture.this, "添加书籍失败,请检查网络。");
				}

				@Override
				public void onCancelled(CancelledException e) {

				}

				@Override
				public void onFinished() {

				}
			});
		}
		// MipcaActivityCapture.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}