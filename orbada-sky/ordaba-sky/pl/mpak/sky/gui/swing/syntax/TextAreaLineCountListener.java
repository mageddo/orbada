package pl.mpak.sky.gui.swing.syntax;

import java.util.EventListener;

public interface TextAreaLineCountListener extends EventListener {
  
  public void lineCountChange(TextAreaLineCountEvent e);
  
}
