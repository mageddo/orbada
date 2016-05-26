/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.wizards.type.CreateVarrayTypeWizard;
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
public class CreateVarrayTypeAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  private Database database;
  
  public CreateVarrayTypeAction(Database database) {
    super();
    setText(stringManager.getString("CreateVarrayTypeAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/type.gif"));
    setTooltip(stringManager.getString("CreateVarrayTypeAction-hint"));
    setActionCommandKey("CreateVarrayTypeAction");
    addActionListener(createActionListener());
    this.database = database;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateVarrayTypeWizard(database, OracleDbInfoProvider.getCurrentSchema(database)), true);
      }
    };
  }

}
