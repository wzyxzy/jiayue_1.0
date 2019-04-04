//package com.jiayue;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//
//import com.jiayue.util.ActivityUtils;
//import com.unity3d.player.UnityPlayer;
//import com.unity3d.player.UnityPlayerNativeActivity;
//@SuppressLint("NewApi")
//public class Unity3DPlayerActivity extends UnityPlayerNativeActivity {
//	private String path;
//	private String filepath;
//	private Button btn_play;
//	private Context context;
//	private LinearLayout mParent;
//	private View mView;
//	private String deletePath;
////
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.book_3d);
//		context = this;
////		ActivityUtils.showToast(context, "抱歉，未加载到数据，请退出后刷新重新加载数据，谢谢");
//		if (getIntent().getStringExtra("filePath") != null
//				&& (!getIntent().getStringExtra("filePath").equals(""))) {
//			deletePath = filepath = getIntent().getStringExtra("filePath");
////			deletePath = filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/123.assetbundle";
//			if (!filepath.startsWith("file:")) {
//				filepath = "file:///" + filepath;
//			}
//		}
//		int unityView = getResources().getIdentifier("unityView", "id",
//				getPackageName());
//		// mParent = (LinearLayout) findViewById(R.id.unityView);
//		mParent = (LinearLayout) findViewById(unityView);
//		mView = mUnityPlayer.getView();
//		mParent.addView(mView);
//		// btn_play = (Button)findViewById(R.id.btn_play);
//
//		playUnity3D(filepath);
////		Toast.makeText(this, filepath, Toast.LENGTH_LONG).show();
//		// btn_play.setOnClickListener(new OnClickListener()
//		// {
//		// @Override
//		// public void onClick(View arg0)
//		// {
//		// UnityPlayer.UnitySendMessage("TestGameObject", "PlayAnimation", "");
//		// }
//		// });
//	}
//	public void playUnity3D(String str)// 加载3d文件
//	{
//		// UnityPlayer.UnitySendMessage("TestGameObject", "LoadGameObject",
//		// "file:///mnt/sdcard/test.assetbundle");
//		UnityPlayer.UnitySendMessage("TestGameObject", "LoadGameObject", str);
//		Log.i("MYDEBUG", str);
//	}
//
//	public void onBackPressed() {
//		Log.i("MYDEBUG", "onBackPressed!");
//		// mUnityPlayer.quit();
//		mUnityPlayer.currentActivity.finish();
//		finish();
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		ActivityUtils.deleteBookFormSD(deletePath);
//	}
//
//	// /**
//	// * 退出
//	// */
//	// @Override
//	// public boolean dispatchKeyEvent(KeyEvent event) {
//	// if (event.getKeyCode() == KeyEvent.KEYCODE_BACK&& event.getAction() ==
//	// KeyEvent.ACTION_DOWN&& event.getRepeatCount() == 0) {
//	// mUnityPlayer.quit();
//	// }
//	// return super.dispatchKeyEvent(event);
//	// }
//
//	// protected void onActivityResult(int requestCode, int resultCode, Intent
//	// data) {
//	// switch (resultCode) {
//	// case RESULT_OK:
//	// Bundle b = data.getExtras();
//	// String str = b.getString("filePath");
//	// if(!str.startsWith("file:")){
//	// str = "file:///" + str;
//	// }
//	// UnityPlayer.UnitySendMessage("TestGameObject", "LoadGameObject", str);
//	// //UnityPlayer.UnitySendMessage("TestGameObject", "LoadGameObject",
//	// "file:///mnt/sdcard/Test.assetbundle");
//	// break;
//	// default:
//	// break;
//	// }
//	// }
//
//}