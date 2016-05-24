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
public class TriggerComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private String schemaName;
  private String objectName;
  private DbDatabaseInfo info;
  
  public TriggerComboBoxModel(Database database) {
    super();
    this.database = database;
    info = OracleDbInfoProvider.instance.getDatabaseInfo(database);
    init();
  }
  
  public void change(String schemaName, String objectName, String tableType) {
    this.objectName = objectName;
    this.schemaName = schemaName == null ? database.getUserName().toUpperCase() : schemaName;
    removeAllElements();
    DbObjectIdentified list = null;
    if (this.objectName != null) {
      if ("TABLE".equalsIgnoreCase(tableType)) {
        list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/TABLES/" +objectName +"/TRIGGERS");
      }
      else if ("VIEW".equalsIgnoreCase(tableType)) {
        list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/VIEWS/" +objectName +"/TRIGGERS");
      }
      else if ("MATERIALIZED VIEW".equalsIgnoreCase(tableType)) {
        list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/MATERIALIZED VIEWS/" +objectName +"/TRIGGERS");
      }
    }
    else {
      list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/TRIGGERS");
    }
    list.saveRefresh();
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        addElement(i);
      }
    }
  }
  
  public void select(String triggerName, JComboBox combo) {
    if (triggerName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).equals(triggerName)) {
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
