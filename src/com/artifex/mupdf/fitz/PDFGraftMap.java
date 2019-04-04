package com.artifex.mupdf.fitz;

public class PDFGraftMap
{
  private long pointer;

  static
  {
    Context.init();
  }

  private PDFGraftMap(long paramLong)
  {
    this.pointer = paramLong;
  }

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native PDFObject graftObject(PDFObject paramPDFObject);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.PDFGraftMap
 * JD-Core Version:    0.6.0
 */