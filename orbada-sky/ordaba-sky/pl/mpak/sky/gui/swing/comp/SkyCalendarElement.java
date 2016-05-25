package pl.mpak.sky.gui.swing.comp;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pl.mpak.sky.gui.swing.comp.SkyCalendar.Event;

public class SkyCalendarElement extends JPanel implements MouseListener {

  /**
   * 
   */
  private static final long serialVersionUID = -4413387492525323420L;
  
  private SkyCalendar owner = null;
  private JPanel titlePanel = null;
  private JLabel titleLabel = null;
  private JPanel labelsPanel = null;
  private static Color color1 = new java.awt.Color(153, 255, 153);
  private static Color color2 = new java.awt.Color(255, 255, 102);
  private static Color titlePanelEnabledColor = new java.awt.Color(153, 153, 153);
  private static Color titlePanelDisabledColor = new java.awt.Color(204, 204, 204);
  private boolean enabled = true;
  
  public Calendar day = null;  
  public ArrayList<EventLabel> labels = null;

  public SkyCalendarElement(Calendar initDay, SkyCalendar parent, boolean longDayName) {
    // TODO Auto-generated constructor stub
    super();
    
    day = Calendar.getInstance();
    labels = new ArrayList<EventLabel>();
    titlePanel = new JPanel();
    titleLabel = new JLabel();
    labelsPanel = new JPanel();
    
    owner = parent;
    day.set(initDay.get(Calendar.YEAR),initDay.get(Calendar.MONTH),initDay.get(Calendar.DAY_OF_MONTH));
    setPreferredSize(new java.awt.Dimension(70, 70));
    setLayout(new java.awt.BorderLayout());
    setDefaultBorder();
    setOpaque(false);
    
    if(longDayName) {
      titleLabel.setText(DateFormat.getDateInstance(DateFormat.LONG).format(day.getTime()));
    } else {
      titleLabel.setText(String.format("%1$td", new Object[] {new Long(day.getTime().getTime())}));
    }  
    titleLabel.setFont(new java.awt.Font(titleLabel.getFont().getFontName(), 1, 11));
    titleLabel.setForeground(new java.awt.Color(255, 255, 255));
    titleLabel.setPreferredSize(new java.awt.Dimension(0, 11));
    
    titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.Y_AXIS));
    titlePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 1, 1));
    titlePanel.setBackground(titlePanelEnabledColor);
    setIfCurrentDay();
    titlePanel.add(titleLabel);
    add(titlePanel, java.awt.BorderLayout.NORTH);
    
    labelsPanel.setLayout(new javax.swing.BoxLayout(labelsPanel, javax.swing.BoxLayout.Y_AXIS));
    labelsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 0, 0, 0));
    labelsPanel.setBackground(new java.awt.Color(255, 255, 228));
    add(labelsPanel, java.awt.BorderLayout.CENTER);
    
    addMouseListener(this);
  }
  
  public void mouseClicked(MouseEvent e) {
    // TODO Auto-generated method stub
    owner.fireSkyCalendarListener(Event.DAY_CLICK, this);
  }
  
  public void mousePressed(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
  }

  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
  }  
  
  public String toString() {
    return DateFormat.getDateInstance().format(day.getTime());
  }
  
  private void setDefaultBorder() {
    //setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
  }
  
  public void addLabel(String title, String desc, int index) {
    labels.add(index, new EventLabel(title, desc, new Color(0,0,0)));
    invalidateLabels();
  }
  
  public void addLabel(String title, String desc) {
    addLabel(title, desc, labels.size());
  }  
  
  public void removeLabel(String title) {
    for(int i=0; i<labels.size()-1; i++) {
      if(labels.get(i).title.equals(title)) {
        removeLabel(i);
        break;
      }
    }
  }
  
  public void removeLabel(int index) {
    labels.remove(index);
    invalidateLabels();
  } 
  
  public void invalidateLabels() {
    labelsPanel.removeAll();
    for(int i=0; i<labels.size(); i++) {
      if(i%2==0) {
        labels.get(i).setBackground(color1);
      } else {
        labels.get(i).setBackground(color2);
      }
      labelsPanel.add(labels.get(i));
      validate();
    }
  }
  
  public boolean getEnabled() {
    return enabled;
  }
  
  public void setEnabled(boolean value) {
    enabled = value;
    if(!value) {
      titlePanel.setBackground(titlePanelDisabledColor);
    } else {
      titlePanel.setBackground(titlePanelEnabledColor);
    }
    setIfCurrentDay();
  }
  
  private void setIfCurrentDay() {
    if(day.equals(Calendar.getInstance())) {
      titlePanel.setBackground(new java.awt.Color(102, 102, 255));
    }
  }
  
}
