/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.firebird.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.gui.wizards.CreateExceptionWizard;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateExceptionAction extends Action {

  private StringManager stringManager = StringManagerFactory.getStringManager("firebird");

  private Database database;
  
  public CreateExceptionAction(Database database) {
    super();
    setText(stringManager.getString("CreateExceptionAction-text"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/exception.gif"));
    setTooltip(stringManager.getString("CreateExceptionAction-hint"));
    setActionCommandKey("CreateExceptionAction");
    addActionListener(createActionListener());
    this.database = database;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateExceptionWizard(database), true);
      }
    };
  }

}
