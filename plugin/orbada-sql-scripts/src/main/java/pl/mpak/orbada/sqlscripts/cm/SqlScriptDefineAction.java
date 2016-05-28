/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlscripts.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.sqlscripts.OrbadaSqlScriptsPlugin;
import pl.mpak.orbada.sqlscripts.gui.SqlScriptListDialog;
import pl.mpak.orbada.sqlscripts.services.SqlScriptPerspectiveProvider;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SqlScriptDefineAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sql-scripts");

  private SqlScriptPerspectiveProvider service;
  
  public SqlScriptDefineAction(SqlScriptPerspectiveProvider service) {
    super();
    setText(stringManager.getString("sql-script-config-action"));
    setActionCommandKey("SqlScriptDefineAction");
    addActionListener(createActionListener());
    this.service = service;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlScriptListDialog.showDialog(service.getAccesibilities().getDatabase());
        service.reloadSqlScripts();
      }
    };
  }

}
