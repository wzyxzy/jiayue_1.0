package com.artifex.mupdf.fitz;

public class Device
{
  public static final int BLEND_COLOR = 14;
  public static final int BLEND_COLOR_BURN = 7;
  public static final int BLEND_COLOR_DODGE = 6;
  public static final int BLEND_DARKEN = 4;
  public static final int BLEND_DIFFERENCE = 10;
  public static final int BLEND_EXCLUSION = 11;
  public static final int BLEND_HARD_LIGHT = 8;
  public static final int BLEND_HUE = 12;
  public static final int BLEND_ISOLATED = 16;
  public static final int BLEND_KNOCKOUT = 32;
  public static final int BLEND_LIGHTEN = 5;
  public static final int BLEND_LUMINOSITY = 15;
  public static final int BLEND_MODEMASK = 15;
  public static final int BLEND_MULTIPLY = 1;
  public static final int BLEND_NORMAL = 0;
  public static final int BLEND_OVERLAY = 3;
  public static final int BLEND_SATURATION = 13;
  public static final int BLEND_SCREEN = 2;
  public static final int BLEND_SOFT_LIGHT = 9;
  public static final int FLAG_COLOR = 2;
  public static final int FLAG_DASHCAP_UNDEFINED = 64;
  public static final int FLAG_ENDCAP_UNDEFINED = 128;
  public static final int FLAG_FILLCOLOR_UNDEFINED = 8;
  public static final int FLAG_LINEJOIN_UNDEFINED = 256;
  public static final int FLAG_LINEWIDTH_UNDEFINED = 1024;
  public static final int FLAG_MASK = 1;
  public static final int FLAG_MITERLIMIT_UNDEFINED = 512;
  public static final int FLAG_STARTCAP_UNDEFINED = 32;
  public static final int FLAG_STROKECOLOR_UNDEFINED = 16;
  public static final int FLAG_UNCACHEABLE = 4;
  public static final int IGNORE_IMAGE = 1;
  public static final int IGNORE_SHADE = 2;
  protected long pointer;

  static
  {
    Context.init();
  }

  protected Device()
  {
    this.pointer = newNative();
  }

  protected Device(long paramLong)
  {
    this.pointer = paramLong;
  }

  private native long newNative();

  public void beginGroup(Rect paramRect, ColorSpace paramColorSpace, boolean paramBoolean1, boolean paramBoolean2, int paramInt, float paramFloat)
  {
  }

  public void beginMask(Rect paramRect, boolean paramBoolean, ColorSpace paramColorSpace, float[] paramArrayOfFloat)
  {
  }

  public int beginTile(Rect paramRect1, Rect paramRect2, float paramFloat1, float paramFloat2, Matrix paramMatrix, int paramInt)
  {
    return 0;
  }

  public void clipImageMask(Image paramImage, Matrix paramMatrix)
  {
  }

  public void clipPath(Path paramPath, boolean paramBoolean, Matrix paramMatrix)
  {
  }

  public void clipStrokePath(Path paramPath, StrokeState paramStrokeState, Matrix paramMatrix)
  {
  }

  public void clipStrokeText(Text paramText, StrokeState paramStrokeState, Matrix paramMatrix)
  {
  }

  public void clipText(Text paramText, Matrix paramMatrix)
  {
  }

  public void close()
  {
  }

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  public void endGroup()
  {
  }

  public void endMask()
  {
  }

  public void endTile()
  {
  }

  public void fillImage(Image paramImage, Matrix paramMatrix, float paramFloat)
  {
  }

  public void fillImageMask(Image paramImage, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat)
  {
  }

  public void fillPath(Path paramPath, boolean paramBoolean, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat)
  {
  }

  public void fillShade(Shade paramShade, Matrix paramMatrix, float paramFloat)
  {
  }

  public void fillText(Text paramText, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat)
  {
  }

  protected native void finalize();

  public void ignoreText(Text paramText, Matrix paramMatrix)
  {
  }

  public void popClip()
  {
  }

  public void strokePath(Path paramPath, StrokeState paramStrokeState, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat)
  {
  }

  public void strokeText(Text paramText, StrokeState paramStrokeState, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat)
  {
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Device
 * JD-Core Version:    0.6.0
 */