package com.artifex.mupdf.fitz;

public class Context
{
  private static boolean inited = false;

  static
  {
    init();
  }

  public static native int gprfSupportedNative();

  public static void init()
  {
    if (!inited)
    {
      inited = true;
      try
      {
        System.loadLibrary("mupdf_java");
        if (initNative() < 0)
          throw new RuntimeException("cannot initialize mupdf library");
      }
      catch (UnsatisfiedLinkError localUnsatisfiedLinkError1)
      {
        while (true)
          try
          {
            System.loadLibrary("mupdf_java64");
          }
          catch (UnsatisfiedLinkError localUnsatisfiedLinkError2)
          {
            System.loadLibrary("mupdf_java32");
          }
      }
    }
  }

  private static native int initNative();
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Context
 * JD-Core Version:    0.6.0
 */