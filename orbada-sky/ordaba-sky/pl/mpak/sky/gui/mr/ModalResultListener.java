package pl.mpak.sky.gui.mr;

import java.util.EventListener;


public interface ModalResultListener extends EventListener {
  
  public void modalResultChange(ModalResultEvent e);
  
}
