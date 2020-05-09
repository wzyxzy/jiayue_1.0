package com.jiayue.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jiayue.BrowerActivity;
import com.jiayue.MediaPlayerActivity;
import com.jiayue.MyApplication;
import com.jiayue.PDFViewActivity;
import com.jiayue.PictureActivity;
import com.jiayue.R;
import com.jiayue.VideoPlayerActivity;
import com.jiayue.WorkWebActivity;
import com.jiayue.dto.base.AudioItem;
import com.jiayue.service.ZipService;
import com.jiayue.vr.Definition;
import com.jiayue.vr.VRPlayActivity;

import org.xutils.DbManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import uk.co.senab.photoview.log.Logger;

import static android.R.attr.type;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActivityUtils {

    private final static String[][] MIME_MapTable = {
            // {后缀名，MIME类型}
            {"3gp", "video/3gpp"}, {"apk", "application/vnd.android.package-archive"}, {"asf", "video/x-ms-asf"}, {"assetbundle", "application/vnd.android.package-archive"}, {"avi", "video/x-msvideo"}, {"bin", "application/octet-stream"}, {"bmp", "image/bmp"}, {"c", "text/plain"}, {"class", "application/octet-stream"}, {"conf", "text/plain"}, {"cpp", "text/plain"}, {"doc", "application/msword"}, {"docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, {"xls", "application/vnd.ms-excel"}, {"xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, {"exe", "application/octet-stream"}, {"gif", "image/gif"}, {"gtar", "application/x-gtar"}, {"gz", "application/x-gzip"}, {"h", "text/plain"}, {"htm", "text/html"}, {"html", "text/html"}, {"jar", "application/java-archive"}, {"java", "text/plain"}, {"jpeg", "image/jpeg"}, {"jpg", "image/jpeg"}, {"js", "application/x-javascript"}, {"log", "text/plain"}, {"m3u", "audio/x-mpegurl"}, {"m4a", "audio/mp4a-latm"}, {"m4b", "audio/mp4a-latm"}, {"m4p", "audio/mp4a-latm"}, {"m4u", "video/vnd.mpegurl"}, {"m4v", "video/x-m4v"}, {"mov", "video/quicktime"}, {"mp2", "audio/x-mpeg"}, {"mp3", "audio/x-mpeg"}, {"mp4", "video/mp4"}, {"mpc", "application/vnd.mpohun.certificate"}, {"mpe", "video/mpeg"}, {"mpeg", "video/mpeg"}, {"mpg", "video/mpeg"}, {"mpg4", "video/mp4"}, {"mpga", "audio/mpeg"}, {"msg", "application/vnd.ms-outlook"}, {"ogg", "audio/ogg"}, {"pdf", "application/pdf"}, {"png", "image/png"}, {"pps", "application/vnd.ms-powerpoint"}, {"ppt", "application/vnd.ms-powerpoint"}, {"pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, {"prop", "text/plain"}, {"rc", "text/plain"}, {"rmvb", "audio/x-pn-realaudio"}, {"rtf", "application/rtf"}, {"sh", "text/plain"}, {"tar", "application/x-tar"}, {"tgz", "application/x-compressed"}, {"txt", "text/plain"}, {"wav", "audio/x-wav"}, {"wma", "audio/x-ms-wma"}, {"wmv", "audio/x-ms-wmv"}, {"wps", "application/vnd.ms-works"}, {"xml", "text/plain"}, {"z", "application/x-compress"}, {"zip", "application/x-zip-compressed"}, {"swf", "application/x-shockwave-flash"}, {"", "*/*"}};
    private static boolean flag;
    private static long lastClickTime;

    public static boolean isAppRunning(Context context) {
        String MY_PKG_NAME = "com.jiayue";

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);

        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                return true;
            }
        }

        return false;

    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * <p>
     * Title
     * </p>
     * : bytes2kb Description: 字节转KB
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(Long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "KB");
    }

    // /**
    // * Title: ShareFile Description: 文件分享
    // *
    // * @param activity
    // * @param bookId
    // * @param encodeFile
    // */
    // public void ShareFile(Activity activity, String bookId, String
    // encodeFile) {
    // Intent intent = new Intent(Intent.ACTION_SEND);
    // intent.setType("application/octet-stream");
    // intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
    // intent.putExtra(Intent.EXTRA_STREAM, "file://" + getSDPath(bookId) +
    // File.separator + encodeFile);
    // activity.startActivity(Intent.createChooser(intent,
    // activity.getTitle()));
    // }
    //
    public static void ShareLink(Activity activity, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, link + " (分享来自M+Book)");
        activity.startActivity(Intent.createChooser(intent, activity.getTitle()));
    }

    /**
     * 设置手机的移动数据
     */
    public static void setMobileData(Context context, boolean pBoolean) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("移动数据设置错误: " + e.toString());
        }
    }

    /**
     * 静默安装
     *
     * @param file
     * @return
     */
    public boolean slientInstall(String path) {
        boolean result = false;
        Process process = null;
        OutputStream out = null;
        File file = new File(path);
        try {
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");
            dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + file.getPath());
            // 提交命令
            dataOutputStream.flush();
            // 关闭流操作
            dataOutputStream.close();
            out.close();
            int value = process.waitFor();

            // 代表成功
            if (value == 0) {
                result = true;
            } else if (value == 1) { // 失败
                result = false;
            } else { // 未知情况
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);
        return returnString;
    }

    // 获取屏幕的宽
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        Log.i("MyGameView", "width : " + widthPixels);
        return widthPixels;
    }

    // 获取屏幕的高
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

    public static void hideSoftInputMethod(EditText ed, Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // 获取字符串宽度
    public static int getStringWidth(String str, Paint paint) {
        return (int) paint.measureText(str);
    }

    // 获取字体高度
    public static int getFontHeight(String str, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, 1, rect);
        return rect.height();
    }

    // 获取本地时间
    public static String getLocalTime() {
        int[] result = new int[6];
        Calendar calendar = Calendar.getInstance();
        result[0] = calendar.get(Calendar.YEAR);
        result[1] = calendar.get(Calendar.MONTH) + 1;
        result[2] = calendar.get(Calendar.DAY_OF_MONTH);
        result[3] = calendar.get(Calendar.HOUR_OF_DAY);
        result[4] = calendar.get(Calendar.MINUTE);
        result[5] = calendar.get(Calendar.SECOND);
        String time = result[0] + "-" + dateToString(result[1]) + "-" + dateToString(result[2]) + " " + dateToString(result[3]) + ":" + dateToString(result[4]) + ":" + dateToString(result[5]);
        return time;
    }

    // 显示吐司
    public static void showToast(Context context, String str) {
        try {
            Toast toast = Toast.makeText(context, str, 0);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    // 显示成功吐司
    public static void showToastForSuccess(Context context, String str) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.my_toast, null);
            TextView tv_image = (TextView) view.findViewById(R.id.tv_image);
            int happy = context.getResources().getIdentifier("happy", "drawable", context.getPackageName());
            tv_image.setBackgroundResource(happy);
            TextView textView = (TextView) view.findViewById(R.id.tv_answer);
            textView.setText(str);
            Toast toast = Toast.makeText(context, str, 0);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    // 显示错误的吐司
    public static void showToastForFail(Context context, String str) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.my_toast, null);

            TextView tv_image = (TextView) view.findViewById(R.id.tv_image);
            int sad = context.getResources().getIdentifier("sad", "drawable", context.getPackageName());
            // tv_image.setBackgroundResource(R.drawable.sad);
            tv_image.setBackgroundResource(sad);
            TextView textView = (TextView) view.findViewById(R.id.tv_answer);
            textView.setText(str);
            Toast toast = Toast.makeText(context, str, 1);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static String dateToString(int date) {
        String str = "" + date;
        if (date < 10) {
            str = "0" + date;
        }
        return str;
    }

//	public static void readPDF(Context context, String bookId, String bookName) {
//		// Uri uri = Uri.parse("/storage/sdcard1/Download/zhiqingchun.pdf");
//		Uri uri = Uri.parse(getSDPath().getAbsolutePath() + "/" + "zhi.pdf");
//		// Uri uri =
//		// Uri.parse(context.getFilesDir().getAbsolutePath()+"/1.pdf");
//		Intent intent = new Intent(context, MuPDFActivity.class);
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.setData(uri);
//		intent.putExtra("bookName", bookName);
//		context.startActivity(intent);
//	}

    public static void readPDFBook(Context context, String bookId, String fileName, String bookName) {
//		Uri uri = Uri.parse(getSDPath(bookId).getAbsolutePath() + "/" + fileName);
//		Intent intent = new Intent(context, MuPDFActivity.class);
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.setData(uri);
//		intent.putExtra("bookName", bookName);
//		intent.putExtra("fileName", fileName);
//		intent.putExtra("bookId", bookId);
//		context.startActivity(intent);

//        File file = new File(getSDPath(bookId).getAbsolutePath() + "/" + fileName);
//        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(context, PDFViewActivity.class);
        intent.putExtra("filePath", getSDPath(bookId).getAbsolutePath() + "/" + fileName);
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 判断网络连接是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断GPS是否打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = lm.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }

    /**
     * 判断WIFI是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断是否是3G网络
     *
     * @param context
     * @return
     */
    public static boolean is3rd(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public static File getSDPath() {
        String storageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(storageState)) {
            return null;
        } else {
            File sdPath = Environment.getExternalStorageDirectory();
            String path = sdPath.getPath() + "/jiayue";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
    }

    public static File getSDLibPath() {
        String storageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(storageState)) {
            return null;
        } else {
            File sdPath = Environment.getExternalStorageDirectory();
            String path = sdPath.getAbsolutePath() + "/jiayue";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String path01 = sdPath.getPath() + "/jiayue/lib";
            File file01 = new File(path01);
            if (!file01.exists()) {
                file01.mkdirs();
            }
            return file01;
        }
    }

    public static File getSDPath(String bookId) {
        String storageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(storageState)) {
            return null;
        } else {
            File sdPath = Environment.getExternalStorageDirectory();
            String path = sdPath.getAbsolutePath() + "/jiayue";
            String path_noPic = sdPath.getAbsolutePath() + "/jiayue/.nomedia";
            File file = new File(path);
            File file_noPic = new File(path_noPic);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file_noPic.exists()) {
                try {
                    file_noPic.createNewFile();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
            String path01 = sdPath.getPath() + "/jiayue/" + bookId;
            File file01 = new File(path01);
            if (!file01.exists()) {
                file01.mkdirs();
            }
            return file01;
        }
    }

    /**
     * 获取sd卡的文件名称
     *
     * @param bookId
     * @return
     */
    /*
     * public static String getSDFileName(String bookId){ return bookId+".pdf";
     * }
     */

    /**
     * 判断sd卡中的文件是否存在
     *
     * @param bookId
     * @return
     */
    /*
     * public static Boolean isExist(String bookId){ if(null
     * ==ActivityUtils.getSDPath()){ return false; } File file= new
     * File(getSDPath(),getSDFileName(bookId)); return file.exists(); }
     */
    public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable, int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 判断sd卡中的图书是否存在并且可阅读
     *
     * @param bookId   文件的id
     * @param bookSize 文件的大小
     * @return
     */
    public static Boolean isExistAndRead(String bookId) {
        if (null == ActivityUtils.getSDPath(bookId)) {
            return false;
        }

        File file = new File(getSDPath(bookId), bookId + ".xml");
        if (!file.exists()) {
            return false;
        }

        File file01 = new File(getSDPath(bookId).getAbsolutePath() + "/images");
        if (!file01.exists()) {
            return false;
        }
        return true;
    }

    public static boolean NetSwitch(Context context, boolean online) {
        if (online) {
            return ActivityUtils.isNetworkAvailable(context);
        } else {
            return !ActivityUtils.is3rd(context);
        }
    }

    /**
     * 判断sd卡中的Epub是否存在并且可阅读
     *
     * @param bookId   文件的id
     * @param bookSize 文件的大小
     * @return
     */
    public static Boolean epubIsExistAndRead(String bookId, String bookSaveName) {
        if (null == ActivityUtils.getSDPath(bookId)) {
            return false;
        }
        File file = new File(getSDPath(bookId), bookSaveName + ".epub");
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 解密文件
     *
     * @param bookId        图书Id
     * @param soureFileName 源文件
     * @param saveFileName  目标文件
     */

    public static void unLock(String bookId, String soureFileName, String saveFileName) {
        File soureFile = new File(getSDPath(bookId).getAbsolutePath() + "/" + soureFileName);
        File saveFile = new File(getSDPath(bookId).getAbsolutePath() + "/" + saveFileName);
        if (saveFile.exists())
            return;
        ZipService.unLockFile(soureFile, saveFile);

    }

    public static void isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            Log.i("onclick", "您点击的太快了");
            return;
        }
    }

    public static void readURL(Context context, String bookId, String url, boolean is3D) {
        Intent intent = new Intent(context, BrowerActivity.class);
        intent.putExtra("filePath", url);
        intent.putExtra("model", BrowerActivity.MODEL_BROWSER);
        context.startActivity(intent);
    }

    /**
     * 读取附件
     *
     * @param context
     * @param bookId
     * @param fileName
     * @param fileType
     */
    public static void readFile(Context context, String bookId, String fileName, String fileType, String bookName) {

        File file = new File(getSDPath(bookId).getAbsolutePath() + "/" + fileName);
        if (fileType.equals("pdf") || fileType.equals("PDF")) {
            readPDFBook(context, bookId, fileName, bookName);
            return;
        } else if (fileType.equalsIgnoreCase("mp4") || fileType.equalsIgnoreCase("mov")) {

            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("filePath", getSDPath(bookId).getAbsolutePath() + "/" + fileName);
            intent.putExtra("bookName", bookName);
            context.startActivity(intent);
            return;
        } else if (fileType.equalsIgnoreCase("mp3") || fileType.equalsIgnoreCase("wav")) {

            Intent intent = new Intent(context, MediaPlayerActivity.class);
            List<AudioItem> list_result = new ArrayList<AudioItem>();
            AudioItem item = new AudioItem();
            item.setData(ActivityUtils.getSDPath(bookId) + File.separator + fileName);
//            DbManager db = MyDbUtils.getOneAttachDb(context);
            item.setOldData(null);
            item.setBookId(bookId);
            item.setTitle(bookName);
            item.setArtist(fileName.replaceAll("copy_", ""));
            list_result.add(item);
            intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) list_result);
            intent.putExtra("index", 0);
//			intent.putExtra("filePath", getSDPath(bookId).getAbsolutePath() + "/" + fileName);
//			intent.putExtra("bookName", bookName);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            return;
        } else if (fileType.equals("assetbundle")) {
//			Intent intent = new Intent(context, Unity3DPlayerActivity.class);
//			intent.putExtra("filePath", getSDPath(bookId).getAbsolutePath() + "/" + fileName);
//			Log.i("MYDEBUG-Activity----->>>>", getSDPath(bookId).getAbsolutePath() + "/" + fileName);
//			context.startActivity(intent);
            return;
        } else if (fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("png") || fileType.equals("gif")) {
            Intent intent = new Intent(context, PictureActivity.class);
            intent.putExtra("filePath", getSDPath(bookId).getAbsolutePath() + "/" + fileName);
            intent.putExtra("bookName", bookName);

            context.startActivity(intent);
            return;
        } else if (fileType.equals("html") || fileType.equals("HTML") || fileType.equals("jsp")) {
            Intent intent = new Intent(context, BrowerActivity.class);
            intent.putExtra("filePath", "file://" + getSDPath(bookId).getAbsolutePath() + "/" + fileName);
            intent.putExtra("model", BrowerActivity.MODEL_BROWSER);
            context.startActivity(intent);
            return;
        } else if (fileType.equals("VR_Video")) {
            Intent intent = new Intent(context, VRPlayActivity.class);
            intent.putExtra(Definition.KEY_PLAY_URL, getSDPath(bookId).getAbsolutePath() + "/" + fileName);
            context.startActivity(intent);
            return;
        } else if (fileType.equals("testPaper")) {
            Intent intent = new Intent(context, WorkWebActivity.class);
            intent.putExtra("filePath", "file://" + getSDPath(bookId).getAbsolutePath() + "/" + fileName);
            context.startActivity(intent);
            return;
        }
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getType(fileType));

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // DialogUtils.showMyDialog(context,
            // MyPreferences.SHOW_ERROR_DIALOG,
            // "信息提示", "未找到查看该文件的应用", null);
        }
    }

    public static void readFileForJiaoda(Context context, String fileName) {
        if (fileName.contains("unity3d_jiayue")) {
            String fileId = fileName.substring(fileName.lastIndexOf("-") + 1, fileName.length());
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("com.jiayue.example3d");
            LaunchIntent.putExtra("id", fileId);
            context.startActivity(LaunchIntent);
            return;
        }
        String filePath = getSDPath().getAbsolutePath();
        File file = new File(filePath + fileName);
        Uri uri = Uri.fromFile(file);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (fileType.equals("mp4")) {
            Intent intent01 = new Intent(context, VideoPlayerActivity.class);
            intent01.putExtra("filePath", file.getAbsolutePath());
            context.startActivity(intent01);
            return;
        } else if (fileType.equals("swf")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(fileName), "text/html");
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                DialogUtils.showMyDialog(context, MyPreferences.SHOW_ERROR_DIALOG, "信息提示", "未找到查看该文件的应用", null);
            }

            /*
             * Intent intent=new Intent(Intent.ACTION_VIEW); Uri uriPath=
             * Uri.parse(fileName);
             * intent.setClassName("com.UCMobile","com.UCMobile.main.UCMobile");
             * intent.setData(uriPath); context.startActivity(intent);
             */
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getType(fileType));
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                DialogUtils.showMyDialog(context, MyPreferences.SHOW_ERROR_DIALOG, "信息提示", "未找到查看该文件的应用", null);
            }
        }

    }

    public static String getType(String fileType) {
        for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (fileType.equals(MIME_MapTable[i][0]))
                return MIME_MapTable[i][1];
        }
        return "*/*";
    }

    /**
     * 判断sd卡中的文件是否存在并且可阅读
     *
     * @param bookId
     *            文件的id
     * @param bookSize
     *            文件的大小
     * @return
     */
    /*
     * public static Boolean isExistAndRead(String bookId,String bookSize){
     * if(null ==ActivityUtils.getSDPath(bookId)){ return false; } File file=
     * new File(getSDPath(bookId),bookId+".xml");
     * if(file.exists()&&file.length()==Long.parseLong(bookSize)){ return true;
     * } return false; }
     */

    /**
     * 判断sd卡中的文件是否存在
     *
     * @param bookname
     * @return
     */
    public static Boolean isExistByName(String bookId, String bookName) {
        if (null == ActivityUtils.getSDPath(bookId)) {
            return false;
        }
        File file = new File(getSDPath(bookId), bookName);
        return file.exists();
    }

    /**
     * 删除整个文件夹里面的所有文件
     *
     * @param bookId
     * @return
     */
    public static boolean deletefile(String bookId) {
        try {

            File file = new File(getSDPath(), bookId);
            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(getSDPath(), bookId + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                        System.out.println(delfile.getAbsolutePath() + "删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deletefile(bookId + File.separator + filelist[i]);
                    }
                }
                System.out.println(file.getAbsolutePath() + "删除成功");
                file.delete();
            }
        } catch (Exception e) {
            System.out.println("deletefile() Exception:" + e.getMessage());
        }
        return true;
    }

    /**
     * 删除整个文件夹里面的所有文件
     *
     * @param bookId
     * @return
     */
    public static boolean deletesiftfile(String path) {
        try {

            File file = new File(path);
            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(path + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                        System.out.println(delfile.getAbsolutePath() + "删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deletesiftfile(path + File.separator + filelist[i]);
                    }
                }
                System.out.println(file.getAbsolutePath() + "删除成功");
                file.delete();
            }
        } catch (Exception e) {
            System.out.println("deletefile() Exception:" + e.getMessage());
        }
        return true;
    }

    /**
     * 删除sd卡中的文件
     *
     * @param bookId
     * @return
     */
    public static void deleteBookFormSD(String bookId, String name) {
        if (isExistByName(bookId, name)) {
            File file = new File(getSDPath(bookId), name);
            boolean b = file.delete();
            Log.d("deleteBookFormSD", "deleteBookFormSD===" + b);
        }
        if (isExistByName(bookId, "copy_" + name)) {
            File file = new File(getSDPath(bookId), "copy_" + name);
            boolean b = file.delete();
            Log.d("deleteBookFormSD", "deleteBookFormSD===" + b);
        }
    }

    public static boolean deleteFoder(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                if (files != null) {
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFoder(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
            }
            boolean isSuccess = file.delete();
            if (!isSuccess) {
                return false;
            }
        }
        Log.d("deleteBookFormSD", "deleteFoder===true");
        return true;
    }

    /**
     * 删除sd卡中的文件
     *
     * @param bookId
     * @return
     */
    public static boolean deleteBookFormSD(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    public static int getFilePackageImageId(Context context, int dataType, String type) {
        if (dataType == 0) {
            return getFileImageId(context, type);
            // }else if(dataType==2){
            // return
            // context.getResources().getIdentifier("picbag","drawable","com.jiayue");
            // // return R.drawable.picbag;
            // }else if(dataType==3){
            // return
            // context.getResources().getIdentifier("mp3bag","drawable","com.jiayue");
            // // return R.drawable.mp3bag;
            // }else if(dataType==4){
            // return
            // context.getResources().getIdentifier("videobag","drawable","com.jiayue");
            // // return R.drawable.videobag;
        } else if (dataType == 1) {
            return context.getResources().getIdentifier("bao", "drawable", context.getPackageName());
            // return R.drawable.wordbag;
            // }else if(dataType==6){
            // return
            // context.getResources().getIdentifier("pptbag","drawable","com.jiayue");
            // // return R.drawable.pptbag;
            // }else if(dataType==7){
            // return
            // context.getResources().getIdentifier("book3d","drawable","com.jiayue");
            // // return R.drawable.pptbag;
        } else {
            return context.getResources().getIdentifier("url", "drawable", context.getPackageName());
        }
        // return R.drawable.dont;
    }

    public static int getFilePackageImageId(Context context, int dataType, String type, String name) {
        if (dataType == 0) {
            return getFileImageId(context, type);
            // }else if(dataType==2){
            // return
            // context.getResources().getIdentifier("picbag","drawable","com.jiayue");
            // // return R.drawable.picbag;
            // }else if(dataType==3){
            // return
            // context.getResources().getIdentifier("mp3bag","drawable","com.jiayue");
            // // return R.drawable.mp3bag;
            // }else if(dataType==4){
            // return
            // context.getResources().getIdentifier("videobag","drawable","com.jiayue");
            // // return R.drawable.videobag;
        } else if (dataType == 1) {
            return context.getResources().getIdentifier("bao", "drawable", context.getPackageName());
            // return R.drawable.wordbag;
            // }else if(dataType==6){
            // return
            // context.getResources().getIdentifier("pptbag","drawable","com.jiayue");
            // // return R.drawable.pptbag;
            // }else if(dataType==7){
            // return
            // context.getResources().getIdentifier("book3d","drawable","com.jiayue");
            // // return R.drawable.pptbag;
        } else {
            if (name.equalsIgnoreCase("当当")) {
                return context.getResources().getIdentifier("dangdang", "drawable", context.getPackageName());
            } else if (name.equalsIgnoreCase("京东")) {
                return context.getResources().getIdentifier("jingdong", "drawable", context.getPackageName());
            } else if (name.equalsIgnoreCase("亚马逊")) {
                return context.getResources().getIdentifier("yamaxun", "drawable", context.getPackageName());
            } else {
                return context.getResources().getIdentifier("url", "drawable", context.getPackageName());
            }

        }
        // return R.drawable.dont;
    }

    /**
     * 返回文件类型所对应的图片
     *
     * @param context
     * @param type
     * @return
     */
    public static int getFileImageId(Context context, String type) {
//		Log.e("fileType",type);
        if (null == type) {
            return R.drawable.other;
        } else if (type.equalsIgnoreCase("pdf")) {
            return context.getResources().getIdentifier("pdf", "drawable", context.getPackageName());
            // return R.drawable.pdf;
        } else if (type.equalsIgnoreCase("ppt")) {
            return context.getResources().getIdentifier("ppt", "drawable", context.getPackageName());
            // return R.drawable.ppt;
        } else if (type.equalsIgnoreCase("excel")) {
            return context.getResources().getIdentifier("excel", "drawable", context.getPackageName());
            // return R.drawable.excel;
        } else if (type.equalsIgnoreCase("video") || type.equalsIgnoreCase("avi") || type.equalsIgnoreCase("swf") || type.equalsIgnoreCase("rmvb") || type.equalsIgnoreCase("mp4") || type.equalsIgnoreCase("IFO") || type.equalsIgnoreCase("VOB") || type
                .equals("mov")) {
            return context.getResources().getIdentifier("video", "drawable", context.getPackageName());
            // return R.drawable.video;
        } else if (type.equalsIgnoreCase("mp3") || type.equalsIgnoreCase("wav")) {
            return context.getResources().getIdentifier("mp3", "drawable", context.getPackageName());
        }
		/*else if (type.equals("doc")) {
			return R.drawable.word;
		} else if (type.equals("txt")) {
			return R.drawable.txt;
		}*/
        else if (type.equalsIgnoreCase("html") || type.equalsIgnoreCase("xml")) {
            return context.getResources().getIdentifier("html", "drawable", context.getPackageName());
        } else if (type.equalsIgnoreCase("zip")) {
            return context.getResources().getIdentifier("html", "drawable", context.getPackageName());
        } else if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("gif")) {
            return context.getResources().getIdentifier("jpg", "drawable", context.getPackageName());
            // return R.drawable.jpg;
        } else if (type.equalsIgnoreCase("assetbundle") || type.equalsIgnoreCase("frame") || type.equalsIgnoreCase("skeleton") || type.equalsIgnoreCase("blender")) {
            return context.getResources().getIdentifier("book3d", "drawable", context.getPackageName());
        } else if (type.equalsIgnoreCase("VR_Video")) {
            return context.getResources().getIdentifier("vr", "drawable", context.getPackageName());
        } else if (type.equalsIgnoreCase("testPaper")) {
            return context.getResources().getIdentifier("testpaper", "drawable", context.getPackageName());
        } else {
            return context.getResources().getIdentifier("other", "drawable", context.getPackageName());
        }
    }

    /**
     * 返回文件类型所对应的图片
     *
     * @param dataType
     * @param type
     * @return
     */
    public static int getFileImageId(String type) {
        // if (null == type) {
        // return R.drawable.dont;
        // } else
        if (type.equals("pdf")) {
            return R.drawable.pdf;
        }
        // else if (type.equals("ppt")) {
        // return R.drawable.ppt;
        // } else if (type.equals("excel")) {
        // return R.drawable.excel;
        // }
        else if (type.equalsIgnoreCase("video") || type.equalsIgnoreCase("avi") || type.equalsIgnoreCase("swf") || type.equalsIgnoreCase("rmvb") || type.equalsIgnoreCase("mp4") || type.equalsIgnoreCase("IFO") || type.equalsIgnoreCase("VOB") || type.equalsIgnoreCase("mov")) {
            return R.drawable.video;
        } else if (type.equalsIgnoreCase("mp3")) {
            return R.drawable.mp3;
        }
        // else if (type.equals("doc")) {
        // return R.drawable.word;
        // } else if (type.equals("txt")) {
        // return R.drawable.txt;
        // }
        else if (type.equalsIgnoreCase("html") || type.equalsIgnoreCase("xml")) {
            return R.drawable.html;
        } else if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("gif")) {
            return R.drawable.jpg;
        } else if (type.equalsIgnoreCase("assetbundle")) {
            return R.drawable.book3d;
        } else {
            return R.drawable.other;
        }
    }

    /**
     * 返回文件类型所对应的图片
     *
     * @param dataType
     * @param type
     * @return
     */
    // public static String getPiontFor1(Float price){
    // java.text.DecimalFormat df =new java.text.DecimalFormat("#.0");
    // String str = df.format(price);
    // if(str.equals(".0")){
    // return "免费";
    // }
    // return "￥"+df.format(price);
    // }
}
