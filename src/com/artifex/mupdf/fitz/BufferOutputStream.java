package com.artifex.mupdf.fitz;

import java.io.OutputStream;

public class BufferOutputStream extends OutputStream
{
  protected Buffer buffer;
  protected int position;
  protected int resetPosition;

  public BufferOutputStream(Buffer paramBuffer)
  {
    this.buffer = paramBuffer;
    this.position = 0;
  }

  public void write(int paramInt)
  {
    this.buffer.writeByte((byte)paramInt);
  }

  public void write(byte[] paramArrayOfByte)
  {
    this.buffer.writeBytes(paramArrayOfByte);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.buffer.writeBytesFrom(paramArrayOfByte, paramInt1, paramInt2);
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.BufferOutputStream
 * JD-Core Version:    0.6.0
 */