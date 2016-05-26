package pl.mpak.sky.gui.swing;

import java.awt.Component;
import java.awt.Cursor;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Steruje kursorem
 * Pozwala pokazywaæ przekazany jako parametr kursor i przywracaæ oryginalny
 * dla komponentu
 *
 */
public class CursorChanger {
  
  private final Component comp;
  private final Cursor newCursor;
  private final Cursor orgCursor;
  private boolean showed = false;

  public CursorChanger(Component comp) {
    this(comp, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }

  public CursorChanger(Component comp, Cursor newCursor) {
    super();

    if (newCursor == null) {
      throw new IllegalArgumentException("null Cursor passed");
    }
    if (comp == null) {
      throw new IllegalArgumentException("null Component passed");
    }

    this.comp = comp;
    this.newCursor = newCursor;
    this.orgCursor = comp.getCursor();
  }

  public void show() {
    this.comp.setCursor(this.newCursor);
    showed = true;
  }

  public void restore() {
    showed = false;
    this.comp.setCursor(this.orgCursor);
  }

  public boolean isShowed() {
    return showed;
  }
}
