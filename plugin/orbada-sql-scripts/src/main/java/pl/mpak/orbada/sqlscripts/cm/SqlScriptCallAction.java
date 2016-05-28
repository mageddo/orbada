/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlscripts.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.sqlscripts.db.SqlScriptRecord;
import pl.mpak.orbada.universal.gui.ErrorBox;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class SqlScriptCallAction extends Action {

  private Database database;
  private SqlScriptRecord sqlScript;
  
  public SqlScriptCallAction(Database database, SqlScriptRecord sqlScript) {
    super(sqlScript.getName());
    setActionCommandKey("SqlScriptCallAction");
    addActionListener(createActionListener());
    this.database = database;
    this.sqlScript = sqlScript;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          database.executeScript(sqlScript.getScript());
        } catch (UseDBException ex) {
          ErrorBox.show(ex);
        }
      }
    };
  }

}
