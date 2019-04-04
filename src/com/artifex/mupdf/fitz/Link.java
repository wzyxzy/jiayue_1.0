package com.artifex.mupdf.fitz;

public class Link
{
  public Rect bounds;
  public int page;
  public String uri;

  public Link(Rect paramRect, int paramInt, String paramString)
  {
    this.bounds = paramRect;
    this.page = paramInt;
    this.uri = paramString;
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Link
 * JD-Core Version:    0.6.0
 */