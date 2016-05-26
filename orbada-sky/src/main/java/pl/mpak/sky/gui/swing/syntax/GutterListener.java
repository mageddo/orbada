package pl.mpak.sky.gui.swing.syntax;

import java.util.EventListener;

public interface GutterListener extends EventListener {

  public void markLineSelected(GutterEvent e);
  
}
