package com.artifex.mupdf.fitz;

public class StrokeState
{
  public static final int LINECAP_BUTT = 0;
  public static final int LINECAP_ROUND = 1;
  public static final int LINECAP_SQUARE = 2;
  public static final int LINECAP_TRIANGLE = 3;
  public static final int LINEJOIN_BEVEL = 2;
  public static final int LINEJOIN_MITER = 0;
  public static final int LINEJOIN_MITER_XPS = 3;
  public static final int LINEJOIN_ROUND = 1;
  private long pointer;

  static
  {
    Context.init();
  }

  public StrokeState(int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2)
  {
    this.pointer = newNative(paramInt1, 0, paramInt2, paramInt3, paramFloat1, paramFloat2, 0.0F, null);
  }

  public StrokeState(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, float paramFloat3, float[] paramArrayOfFloat)
  {
    this.pointer = newNative(paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramFloat2, paramFloat3, paramArrayOfFloat);
  }

  private StrokeState(long paramLong)
  {
    this.pointer = paramLong;
  }

  private native long newNative(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, float paramFloat3, float[] paramArrayOfFloat);

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native int getDashCap();

  public native float getDashPhase();

  public native float[] getDashes();

  public native int getEndCap();

  public native int getLineJoin();

  public native float getLineWidth();

  public native float getMiterLimit();

  public native int getStartCap();
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.StrokeState
 * JD-Core Version:    0.6.0
 */