package pl.mpak.sky.gui.swing.comp;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JPanel;

import pl.mpak.sky.gui.swing.SwingUtil;

public class StatusBar extends JPanel {
  private static final long serialVersionUID = -3761446892020998219L;
  
  public StatusBar() {
    super(new FlowLayout(FlowLayout.LEFT, 2, 2));
    init();
  }

  private void init() {
    
  }

  public StatusPanel addPanel(String name, String text, Icon icon, int width, StatusPanel panel) {
    panel.setName(name);
    add(panel);
    if (width > 0) {
      panel.setMinimumSize(new Dimension(width, panel.getHeight()));
      panel.setPreferredSize(panel.getMinimumSize());
    }
    return panel;
  }
  
  public StatusPanel addPanel(String name, String text, Icon icon, int width) {
    return addPanel(name, text, icon, width, new StatusPanel(name, text, icon));
  }
  
  public StatusPanel addPanel(String name, String text) {
    return addPanel(name, text, null, -1);
  }
  
  public StatusPanel addPanel(String name) {
    return addPanel(name, "", null, -1);
  }
  
  public StatusPanel addPanel(String name, StatusPanel panel) {
    return addPanel(name, "", null, -1, panel);
  }
  
  public void hidePanel(String name) {
    getPanel(name).setVisible(false);
  }
  
  public void hidePanels(String[] names) {
    for (int i=0; i<names.length; i++) {
      hidePanel(names[i]);
    }
  }
  
  public StatusPanel removePanel(String name) {
    for (int i=0; i<getComponentCount(); i++ ) {
      if (getComponent(i).getName().equals(name) && getComponent(i) instanceof StatusPanel) {
        StatusPanel panel = (StatusPanel)getComponent(i);
        remove(i);
        return panel;
      }
    }
    return null;
  }
  
  public StatusPanel getPanel(String name) {
    for (int i=0; i<getComponentCount(); i++ ) {
      if (getComponent(i) instanceof StatusPanel && name.equalsIgnoreCase(getComponent(i).getName())) {
        return (StatusPanel)getComponent(i);
      }
    }
    return null;
  }
  

  /**
   * Pozwala uzyskaæ indeks komponentu le¿¹cego na innym komponencie
   * @param item
   * @return
   */
  public int getIndexOf(Component item) {
    return SwingUtil.getIndexOf(this, item);
  }
  
  /**
   * Dodaje za komponentem comp komponent newItem
   * @param comp
   * @param newItem
   */
  public void addBefore(Component comp, Component newItem) {
    SwingUtil.addBefore(this, comp, newItem);
    refresh();
  }
  
  /**
   * Dodaje przed komponentem comp komponent newItem
   * @param comp
   * @param newItem
   */
  public void addAfter(Component comp, Component newItem) {
    SwingUtil.addAfter(this, comp, newItem);
    refresh();
  }
  
  public void refresh() {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        revalidate();
        repaint();
      }
    });
  }
}
