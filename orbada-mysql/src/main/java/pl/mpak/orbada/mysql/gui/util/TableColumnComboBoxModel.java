package pl.mpak.orbada.mysql.gui.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.gui.util.helpers.TableColumnInfo;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableColumnComboBoxModel extends DefaultComboBoxModel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");
  
  private Database database;
  
  public TableColumnComboBoxModel(Database database) {
    super();
    this.database = database;
    init();
  }
  
  public void change(String databaseName, String tableName) {
    removeAllElements();
    try {
      Query query = database.createQuery(Sql.getColumnList(null), false);
      try {
        query.paramByName("schema_name").setString(databaseName);
        query.paramByName("table_name").setString(tableName);
        query.open();
        while (!query.eof()) {
          addElement(new TableColumnInfo(
            query.fieldByName("ordinal_position").getInteger(),
            query.fieldByName("table_schema").getString(),
            query.fieldByName("table_name").getString(),
            query.fieldByName("column_name").getString(),
            query.fieldByName("column_default").getString(),
            query.fieldByName("is_nullable").getBoolean(),
            query.fieldByName("data_type").getString(),
            query.fieldByName("column_type").getString(),
            query.fieldByName("character_set_name").getString(),
            query.fieldByName("collation_name").getString(),
            query.fieldByName("column_key").getString(),
            query.fieldByName("extra").getString(),
            query.fieldByName("column_comment").getString()
            ));
          query.next();
        }
      }
      finally {
        query.close();
      }
    } catch (Exception ex) {
      MessageBox.show(null, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
  
  public void select(String columnName, JComboBox combo) {
    if (columnName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).toString().equalsIgnoreCase(columnName)) {
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
