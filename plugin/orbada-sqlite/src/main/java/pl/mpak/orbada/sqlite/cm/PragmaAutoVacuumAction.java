/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.gui.wizards.PragmaAutoVacuumWizard;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PragmaAutoVacuumAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  private Database database;

  public PragmaAutoVacuumAction(Database database) {
    this.database = database;
    setText(stringManager.getString("auto-vacuum"));
    setActionCommandKey("PragmaAutoVacuumAction");
    addActionListener(createActionListener());
  }
  
  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          SqlCodeWizardDialog.show(new PragmaAutoVacuumWizard(database), true);
        } catch (Exception ex) {
          MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
        }
      }
    };
  }

}
