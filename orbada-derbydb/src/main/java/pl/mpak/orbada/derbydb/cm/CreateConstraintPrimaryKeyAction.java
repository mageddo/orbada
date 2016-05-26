/*
 * CreateConstraintFKAction.java
 *
 * Created on 2007-11-28, 21:39:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.universal.gui.wizards.CreateConstraintPrimaryKeyWizardPanel;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateConstraintPrimaryKeyAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaDerbyDbPlugin.class);

  private Database database;
  
  public CreateConstraintPrimaryKeyAction(Database database) {
    super(stringManager.getString("CreateConstraintPrimaryKeyAction-text"));
    this.database = database;
    setActionCommandKey("cmCreateConstraintPrimaryKey");
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/primary_key16.gif"));
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateConstraintPrimaryKeyWizardPanel(database, null, null), true);
      }
    };
  }
  
}
