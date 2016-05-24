/*
 * DerbyDbPerspectiveProvider.java
 *
 * Created on 2007-11-11, 19:20:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.hsqldb.services;

import javax.swing.JMenu;
import pl.mpak.orbada.hsqldb.OrbadaHSqlDbPlugin;
import pl.mpak.orbada.hsqldb.cm.AlterUserPasswordWizardAction;
import pl.mpak.orbada.hsqldb.cm.AlterUserSchemaWizardAction;
import pl.mpak.orbada.hsqldb.cm.CreateAliasWizardAction;
import pl.mpak.orbada.hsqldb.cm.CreateConstraintCheckWizardAction;
import pl.mpak.orbada.hsqldb.cm.CreateConstraintForeignKeyAction;
import pl.mpak.orbada.hsqldb.cm.CreateConstraintPrimaryKeyAction;
import pl.mpak.orbada.hsqldb.cm.CreateIndexWizardAction;
import pl.mpak.orbada.hsqldb.cm.CreateSequenceWizardAction;
import pl.mpak.orbada.hsqldb.cm.CreateUserWizardAction;
import pl.mpak.orbada.hsqldb.cm.GrantClassWizardAction;
import pl.mpak.orbada.hsqldb.cm.HSqlDbSearchObjectAction;
import pl.mpak.orbada.hsqldb.cm.RevokeClassWizardAction;
import pl.mpak.orbada.hsqldb.cm.SetSchemaWizardAction;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class HSqlDbPerspectiveProvider extends PerspectiveProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaHSqlDbPlugin.class);

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaHSqlDbPlugin.hsqlDbDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("HSqlDbPerspectiveProvider-description");
  }

  public String getGroupName() {
    return OrbadaHSqlDbPlugin.hsqlDbDriverType;
  }

  public void initialize() {
    SetSchemaWizardAction setSchema = new SetSchemaWizardAction(accesibilities.getDatabase());
    HSqlDbSearchObjectAction searchObject = new HSqlDbSearchObjectAction(accesibilities);
    
    JMenu menu = new JMenu(OrbadaHSqlDbPlugin.hsqlDbDriverType);
    menu.add(searchObject);

    //menu.addSeparator();
    JMenu wizards = new JMenu(stringManager.getString("wizzards"));
    wizards.add(new CreateIndexWizardAction(accesibilities.getDatabase()));
    wizards.add(new CreateConstraintCheckWizardAction(accesibilities.getDatabase()));
    wizards.add(new CreateConstraintForeignKeyAction(accesibilities.getDatabase()));
    wizards.add(new CreateConstraintPrimaryKeyAction(accesibilities.getDatabase()));
    wizards.addSeparator();
    wizards.add(new CreateAliasWizardAction(accesibilities.getDatabase()));
    wizards.add(new GrantClassWizardAction(accesibilities.getDatabase()));
    wizards.add(new RevokeClassWizardAction(accesibilities.getDatabase()));
    wizards.addSeparator();
    wizards.add(new CreateSequenceWizardAction(accesibilities.getDatabase()));
    wizards.addSeparator();
    wizards.add(setSchema);
    wizards.addSeparator();
    wizards.add(new CreateUserWizardAction(accesibilities.getDatabase()));
    wizards.add(new AlterUserPasswordWizardAction(accesibilities.getDatabase()));
    wizards.add(new AlterUserSchemaWizardAction(accesibilities.getDatabase()));
    menu.add(wizards);

    accesibilities.addMenu(menu);
    accesibilities.addAction(setSchema);
    accesibilities.addAction(searchObject);
  }
  
}
