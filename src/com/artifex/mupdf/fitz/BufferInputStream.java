package com.artifex.mupdf.fitz;

import java.io.IOException;
import java.io.InputStream;

public class BufferInputStream extends InputStream
{
  protected Buffer buffer;
  protected int position;
  protected int resetPosition;

  public BufferInputStream(Buffer paramBuffer)
  {
    this.buffer = paramBuffer;
    this.position = 0;
    this.resetPosition = -1;
  }

  public int available()
  {
    return this.buffer.getLength();
  }

  public void mark(int paramInt)
  {
    this.resetPosition = this.position;
  }

  public boolean markSupported()
  {
    return true;
  }

  public int read()
  {
    int i = this.buffer.readByte(this.position);
    if (i >= 0)
      this.position = (1 + this.position);
    return i;
  }

  public int read(byte[] paramArrayOfByte)
  {
    int i = this.buffer.readBytes(this.position, paramArrayOfByte);
    if (i >= 0)
      this.position = (i + this.position);
    return i;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = this.buffer.readBytesInto(this.position, paramArrayOfByte, paramInt1, paramInt2);
    if (i >= 0)
      this.position = (i + this.position);
    return i;
  }

  public void reset()
    throws IOException
  {
    if (this.resetPosition < 0)
      throw new IOException("cannot reset because mark never set");
    if (this.resetPosition >= this.buffer.getLength())
      throw new IOException("cannot reset because mark set outside of buffer");
    this.position = this.resetPosition;
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.BufferInputStream
 * JD-Core Version:    0.6.0
 */