package com.jiayue.view;
//package com.jiayue.view;
//
//import java.io.FileOutputStream;
//import java.util.List;
//
//import org.opencv.android.JavaCameraView;
//import org.opencv.android.Utils;
//import org.opencv.core.Mat;

//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.hardware.Camera;
//import android.hardware.Camera.PictureCallback;
//import android.hardware.Camera.Size;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Message;
//import android.util.AttributeSet;
//import android.util.Log;
//
//public class Tutorial3View extends JavaCameraView implements PictureCallback {
//
//    private static final String TAG = "MYDEBUG-Tutorial3View";
//    private String mPictureFileName;
//    private OpenCVActivity mOpenCVActivity = null;
//    private Mat mCurrentMat = null;
//
//    public Tutorial3View(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public List<String> getEffectList() {
//        return mCamera.getParameters().getSupportedColorEffects();
//    }
//
//    public boolean isEffectSupported() {
//        return (mCamera.getParameters().getColorEffect() != null);
//    }
//
//    public String getEffect() {
//        return mCamera.getParameters().getColorEffect();
//    }
//
//    public void setEffect(String effect) {
//        Camera.Parameters params = mCamera.getParameters();
//        params.setColorEffect(effect);
//        mCamera.setParameters(params);
//    }
//
//    public List<Size> getResolutionList() {
//        return mCamera.getParameters().getSupportedPreviewSizes();
//    }
//
//    public void setResolution(Size resolution) {
//        disconnectCamera();
//        mMaxHeight = resolution.height;
//        mMaxWidth = resolution.width;
//        connectCamera(getWidth(), getHeight());
//    }
//
//    public Size getResolution() {
//        return mCamera.getParameters().getPreviewSize();
//    }
//
//    public void takePicture(final String fileName) {
//        Log.i(TAG, "Taking picture");
//        this.mPictureFileName = fileName;
//        mCamera.setPreviewCallback(null);
//
//        // PictureCallback is implemented by the current class
//        mCamera.takePicture(null, null, this);
//    }
//    
//    public void takePicture(OpenCVActivity activity) {
//        Log.i(TAG, "Taking picture");
//        this.mOpenCVActivity = activity;
//        mCamera.setPreviewCallback(null);
//        
//        // PictureCallback is implemented by the current class
//        mCamera.takePicture(null, null, this);
//        
//    }
//
//    @Override
//    public void onPictureTaken(byte[] data, Camera camera) {
//        Log.i(TAG, "Saving a bitmap to file");
//        // The camera preview was automatically stopped. Start it again.
//        mCamera.startPreview();
//        mCamera.setPreviewCallback(this);
//
//        /*
//        // Write the image in a file (in jpeg format)
//        try {
//            FileOutputStream fos = new FileOutputStream(mPictureFileName);
//
//            fos.write(data);
//            fos.flush();
//            fos.close();
//        	if(mCurrentMat != null){
//        		mCurrentMat.release();
//        	}
//        	
//
//        } catch (java.io.IOException e) {
//            Log.e(TAG, "Exception in photoCallback", e);
//        }
//        */
//        
//        if(mOpenCVActivity == null){
//        	return;
//        }
//        
//        
//        if(mCurrentMat != null){
//    		mCurrentMat.release();
//    	}
//        
//        
//        
//        mCurrentMat = new Mat();
//        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); 
//        Utils.bitmapToMat(bitmap, mCurrentMat);
//        Log.e(TAG, "Take picture complete!");
//        AsyncTask task = new MyProgressTask(mOpenCVActivity).execute(mCurrentMat);
//    }
//    
//    public Mat getCurrentMat(){
//    	return mCurrentMat;
//    }
//    
//    class MyProgressTask extends AsyncTask<Mat, Void, Boolean> {
//        private ProgressDialog dialog;
//        private OpenCVActivity activity;
//
//        public MyProgressTask(OpenCVActivity a) {
//            activity = a;
//            dialog = new ProgressDialog(activity);
//        }
//
//        @Override
//        protected void onPreExecute() {
//        	OpenCVActivity.stance.onPause();
//        	dialog.setTitle("请稍等...");
//            dialog.setMessage("正在拼命解析中...");
//            dialog.setIndeterminate(true);
//            dialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//        	OpenCVActivity.stance.onResume();
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//
//        }
//
//		@Override
//		protected Boolean doInBackground(Mat... params) {
//			// TODO Auto-generated method stub
//			Mat mat = (Mat)params[0];
//			if(mOpenCVActivity != null){
//	        	mOpenCVActivity.getMatchpage(mat);
//	        }else{
//	        	Log.e(TAG, "mShowLoadActivity == null!");
//	        }
//			return true;
//        }
//    }
// }
