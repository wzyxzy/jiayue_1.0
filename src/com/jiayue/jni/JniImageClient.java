package com.jiayue.jni;


public class JniImageClient {
    static {
        System.loadLibrary("img_match");
    }

    public static native long load_features(String img_lib_path);

    public static native int match_img(long object, String test_img_path, byte[] matched_img, int[] confidence, int scale_to_fixed_pixels);

    public static native int release_features(long object);
    
    public static native int match_topk_img(long object,String test_img_path,byte[] matched_img,int size,int[] topk,int scale_to_fixed_pixels);
//  int expected_k = 5 ;
//  IntByReference real_k = new IntByReference(expected_k);
//  Integer compressed_to_pixels = 136;
//  byte[] matched_info_list = new byte[expected_k * 2 * item_size];
//  imageRecognition.Clibrary.INSTANTCE.match_img_topk(im_handle, imageRecognition.JavaStrToCchar(tempDestFile.getAbsolutePath()), matched_info_list, item_size, real_k, compressed_to_pixels);
//  for(int i = 0; i < real_k.getValue(); ++i)
//  {
//      StringBuffer img_name = new StringBuffer(item_size);
//      StringBuffer confidence = new StringBuffer(item_size);
//      for (int j = 0; j < item_size; j++) {
//          if(matched_info_list[2*i*item_size+j] != 0)
//              img_name.append(""+(char)matched_info_list[2*i*item_size+j]);
//          if(matched_info_list[(2*i+1)*item_size+j] != 0)
//              confidence.append(""+(char)matched_info_list[(2*i+1)*item_size+j]);
//      }
//      System.out.println("Top " + i + ": [" + img_name.toString() + " : " + confidence.toString() + "]");
//  }
}
