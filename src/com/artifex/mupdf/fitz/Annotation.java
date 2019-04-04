package com.artifex.mupdf.fitz;

public class Annotation
{
  private long pointer;

  static
  {
    Context.init();
  }

  protected Annotation(long paramLong)
  {
    this.pointer = paramLong;
  }

  private native long advance();

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native Rect getBounds();

  public native void run(Device paramDevice, Matrix paramMatrix, Cookie paramCookie);

  public native DisplayList toDisplayList();

  public native Pixmap toPixmap(Matrix paramMatrix, ColorSpace paramColorSpace, boolean paramBoolean);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Annotation
 * JD-Core Version:    0.6.0
 */