/*
 * CreateConstraintWizardAction.java
 *
 * Created on 2007-11-28, 20:26:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintCheckWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateConstraintCheckWizardAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  private Database database;
  
  public CreateConstraintCheckWizardAction(Database database) {
    super(stringManager.getString("CreateConstraintCheckWizardAction-text"));
    this.database = database;
    setActionCommandKey("cmCreateConstraintCheckWizard");
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/verify_document_16.gif"));
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateConstraintCheckWizardPanel(database, null, null), true);
      }
    };
  }
  
}
