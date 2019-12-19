package com.jiayue.view.camera;

/**
 * Created by hezhisu on 2017/5/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.jiayue.R;
import com.jiayue.constants.Preferences;
import com.jiayue.dto.base.PhotoBean;
import com.jiayue.dto.base.SiftVO;
import com.jiayue.jni.JniImageClient;
import com.jiayue.model.UserUtil;
import com.jiayue.util.ActivityUtils;
import com.jiayue.util.DensityUtil;
import com.jiayue.util.DialogUtils;
import com.jiayue.util.MyDbUtils;
import com.jiayue.util.MyPreferences;
import com.jiayue.util.SPUtility;
import com.jiayue.util.UriUtils;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.decoding.RGBLuminanceSource;
import com.mining.app.zxing.view.ViewfinderView;

import org.xutils.DbManager;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class AutoFocusActivity extends Activity implements Callback {
    private SurfaceView mSurfaceView;
    private QrCodeFinderView mQrCodeFinderView;
    private boolean mHasSurface = false;
    private static final int PREVIEW_CODE = 1000;
    private static final int FOCUS_CODE = 1000;
    private static final int CAMERA_PERMISSION_REQUEST_ID = 2;
    private static final int CAPTURE_MODE_SQ = 1;
    private static final int CAPTURE_MODE_RE = 2;
    private static final int CAPTURE_MODE_FILL = 3;
    private int mCurrentCaptureMode = CAPTURE_MODE_SQ;
    private boolean saveFrame = false;
    private ImageView mIvFill;
    private ImageView mIvSq;
    private ImageView mIvRe;
    private boolean mIsMove = false;
    private final String TAG = getClass().getSimpleName();
    public static final String BOOKID = "BOOKID";
    private String bookId;
    public static final String SHITU = "SHITU";
    public static final String ERWEIMA = "ERWEIMA";
    private CaptureDialog mDialog;

    private MediaPlayer mediaPlayer;
    private boolean playBeep = true;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate = true;

    private RelativeLayout layout_photo, layout_erweima;// 识别布局，二维码布局
    private boolean isLayout_Photo = false;// 判断识别或者二维码布局
    private LinearLayout btn_tu, btn_ma;
    private View flag_tu, flag_ma;// 识别按钮标识

    private Handler mDeloyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CameraManager.get().requestAutoFocus(mAutoFocusHandler, FOCUS_CODE);
        }
    };
    private Handler mPreviewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PREVIEW_CODE && saveFrame) {
                boolean crop;
                int width = 256;
                int height = 256;
                if (mCurrentCaptureMode == CAPTURE_MODE_FILL) {
                    crop = false;
                    width = 512;
                    height = 288;
                } else if (mCurrentCaptureMode == CAPTURE_MODE_SQ) {
                    crop = true;
                    width = 256;
                    height = 256;
                } else {// 长方形
                    crop = true;
                    width = 256;
                    height = 128;
                }
                saveFrame = false;
                if (!mIsMove) {
                    Bitmap bmp = ImageUtils.convertDataToBitmap(AutoFocusActivity.this, msg.arg1, msg.arg2, (byte[]) msg.obj, crop, mCurrentCaptureMode, DensityUtil
                            .dip2px(AutoFocusActivity.this, (double) 170));
                    // mDialog.show(bmp);

                    // try {
                    // Bitmap b4 = Bitmap.createScaledBitmap(bmp, width, height,
                    // false);
                    // if (null != b4 && bmp != b4) {
                    // bmp.recycle();
                    // bmp = b4;
                    // }
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }
                    ImageUtils.saveBitmap(bmp);// 保存
                    mHandler.removeMessages(1);
                    mHandler.sendEmptyMessageDelayed(1, 800);
                } else {
                    SensorControler.getInstance().unlockFocus();// 解锁
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ActivityUtils.showToast(AutoFocusActivity.this, "获取失败，请继续。");
                    break;
                case 1:
                    try {
                        searchPhoto(ImageUtils.getOutputMediaFile(0).getAbsolutePath());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case CMD_RESULT:
                    handleDecode((Result) msg.obj, null);
                    break;
                case CMD_NORESULT:
                    ActivityUtils.showToast(getApplicationContext(), "图片格式有误");
                    break;
                default:
                    break;
            }

        }

        ;
    };

    private FaceTask mFaceTask;

    /* 自定义的FaceTask类，开启一个线程分析数据 */
    private class FaceTask extends AsyncTask<Void, Void, Void> {

        private String path;

        // 构造函数
        FaceTask(String bitmap) {
            this.path = bitmap;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                searchNativePhoto(path);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // 自己定义的实时分析预览帧视频的算法
            return null;
        }

    }

    // 图片搜索
    private void searchPhoto(String path) throws Exception {

        if (ActivityUtils.isNetworkAvailable(this)) {
            searchNetPhoto(path);
        } else {
            if (null != mFaceTask) {
                switch (mFaceTask.getStatus()) {
                    case RUNNING:
                        return;
                    case PENDING:
                        mFaceTask.cancel(false);
                        break;
                }
            }
            mFaceTask = new FaceTask(path);
            mFaceTask.execute((Void) null);
            if (isFinishing())
                return;
            DialogUtils.showMyDialog(AutoFocusActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "加载中", null);
            playBeepSoundAndVibrate();
        }
    }

    private final int SCALE_NONE = 0;
    private final int SCALE_DONE = 136;

    // 从本地获取图像识别数据
    private void searchNativePhoto(String path) throws Exception {
        // TODO Auto-generated method stub
        String libPath = ActivityUtils.getSDLibPath().getAbsolutePath() + "/" + bookId;
        File f = new File(libPath);
        if (!f.exists()) {
            Log.d(TAG, "本地不存在识别文件");
            ActivityUtils.showToast(AutoFocusActivity.this, "请检测您的网络");
            photoresult(true, null);
            return;
        }
        DbManager db = MyDbUtils.getSiftVoDb(AutoFocusActivity.this);
        SiftVO vo = db.selector(SiftVO.class).where("bookId", "=", bookId).findFirst();
        long handler = JniImageClient.load_features(libPath);
        // byte[] matched_img = new byte[256];
        // int[] confidence = new int[1];

        int expected_k = 5;
        int item_size = 256;
        int[] real_k = new int[]{expected_k};
        // IntByReference real_k = new IntByReference(expected_k);
        // Integer compressed_to_pixels = 136;
        byte[] matched_info_list = new byte[expected_k * 2 * item_size];
        int k = JniImageClient.match_topk_img(handler, path, matched_info_list, item_size, real_k, SCALE_DONE);
        ArrayList<String> ss = new ArrayList<>();
        for (int i = 0; i < real_k[0]; ++i) {
            StringBuffer img_name = new StringBuffer(item_size);
            StringBuffer confidence = new StringBuffer(item_size);
            for (int j = 0; j < item_size; j++) {
                if (matched_info_list[2 * i * item_size + j] != 0)
                    img_name.append("" + (char) matched_info_list[2 * i * item_size + j]);
                if (matched_info_list[(2 * i + 1) * item_size + j] != 0)
                    confidence.append("" + (char) matched_info_list[(2 * i + 1) * item_size + j]);
            }
            if (Integer.parseInt(confidence.toString().trim()) >= vo.getConfidence()) {
                ss.add(img_name.toString());
            }
            Log.d(TAG, "Top " + i + ": [" + img_name.toString() + " : " + confidence.toString() + "]" + "-----vo.getConfidence()=" + vo.getConfidence());
        }
        // JniImageClient.match_img(handler, path, matched_img, confidence,
        // SCALE_DONE);
        JniImageClient.release_features(handler);

        ArrayList<PhotoBean.Data> flags = new ArrayList<>();
        for (int i = 0; i < ss.size(); i++) {
            try {
                vo = db.selector(SiftVO.class).where("bookId", "=", bookId).and("siftName", "=", ss.get(i)).findFirst();
            } catch (org.xutils.ex.DbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (vo != null) {
                PhotoBean.Data bean = new PhotoBean.Data();
                bean.setAttachName(vo.getAttachFlag());
                bean.setImagePath(vo.getImageName());
                bean.setAttachType(SHITU);
                flags.add(bean);
            }
        }
        if (flags.size() != 0) {
            photoresult(true, flags);
        } else {
            photoresult(false, null);
        }
    }

    private void photoresult(boolean isOK, ArrayList<PhotoBean.Data> flags) {
        DialogUtils.dismissMyDialog();
        if (isOK) {
            Intent intent = new Intent();
            // intent.putStringArrayListExtra("flag", flags);
            intent.putParcelableArrayListExtra("flags", flags);
            setResult(888, intent);
            finish();
        } else {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessage(0);
            SensorControler.getInstance().unlockFocus();// 解锁
        }
    }

    // 从网络获取图像识别数据
    private void searchNetPhoto(String path) {
        // TODO Auto-generated method stub
        if (isFinishing())
            return;
        DialogUtils.showMyDialog(AutoFocusActivity.this, MyPreferences.SHOW_PROGRESS_DIALOG, null, "加载中", null);
        playBeepSoundAndVibrate();
        File file = new File(path);
        if (!file.exists()) {
            photoresult(false, null);
            return;
        }
        RequestParams params = new RequestParams(Preferences.SEARCH_PHOTO);
        params.setMultipart(true);
        params.addQueryStringParameter("bookId", bookId);
        params.addQueryStringParameter("userId", UserUtil.getInstance(this).getUserId());
        // 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
        // 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
        // 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
        //
        // MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
        // 例如发送json参数：params.setBodyEntity(new StringEntity(jsonStr,charset));
        // Log.d(TAG, "path==" + path + "file====" + new File(path).exists());
        params.addBodyParameter("Image", file);
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("Search responseInfo", "responseInfo=====" + s);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<PhotoBean>() {
                }.getType();
                PhotoBean bean = gson.fromJson(s, type);
                if (bean.getCode().equals("SUCCESS") && bean.getData() != null && bean.getData().size() != 0) {
                    for (PhotoBean.Data data : bean.getData())
                        data.setAttachType(SHITU);
                    photoresult(true, bean.getData());
                } else {
                    ActivityUtils.showToast(AutoFocusActivity.this, "未查询到相关信息");
                    photoresult(false, null);
                }

                // Gson gson = new Gson();
                // java.lang.reflect.Type type = new TypeToken<Bean>() {
                // }.getType();
                // Bean bean = gson.fromJson(s, type);
                // if (bean.getCode().equals("SUCCESS")) {
                // ActivityUtils.showToast(AutoFocusActivity.this,"OK");
                // } else {
                // ActivityUtils.showToast(AutoFocusActivity.this,bean.getCodeInfo());
                // }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                // ActivityUtils.showToast(AutoFocusActivity.this,throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }

        });
    }

    private Handler mAutoFocusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FOCUS_CODE) {
                saveFrame = true;
                mDeloyHandler.sendMessageDelayed(new Message(), 2000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 界面唤醒状态
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_auto_focus);

        bookId = getIntent().getStringExtra(BOOKID);
        initView();
    }

    public void initView() {
        initView_Photo();
        initView_Erweima();
        mSurfaceView = (SurfaceView) findViewById(R.id.qr_code_preview_view);
        CameraManager.init(this);

        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }

        initBeepSound();
        SensorControler.getInstance().setCameraFocusListener(new SensorControler.CameraFocusListener() {
            @Override
            public void onFocus() {
                if (!SensorControler.getInstance().isFocusLocked()) {
                    SensorControler.getInstance().lockFocus();
                    mIsMove = false;
                }
            }

            @Override
            public void onMove() {
                mIsMove = true;
                saveFrame = false;
                // Log.i("ttt", "isMove");
            }
        });
        btn_tu = (LinearLayout) findViewById(R.id.btn_tu);
        flag_tu = findViewById(R.id.flag_tu);
        flag_ma = findViewById(R.id.flag_ma);
        btn_tu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                isLayout_Photo = true;
                chooseLayout(isLayout_Photo);
            }
        });
        btn_ma = (LinearLayout) findViewById(R.id.btn_ma);
        btn_ma.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                isLayout_Photo = false;
                chooseLayout(isLayout_Photo);
            }
        });


    }

    private void initView_Erweima() {
        // TODO Auto-generated method stub
        layout_erweima = (RelativeLayout) findViewById(R.id.layout_erweima);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        mBtn_Local = (Button) findViewById(R.id.btn_header_right);
        mBtn_Local.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                openPhotoAlbum();
            }
        });
    }

    /*
     * 图像识别布局
     */
    private void initView_Photo() {
        // TODO Auto-generated method stub
        layout_photo = (RelativeLayout) findViewById(R.id.layout_photo);
        mQrCodeFinderView = (QrCodeFinderView) findViewById(R.id.qr_code_view_finder);
        mIvFill = (ImageView) findViewById(R.id.iv_fill);
        mIvRe = (ImageView) findViewById(R.id.iv_re);
        mIvSq = (ImageView) findViewById(R.id.iv_sq);
        mIvFill.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int ic_fill_select = getResources().getIdentifier("ic_fill_select", "drawable", getPackageName());
                mIvFill.setImageResource(ic_fill_select);
                int ic_re = getResources().getIdentifier("ic_re", "drawable", getPackageName());
                mIvRe.setImageResource(ic_re);
                int ic_sq = getResources().getIdentifier("ic_sq", "drawable", getPackageName());
                mIvSq.setImageResource(ic_sq);
                mQrCodeFinderView.setVisibility(View.GONE);
                mCurrentCaptureMode = CAPTURE_MODE_FILL;
                saveFrame = false;
            }
        });
        mIvRe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentCaptureMode = CAPTURE_MODE_RE;
                mQrCodeFinderView.setVisibility(View.VISIBLE);
                mQrCodeFinderView.refreshFrameRect(AutoFocusActivity.this, mCurrentCaptureMode);
                int ic_re_select = getResources().getIdentifier("ic_re_select", "drawable", getPackageName());
                mIvRe.setImageResource(ic_re_select);
                int ic_sq = getResources().getIdentifier("ic_sq", "drawable", getPackageName());
                mIvSq.setImageResource(ic_sq);
                int ic_fill = getResources().getIdentifier("ic_fill", "drawable", getPackageName());
                mIvFill.setImageResource(ic_fill);
                saveFrame = false;
            }
        });
        mIvSq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentCaptureMode = CAPTURE_MODE_SQ;
                mQrCodeFinderView.setVisibility(View.VISIBLE);
                mQrCodeFinderView.refreshFrameRect(AutoFocusActivity.this, mCurrentCaptureMode);
                int ic_re = getResources().getIdentifier("ic_re", "drawable", getPackageName());
                mIvRe.setImageResource(ic_re);
                int ic_fill = getResources().getIdentifier("ic_fill", "drawable", getPackageName());
                mIvFill.setImageResource(ic_fill);
                int ic_sq_select = getResources().getIdentifier("ic_sq_select", "drawable", getPackageName());
                mIvSq.setImageResource(ic_sq_select);
                saveFrame = false;
            }
        });
    }

    /*
     * 选择布局及其逻辑
     */
    private void chooseLayout(boolean isLayout) {
        layout_photo.setVisibility(isLayout ? View.VISIBLE : View.GONE);
        layout_erweima.setVisibility(!isLayout ? View.VISIBLE : View.GONE);
        mBtn_Local.setVisibility(!isLayout ? View.VISIBLE : View.GONE);
        flag_tu.setVisibility(isLayout ? View.VISIBLE : View.GONE);
        flag_ma.setVisibility(!isLayout ? View.VISIBLE : View.GONE);
        setCaptureType(isLayout);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            // 基本不会出现相机不存在的情况
            finish();
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            return;
        }
        CameraManager.get().startPreview();
        CameraManager.get().requestPreviewFrame(mPreviewHandler, PREVIEW_CODE, true);
        mDeloyHandler.sendMessageDelayed(new Message(), 2000);
    }

    private void setCaptureType(boolean isLayout) {
        SPUtility.putSPString(AutoFocusActivity.this, "isLayout", String.valueOf(isLayout));
        if (isLayout) {
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            if (inactivityTimer != null)
                inactivityTimer.shutdown();

            CameraManager.get().startPreview();
            CameraManager.get().requestPreviewFrame(mPreviewHandler, PREVIEW_CODE, true);
            mDeloyHandler.sendMessageDelayed(new Message(), 2000);
        } else {
            mPreviewHandler.removeMessages(PREVIEW_CODE);
            mAutoFocusHandler.removeMessages(FOCUS_CODE);

            decodeFormats = null;
            characterSet = null;
            inactivityTimer = new InactivityTimer(this);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraManager.get().stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreviewHandler.removeMessages(PREVIEW_CODE);
        mAutoFocusHandler.removeMessages(FOCUS_CODE);
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if (inactivityTimer != null)
            inactivityTimer.shutdown();
        CameraManager.get().closeDriver();
    }

    public void btnBack(View v) {
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SensorControler.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            SensorControler.getInstance().onStop();
        } catch (Exception e) {
            // TODO: handle exception
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
        try {
            isLayout_Photo = Boolean.valueOf(SPUtility.getSPString(this, "isLayout"));
        } catch (Resources.NotFoundException e) {
            isLayout_Photo = false;
        }
        chooseLayout(isLayout_Photo);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
        holder.removeCallback(this);
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
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * 二维码
     */

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private Button mBtn_Local;
    private final int REQUEST_CODE = 0x01;
    private String photo_path;

    private final int CMD_RESULT = 0x02;
    private final int CMD_NORESULT = 0x03;

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

                    String[] proj = {MediaStore.Images.Media.DATA};
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

    /**
     * ����ɨ����
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "resultString====" + resultString);
            if (resultString.contains("http://www.pndoo.com/jiayue.html?flag=")) {
                String flag = resultString.replace("http://www.pndoo.com/jiayue.html?flag=", "");
                PhotoBean.Data bean = new PhotoBean.Data();
                bean.setAttachName(flag);
                Log.d(TAG, "resultString====flag=" + flag);
                bean.setAttachType(ERWEIMA);
                ArrayList<PhotoBean.Data> flags = new ArrayList<>();
                flags.add(bean);

                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("flags", flags);
                setResult(888, intent);
            } else {
                ActivityUtils.showToast(this, "二维码错误!");
            }
            finish();
        }

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

}
