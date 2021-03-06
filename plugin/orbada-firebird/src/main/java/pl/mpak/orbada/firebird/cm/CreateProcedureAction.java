/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.firebird.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.gui.wizards.CreateProcedureWizard;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateProcedureAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  private Database database;
  
  public CreateProcedureAction(Database database) {
    super();
    setText(stringManager.getString("CreateProcedureAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/procedure.gif"));
    setTooltip(stringManager.getString("CreateProcedureAction-hint"));
    setActionCommandKey("CreateProcedureAction");
    addActionListener(createActionListener());
    this.database = database;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateProcedureWizard(database), true);
      }
    };
  }

}
