package pl.mpak.sky.gui.swing.comp;

import java.util.EventObject;

public class DayEventObject extends EventObject {

  /**
   * 
   */
  private static final long serialVersionUID = -9209239714005803234L;
  
  public SkyCalendarElement dayElement = null;

  public DayEventObject(Object source) {
    super(source);
    // TODO Auto-generated constructor stub
  }
 
  /**
   * Returns a String representation of this EventObject.
   *
   * @return  A a String representation of this EventObject.
   */
  public String toString() {
    return getClass().getName() + "[source=" + source + "]";
  }
  
}
