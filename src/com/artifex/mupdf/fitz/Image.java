package com.artifex.mupdf.fitz;

public class Image
{
  protected long pointer;

  static
  {
    Context.init();
  }

  protected Image(long paramLong)
  {
    this.pointer = paramLong;
  }

  public Image(Pixmap paramPixmap)
  {
    this.pointer = newNativeFromPixmap(paramPixmap);
  }

  public Image(String paramString)
  {
    this.pointer = newNativeFromFile(paramString);
  }

  private native long newNativeFromFile(String paramString);

  private native long newNativeFromPixmap(Pixmap paramPixmap);

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native int getBitsPerComponent();

  public native ColorSpace getColorSpace();

  public native int getHeight();

  public native boolean getImageMask();

  public native boolean getInterpolate();

  public native Image getMask();

  public native int getNumberOfComponents();

  public native int getWidth();

  public native int getXResolution();

  public native int getYResolution();

  public native Pixmap toPixmap();
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Image
 * JD-Core Version:    0.6.0
 */