package com.artifex.mupdf.fitz;

public class Path
  implements PathWalker
{
  private long pointer;

  static
  {
    Context.init();
  }

  public Path()
  {
    this.pointer = newNative();
  }

  private Path(long paramLong)
  {
    this.pointer = paramLong;
  }

  public Path(Path paramPath)
  {
    this.pointer = paramPath.cloneNative();
  }

  private native long cloneNative();

  private native long newNative();

  public native void closePath();

  public native Point currentPoint();

  public native void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);

  public void curveTo(Point paramPoint1, Point paramPoint2, Point paramPoint3)
  {
    curveTo(paramPoint1.x, paramPoint1.y, paramPoint2.x, paramPoint2.y, paramPoint3.x, paramPoint3.y);
  }

  public native void curveToV(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

  public void curveToV(Point paramPoint1, Point paramPoint2)
  {
    curveToV(paramPoint1.x, paramPoint1.y, paramPoint2.x, paramPoint2.y);
  }

  public native void curveToY(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

  public void curveToY(Point paramPoint1, Point paramPoint2)
  {
    curveToY(paramPoint1.x, paramPoint1.y, paramPoint2.x, paramPoint2.y);
  }

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native Rect getBounds(StrokeState paramStrokeState, Matrix paramMatrix);

  public native void lineTo(float paramFloat1, float paramFloat2);

  public void lineTo(Point paramPoint)
  {
    lineTo(paramPoint.x, paramPoint.y);
  }

  public native void moveTo(float paramFloat1, float paramFloat2);

  public void moveTo(Point paramPoint)
  {
    moveTo(paramPoint.x, paramPoint.y);
  }

  public native void rect(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public native void transform(Matrix paramMatrix);

  public native void walk(PathWalker paramPathWalker);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Path
 * JD-Core Version:    0.6.0
 */