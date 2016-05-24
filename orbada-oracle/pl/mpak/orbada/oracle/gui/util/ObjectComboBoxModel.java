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
import pl.mpak.orbada.oracle.dbinfo.OracleObjectInfo;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class ObjectComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private String schemaName;
  private DbDatabaseInfo info;
  
  public ObjectComboBoxModel(Database database) {
    super();
    this.database = database;
    info = OracleDbInfoProvider.instance.getDatabaseInfo(database);
    init();
  }
  
  public ObjectComboBoxModel(Database database, String schemaName) {
    this(database);
    change(schemaName);
  }
  
  public void change(String schemaName) {
    this.schemaName = schemaName == null ? database.getUserName().toUpperCase() : schemaName;
    removeAllElements();
    DbObjectIdentified list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/ALL OBJECTS");
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
  
  public void select(String objectType, String objectName, JComboBox combo) {
    if (objectName != null) {
      for (int i=0; i<getSize(); i++) {
        OracleObjectInfo object = (OracleObjectInfo)getElementAt(i);
        if (object.getName().equals(objectName) && object.getObjectType().equals(objectType)) {
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
