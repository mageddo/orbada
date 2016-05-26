/*
 * DerbyDbPerspectiveProvider.java
 *
 * Created on 2007-11-11, 19:20:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.services;

import javax.swing.JMenu;
import pl.mpak.orbada.derbydb.cm.DerbyDbSearchObjectAction;
import pl.mpak.orbada.derbydb.OrbadaDerbyDbPlugin;
import pl.mpak.orbada.derbydb.cm.CreateConstraintCheckWizardAction;
import pl.mpak.orbada.derbydb.cm.CreateConstraintForeignKeyAction;
import pl.mpak.orbada.derbydb.cm.CreateConstraintPrimaryKeyAction;
import pl.mpak.orbada.derbydb.cm.CreateIndexWizardAction;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DerbyDbPerspectiveProvider extends PerspectiveProvider {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaDerbyDbPlugin.class);

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return String.format(stringManager.getString("DerbyDbPerspectiveProvider-description"), new Object[] {OrbadaDerbyDbPlugin.apacheDerbyDriverType});
  }

  public String getGroupName() {
    return OrbadaDerbyDbPlugin.apacheDerbyDriverType;
  }

  public void initialize() {
    DerbyDbSearchObjectAction searchObject = new DerbyDbSearchObjectAction(accesibilities);
    
    JMenu menu = new JMenu(OrbadaDerbyDbPlugin.apacheDerbyDriverType);
    menu.add(searchObject);

    menu.addSeparator();
    JMenu wizards = new JMenu(stringManager.getString("DerbyDbPerspectiveProvider-wizzards"));
    wizards.add(new CreateIndexWizardAction(accesibilities.getDatabase()));
    wizards.add(new CreateConstraintCheckWizardAction(accesibilities.getDatabase()));
    wizards.add(new CreateConstraintForeignKeyAction(accesibilities.getDatabase()));
    wizards.add(new CreateConstraintPrimaryKeyAction(accesibilities.getDatabase()));
    menu.add(wizards);

    accesibilities.addMenu(menu);
    accesibilities.addAction(searchObject);
    
  }
  
}
