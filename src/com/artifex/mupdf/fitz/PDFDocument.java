package com.artifex.mupdf.fitz;

public class PDFDocument extends Document
{
  static
  {
    Context.init();
  }

  public PDFDocument()
  {
    super(newNative());
  }

  protected PDFDocument(long paramLong)
  {
    super(paramLong);
  }

  private native PDFObject addPageBuffer(Rect paramRect, int paramInt, PDFObject paramPDFObject, Buffer paramBuffer);

  private native PDFObject addPageString(Rect paramRect, int paramInt, PDFObject paramPDFObject, String paramString);

  private native PDFObject addStreamBuffer(Buffer paramBuffer, Object paramObject, boolean paramBoolean);

  private native PDFObject addStreamString(String paramString, Object paramObject, boolean paramBoolean);

  private static native long newNative();

  public native PDFObject addFont(Font paramFont);

  public native PDFObject addImage(Image paramImage);

  public native PDFObject addObject(PDFObject paramPDFObject);

  public PDFObject addPage(Rect paramRect, int paramInt, PDFObject paramPDFObject, Buffer paramBuffer)
  {
    return addPageBuffer(paramRect, paramInt, paramPDFObject, paramBuffer);
  }

  public PDFObject addPage(Rect paramRect, int paramInt, PDFObject paramPDFObject, String paramString)
  {
    return addPageString(paramRect, paramInt, paramPDFObject, paramString);
  }

  public PDFObject addRawStream(Buffer paramBuffer)
  {
    return addStreamBuffer(paramBuffer, null, true);
  }

  public PDFObject addRawStream(Buffer paramBuffer, Object paramObject)
  {
    return addStreamBuffer(paramBuffer, paramObject, true);
  }

  public PDFObject addRawStream(String paramString)
  {
    return addStreamString(paramString, null, true);
  }

  public PDFObject addRawStream(String paramString, Object paramObject)
  {
    return addStreamString(paramString, paramObject, true);
  }

  public native PDFObject addSimpleFont(Font paramFont);

  public PDFObject addStream(Buffer paramBuffer)
  {
    return addStreamBuffer(paramBuffer, null, false);
  }

  public PDFObject addStream(Buffer paramBuffer, Object paramObject)
  {
    return addStreamBuffer(paramBuffer, paramObject, false);
  }

  public PDFObject addStream(String paramString)
  {
    return addStreamString(paramString, null, false);
  }

  public PDFObject addStream(String paramString, Object paramObject)
  {
    return addStreamString(paramString, paramObject, false);
  }

  public native boolean canBeSavedIncrementally();

  public native int countObjects();

  public native PDFObject createObject();

  public native void deleteObject(int paramInt);

  public void deleteObject(PDFObject paramPDFObject)
  {
    deleteObject(paramPDFObject.asIndirect());
  }

  public native void deletePage(int paramInt);

  public native PDFObject findPage(int paramInt);

  public native PDFObject getTrailer();

  public native PDFObject graftObject(PDFObject paramPDFObject);

  public native boolean hasUnsavedChanges();

  public native void insertPage(int paramInt, PDFObject paramPDFObject);

  public boolean isPDF()
  {
    return true;
  }

  public native PDFObject newArray();

  public native PDFObject newBoolean(boolean paramBoolean);

  public native PDFObject newByteString(byte[] paramArrayOfByte);

  public native PDFObject newDictionary();

  public native PDFObject newIndirect(int paramInt1, int paramInt2);

  public native PDFObject newInteger(int paramInt);

  public native PDFObject newName(String paramString);

  public native PDFObject newNull();

  public native PDFGraftMap newPDFGraftMap();

  public native PDFObject newReal(float paramFloat);

  public native PDFObject newString(String paramString);

  public native int save(String paramString1, String paramString2);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.PDFDocument
 * JD-Core Version:    0.6.0
 */