package com.artifex.mupdf.fitz;

public class NativeDevice extends Device
{
  private long nativeInfo;
  private Object nativeResource;

  static
  {
    Context.init();
  }

  protected NativeDevice(long paramLong)
  {
    super(paramLong);
  }

  public final native void beginGroup(Rect paramRect, boolean paramBoolean1, boolean paramBoolean2, int paramInt, float paramFloat);

  public final native void beginMask(Rect paramRect, boolean paramBoolean, ColorSpace paramColorSpace, float[] paramArrayOfFloat, int paramInt);

  public final native int beginTile(Rect paramRect1, Rect paramRect2, float paramFloat1, float paramFloat2, Matrix paramMatrix, int paramInt);

  public final native void clipImageMask(Image paramImage, Matrix paramMatrix);

  public final native void clipPath(Path paramPath, boolean paramBoolean, Matrix paramMatrix);

  public final native void clipStrokePath(Path paramPath, StrokeState paramStrokeState, Matrix paramMatrix);

  public final native void clipStrokeText(Text paramText, StrokeState paramStrokeState, Matrix paramMatrix);

  public final native void clipText(Text paramText, Matrix paramMatrix);

  public final native void close();

  public void destroy()
  {
    super.destroy();
    this.nativeInfo = 0L;
    this.nativeResource = null;
  }

  public final native void endGroup();

  public final native void endMask();

  public final native void endTile();

  public final native void fillImage(Image paramImage, Matrix paramMatrix, float paramFloat, int paramInt);

  public final native void fillImageMask(Image paramImage, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat, int paramInt);

  public final native void fillPath(Path paramPath, boolean paramBoolean, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat, int paramInt);

  public final native void fillShade(Shade paramShade, Matrix paramMatrix, float paramFloat, int paramInt);

  public final native void fillText(Text paramText, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat, int paramInt);

  protected native void finalize();

  public final native void ignoreText(Text paramText, Matrix paramMatrix);

  public final native void popClip();

  public final native void strokePath(Path paramPath, StrokeState paramStrokeState, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat, int paramInt);

  public final native void strokeText(Text paramText, StrokeState paramStrokeState, Matrix paramMatrix, ColorSpace paramColorSpace, float[] paramArrayOfFloat, float paramFloat, int paramInt);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.NativeDevice
 * JD-Core Version:    0.6.0
 */