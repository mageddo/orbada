/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.CompileAllObjectsWizard;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CompileAllObjectsAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  
  public CompileAllObjectsAction(Database database) {
    super();
    setText(stringManager.getString("CompileAllObjectsAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_sql_execute_all.gif"));
    setTooltip(stringManager.getString("CompileAllObjectsAction-hint"));
    setActionCommandKey("CompileAllObjectsAction");
    addActionListener(createActionListener());
    this.database = database;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CompileAllObjectsWizard(database, OracleDbInfoProvider.getCurrentSchema(database), null, null), true);
      }
    };
  }

}
