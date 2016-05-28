package pl.mpak.orbada.universal.gui.history;

import java.util.Date;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

public class QueryHistoryTableModel extends AbstractTableModel {

  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  private QueryHistory history;
  
  public QueryHistoryTableModel(QueryHistory history) {
    super();
    this.history = history;
  }

  public int getRowCount() {
    return history == null ? 0 : history.getCount();
  }

  public int getColumnCount() {
    return 0;
  }

  @SuppressWarnings("unchecked")
  public Class getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0: return Date.class;
      case 1: return String.class;
      case 2: return Long.class;
      case 3: return String.class;
      default: return Object.class;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    QueryHistoryItem item = history.get(rowIndex);
    
    switch (columnIndex) {
      case 0: return new Date(item.getExecutedTime());
      case 1: return StringUtil.formatTime(item.getExecutingTime());
      case 2: return item.getFetchedRows();
      case 3: return SQLUtil.removeWhiteSpaces(item.getSqlText());
    }
    return "???";
  }
  
  protected TableColumn createColumn(int index, int width, String title) {
    TableColumn tc = new TableColumn(index, width);
    
    tc.setHeaderValue(title);
    tc.setCellRenderer(new QueryTableCellRenderer(true));
    
    return tc;
  }

  public void configureTable(JTable table) {
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    DefaultTableColumnModel tableColumnModel = new DefaultTableColumnModel();
    table.setColumnModel(tableColumnModel);
    tableColumnModel.addColumn(createColumn(0, 110, stringManager.getString("cmd-executed")));
    tableColumnModel.addColumn(createColumn(1, 50, stringManager.getString("time")));
    tableColumnModel.addColumn(createColumn(2, 50, stringManager.getString("rows")));
    tableColumnModel.addColumn(createColumn(3, 1000, stringManager.getString("sql-command")));

    if (history.getCount() > 0) {
      table.changeSelection(0, 0, false, false);
    }
    else {
      table.changeSelection(-1, 0, false, false);
    }
  }
}
