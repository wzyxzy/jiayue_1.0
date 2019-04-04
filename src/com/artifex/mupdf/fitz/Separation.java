package com.artifex.mupdf.fitz;

public class Separation
{
  public int bgra;
  public int cmyk;
  public String name;

  public Separation(String paramString, int paramInt1, int paramInt2)
  {
    this.name = paramString;
    this.bgra = paramInt1;
    this.cmyk = paramInt2;
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Separation
 * JD-Core Version:    0.6.0
 */