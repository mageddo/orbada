/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.gui.wizards.PragmaDatabaseLockingModeWizard;
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
public class PragmaLockingModeAction extends Action {

  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  private Database database;

  public PragmaLockingModeAction(Database database) {
    this.database = database;
    setText(stringManager.getString("database-locking-mode"));
    setActionCommandKey("PragmaLockingModeAction");
    addActionListener(createActionListener());
  }
  
  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          SqlCodeWizardDialog.show(new PragmaDatabaseLockingModeWizard(database, null), true);
        } catch (Exception ex) {
          MessageBox.show(stringManager.getString("error"), ex.getMessage(), ModalResult.OK);
        }
      }
    };
  }

}
