/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.table.CreateTableTriggerWizard;
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
public class CreateTableTriggerAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private Database database;
  
  public CreateTableTriggerAction(Database database) {
    super();
    setText(stringManager.getString("CreateTableTriggerAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/trigger.gif"));
    setTooltip(stringManager.getString("CreateTableTriggerAction-hint"));
    setActionCommandKey("CreateTableTriggerAction");
    addActionListener(createActionListener());
    this.database = database;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateTableTriggerWizard(database, OracleDbInfoProvider.getCurrentSchema(database), null), true);
      }
    };
  }

}
