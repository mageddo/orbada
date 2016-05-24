/*
 * TableColumnComboBoxModel.java
 *
 * Created on 2007-11-23, 18:54:47
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
public class RecyclebinComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private DbDatabaseInfo info;
  
  public RecyclebinComboBoxModel(Database database) {
    super();
    this.database = database;
    info = OracleDbInfoProvider.instance.getDatabaseInfo(database);
    init();
  }
  
  public void change() {
    removeAllElements();
    DbObjectIdentified list = info.getObjectInfo("/RECYCLEBIN");
    list.saveRefresh();
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        addElement(i);
      }
    }
  }
  
  public void select(String objectName, JComboBox combo) {
    if (objectName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).equals(objectName)) {
          combo.setSelectedIndex(i);
          break;
        }
      }
    } else if (getSize() > 0) {
      combo.setSelectedIndex(0);
    }
  }
  
  private void init() {
  }
  
}
