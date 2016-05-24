/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.parameters.PlSqlCcFlagsWizard;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PlSqlCcFlagsAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  
  public PlSqlCcFlagsAction(Database database) {
    super();
    setText(stringManager.getString("PlSqlCcFlagsAction-text"));
    setActionCommandKey("PlSqlCcFlagsAction");
    addActionListener(createActionListener());
    this.database = database;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new PlSqlCcFlagsWizard(database), true);
      }
    };
  }

}
