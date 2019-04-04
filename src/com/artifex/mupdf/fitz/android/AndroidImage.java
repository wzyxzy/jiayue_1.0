package com.artifex.mupdf.fitz.android;

import android.graphics.Bitmap;
import com.artifex.mupdf.fitz.Context;
import com.artifex.mupdf.fitz.Image;

public final class AndroidImage extends Image
{
  static
  {
    Context.init();
  }

  public AndroidImage(Bitmap paramBitmap, AndroidImage paramAndroidImage)
  {
    super(0L);
    this.pointer = newAndroidImageFromBitmap(paramBitmap, paramAndroidImage.pointer);
  }

  private native long newAndroidImageFromBitmap(Bitmap paramBitmap, long paramLong);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.android.AndroidImage
 * JD-Core Version:    0.6.0
 */