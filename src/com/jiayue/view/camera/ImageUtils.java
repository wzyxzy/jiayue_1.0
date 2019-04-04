package com.jiayue.view.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.util.Log;

/**
 * Created by hezhisu on 2017/5/16.
 */

public class ImageUtils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // save image to sdcard path: Pictures/MyTestImage/
    public static String saveImageData(int width, int height, byte[] imageData) {

        YuvImage image = new YuvImage(imageData, ImageFormat.NV21, width, height, null);
        if(image!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);

            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

            //**********************
            //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
            Bitmap newbmp = rotateMyBitmap(bmp);

            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File imageFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (imageFile == null) return "";
            try {
                FileOutputStream out = new FileOutputStream(imageFile);
                newbmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return imageFile.getAbsolutePath();
        }
        return "";

    }


    public static String saveBitmap(Bitmap newbmp){
        File imageFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (imageFile.isFile()) 
        	imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            newbmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageFile.getAbsolutePath();
    }

    public static Bitmap convertDataToBitmap(Context context, int width, int height, byte[] imageData, boolean crop, int mode, int marginTop){
    	Log.d("ppppppppppppppppp", "width="+width+"---height="+height+"---marginTop=="+marginTop);
        YuvImage image = new YuvImage(imageData, ImageFormat.NV21, width, height, null);
        if(image!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);

            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

            //**********************
            //因为图片会放生旋转，因此要对图片进行旋转到和手机在一个方向上
            Bitmap newbmp = rotateMyBitmap(bmp);
            if(crop){
                int x = newbmp.getWidth() / 8;
                int y = newbmp.getHeight()/4+10;
                Log.d("ppppppppppppppppp", "getWidth="+newbmp.getWidth()+"---height="+newbmp.getHeight());
                return Bitmap.createBitmap(newbmp, x, y, (newbmp.getWidth() * 7 / 8)-x, mode == 1 ? newbmp.getWidth() * 3 / 4 : newbmp.getWidth() * 3 / 8, null, false);
            }
            return newbmp;
        }
        return null;
    }

    public static Bitmap rotateMyBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.setRotate(90, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        targetX = bitmap.getHeight();
        targetY = 0;


        final float[] values = new float[9];
        matrix.getValues(values);


        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];


        matrix.postTranslate(targetX - x1, targetY - y1);


        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_4444);


        Paint paint = new Paint();
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);


        return canvasBitmap;

    };

    public static File getOutputMediaFile(int type) {
        File imageFileDir =
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/jiayue/filename.jpg");
//        if (!imageFileDir.exists()) {
//            imageFileDir.mkdirs();
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File imageFile;
//        if (type == MEDIA_TYPE_IMAGE) {
//            imageFile = new File(imageFileDir.getPath() + File.separator +
//                    "IMG_" + timeStamp + ".jpg");
//        } else if (type == MEDIA_TYPE_VIDEO) {
//            imageFile = new File(imageFileDir.getPath() + File.separator +
//                    "VID_" + timeStamp + ".mp4");
//        } else return null;
        return imageFileDir;
    }

}
