package com.artifex.mupdf.fitz;

public class Page
{
  private long pointer;

  static
  {
    Context.init();
  }

  protected Page(long paramLong)
  {
    this.pointer = paramLong;
  }

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native Annotation[] getAnnotations();

  public native Rect getBounds();

  public native Link[] getLinks();

  public native Separations getSeparations();

  public void run(Device paramDevice, Matrix paramMatrix)
  {
    run(paramDevice, paramMatrix, null);
  }

  public native void run(Device paramDevice, Matrix paramMatrix, Cookie paramCookie);

  public native void runPageContents(Device paramDevice, Matrix paramMatrix, Cookie paramCookie);

  public native Rect[] search(String paramString);

  public native byte[] textAsHtml();

  public native DisplayList toDisplayList(boolean paramBoolean);

  public native Pixmap toPixmap(Matrix paramMatrix, ColorSpace paramColorSpace, boolean paramBoolean);

  public StructuredText toStructuredText()
  {
    return toStructuredText(null);
  }

  public native StructuredText toStructuredText(String paramString);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Page
 * JD-Core Version:    0.6.0
 */