/*
 * SelectTableColumnAction.java
 *
 * Created on 2007-10-09, 17:11:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.gui.comps.table.cm;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import orbada.Consts;
import orbada.gui.util.SimpleSelectDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SelectTableColumnAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private JTable table;
  private String formatData;
  
  public SelectTableColumnAction(JTable table, String formatData) {
    super();
    this.table = table;
    this.formatData = formatData;
    setText(stringManager.getString("SelectTableColumn-text"));
    setShortCut(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    setActionCommandKey("cmSelectTableColumn");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final Vector<Vector<String>> columns = new Vector<Vector<String>>();
        for (int i=0; i<table.getColumnCount(); i++) {
          TableColumn tc = table.getColumnModel().getColumn(i);
          Vector<String> item = new Vector<String>();
          item.add(new Integer(i).toString());
          item.add(tc.getHeaderValue().toString());
          if (tc instanceof QueryTableColumn) {
            QueryField qf = ((QueryTableColumn)tc).getField();
            if (qf != null) {
              item.add(qf.getDataTypeName());
            } else {
              item.add("");
            }
            item.add(SQLUtil.createSqlName(qf.getSchemaName(), qf.getTableName()));
          }
          columns.add(item);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            Rectangle rect = table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), true);
            Point point = table.getLocationOnScreen();
            SimpleSelectDialog.select(
              (Window)SwingUtil.getWindowComponent(table),
              point.x +rect.x, point.y +table.getVisibleRect().y,
              formatData,
              columns,
              table.getSelectedColumn(),
              new SimpleSelectDialog.CallBack() {
                public void selected(Object o) {
                  final Vector<String> value = (Vector<String>)o;
                  if (value != null) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                      public void run() {
                        table.changeSelection(table.getSelectedRow(), Integer.parseInt(value.get(0)), false, false);
                      }
                    });
                  }
                }
              });
          }
        });
      }
    };
  }
  
}
