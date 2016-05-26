/*
 * SearchOnTable.java
 *
 * Created on 2007-10-27, 19:31:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import pl.mpak.util.StringUtil;
import pl.mpak.util.timer.Timer;

/**
 * <p>Klasa s³u¿y do udostêpnienia wyszukiwania w tabeli ci¹gu znaków wpisywanych z palca.
 * <p>Przy zamykaniu panelu, okna nale¿y wywo³aæ done() by wy³¹czyæ timer-a.
 * @author akaluza
 */
public class SearchOnTable {
  
  private final static pl.mpak.util.timer.TimerQueue timerQueue = pl.mpak.util.timer.TimerManager.getTimer("orbada-search-on-query-table");
  
  private JTable table;
  private Timer searchTimer;
  private String searchText = "";
  private long lastSearch = System.currentTimeMillis();
  
  public SearchOnTable(JTable table) {
    this.table = table;
    init();
  }
  
  public void setSearchText(String searchText) {
    if (!StringUtil.nvl(this.searchText, "").equals(searchText)) {
      this.searchText = searchText;
      lastSearch = System.currentTimeMillis();
      if (this.searchText.length() > 0 && searchTimer != null) {
        searchTimer.restart();
      }
    }
  }

  private void init() {
    table.addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {
      }
      public void keyPressed(KeyEvent e) {
        if (!table.isEditing() && searchTimer != null && !e.isAltDown() && !e.isControlDown()) {
          if (System.currentTimeMillis() -lastSearch > 1000) {
            searchText = "";
          }
          if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!StringUtil.nvl(searchText, "").equals("")) {
              setSearchText(searchText.substring(0, searchText.length() -1));
            }
          }
          else if (Character.isJavaIdentifierPart(e.getKeyChar()) && e.getKeyChar() > 31) {
            setSearchText(searchText +e.getKeyChar());
          }
        }
      }
      public void keyReleased(KeyEvent e) {
      }
    });
    timerQueue.add(searchTimer = new Timer(250) {
      {
        setEnabled(false);
      }
      public void run() {
        setEnabled(false);
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            findRowBySearchText(false);
          }
        });
      }
    });
  }
  
  public void done() {
    if (searchTimer != null) {
      searchTimer.cancel();
      searchTimer = null;
    }
  }
  
  public int findRowBySearchText(boolean startSelected) {
    String searchText = new String(this.searchText);
    if (table.getSelectedColumn() >= 0 && !StringUtil.nvl(searchText, "").equals("")) {
      TableModel tm = table.getModel();
      int modelIndex = table.getColumnModel().getColumn(table.getSelectedColumn()).getModelIndex();
      int r = (startSelected ? Math.max(table.getSelectedRow() +1, 0) : 0);
      while (r<tm.getRowCount()) {
        Object value = tm.getValueAt(r, modelIndex);
        if (value != null && value.toString().toUpperCase().startsWith(searchText.toUpperCase())) {
          final int row = r;
          java.awt.EventQueue.invokeLater(new Runnable() {
            int column = table.getSelectedColumn();
            public void run() {
              table.changeSelection(row, column, false, false);
            }
          });
          return row;
        }
        r++;
      }
    }
    return -1;
  }
  
}
