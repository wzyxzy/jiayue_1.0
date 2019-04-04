package com.artifex.mupdf.fitz;

public final class DrawDevice extends NativeDevice
{
  static
  {
    Context.init();
  }

  public DrawDevice(Pixmap paramPixmap)
  {
    super(newNative(paramPixmap));
  }

  private static native long newNative(Pixmap paramPixmap);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.DrawDevice
 * JD-Core Version:    0.6.0
 */