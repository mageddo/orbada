package pl.mpak.usedb.gui.swing;

import java.awt.event.InputEvent;
import java.sql.Types;
import java.util.EventObject;

import javax.swing.table.AbstractTableModel;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.CacheRecord;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.DefaultUpdateListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.usedb.core.UpdateListener;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.HexUtils;
import pl.mpak.util.Order;
import pl.mpak.util.SortableTable;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

public class QueryTableModel extends AbstractTableModel implements SortableTable {
  private static final long serialVersionUID = 5520599104901882832L;

  private Query query = null;
  private int lastRowIndex = -1;
  private transient CacheRecord lastRecord = null;
  private transient QueryListener queryListener;
  private transient UpdateListener updateListener;
  private int lastRowCount = 0;
  private boolean editable;

  public QueryTableModel() {
    super();
    editable = false;
  }

  public QueryTableModel(Query query) {
    this();
    setQuery(query);
  }

  private QueryListener getQueryListener() {
    if (queryListener == null) {
      queryListener = new DefaultQueryListener() {
        public void afterOpen(EventObject e) {
          reset();
          fireTableDataChanged();
        }

        public void afterClose(EventObject e) {
          reset();
          fireTableDataChanged();
        }

        public void flushedPerformed(EventObject e) {
          java.awt.EventQueue.invokeLater(new Runnable() {
            private int rowStart = lastRowCount;
            private int rowEnd = getRowCount();
            public void run() {
              if (query.isActive()) {
                fireTableRowsInserted(rowStart, rowEnd);
              }
            }
          });
        }
      };
    }
    return queryListener;
  }

  private UpdateListener getUpdateListener() {
    if (updateListener == null) {
      updateListener = new DefaultUpdateListener() {
        public void afterInsert(EventObject e) {
          fireTableRowsInserted(getRowCount(), getRowCount());
          lastRowCount = getRowCount();
        }

        public void afterDelete(EventObject e) {
          fireTableRowsDeleted(lastRowCount, lastRowCount);
        }

        public void afterUpdate(EventObject e) {
          fireTableRowsUpdated(lastRowCount, lastRowCount);
        }
      };
    }
    return updateListener;
  }

  public void reset() {
    lastRowIndex = -1;
    lastRecord = null;
  }

  public void setQuery(Query query) {
    if (this.query != query) {
      if (this.query != null) {
        this.query.removeQueryListener(getQueryListener());
        this.query.removeUpdateListener(getUpdateListener());
      }
      this.query = query;
      if (this.query != null) {
        this.query.addQueryListener(getQueryListener());
        this.query.addUpdateListener(getUpdateListener());
      }
      reset();
    }
  }

  public Query getQuery() {
    return query;
  }

  public int getRowCount() {
    try {
      if (query != null && query.isActive()) {
        if (query.isFlushed()) {
          return lastRowCount = query.getRecordCount();
        } else {
          return lastRowCount = query.getRecordCount() + 1;
        }
      } else {
        return 0;
      }
    } catch (Exception e) {
      ExceptionUtil.processException(e);
      return 0;
    }
  }

  public int getColumnCount() {
    return 0;
  }

  @SuppressWarnings("unchecked")
  public Class getColumnClass(int columnIndex) {
    if (query != null && query.isActive()) {
      return query.classByField(columnIndex);
    } else {
      return Object.class;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (query != null && query.isActive() && columnIndex != -1) {
      try {
        if (lastRowIndex != rowIndex || lastRecord == null) {
          lastRecord = query.getRecord(rowIndex);
          lastRowIndex = rowIndex;
        }
        if (lastRecord != null) {
          Variant value = lastRecord.getField(columnIndex).getValue();
          if (editable) {
            return value;
          }
          else {
            if (value.isNullValue()) {
              return null;
            }
            else if (value.getValueType() == VariantType.varBinary) {
              if (query.getField(columnIndex).getDataType() == Types.VARBINARY) {
                return HexUtils.convert(value.getBinary()).toUpperCase();
              }
              return value.getString();
            }
            else {
              return value;
            }
          }
        } else {
          return null;
        }
      } catch (Exception e) {
        ExceptionUtil.processException(e);
        return "";
      }
    } else {
      return "";
    }
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return editable;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (editable && query != null && query.isActive() && columnIndex != -1) {
      try {
        if (lastRowIndex != rowIndex || lastRecord == null) {
          lastRecord = query.getRecord(rowIndex);
          lastRowIndex = rowIndex;
        }
        if (lastRecord != null) {
          Variant value = null;
          if (aValue instanceof Variant) {
            value = (Variant)aValue;
          }
          else {
            value = new Variant(aValue);
          }
          if (!value.isNullValue()) {
            try {
              value.cast(SQLUtil.sqlTypeToVariant(query.getField(columnIndex).getDataType()));
            }
            catch (java.text.ParseException pe) {
              if (!value.equals("")) {
                throw pe;
              }
              else {
                value.setNull();
              }
            }
            catch (java.lang.NumberFormatException nfe) {
              if (!value.equals("")) {
                throw nfe;
              }
              else {
                value.setNull();
              }
            }
          }
          lastRecord.getField(columnIndex).setValue(value);
        }
      } catch (Exception e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public void sortByColumn(int modelIndex, Order order, int modifiers) {
    try {
      if ((modifiers & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
        query.orderByColumn(modelIndex, order);
      }
      else {
        query.sortByColumn(modelIndex, order);
      }
    } catch (UseDBException e) {
      ExceptionUtil.processException(e);
    }
  }

}
