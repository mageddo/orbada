/*
 * ViewValueAction.java
 *
 * Created on 2007-11-26, 18:22:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.TableColumn;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.comps.ViewValueDialog;
import pl.mpak.orbada.gui.comps.ViewValueDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ViewQueryFieldAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private QueryTable table;

  public ViewQueryFieldAction(QueryTable table) {
    this.table = table;
    setText(stringManager.getString("ViewQueryFieldAction-text"));
    setActionCommandKey("cmViewQueryField");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (table.getSelectedRow() >= 0 && table.getSelectedColumn() >= 0 && table.getQuery().isActive()) {
          try {
            table.getQuery().getRecord(table.getSelectedRow());
            TableColumn tc = table.getColumnModel().getColumn(table.getSelectedColumn());
            if (tc instanceof QueryTableColumn) {
              QueryTableColumn qtc = (QueryTableColumn)tc;
              QueryField o = qtc.getField();
              if (o != null) {
                ViewValueDialog.show(o);
              }
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
            MessageBox.show(table, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
          }
        }
      }
    };
  }
  
}
