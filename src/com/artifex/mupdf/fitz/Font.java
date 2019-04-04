package com.artifex.mupdf.fitz;

public class Font
{
  private long pointer;

  static
  {
    Context.init();
  }

  private Font(long paramLong)
  {
    this.pointer = paramLong;
  }

  public Font(String paramString)
  {
    this.pointer = newNative(paramString, 0);
  }

  public Font(String paramString, int paramInt)
  {
    this.pointer = newNative(paramString, paramInt);
  }

  private native long newNative(String paramString, int paramInt);

  public float advanceGlyph(int paramInt)
  {
    return advanceGlyph(paramInt, false);
  }

  public native float advanceGlyph(int paramInt, boolean paramBoolean);

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  public native int encodeCharacter(int paramInt);

  protected native void finalize();

  public native String getName();

  public String toString()
  {
    return "Font(" + getName() + ")";
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Font
 * JD-Core Version:    0.6.0
 */