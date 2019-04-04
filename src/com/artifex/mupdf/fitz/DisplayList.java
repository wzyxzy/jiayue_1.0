package com.artifex.mupdf.fitz;

public class DisplayList
{
  private long pointer;

  static
  {
    Context.init();
  }

  public DisplayList()
  {
    this.pointer = newNative();
  }

  private DisplayList(long paramLong)
  {
    this.pointer = paramLong;
  }

  private native long newNative();

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public void run(Device paramDevice, Matrix paramMatrix, Cookie paramCookie)
  {
    run(paramDevice, paramMatrix, null, paramCookie);
  }

  public native void run(Device paramDevice, Matrix paramMatrix, Rect paramRect, Cookie paramCookie);

  public native Rect[] search(String paramString);

  public native Pixmap toPixmap(Matrix paramMatrix, ColorSpace paramColorSpace, boolean paramBoolean);

  public StructuredText toStructuredText()
  {
    return toStructuredText(null);
  }

  public native StructuredText toStructuredText(String paramString);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.DisplayList
 * JD-Core Version:    0.6.0
 */