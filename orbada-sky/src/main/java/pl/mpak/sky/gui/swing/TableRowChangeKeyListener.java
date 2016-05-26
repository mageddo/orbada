package pl.mpak.sky.gui.swing;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTable;

/**
 * Klasa s³u¿y do obs³ugi naciœniecia klawiszy UP/DOWN w kontrolkach edycyjnych
 * Obs³uga ta polega na przesuniêciu wskaŸnika wiersza JTable
 * 
 * Wystarczy wykonaæ np
 * JTextField.addKeyListener(new TableRowChangeKeyListener(JTable));
 * a bêd¹c w JTextField, naciœniêcie UP/DOWN spowoduje przesuniêcie siê wiersza w JTable
 * 
 * @author Andrzej Ka³u¿a
 *
 */
public class TableRowChangeKeyListener implements KeyListener {
  
  private JTable table = null;

  public TableRowChangeKeyListener(JTable table) {
    super();
    this.setTable(table);
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    if (getTable() == null) {
      return;
    }
    
    if (e.getKeyCode() == KeyEvent.VK_UP && e.getModifiers() == 0) {
      if (getTable().getSelectedRow() > 0) {
        getTable().changeSelection(getTable().getSelectedRow() -1, getTable().getSelectedColumn(), false, false);
        e.consume();
      }
    }
    else if (e.getKeyCode() == KeyEvent.VK_DOWN && e.getModifiers() == 0) {
      if (getTable().getSelectedRow() < getTable().getRowCount() -1) {
        getTable().changeSelection(getTable().getSelectedRow() +1, getTable().getSelectedColumn(), false, false);
        e.consume();
      }
    }
    else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && e.getModifiers() == 0) {
      int visibleRowCount = getTable().getVisibleRect().height /getTable().getRowHeight();
      final int i = Math.min(getTable().getModel().getRowCount() -1, getTable().getSelectedRow() +visibleRowCount);
      getTable().changeSelection(i, getTable().getSelectedColumn(), false, false);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          Rectangle rect = getTable().getCellRect(i, getTable().getSelectedColumn(), true);
          getTable().scrollRectToVisible(rect);
        }
      });
      e.consume();
    }
    else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP && e.getModifiers() == 0) {
      int visibleRowCount = getTable().getVisibleRect().height /getTable().getRowHeight();
      final int i = Math.max(0, getTable().getSelectedRow() -visibleRowCount);
      getTable().changeSelection(i, getTable().getSelectedColumn(), false, false);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          Rectangle rect = getTable().getCellRect(i, getTable().getSelectedColumn(), true);
          getTable().scrollRectToVisible(rect);
        }
      });
      e.consume();
    }
  }

  public void keyReleased(KeyEvent e) {
  }

  public void setTable(JTable table) {
    this.table = table;
  }

  public JTable getTable() {
    return table;
  }
  
}
