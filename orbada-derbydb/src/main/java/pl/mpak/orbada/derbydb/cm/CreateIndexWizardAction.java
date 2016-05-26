/*
 * CreateIndexWizardAction.java
 *
 * Created on 2007-11-22, 23:11:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.orbada.universal.gui.wizards.CreateIndexWizardPanel;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class CreateIndexWizardAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("derbydb");

  private Database database;
  
  public CreateIndexWizardAction(Database database) {
    super(stringManager.getString("CreateIndexWizardAction-text"));
    this.database = database;
    setActionCommandKey("cmCreateIndexWizard");
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/create_index16.gif"));
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new CreateIndexWizardPanel(database, null, null), true);
      }
    };
  }
  
}
