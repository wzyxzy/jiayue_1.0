package com.artifex.mupdf.fitz;

public final class DisplayListDevice extends NativeDevice
{
  public DisplayListDevice(DisplayList paramDisplayList)
  {
    super(newNative(paramDisplayList));
  }

  private static native long newNative(DisplayList paramDisplayList);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.DisplayListDevice
 * JD-Core Version:    0.6.0
 */