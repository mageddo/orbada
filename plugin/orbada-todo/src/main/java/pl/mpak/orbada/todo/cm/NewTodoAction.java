/*
 * NewTodoAction.java
 *
 * Created on 2007-11-24, 13:08:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.todo.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.todo.OrbadaTodoPlugin;
import pl.mpak.orbada.todo.gui.TodoEditDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class NewTodoAction extends Action {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("todo");

  private String td_sch_id;
  
  public NewTodoAction(Database database) {
    super();
    setText(stringManager.getString("NewTodoAction-text"));
    setTooltip(stringManager.getString("NewTodoAction-hint"));
    setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tasks16.gif")));
    setActionCommandKey("NewTodoAction");
    addActionListener(createActionListener());
    if (database != null) {
      td_sch_id = database.getUserProperties().getProperty("schemaId");
    }
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            try {
              TodoEditDialog.showDialog(null, td_sch_id);
            } catch (Exception ex) {
              ExceptionUtil.processException(ex);
            }
          }
        });
      }
    };
  }
  
}
