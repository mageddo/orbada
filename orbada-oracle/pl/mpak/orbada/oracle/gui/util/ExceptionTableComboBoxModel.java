/*
 * ExceptionsTableComboBoxModel.java
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
public class ExceptionTableComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private DbDatabaseInfo info;
  
  public ExceptionTableComboBoxModel(Database database) {
    super();
    this.database = database;
    info = OracleDbInfoProvider.instance.getDatabaseInfo(database);
    init();
  }
  
  private void init() {
  }
  
  public void change() {
    removeAllElements();
    DbObjectIdentified list = info.getObjectInfo("/EXCEPTION TABLES");
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        addElement(i);
      }
    }
  }
  
  public void select(String tableName, JComboBox combo) {
    if (tableName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).equals(tableName)) {
          combo.setSelectedIndex(i);
          break;
        }
      }
    } else if (getSize() > 0) {
      combo.setSelectedIndex(0);
    }
  }
  
}
