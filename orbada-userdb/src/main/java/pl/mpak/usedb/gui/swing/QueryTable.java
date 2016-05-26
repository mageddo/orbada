package pl.mpak.usedb.gui.swing;

import java.awt.Rectangle;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.actions.CmEnter;
import pl.mpak.sky.gui.swing.comp.actions.CmTab;
import pl.mpak.sky.gui.swing.comp.actions.CmTabBackward;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.usedb.gui.swing.cm.CmFetchAllEnd;
import pl.mpak.util.ButtonTableHeader;
import pl.mpak.util.ExceptionUtil;

public class QueryTable extends JTable {
  private static final long serialVersionUID = 2006274493480411986L;
  
  private transient QueryListener queryListener;
  private transient ButtonTableHeader tableHeader;
  private Query query;
  
  public QueryTable(boolean createQuery) {
    super();
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setColumnModel(new QueryTableColumnModel());
    setModel(new QueryTableModel());
    setTableHeader(tableHeader = new ButtonTableHeader(getColumnModel()));
    if (createQuery) {
      setQuery(new Query());
    }
    setRequestFocusEnabled(true);
    SwingUtil.addAction(this, "cmTab", new CmTab(this));
    SwingUtil.addAction(this, "cmTabBackward", new CmTabBackward(this));
    SwingUtil.addAction(this, "cmEnter", new CmEnter(this));
    SwingUtil.addAction(this, "cmFetchALlEnd", new CmFetchAllEnd(this));
  }
  
  public QueryTable(Query query) {
    this(false);
    setQuery(query);
  }

  public QueryTable() {
    this(true);
  }

  public void setQuery(Query query) {
    if (this.query != null) {
      this.query.removeQueryListener(getQueryListener());
    }
    this.query = query;
    if (this.query != null) {
      this.query.addQueryListener(getQueryListener());
    }
    if (getModel() instanceof QueryTableModel) {
      ((QueryTableModel)getModel()).setQuery(this.query);
    }
    if (getColumnModel() instanceof QueryTableColumnModel) {
      ((QueryTableColumnModel)getColumnModel()).setQuery(this.query);
    }
    if (this.query != null) {
      try {
        this.query.setCacheData(true);
      } catch (UseDBException e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public Query getQuery() {
    return query;
  }

  private QueryListener getQueryListener() {
    if (queryListener == null) {
      queryListener = new DefaultQueryListener() {
        public void afterClose(EventObject e) {
          if (!query.isOnOrderByAction()) {
            tableHeader.resetOrder();
          }
        }
        public void beforeOpen(EventObject e) {
          if (!query.isOnOrderByAction()) {
            tableHeader.resetOrder();
          }
        }
      };
    }
    return queryListener;
  }

  public void changeSelection(final int row, final int column) {
    if (!java.awt.EventQueue.isDispatchThread()) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          changeSelection(row, column, false, false);
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              Rectangle rect = getCellRect(row, column, true);
              scrollRectToVisible(rect);
            }
          });
        }
      });
    }
    else {
      changeSelection(row, column, false, false);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          Rectangle rect = getCellRect(row, column, true);
          scrollRectToVisible(rect);
        }
      });
    }
  }

}
