package com.artifex.mupdf.fitz;

public class StructuredText
{
  private long pointer;

  static
  {
    Context.init();
  }

  private StructuredText(long paramLong)
  {
    this.pointer = paramLong;
  }

  public native String copy(Point paramPoint1, Point paramPoint2);

  public void destroy()
  {
    finalize();
    this.pointer = 0L;
  }

  protected native void finalize();

  public native TextBlock[] getBlocks();

  public native Rect[] highlight(Point paramPoint1, Point paramPoint2);

  public native Rect[] search(String paramString);

  public class TextBlock
  {
    public Rect bbox;
    public StructuredText.TextLine[] lines;

    public TextBlock()
    {
    }
  }

  public class TextChar
  {
    public Rect bbox;
    public int c;

    public TextChar()
    {
    }

    public boolean isWhitespace()
    {
      return Character.isWhitespace(this.c);
    }
  }

  public class TextLine
  {
    public Rect bbox;
    public StructuredText.TextChar[] chars;

    public TextLine()
    {
    }
  }
}

/* Location:           E:\Developer\Kit\lib\dex2jar\classes_dex2jar.jar
 * Qualified Name:     com.artifex.mupdf.fitz.StructuredText
 * JD-Core Version:    0.6.0
 */