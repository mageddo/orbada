/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.todo.gui;

import javax.swing.table.AbstractTableModel;
import pl.mpak.orbada.todo.OrbadaTodoPlugin;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ImportTodoTableModel extends AbstractTableModel {

  private final StringManager stringManager = StringManagerFactory.getStringManager("todo");

  private ImportTodoItem[] items;
  
  public ImportTodoTableModel(ImportTodoItem[] items) {
    this.items = items;
  }
  
  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0: return stringManager.getString("imp");
      case 1: return stringManager.getString("op");
      case 2: return stringManager.getString("title");
      case 3: return stringManager.getString("updated");
      default: return "???";
    }
  }
  
  @Override
  public Class getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0: return Boolean.class;
      default: return String.class;
    }
  }
  
  public int getRowCount() {
    return items.length;
  }

  public int getColumnCount() {
    return 0;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    try {
      switch (columnIndex) {
        case 0: return items[rowIndex].isImportTodo();
        case 1: return items[rowIndex].isInsert() ? stringManager.getString("new") : stringManager.getString("act");
        case 2: return items[rowIndex].getNewTodo().fieldByName("TD_TITLE").getString();
        case 3: return items[rowIndex].getNewTodo().fieldByName("TD_UPDATED").getString();
        default: return "???";
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }
  
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      items[rowIndex].setImportTodo((Boolean)aValue);
    }
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 0;
  }
  
  public ImportTodoItem getItem(int rowIndex) {
    return items[rowIndex];
  }

}
