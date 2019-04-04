package com.artifex.mupdf.fitz;

public class Cookie
{
  private long pointer = newNative();

  static
  {
    Context.init();
  }

  private native long newNative();

  public native void abort();

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Cookie
 * JD-Core Version:    0.6.0
 */