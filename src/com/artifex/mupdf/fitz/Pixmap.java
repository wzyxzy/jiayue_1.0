package com.artifex.mupdf.fitz;

public class Pixmap
{
  private long pointer;

  static
  {
    Context.init();
  }

  private Pixmap(long paramLong)
  {
    this.pointer = paramLong;
  }

  public Pixmap(ColorSpace paramColorSpace, int paramInt1, int paramInt2)
  {
    this(paramColorSpace, 0, 0, paramInt1, paramInt2, false);
  }

  public Pixmap(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramColorSpace, paramInt1, paramInt2, paramInt3, paramInt4, false);
  }

  public Pixmap(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    this.pointer = newNative(paramColorSpace, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
  }

  public Pixmap(ColorSpace paramColorSpace, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramColorSpace, 0, 0, paramInt1, paramInt2, paramBoolean);
  }

  public Pixmap(ColorSpace paramColorSpace, Rect paramRect)
  {
    this(paramColorSpace, paramRect, false);
  }

  public Pixmap(ColorSpace paramColorSpace, Rect paramRect, boolean paramBoolean)
  {
    this(paramColorSpace, (int)paramRect.x0, (int)paramRect.y0, (int)(paramRect.x1 - paramRect.x0), (int)(paramRect.y1 - paramRect.y0), paramBoolean);
  }

  private native void clearWithValue(int paramInt);

  private native long newNative(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

  public native void clear();

  public void clear(int paramInt)
  {
    clearWithValue(paramInt);
  }

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native boolean getAlpha();

  public Rect getBounds()
  {
    int i = getX();
    int j = getY();
    return new Rect(i, j, i + getWidth(), j + getHeight());
  }

  public native ColorSpace getColorSpace();

  public native int getHeight();

  public native int getNumberOfComponents();

  public native int[] getPixels();

  public native byte getSample(int paramInt1, int paramInt2, int paramInt3);

  public native byte[] getSamples();

  public native int getStride();

  public native int getWidth();

  public native int getX();

  public native int getXResolution();

  public native int getY();

  public native int getYResolution();

  public native void saveAsPNG(String paramString);

  public String toString()
  {
    return "Pixmap(w=" + getWidth() + " h=" + getHeight() + " x=" + getX() + " y=" + getY() + " n=" + getNumberOfComponents() + " alpha=" + getAlpha() + " cs=" + getColorSpace() + ")";
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Pixmap
 * JD-Core Version:    0.6.0
 */