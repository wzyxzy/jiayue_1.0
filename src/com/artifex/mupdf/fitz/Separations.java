package com.artifex.mupdf.fitz;

public class Separations
{
  public static final int SEPARATION_COMPOSITE = 0;
  public static final int SEPARATION_DISABLED = 2;
  public static final int SEPARATION_SPOT = 1;
  private long pointer;

  static
  {
    Context.init();
  }

  protected Separations(long paramLong)
  {
    this.pointer = paramLong;
  }

  public native boolean areSeparationsControllable();

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native int getNumberOfSeparations();

  public native Separation getSeparation(int paramInt);

  public native int getSeparationBehavior(int paramInt);

  public native void setSeparationBehavior(int paramInt1, int paramInt2);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Separations
 * JD-Core Version:    0.6.0
 */