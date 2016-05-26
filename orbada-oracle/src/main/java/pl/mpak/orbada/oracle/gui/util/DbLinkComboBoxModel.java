/*
 * TableComboBoxModel.java
 *
 * Created on 2007-11-23, 18:36:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class DbLinkComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private String schemaName;
  private DbDatabaseInfo info;
  
  public DbLinkComboBoxModel(Database database) {
    super();
    this.database = database;
    info = OracleDbInfoProvider.instance.getDatabaseInfo(database);
    init();
  }
  
  public DbLinkComboBoxModel(Database database, String schemaName) {
    this(database);
    change(schemaName);
  }
  
  private void init() {
  }
  
  public void change(String schemaName) {
    this.schemaName = schemaName == null ? database.getUserName().toUpperCase() : schemaName;
    change(false);
  }
  
  private void change(boolean refresh) {
    removeAllElements();
    DbObjectIdentified list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/DB LINKS");
    if (refresh) {
      list.saveRefresh();
    }
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        addElement(i);
      }
    }
  }
  
  public void select(String dbLinkName, JComboBox combo) {
    if (dbLinkName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).equals(dbLinkName)) {
          combo.setSelectedIndex(i);
          break;
        }
      }
    } else if (getSize() > 0) {
      combo.setSelectedIndex(0);
    }
  }
  
}
