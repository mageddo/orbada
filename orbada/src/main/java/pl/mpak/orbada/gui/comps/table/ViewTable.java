/*
 * ViewTable.java
 *
 * Created on 2007-10-23, 20:51:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.StringTokenizer;
import javax.swing.table.TableColumn;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.util.Utils;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.gui.swing.QueryTableColumnModel;

/**
 *
 * @author akaluza
 */
public class ViewTable extends QueryTable {
  
  private QueryListener queryListener;
  private ArrayList<OryginalColumnProps> orygColumnList;
  
  public ViewTable() {
    super();
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        setComponentPopupMenu(new ViewTablePopupMenu(ViewTable.this));
      }
    });
    new SearchOnQueryTable(this);
    setRowHeight(18);
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
  
  private QueryListener getQueryListener() {
    if (queryListener == null) {
      queryListener = new DefaultQueryListener() {
        @Override
        public void afterOpen(EventObject e) {
          restoreColumnProps();
        }
        @Override
        public void beforeClose(EventObject e) {
          storeColumnProps();
        }
      };
    }
    return queryListener;
  }
  
  public void restoreOryginalColumnProps() {
    if (getColumnModel() instanceof QueryTableColumnModel && orygColumnList.size() > 0) {
      QueryTableColumnModel model = (QueryTableColumnModel)getColumnModel();
      model.clearColumns();
      for (OryginalColumnProps ocp : orygColumnList) {
        ocp.column.setPreferredWidth(ocp.width);
        model.addColumn(ocp.column);
      }
    }
  }
  
  private void restoreColumnProps() {
    if (Application.get() != null && Application.get().getSettings().getValue(Consts.autoSaveColumnWidths, true) && getColumnModel() instanceof QueryTableColumnModel) {
      ISettings settings = Application.get().getSettings(getQuery().getDatabase().getUserProperties().getProperty("schemaId"), "orbada-view-table-columns-props-" + Utils
              .getUniqueCompId(this));
      QueryTableColumnModel model = (QueryTableColumnModel)getColumnModel();
      if (orygColumnList == null) {
        orygColumnList = new ArrayList<OryginalColumnProps>();
        for (int i=0; i<model.getColumnCount(); i++) {
          TableColumn tc = model.getColumn(i);
          orygColumnList.add(new OryginalColumnProps(tc, tc.getWidth()));
        }
      }
      
      TableColumn tcs[] = new TableColumn[model.getColumnCount()];
      for (int i=0; i<model.getColumnCount(); i++) {
        tcs[i] = model.getColumn(i);
      }
      for (TableColumn tc : tcs) {
        if (tc instanceof QueryTableColumn) {
          QueryTableColumn qtc = (QueryTableColumn)tc;
          String fieldName = qtc.getFieldName();
          if (fieldName.length() > 200) {
            fieldName = fieldName.substring(0, 199);
          }
          int oinx = -1;
          for (int i=0; i<model.getColumnCount(); i++) {
            if (model.getColumn(i) == tc) {
              oinx = i;
              break;
            }
          }
          StringTokenizer st = new StringTokenizer(settings.getValue(fieldName, qtc.getWidth() +"," +oinx), ",");
          //System.out.println(fieldName +":" +qtc.getWidth() +"," +oinx +":" +settings.getValue(fieldName, qtc.getWidth() +"," +oinx));
          if (st.hasMoreTokens()) {
            int width = Integer.parseInt(st.nextToken());
            if (width != qtc.getWidth()) {
              qtc.setPreferredWidth((int)width);
            }
            if (st.hasMoreTokens()) {
              int index = Integer.parseInt(st.nextToken());
              if (index != oinx) {
                try {
                  model.moveColumn(oinx, index);
                }
                catch (IllegalArgumentException ex) {
                }
              }
            }
          }
        }
      }
    }
  }

  private void storeColumnProps() {
    if (Application.get() != null && Application.get().getSettings().getValue(Consts.autoSaveColumnWidths, true) && getColumnModel() instanceof QueryTableColumnModel) {
      ISettings settings = Application.get().getSettings(getQuery().getDatabase().getUserProperties().getProperty("schemaId"), "orbada-view-table-columns-props-" +Utils.getUniqueCompId(this));
      for (int i=0; i<getColumnCount(); i++) {
        Object o = getColumnModel().getColumn(i);
        if (o instanceof QueryTableColumn) {
          QueryTableColumn qtc = (QueryTableColumn)o;
          String fieldName = qtc.getFieldName();
          if (fieldName.length() > 200) {
            fieldName = fieldName.substring(0, 199);
          }
          settings.setValue(fieldName, qtc.getWidth() +"," +i);
        }
      }
      settings.store();
    }
  }
  
  private static class OryginalColumnProps {
    TableColumn column;
    int width;
    public OryginalColumnProps(TableColumn column, int width) {
      this.column = column;
      this.width = width;
    }
  }
  
}
