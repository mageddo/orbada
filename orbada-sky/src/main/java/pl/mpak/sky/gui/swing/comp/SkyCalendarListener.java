package pl.mpak.sky.gui.swing.comp;

import java.util.EventListener;

public interface SkyCalendarListener extends EventListener {
  
  public void dayClick(DayEventObject e);
  
  public void dayDraw(DayEventObject e);
}
