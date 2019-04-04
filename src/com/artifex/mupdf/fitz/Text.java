package com.artifex.mupdf.fitz;

public class Text
  implements TextWalker
{
  private long pointer;

  static
  {
    Context.init();
  }

  public Text()
  {
    this.pointer = newNative();
  }

  private Text(long paramLong)
  {
    this.pointer = paramLong;
  }

  public Text(Text paramText)
  {
    this.pointer = cloneNative(paramText);
  }

  private native long cloneNative(Text paramText);

  private native long newNative();

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native Rect getBounds(StrokeState paramStrokeState, Matrix paramMatrix);

  public void showGlyph(Font paramFont, Matrix paramMatrix, int paramInt1, int paramInt2)
  {
    showGlyph(paramFont, paramMatrix, paramInt1, paramInt2, false);
  }

  public native void showGlyph(Font paramFont, Matrix paramMatrix, int paramInt1, int paramInt2, boolean paramBoolean);

  public void showString(Font paramFont, Matrix paramMatrix, String paramString)
  {
    showString(paramFont, paramMatrix, paramString, false);
  }

  public native void showString(Font paramFont, Matrix paramMatrix, String paramString, boolean paramBoolean);

  public native void walk(TextWalker paramTextWalker);
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.Text
 * JD-Core Version:    0.6.0
 */