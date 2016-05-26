package pl.mpak.sky.gui.swing.comp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
 * SkyCalendar
 * @author Pawe³ Rutkowski
 * @version 1.0 beta
 *
 */
public class SkyCalendar extends JPanel implements Serializable {

  private static final long serialVersionUID = -5992844212959663194L;

  private ArrayList<SkyCalendarElement> days = null;

  private EventListenerList calendarListenerList = new EventListenerList();

  private JPanel titlePanel = null;

  private JPanel dayPanel = null;

  private int Year;

  private int Month;

  public enum Event {
    DAY_CLICK, DAY_DRAW
  }

  public SkyCalendar() {
    super();
    Month = Calendar.getInstance().get(Calendar.MONTH);
    Year = Calendar.getInstance().get(Calendar.YEAR);
    days = new ArrayList<SkyCalendarElement>();

    setLayout(new java.awt.BorderLayout());
    paintSkyCalendar();
  }

  private void paintSkyCalendar() {
    paintTitle(); // paints the panel with week days names
    paintDays(); // paints the panel with days

    add(titlePanel, java.awt.BorderLayout.NORTH);
    add(dayPanel, java.awt.BorderLayout.CENTER);
  }

  private void paintTitle() {
    Calendar d = Calendar.getInstance();
    titlePanel = new JPanel();
    titlePanel.setLayout(new java.awt.GridLayout(1, 7));
    titlePanel.setPreferredSize(new java.awt.Dimension(100, 20));

    int startDay = 1;
    d.set(Year, Month, startDay);
    int fdow = d.getFirstDayOfWeek();
    while (d.get(Calendar.DAY_OF_WEEK) != fdow) {
      d.set(Year, Month, ++startDay);
    }
    startDay--;

    for (int i = 1; i <= 7; i++) {
      d.set(Year, Month, startDay + i);
      JLabel l = new JLabel();
      l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      l.setFont(new java.awt.Font(l.getFont().getFontName(), 0, 11));
      l.setText(String.format("%1$tA", new Object[] { new Long(d.getTime()
          .getTime()) }));
      // l.setBorder(new
      // javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
      l.setPreferredSize(new java.awt.Dimension(70, 20));
      titlePanel.add(l);
    }
  }

  private void paintDays() {
    Calendar d = Calendar.getInstance();
    dayPanel = new JPanel();
    dayPanel.setLayout(new java.awt.GridLayout(0, 7));

    d.set(Year, Month, 1);
    int dow = d.get(Calendar.DAY_OF_WEEK);
    int fdow = d.getFirstDayOfWeek();
    int offset = dow - fdow + 1;
    int l = 0;

    for (int i = fdow; i < dow; i++) {
      d.set(Year, Month, i - offset);
      if (i == fdow) {
        days.add(days.size(), new SkyCalendarElement(d, this, true));
      } else {
        days.add(days.size(), new SkyCalendarElement(d, this, false));
      }
      days.get(days.size() - 1).setEnabled(false);
      dayPanel.add(days.get(days.size() - 1));
      l++;
    }

    int daysCount = 35 - l;
    Calendar d2 = Calendar.getInstance();
    for (int i = 1; i <= daysCount; i++) {
      d.set(Year, Month, i);
      d2.set(Year, Month, i - 1);
      if (d.get(Calendar.MONTH) != Month) {
        if (d.get(Calendar.MONTH) != d2.get(Calendar.MONTH)) {
          days.add(days.size(), new SkyCalendarElement(d, this, true));
        } else {
          days.add(days.size(), new SkyCalendarElement(d, this, false));
        }
        days.get(days.size() - 1).setEnabled(false);
      } else {
        if (d.get(Calendar.MONTH) != d2.get(Calendar.MONTH)) {
          days.add(days.size(), new SkyCalendarElement(d, this, true));
        } else {
          days.add(days.size(), new SkyCalendarElement(d, this, false));
        }
      }
      dayPanel.add(days.get(days.size() - 1));
    }

  }

  public int getMonth() {
    return Month + 1;
  }

  public void setMonth(int month) {
    removeAll();
    days.clear();
    Month = month - 1;
    paintSkyCalendar();
    // fires DAY_DRAW event for every day in SkyCalendar
    for (int i = 0; i < days.size(); i++) {
      fireSkyCalendarListener(Event.DAY_DRAW, days.get(i));
    }
    validate();
  }

  public int getYear() {
    return Year;
  }

  public void setYear(int year) {
    Year = year;
    setMonth(getMonth());
  }

  public void addSkyCalendarListener(SkyCalendarListener listener) {
    synchronized (calendarListenerList) {
      calendarListenerList.add(SkyCalendarListener.class, listener);
    }
    // fires DAY_DRAW event for every day in SkyCalendar
    for (int i = 0; i < days.size(); i++) {
      fireSkyCalendarListener(Event.DAY_DRAW, days.get(i));
    }
  }

  public void removeSkyCalendarListener(SkyCalendarListener listener) {
    synchronized (calendarListenerList) {
      calendarListenerList.remove(SkyCalendarListener.class, listener);
    }
  }

  public void fireSkyCalendarListener(Event event, SkyCalendarElement source) {
    synchronized (calendarListenerList) {
      DayEventObject eo = new DayEventObject(source);
      eo.dayElement = source;
      SkyCalendarListener[] listeners = calendarListenerList
          .getListeners(SkyCalendarListener.class);
      for (int i = 0; i < listeners.length; i++) {
        switch (event) {
        case DAY_CLICK:
          listeners[i].dayClick(eo);
          break;
        case DAY_DRAW:
          listeners[i].dayDraw(eo);
          break;
        }
      }
    }
  }

}
