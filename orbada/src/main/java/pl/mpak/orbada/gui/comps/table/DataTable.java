/*
 * DataTable.java
 *
 * Created on 2007-10-23, 21:07:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.EventObject;
import java.util.concurrent.Callable;
import javax.swing.table.TableColumnModel;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.comps.table.cm.ColumnFitWidthAction;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.comps.table.cm.ColumnFitWidthAction;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.QueryTableColumnModel;
import pl.mpak.usedb.gui.swing.QueryTableColumnModelListener;

/**
 *
 * @author akaluza
 */
public class DataTable extends QueryTable {
  
  private QueryTableColumnModelListener columnListener;
  public static Font dataFont = new Font("Courier New", Font.PLAIN, 11);
  private boolean autoFitWidth = true;
  private QueryListener queryListener;
  
  public DataTable(Query query) {
    super(query);
    init();
  }
  
  public DataTable() {
    super();
    init();
  }
  
  private void init() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        setComponentPopupMenu(new DataTablePopupMenu(DataTable.this));
      }
    });
  }
  
  @Override
  public void setColumnModel(TableColumnModel tcm) {
    if (getColumnModel() instanceof QueryTableColumnModel) {
      ((QueryTableColumnModel)getColumnModel()).removeQueryTableColumnListener(getColumnListener());
    }
    super.setColumnModel(tcm);
    if (getColumnModel() instanceof QueryTableColumnModel) {
      ((QueryTableColumnModel)getColumnModel()).addQueryTableColumnListener(getColumnListener());
    }
  }
  
  private void performAutoFitWidth() {
    if (Application.get() != null && Application.get().getSettings().getValue(Consts.dataTableAutoFitWidth, true) && autoFitWidth) {
      if (getQuery() != null && getQuery().isActive()) {
        new ColumnFitWidthAction(DataTable.this).performe();
      }
    }
  }
  
  private QueryTableColumnModelListener getColumnListener() {
    if (columnListener == null) {
      columnListener = new QueryTableColumnModelListener() {
        @Override
        public void beforeCreateColumns(EventObject e) {
        }
        @Override
        public void afterCreateColumns(EventObject e) {
          SwingUtil.invokeLater(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
              performAutoFitWidth();
              restoreColumnWidths();
              return null;
            }
          });
        }
      };
    }
    return columnListener;
  }

  public boolean isAutoFitWidth() {
    return autoFitWidth;
  }

  public void setAutoFitWidth(boolean autoFitWidth) {
    this.autoFitWidth = autoFitWidth;
  }
  
  @Override
  public void setQuery(Query query) {
    if (getQuery() != null) {
      getQuery().removeQueryListener(getQueryListener());
    }
    super.setQuery(query);
    if (getQuery() != null) {
      getQuery().addQueryListener(getQueryListener());
    }
  }
  
  @Override
  public void setFont(Font font) {
    super.setFont(font);
    Graphics2D g = (Graphics2D)getGraphics();
    if (g != null) {
      FontMetrics fm = g.getFontMetrics();
      setRowHeight(fm.getHeight() +1);
    }
    getTableHeader().setFont(dataFont);
  }
  
  private QueryListener getQueryListener() {
    if (queryListener == null) {
      queryListener = new DefaultQueryListener() {
        @Override
        public void beforeOpen(EventObject e) {
          setFont(dataFont);
        }
        @Override
        public void beforeClose(EventObject e) {
          saveColumnWidths();
        }
      };
    }
    return queryListener;
  }
  
  private void restoreColumnWidths() {
    if (Application.get() != null && Application.get().getSettings().getValue(Consts.autoSaveColumnWidths, true)) {
      ISettings settings = Application.get().getSettings(getQuery().getDatabase().getUserProperties().getProperty("schemaId"), "orbada-data-table-columns-props");
      for (int i=0; i<getColumnCount(); i++) {
        if (i >= getColumnModel().getColumnCount()) {
          break;
        }
        Object o = getColumnModel().getColumn(i);
        if (o instanceof QueryTableColumn) {
          QueryTableColumn qtc = (QueryTableColumn)o;
          String fieldName = qtc.getFieldName();
          if (fieldName.length() > 200) {
            fieldName = fieldName.substring(0, 199);
          }
          long width = settings.getValue(fieldName, (long)qtc.getWidth());
          if (width != qtc.getWidth()) {
            qtc.setPreferredWidth((int)width);
          }
        }
      }
    }
  }

  private void saveColumnWidths() {
    if (Application.get() != null && Application.get().getSettings().getValue(Consts.autoSaveColumnWidths, true)) {
      ISettings settings = Application.get().getSettings(getQuery().getDatabase().getUserProperties().getProperty("schemaId"), "orbada-data-table-columns-props");
      for (int i=0; i<getColumnCount(); i++) {
        Object o = getColumnModel().getColumn(i);
        if (o instanceof QueryTableColumn) {
          QueryTableColumn qtc = (QueryTableColumn)o;
          String fieldName = qtc.getFieldName();
          if (fieldName.length() > 200) {
            fieldName = fieldName.substring(0, 199);
          }
          settings.setValue(fieldName, (long)qtc.getWidth());
        }
      }
    }
  }
  
}
