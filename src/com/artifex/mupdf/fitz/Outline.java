package com.artifex.mupdf.fitz;

public class Outline
{
  public Outline[] down;
  public int page;
  public String title;
  public String uri;
  public float x;
  public float y;

  public Outline(String paramString1, int paramInt, String paramString2, float paramFloat1, float paramFloat2, Outline[] paramArrayOfOutline)
  {
    this.title = paramString1;
    this.page = paramInt;
    this.uri = paramString2;
    this.down = paramArrayOfOutline;
    this.x = paramFloat1;
    this.y = paramFloat2;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(this.page);
    localStringBuffer.append(": ");
    localStringBuffer.append(this.title);
    localStringBuffer.append(' ');
    localStringBuffer.append(this.uri);
    localStringBuffer.append('\n');
    if (this.down != null)
      for (int i = 0; i < this.down.length; i++)
      {
        localStringBuffer.append('\t');
        localStringBuffer.append(this.down[i]);
        localStringBuffer.append('\n');
      }
    localStringBuffer.deleteCharAt(-1 + localStringBuffer.length());
    return localStringBuffer.toString();
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Outline
 * JD-Core Version:    0.6.0
 */