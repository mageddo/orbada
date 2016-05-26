package pl.mpak.sky.gui.swing.syntax;

import java.util.EventObject;

import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.LineMark;

public class GutterEvent extends EventObject {
  private static final long serialVersionUID = 661658440055813532L;

  private int line;
  private int clickCount;
  private LineMark selected;
  private LineMark[] allMarks;
  
  public GutterEvent(Object source, int line, int clickCount, LineMark selected, LineMark[] allMarks) {
    super(source);
    this.line = line;
    this.clickCount = clickCount;
    this.selected = selected;
    this.allMarks = allMarks;
  }

  /**
   * <p>Zwraca numer linii (od zera)
   * @return
   */
  public int getLine() {
    return line;
  }

  /**
   * <p>Ile razy klikniêto myszk¹
   * @return
   */
  public int getClickCount() {
    return clickCount;
  }

  /**
   * <p>Zdarzenie dotyczy obiektu LineMark, mo¿e byæ null jeœli wybrano poza jakimkolwiek elementem
   * @return
   */
  public LineMark getSelected() {
    return selected;
  }

  /**
   * <p>Wszystkie LineMark zwi¹zane z wybran¹ lini¹
   * @return
   */
  public LineMark[] getAllMarks() {
    return allMarks;
  }

}
