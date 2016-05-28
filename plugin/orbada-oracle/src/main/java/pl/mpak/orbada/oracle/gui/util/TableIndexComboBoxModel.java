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
public class TableIndexComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private String schemaName;
  private String tableName;
  private DbDatabaseInfo info;
  
  public TableIndexComboBoxModel(Database database) {
    super();
    this.database = database;
    info = OracleDbInfoProvider.instance.getDatabaseInfo(database);
    init();
  }
  
  public TableIndexComboBoxModel(Database database, String schemaName, String tableName) {
    this(database);
    change(schemaName, tableName);
  }
  
  public void change(String schemaName, String tableName) {
    this.tableName = tableName;
    this.schemaName = schemaName == null ? database.getUserName().toUpperCase() : schemaName;
    removeAllElements();
    DbObjectIdentified list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/TABLES/" +tableName +"/INDEXES");
    list.saveRefresh();
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        if (getIndexOf(i) == -1) {
          addElement(i);
        }
      }
    }
  }
  
  public void select(String indexName, JComboBox combo) {
    if (indexName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).equals(indexName)) {
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
