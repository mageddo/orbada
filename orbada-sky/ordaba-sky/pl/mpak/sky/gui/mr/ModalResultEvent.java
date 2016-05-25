package pl.mpak.sky.gui.mr;

import java.util.EventObject;

public class ModalResultEvent extends EventObject {
  private static final long serialVersionUID = 4261272657842387590L;

  private int modalResult;
  
  public ModalResultEvent(Object source, int modalResult) {
    super(source);
    setModalResult(modalResult);
  }

  public void setModalResult(int modalResult) {
    this.modalResult = modalResult;
  }

  public int getModalResult() {
    return modalResult;
  }

}
