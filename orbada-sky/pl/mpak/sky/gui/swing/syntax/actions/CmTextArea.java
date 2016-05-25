package pl.mpak.sky.gui.swing.syntax.actions;

import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import pl.mpak.sky.gui.swing.Action;

public class CmTextArea extends Action {
  private static final long serialVersionUID = -1L;
  
  protected JTextArea textArea;
  
  public CmTextArea(JTextArea textArea, String text, KeyStroke keyStroke) {
    super(text);
    this.textArea = textArea;
    setShortCut(keyStroke);
  }

}
