/*
 * CreateConstraintWizardAction.java
 *
 * Created on 2007-11-28, 20:26:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.gui.wizards.AlterUserSchemaWizardPanel;
import pl.mpak.orbada.hsqldb.services.HSqlDbInfoProvider;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class AlterUserSchemaWizardAction extends Action {
  
  private StringManager stringManager = StringManagerFactory.getStringManager("hsqldb");

  private Database database;
  
  public AlterUserSchemaWizardAction(Database database) {
    super();
    setText(stringManager.getString("AlterUserSchemaWizardAction-text"));
    this.database = database;
    setActionCommandKey("AlterUserSchemaWizardAction");
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit_user.gif"));
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(new AlterUserSchemaWizardPanel(database, HSqlDbInfoProvider.getCurrentSchema(database)), true);
      }
    };
  }
  
}
