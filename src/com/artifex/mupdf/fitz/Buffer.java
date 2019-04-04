package com.artifex.mupdf.fitz;

public class Buffer
{
  public static final int DEFAULT_BUFFER_SIZE = 1024;
  private long pointer;

  static
  {
    Context.init();
  }

  public Buffer()
  {
    this.pointer = newNativeBuffer(1024);
  }

  public Buffer(int paramInt)
  {
    this.pointer = newNativeBuffer(paramInt);
  }

  private native long newNativeBuffer(int paramInt);

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native int getLength();

  public native int readByte(int paramInt);

  public native int readBytes(int paramInt, byte[] paramArrayOfByte);

  public native int readBytesInto(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);

  public native void save(String paramString);

  public native void writeBuffer(Buffer paramBuffer);

  public native void writeByte(byte paramByte);

  public native void writeBytes(byte[] paramArrayOfByte);

  public native void writeBytesFrom(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public native void writeLine(String paramString);

  public native void writeLines(String[] paramArrayOfString);

  public native void writeRune(int paramInt);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Buffer
 * JD-Core Version:    0.6.0
 */