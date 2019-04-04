package com.artifex.mupdf.fitz;

public class PDFPage extends Page
{
  static
  {
    Context.init();
  }

  private PDFPage(long paramLong)
  {
    super(paramLong);
  }

  public native PDFAnnotation createAnnotation(int paramInt);

  public native void deleteAnnotation(Annotation paramAnnotation);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.PDFPage
 * JD-Core Version:    0.6.0
 */