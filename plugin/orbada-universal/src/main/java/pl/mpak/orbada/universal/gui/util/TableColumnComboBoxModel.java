package pl.mpak.orbada.universal.gui.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbDatabaseInfo;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class TableColumnComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  private String schemaName;
  private String tableName;
  private JdbcDbDatabaseInfo info;
  
  public TableColumnComboBoxModel(Database database) {
    super();
    this.database = database;
    info = new JdbcDbDatabaseInfo(database);
    init();
  }
  
  public TableColumnComboBoxModel(Database database, String schemaName, String tableName) {
    this(database);
    change(schemaName, tableName);
  }
  
  public void change(String schemaName, String tableName) {
    this.tableName = tableName;
    this.schemaName = schemaName == null ? database.getUserName().toUpperCase() : schemaName;
    try {
      if (!StringUtil.equalAnyOfString(this.schemaName, database.getSchemaArray())) {
        this.schemaName = null;
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      this.schemaName = null;
    }
    removeAllElements();
    DbObjectIdentified list;
    if (this.schemaName == null) {
      list = info.getObjectInfo("/TABLES/" +tableName +"/COLUMNS");
    }
    else {
      list = info.getObjectInfo("/SCHEMAS/" +this.schemaName +"/TABLES/" +tableName +"/COLUMNS");
    }
    if (list instanceof DbObjectContainer) {
      for (DbObjectIdentified i : ((DbObjectContainer)list).objectsArray(true)) {
        addElement(i);
      }
    }
  }
  
  public void select(String columnName, JComboBox combo) {
    if (columnName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).equals(columnName)) {
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
