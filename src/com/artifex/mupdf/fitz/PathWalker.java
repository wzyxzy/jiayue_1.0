package com.artifex.mupdf.fitz;

public abstract interface PathWalker
{
  public abstract void closePath();

  public abstract void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);

  public abstract void lineTo(float paramFloat1, float paramFloat2);

  public abstract void moveTo(float paramFloat1, float paramFloat2);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.PathWalker
 * JD-Core Version:    0.6.0
 */