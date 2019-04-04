package com.artifex.mupdf.fitz;

import java.util.Date;

public class PDFAnnotation extends Annotation
{
  public static final int LINE_ENDING_BUTT = 6;
  public static final int LINE_ENDING_CIRCLE = 2;
  public static final int LINE_ENDING_CLOSEDARROW = 5;
  public static final int LINE_ENDING_DIAMOND = 3;
  public static final int LINE_ENDING_NONE = 0;
  public static final int LINE_ENDING_OPENARROW = 4;
  public static final int LINE_ENDING_RCLOSEDARROW = 8;
  public static final int LINE_ENDING_ROPENARR = 7;
  public static final int LINE_ENDING_SLASH = 9;
  public static final int LINE_ENDING_SQUARE = 1;
  public static final int TYPE_3D = 24;
  public static final int TYPE_CARET = 13;
  public static final int TYPE_CIRCLE = 5;
  public static final int TYPE_FILE_ATTACHMENT = 16;
  public static final int TYPE_FREE_TEXT = 2;
  public static final int TYPE_HIGHLIGHT = 8;
  public static final int TYPE_INK = 14;
  public static final int TYPE_LINE = 3;
  public static final int TYPE_LINK = 1;
  public static final int TYPE_MOVIE = 18;
  public static final int TYPE_POLYGON = 6;
  public static final int TYPE_POLY_LINE = 7;
  public static final int TYPE_POPUP = 15;
  public static final int TYPE_PRINTER_MARK = 21;
  public static final int TYPE_SCREEN = 20;
  public static final int TYPE_SOUND = 17;
  public static final int TYPE_SQUARE = 4;
  public static final int TYPE_SQUIGGLY = 10;
  public static final int TYPE_STAMP = 12;
  public static final int TYPE_STRIKE_OUT = 11;
  public static final int TYPE_TEXT = 0;
  public static final int TYPE_TRAP_NET = 22;
  public static final int TYPE_UNDERLINE = 9;
  public static final int TYPE_UNKNOWN = -1;
  public static final int TYPE_WATERMARK = 23;
  public static final int TYPE_WIDGET = 19;

  static
  {
    Context.init();
  }

  private PDFAnnotation(long paramLong)
  {
    super(paramLong);
  }

  public native String getAuthor();

  public native float getBorder();

  public native float[] getColor();

  public native String getContents();

  public native int getFlags();

  public native String getIcon();

  public native float[][] getInkList();

  public native float[] getInteriorColor();

  public native int[] getLineEndingStyles();

  public Date getModificationDate()
  {
    return new Date(getModificationDateNative());
  }

  protected native long getModificationDateNative();

  public native float[][] getQuadPoints();

  public native Rect getRect();

  public native int getType();

  public native float[] getVertices();

  public native boolean isOpen();

  public native void setAuthor(String paramString);

  public native void setBorder(float paramFloat);

  public native void setColor(float[] paramArrayOfFloat);

  public native void setContents(String paramString);

  public native void setFlags(int paramInt);

  public native void setIcon(String paramString);

  public native void setInkList(float[][] paramArrayOfFloat);

  public native void setInteriorColor(float[] paramArrayOfFloat);

  public native void setIsOpen(boolean paramBoolean);

  public native void setLineEndingStyles(int paramInt1, int paramInt2);

  public void setLineEndingStyles(int[] paramArrayOfInt)
  {
    setLineEndingStyles(paramArrayOfInt[0], paramArrayOfInt[1]);
  }

  protected native void setModificationDate(long paramLong);

  public void setModificationDate(Date paramDate)
  {
    setModificationDate(paramDate.getTime());
  }

  public native void setQuadPoints(float[][] paramArrayOfFloat);

  public native void setRect(Rect paramRect);

  public native void setVertices(float[] paramArrayOfFloat);

  public native void updateAppearance();
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.PDFAnnotation
 * JD-Core Version:    0.6.0
 */