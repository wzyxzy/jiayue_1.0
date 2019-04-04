package com.artifex.mupdf.fitz;

public class ColorSpace
{
  public static ColorSpace DeviceBGR;
  public static ColorSpace DeviceCMYK;
  public static ColorSpace DeviceGray;
  public static ColorSpace DeviceRGB;
  private long pointer;

  static
  {
    Context.init();
    DeviceGray = new ColorSpace(nativeDeviceGray());
    DeviceRGB = new ColorSpace(nativeDeviceRGB());
    DeviceBGR = new ColorSpace(nativeDeviceBGR());
    DeviceCMYK = new ColorSpace(nativeDeviceCMYK());
  }

  private ColorSpace(long paramLong)
  {
    this.pointer = paramLong;
  }

  protected static ColorSpace fromPointer(long paramLong)
  {
    if (paramLong == DeviceGray.pointer)
      return DeviceGray;
    if (paramLong == DeviceRGB.pointer)
      return DeviceRGB;
    if (paramLong == DeviceBGR.pointer)
      return DeviceBGR;
    if (paramLong == DeviceCMYK.pointer)
      return DeviceCMYK;
    return new ColorSpace(paramLong);
  }

  private static native long nativeDeviceBGR();

  private static native long nativeDeviceCMYK();

  private static native long nativeDeviceGray();

  private static native long nativeDeviceRGB();

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native int getNumberOfComponents();

  public String toString()
  {
    if (this == DeviceGray)
      return "DeviceGray";
    if (this == DeviceRGB)
      return "DeviceRGB";
    if (this == DeviceBGR)
      return "DeviceBGR";
    if (this == DeviceCMYK)
      return "DeviceCMYK";
    return "ColorSpace(" + getNumberOfComponents() + ")";
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.ColorSpace
 * JD-Core Version:    0.6.0
 */