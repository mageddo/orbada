package pl.mpak.usedb.gui.swing;

import java.text.DateFormat;
import java.util.Date;
import java.util.EventObject;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pl.mpak.sky.gui.swing.comp.StatusBar;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.util.StringUtil;

public class QueryTableStatusBar extends StatusBar {
  private static final long serialVersionUID = -2751111528455902534L;

  /**
   * Informacja o numerze wskazanego rekordu
   */
  public final static String PANEL_REC_NO = "panel.recno";

  /**
   * Wartoœæ komórki
   */
  public final static String PANEL_FIELD_VALUE = "panel.value";

  /**
   * Informacja o iloœci rekordów w tabeli
   */
  public final static String PANEL_REC_COUNT = "panel.reccount";

  /**
   * Informacje o szukanym wierszu
   */
  /**
   * Informacja o czasie otwierania zapytania
   */
  public final static String PANEL_OPEN_TIME = "panel.opentime";
  /**
   * Informacja o typie kolumny
   */
  public final static String PANEL_COL_TYPE = "panel.type";
  
  private QueryTable table;

  private transient TableColumnModelListener columnListener = null;
  private transient ListSelectionListener selectionListener = null;
  private transient QueryListener queryListener = null;

  public QueryTableStatusBar() {
    super();
    init();
  }
  
  public QueryTableStatusBar(QueryTable table) {
    super();
    init();
    setTable(table);
  }

  private void init() {
    addPanel(PANEL_REC_NO);
    addPanel(PANEL_REC_COUNT);
    addPanel(PANEL_OPEN_TIME);
    addPanel(PANEL_FIELD_VALUE);
    addPanel(PANEL_COL_TYPE);

    columnListener = new TableColumnModelListener() {
      public void columnAdded(TableColumnModelEvent e) {
      }

      public void columnRemoved(TableColumnModelEvent e) {
      }

      public void columnMoved(TableColumnModelEvent e) {
      }

      public void columnMarginChanged(ChangeEvent e) {
      }

      public void columnSelectionChanged(ListSelectionEvent e) {
        updateStatus();
      }
    };

    selectionListener = new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updateStatus();
      }
    };

    queryListener = new DefaultQueryListener() {
      public void afterOpen(EventObject e) {
        updateStatus();
      }
      public void afterClose(EventObject e) {
        updateStatus();
      }
      public void flushedPerformed(EventObject e) {
        updateStatus();
      }
    };
  }

  public void close() {
    setTable(null);
    columnListener = null;
    selectionListener = null;
    queryListener = null;
  }

  public void setTable(QueryTable table) {
    if (this.table != table) {
      if (this.table != null) {
        this.table.getColumnModel().removeColumnModelListener(columnListener);
        this.table.getSelectionModel().removeListSelectionListener(selectionListener);
        this.table.getQuery().removeQueryListener(queryListener);
      }

      this.table = table;

      if (this.table != null) {
        this.table.getColumnModel().addColumnModelListener(columnListener);
        this.table.getSelectionModel().addListSelectionListener(selectionListener);
        this.table.getQuery().addQueryListener(queryListener);
      }
    }
  }

  public QueryTable getTable() {
    return table;
  }
  
  protected void updateStatus() {
    if (table == null) {
      return;
    }
    if (getPanel(PANEL_REC_NO).isVisible()) {
      getPanel(PANEL_REC_NO).setText(" No: " + (table.getSelectedRow() + 1) + " ");
    }

    int column = table.getSelectedColumn();
    TableColumnModel tcm = table.getColumnModel();
    if (getPanel(PANEL_FIELD_VALUE).isVisible()) {
      if (column < 0 || table.getSelectedRow() < 0 || tcm.getColumnCount() < column ) {
        getPanel(PANEL_FIELD_VALUE).setText(" ");
      } 
      else if (table.getQuery().isActive()) {
        try {
          int modelIndex = tcm.getColumn(column).getModelIndex();
          Object object = table.getModel().getValueAt(table.getSelectedRow(), modelIndex);
          if (object instanceof Date) {
            getPanel(PANEL_FIELD_VALUE).setText(" Value: " + DateFormat.getDateTimeInstance().format(object) + " ");
          } 
          else if (object instanceof String) {
            String t = (String) object;
            getPanel(PANEL_FIELD_VALUE).setText(" Value (" + t.length() + "): " + t.substring(0, Math.min(100, t.length())) + " ");
          } 
          else {
            String t = object.toString();
            getPanel(PANEL_FIELD_VALUE).setText(" Value: " + t.substring(0, Math.min(100, t.length())) + " ");
          }
        } catch (Exception e) {
          getPanel(PANEL_FIELD_VALUE).setText(" ??? ");
        }
      }
      else {
        getPanel(PANEL_FIELD_VALUE).setText(" ");
      }
    }

    if (getPanel(PANEL_OPEN_TIME).isVisible()) {
      getPanel(PANEL_OPEN_TIME).setText(" Time: " +StringUtil.formatTime(table.getQuery().getOpeningTime()) +" ");
    }
    
    if (getPanel(PANEL_REC_COUNT).isVisible()) {
      getPanel(PANEL_REC_COUNT).setText(" Recs: " +table.getModel().getRowCount() +" ");
      try {
        getPanel(PANEL_REC_COUNT).setText(getPanel(PANEL_REC_COUNT).getText() +(table.getQuery().isAfterLast() ? "" : "+"));
      }
      catch (Exception e) {
        getPanel(PANEL_REC_COUNT).setText(" ??? ");
      }
    }

    if (getPanel(PANEL_COL_TYPE).isVisible() && column >= 0 && table.getQuery().isActive()) {
      try {
        TableColumn tc = tcm.getColumn(column);
        if (tc == null) {
          getPanel(PANEL_COL_TYPE).setText(" ");
        }
        else if (table.getQuery().isActive()) {
          QueryField field = table.getQuery().getFieldList().getField(tc.getModelIndex());
          getPanel(PANEL_COL_TYPE).setText(" Type: " +field.getDataTypeName() +" ");
        }
        else {
          getPanel(PANEL_COL_TYPE).setText(" Type: ??? ");
        }
      } catch (Exception e) {
        getPanel(PANEL_COL_TYPE).setText(" ??? ");
      }
    }
    else {
      getPanel(PANEL_COL_TYPE).setText(" ");
    }
  }

  public boolean isShowFieldType() {
    return getPanel(PANEL_COL_TYPE).isVisible();
  }

  public void setShowFieldType(boolean fieldType) {
    getPanel(PANEL_COL_TYPE).setVisible(fieldType);
  }

  public boolean isShowOpenTime() {
    return getPanel(PANEL_OPEN_TIME).isVisible();
  }

  public void setShowOpenTime(boolean openTime) {
    getPanel(PANEL_OPEN_TIME).setVisible(openTime);
  }

  public boolean isShowFieldValue() {
    return getPanel(PANEL_FIELD_VALUE).isVisible();
  }

  public void setShowFieldValue(boolean fieldValue) {
    getPanel(PANEL_FIELD_VALUE).setVisible(fieldValue);
  }

}
