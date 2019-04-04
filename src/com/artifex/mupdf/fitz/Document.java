package com.artifex.mupdf.fitz;

public class Document
{
  public static final String META_ENCRYPTION = "encryption";
  public static final String META_FORMAT = "format";
  public static final String META_INFO_AUTHOR = "info:Author";
  public static final String META_INFO_TITLE = "info:Title";
  protected String path;
  protected long pointer;

  static
  {
    Context.init();
  }

  protected Document(long paramLong)
  {
    this.pointer = paramLong;
  }

  public static Document openDocument(String paramString)
  {
    Document localDocument = openNativeWithPath(paramString);
    localDocument.path = paramString;
    return localDocument;
  }

  public static Document openDocument(byte[] paramArrayOfByte, String paramString)
  {
    return openNativeWithBuffer(paramArrayOfByte, paramString);
  }

  protected static native Document openNativeWithBuffer(byte[] paramArrayOfByte, String paramString);

  protected static native Document openNativeWithPath(String paramString);

  public static native boolean recognize(String paramString);

  public native boolean authenticatePassword(String paramString);

  public native int countPages();

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native int findBookmark(long paramLong);

  public native String getMetaData(String paramString);

  public String getPath()
  {
    return this.path;
  }

  public boolean isPDF()
  {
    return false;
  }

  public native boolean isReflowable();

  public native boolean isUnencryptedPDF();

  public native void layout(float paramFloat1, float paramFloat2, float paramFloat3);

  public native Outline[] loadOutline();

  public native Page loadPage(int paramInt);

  public native long makeBookmark(int paramInt);

  public String makeProof(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    return proofNative(paramString1, paramString2, paramString3, paramInt);
  }

  public native boolean needsPassword();

  protected native String proofNative(String paramString1, String paramString2, String paramString3, int paramInt);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Document
 * JD-Core Version:    0.6.0
 */