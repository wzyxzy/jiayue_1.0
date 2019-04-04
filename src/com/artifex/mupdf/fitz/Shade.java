package com.artifex.mupdf.fitz;

public class Shade
{
  private long pointer;

  static
  {
    Context.init();
  }

  private Shade(long paramLong)
  {
    this.pointer = paramLong;
  }

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Shade
 * JD-Core Version:    0.6.0
 */