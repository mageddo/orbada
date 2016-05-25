package pl.mpak.sky.gui.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;

/**
 * Klasa s³u¿y do obs³ugi naciœniecia klawiszy UP/DOWN w kontrolkach edycyjnych
 * Obs³uga ta polega na przesuniêciu wskaŸnika wiersza JList
 * 
 * Wystarczy wykonaæ np
 * JTextField.addKeyListener(new ListRowChangeKeyListener(JTable));
 * a bêd¹c w JTextField, naciœniêcie UP/DOWN spowoduje przesuniêcie siê wiersza w JList
 * 
 * @author Andrzej Ka³u¿a
 *
 */
public class ListRowChangeKeyListener implements KeyListener {
  
  private JList list = null;

  public ListRowChangeKeyListener(JList list) {
    super();
    this.setList(list);
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    if (getList() == null) {
      return;
    }
    
    if (e.getKeyCode() == KeyEvent.VK_UP && e.getModifiers() == 0) {
      if (getList().getSelectedIndex() > 0) {
        getList().setSelectedIndex(getList().getSelectedIndex() -1);
        getList().ensureIndexIsVisible(getList().getSelectedIndex());
        e.consume();
      }
    }
    else if (e.getKeyCode() == KeyEvent.VK_DOWN && e.getModifiers() == 0) {
      if (getList().getSelectedIndex() < getList().getModel().getSize() -1) {
        getList().setSelectedIndex(getList().getSelectedIndex() +1);
        getList().ensureIndexIsVisible(getList().getSelectedIndex());
        e.consume();
      }
    }
    else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && e.getModifiers() == 0) {
      int visibleRowCount = getList().getVisibleRowCount();
      int i = Math.min(getList().getModel().getSize()-1, getList().getSelectedIndex()+visibleRowCount);
      getList().setSelectedIndex(i);
      getList().ensureIndexIsVisible(i);
      e.consume();
    }
    else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP && e.getModifiers() == 0) {
      int visibleRowCount = getList().getVisibleRowCount();
      int i = Math.max(0, getList().getSelectedIndex()-visibleRowCount);
      getList().setSelectedIndex(i);
      getList().ensureIndexIsVisible(i);
      e.consume();
    }
  }

  public void keyReleased(KeyEvent e) {
  }

  public void setList(JList list) {
    this.list = list;
  }

  public JList getList() {
    return list;
  }
  
}
