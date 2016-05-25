package pl.mpak.sky.gui.swing.syntax;

import java.awt.Point;
import java.io.Closeable;
import java.io.IOException;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.comp.StatusBar;


public class TextAreaStatusBar extends StatusBar implements Closeable {
  private static final long serialVersionUID = -456426216284742383L;
  
  /**
   * Informacja o iloœci wierszy w edytorze  
   */
  public final static String PANEL_LINES = "editor.lines"; //$NON-NLS-1$
  /**
   * Informacja o pozycji kursora w edytorze  
   */
  public final static String PANEL_CARET = "editor.caret"; //$NON-NLS-1$
  /**
   * Informacja o tym czy nast¹pi³a zmiana w edytorze  
   */
  public final static String PANEL_CHANGED = "editor.changed"; //$NON-NLS-1$

  private JTextComponent textComponent = null;
  private transient CaretListener caretListener = null;

  public TextAreaStatusBar() {
    super();
    init();
  }

  public TextAreaStatusBar(JTextComponent textComponent) {
    super();
    init();
    setTextComponent(textComponent);
  }
  
  public Point getCaretPoint() {
    int offset = textComponent.getCaretPosition();
    int line = ((SyntaxEditor)textComponent).getLineOfOffset(offset);
    return new Point(offset -((SyntaxEditor)textComponent).getLineStartOffset(line) +1, line +1);
  }

  private void init() {
    addPanel(PANEL_CARET);
    addPanel(PANEL_LINES);
    addPanel(PANEL_CHANGED);
    
    caretListener = new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        if (textComponent instanceof SyntaxEditor) {
          Point p = getCaretPoint();
          getPanel(PANEL_CARET).setText(" " +p.y +" : " +p.x +" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          getPanel(PANEL_LINES).setText(Messages.getString("TextAreaStatusBar.lines-dd") +((SyntaxEditor)textComponent).getLineCount() +" "); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else {
        }
      }
    };
  }

  public void setTextComponent(JTextComponent textComponent) {
    if (this.textComponent != textComponent) {
      if (this.textComponent != null) {
        this.textComponent.removeCaretListener(caretListener);
      }
      this.textComponent = textComponent;
      if (this.textComponent != null) {
        this.textComponent.addCaretListener(caretListener);
        caretListener.caretUpdate(null);
      }
    }
  }

  public JTextComponent getTextComponent() {
    return textComponent;
  }

  public void close() throws IOException {
    removeAll();
    setTextComponent(null);
    textComponent = null;
    caretListener = null;
  }

}
