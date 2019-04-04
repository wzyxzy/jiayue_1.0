package com.artifex.mupdf.fitz;

public class Point
{
  public float x;
  public float y;

  public Point(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }

  public Point(Point paramPoint)
  {
    this.x = paramPoint.x;
    this.y = paramPoint.y;
  }

  public String toString()
  {
    return "[" + this.x + " " + this.y + "]";
  }

  public Point transform(Matrix paramMatrix)
  {
    float f = this.x;
    this.x = (f * paramMatrix.a + this.y * paramMatrix.c + paramMatrix.e);
    this.y = (f * paramMatrix.b + this.y * paramMatrix.d + paramMatrix.f);
    return this;
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Point
 * JD-Core Version:    0.6.0
 */