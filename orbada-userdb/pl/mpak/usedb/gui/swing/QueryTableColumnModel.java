package pl.mpak.usedb.gui.swing;

import java.util.EventObject;

import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.core.QueryFieldList;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.util.ExceptionUtil;

public class QueryTableColumnModel extends DefaultTableColumnModel {
  private static final long serialVersionUID = 182344261104982198L;
  
  public enum Event {
    BEFORE_CREATE_COLUMNS, AFTER_CREATE_COLUMNS
  }

  private Query query;
  private boolean autoColumns = true;
  private transient QueryListener queryListener;
  private boolean showNullValue = true;
  private EventListenerList createColumnsListenerList = new EventListenerList();

  public QueryTableColumnModel() {
    super();
  }

  public QueryTableColumnModel(Query query) {
    this();
    setQuery(query);
  }
  
  public void addQueryTableColumnListener(QueryTableColumnModelListener listener) {
    synchronized (createColumnsListenerList) {
      createColumnsListenerList.add(QueryTableColumnModelListener.class, listener);
    }
  }

  public void removeQueryTableColumnListener(QueryTableColumnModelListener listener) {
    synchronized (createColumnsListenerList) {
      createColumnsListenerList.remove(QueryTableColumnModelListener.class, listener);
    }
  }

  public void fireQueryTableColumnListener(Event event) {
    synchronized (createColumnsListenerList) {
      EventObject eo = new EventObject(this);
      QueryTableColumnModelListener[] listeners = createColumnsListenerList.getListeners(QueryTableColumnModelListener.class);
      for (int i = 0; i < listeners.length; i++) {
        switch (event) {
        case BEFORE_CREATE_COLUMNS:
          listeners[i].beforeCreateColumns(eo);
          break;
        case AFTER_CREATE_COLUMNS:
          listeners[i].afterCreateColumns(eo);
          break;
        }
      }
    }
  }

  private QueryListener getQueryListener() {
    if (queryListener == null) {
      queryListener = new DefaultQueryListener() {
        public void afterOpen(EventObject e) {
          if (autoColumns) {
            createColumnByDataSource();
          }
          else {
            updateColumnIndexes();
          }
        }
        public void beforeClose(EventObject e) {
          if (autoColumns) {
            clearColumns();
          }
        }
      };
    }
    return queryListener;
  }

  public void clearColumns() {
    tableColumns.clear();
  }

  public void setQuery(Query query) {
    if (this.query != query) {
      if (this.query != null) {
        this.query.removeQueryListener(getQueryListener());
      }
      this.query = query;
      if (this.query != null) {
        this.query.addQueryListener(getQueryListener());
        if (autoColumns) {
          createColumnByDataSource();
        }
      }
      else {
        clearColumns();
      }
    }
  }

  public Query getQuery() {
    return query;
  }
  
  public boolean isAutoColumns() {
    return autoColumns;
  }
  
  private void createColumns(QueryFieldList fl) {
    for (int i = 0; i < fl.getFieldCount(); i++) {
      QueryTableColumn tc = new QueryTableColumn(fl.getField(i));
      tc.setShowNullValue(showNullValue);
      addColumn(tc);
    }
  }

  protected void createColumnByDataSource() {
    if (query == null || !query.isActive()) {
      return;
    }
    fireQueryTableColumnListener(Event.BEFORE_CREATE_COLUMNS);
    final QueryFieldList fl = query.getFieldList();
    if (java.awt.EventQueue.isDispatchThread()) {
      createColumns(fl);
    }
    else {
      try {
        java.awt.EventQueue.invokeAndWait(new Runnable() {
          public void run() {
            createColumns(fl);
          }
        });
      } catch (Exception e) {
        ExceptionUtil.processException(e);
      }
    }
    autoColumns = true;
    fireQueryTableColumnListener(Event.AFTER_CREATE_COLUMNS);
  }
  
  public void addColumn(TableColumn aColumn) {
    if (autoColumns) {
      clearColumns();
    }
    super.addColumn(aColumn);
    if (query != null && query.isActive()) {
      updateColumnIndex(getColumnCount() -1);
    }
    autoColumns = false;
  }
  
  public void addColumn(QueryField tableField) {
    addColumn(new QueryTableColumn(tableField));
  }

  public void addColumn(QueryField tableField, String title) {
    addColumn(new QueryTableColumn(tableField, title));
  }

  public void addColumn(String fieldName, String title, int width) {
    addColumn(new QueryTableColumn(fieldName, title, width));
  }
  
  private void updateColumnIndex(int index) {
    QueryTableColumn tc = (QueryTableColumn)getColumn(index);
    try {
      tc.setField(query.fieldByName(tc.getFieldName()));
    }
    catch (UseDBException e) {
      ExceptionUtil.processException(e);
    }
  }
  
  protected void updateColumnIndexes() {
    for (int i=0; i<getColumnCount(); i++) {
      if (getColumn(i) instanceof QueryTableColumn) {
        updateColumnIndex(i);
      }
    }
  }

  public TableColumn findColumn(String fieldName) {
    for (int i=0; i<getColumnCount(); i++) {
      if (getColumn(i) instanceof QueryTableColumn) {
        QueryTableColumn tc = ((QueryTableColumn)getColumn(i));
        if (tc.getFieldName().equals(fieldName)) {
          return tc;
        }
      }
    }
    for (int i=0; i<getColumnCount(); i++) {
      if (getColumn(i) instanceof QueryTableColumn) {
        QueryTableColumn tc = ((QueryTableColumn)getColumn(i));
        if (tc.getFieldName().equalsIgnoreCase(fieldName)) {
          return tc;
        }
      }
    }
    return null;
  }

  public TableColumn getColumn(String fieldName) {
    return findColumn(fieldName);
  }

  public void setShowNullValue(boolean showNullValue) {
    if (this.showNullValue != showNullValue) {
      this.showNullValue = showNullValue;
      for (int i=0; i<getColumnCount(); i++) {
        if (getColumn(i) instanceof QueryTableColumn) {
          ((QueryTableColumn)getColumn(i)).setShowNullValue(this.showNullValue);
        }
      }
    }
  }

  public boolean isShowNullValue() {
    return showNullValue;
  }

  public void setAutoColumns(boolean autoColumns) {
    this.autoColumns = autoColumns;
  }
  
  /**
   * <p>Dodaje columnê do listy bez wywo³ywania listenerów i innych taki. Tylko kolumna do listy.
   * @param column
   */
  public void add(TableColumn column) {
    tableColumns.addElement(column);
  }
  
  /**
   * <p>Usuwa kolumnê z listy bez wywo³ywania listenerów i innych takich.
   * @param index
   * @return
   */
  public TableColumn remove(int index) {
    return tableColumns.remove(index);
  }

}
