package com.artifex.mupdf.fitz;

public class PDFObject
{
  public static final PDFObject Null;
  private long pointer;

  static
  {
    Context.init();
    Null = new PDFObject(newNull());
  }

  private PDFObject(long paramLong)
  {
    this.pointer = paramLong;
  }

  private native void deleteArray(int paramInt);

  private native void deleteDictionaryPDFObject(PDFObject paramPDFObject);

  private native void deleteDictionaryString(String paramString);

  private native PDFObject getArray(int paramInt);

  private native PDFObject getDictionary(String paramString);

  private static native long newNull();

  private native void pushBoolean(boolean paramBoolean);

  private native void pushFloat(float paramFloat);

  private native void pushInteger(int paramInt);

  private native void pushPDFObject(PDFObject paramPDFObject);

  private native void pushString(String paramString);

  private native void putArrayBoolean(int paramInt, boolean paramBoolean);

  private native void putArrayFloat(int paramInt, float paramFloat);

  private native void putArrayInteger(int paramInt1, int paramInt2);

  private native void putArrayPDFObject(int paramInt, PDFObject paramPDFObject);

  private native void putArrayString(int paramInt, String paramString);

  private native void putDictionaryPDFObjectBoolean(PDFObject paramPDFObject, boolean paramBoolean);

  private native void putDictionaryPDFObjectFloat(PDFObject paramPDFObject, float paramFloat);

  private native void putDictionaryPDFObjectInteger(PDFObject paramPDFObject, int paramInt);

  private native void putDictionaryPDFObjectPDFObject(PDFObject paramPDFObject1, PDFObject paramPDFObject2);

  private native void putDictionaryPDFObjectString(PDFObject paramPDFObject, String paramString);

  private native void putDictionaryStringBoolean(String paramString, boolean paramBoolean);

  private native void putDictionaryStringFloat(String paramString, float paramFloat);

  private native void putDictionaryStringInteger(String paramString, int paramInt);

  private native void putDictionaryStringPDFObject(String paramString, PDFObject paramPDFObject);

  private native void putDictionaryStringString(String paramString1, String paramString2);

  private native void writeRawStreamBuffer(Buffer paramBuffer);

  private native void writeRawStreamString(String paramString);

  private native void writeStreamBuffer(Buffer paramBuffer);

  private native void writeStreamString(String paramString);

  public native boolean asBoolean();

  public native byte[] asByteString();

  public native float asFloat();

  public native int asIndirect();

  public native int asInteger();

  public native String asName();

  public native String asString();

  public void delete(int paramInt)
  {
    deleteArray(paramInt);
  }

  public void delete(PDFObject paramPDFObject)
  {
    deleteDictionaryPDFObject(paramPDFObject);
  }

  public void delete(String paramString)
  {
    deleteDictionaryString(paramString);
  }

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public PDFObject get(int paramInt)
  {
    return getArray(paramInt);
  }

  public PDFObject get(String paramString)
  {
    return getDictionary(paramString);
  }

  public native boolean isArray();

  public native boolean isBoolean();

  public native boolean isDictionary();

  public native boolean isIndirect();

  public native boolean isInteger();

  public native boolean isName();

  public native boolean isNull();

  public native boolean isNumber();

  public native boolean isReal();

  public native boolean isStream();

  public native boolean isString();

  public void push(float paramFloat)
  {
    pushFloat(paramFloat);
  }

  public void push(int paramInt)
  {
    pushInteger(paramInt);
  }

  public void push(PDFObject paramPDFObject)
  {
    pushPDFObject(paramPDFObject);
  }

  public void push(String paramString)
  {
    pushString(paramString);
  }

  public void push(boolean paramBoolean)
  {
    pushBoolean(paramBoolean);
  }

  public void put(int paramInt, float paramFloat)
  {
    putArrayFloat(paramInt, paramFloat);
  }

  public void put(int paramInt1, int paramInt2)
  {
    putArrayInteger(paramInt1, paramInt2);
  }

  public void put(int paramInt, PDFObject paramPDFObject)
  {
    putArrayPDFObject(paramInt, paramPDFObject);
  }

  public void put(int paramInt, String paramString)
  {
    putArrayString(paramInt, paramString);
  }

  public void put(int paramInt, boolean paramBoolean)
  {
    putArrayBoolean(paramInt, paramBoolean);
  }

  public void put(PDFObject paramPDFObject, float paramFloat)
  {
    putDictionaryPDFObjectFloat(paramPDFObject, paramFloat);
  }

  public void put(PDFObject paramPDFObject, int paramInt)
  {
    putDictionaryPDFObjectInteger(paramPDFObject, paramInt);
  }

  public void put(PDFObject paramPDFObject1, PDFObject paramPDFObject2)
  {
    putDictionaryPDFObjectPDFObject(paramPDFObject1, paramPDFObject2);
  }

  public void put(PDFObject paramPDFObject, String paramString)
  {
    putDictionaryPDFObjectString(paramPDFObject, paramString);
  }

  public void put(PDFObject paramPDFObject, boolean paramBoolean)
  {
    putDictionaryPDFObjectBoolean(paramPDFObject, paramBoolean);
  }

  public void put(String paramString, float paramFloat)
  {
    putDictionaryStringFloat(paramString, paramFloat);
  }

  public void put(String paramString, int paramInt)
  {
    putDictionaryStringInteger(paramString, paramInt);
  }

  public void put(String paramString, PDFObject paramPDFObject)
  {
    putDictionaryStringPDFObject(paramString, paramPDFObject);
  }

  public void put(String paramString1, String paramString2)
  {
    putDictionaryStringString(paramString1, paramString2);
  }

  public void put(String paramString, boolean paramBoolean)
  {
    putDictionaryStringBoolean(paramString, paramBoolean);
  }

  public native byte[] readRawStream();

  public native byte[] readStream();

  public native PDFObject resolve();

  public native int size();

  public String toString()
  {
    return toString(false);
  }

  public native String toString(boolean paramBoolean);

  public native void writeObject(PDFObject paramPDFObject);

  public void writeRawStream(Buffer paramBuffer)
  {
    writeRawStreamBuffer(paramBuffer);
  }

  public void writeRawStream(String paramString)
  {
    writeRawStreamString(paramString);
  }

  public void writeStream(Buffer paramBuffer)
  {
    writeStreamBuffer(paramBuffer);
  }

  public void writeStream(String paramString)
  {
    writeStreamString(paramString);
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.PDFObject
 * JD-Core Version:    0.6.0
 */