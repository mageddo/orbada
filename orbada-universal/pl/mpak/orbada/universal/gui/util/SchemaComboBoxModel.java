/*
 * TableComboBoxModel.java
 *
 * Created on 2007-11-23, 18:36:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbDatabaseInfo;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class SchemaComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private JdbcDbDatabaseInfo info;
  
  public SchemaComboBoxModel(Database database) {
    super();
    this.database = database;
    info = new JdbcDbDatabaseInfo(database);
    init();
    change();
  }
  
  private void init() {
  }
  
  public void change() {
    removeAllElements();
    DbObjectIdentified list = info.getObjectInfo("/SCHEMAS");
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        addElement(i);
      }
    }
  }
  
  public void select(String schemaName, JComboBox combo) {
    if (schemaName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).equals(schemaName)) {
          combo.setSelectedIndex(i);
          break;
        }
      }
    } else if (getSize() > 0) {
      combo.setSelectedIndex(0);
    }
  }
  
}
