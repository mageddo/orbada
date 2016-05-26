package pl.mpak.orbada.mysql.gui.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.gui.util.helpers.TableInfo;
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
public class TableComboBoxModel extends DefaultComboBoxModel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);
  
  private Database database;
  
  public TableComboBoxModel(Database database) {
    super();
    this.database = database;
    init();
  }
  
  public void change(String databaseName) {
    removeAllElements();
    try {
      Query query = database.createQuery(Sql.getTableList(null), false);
      try {
        query.paramByName("schema_name").setString(databaseName);
        query.open();
        while (!query.eof()) {
          addElement(new TableInfo(
            query.fieldByName("table_schema").getString(),
            query.fieldByName("table_name").getString(),
            query.fieldByName("table_type").getString(),
            query.fieldByName("engine").getString(),
            query.fieldByName("row_format").getString(),
            query.fieldByName("create_time").getDate(),
            query.fieldByName("update_time").getDate(),
            query.fieldByName("table_comment").getString()
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
  
  public void select(String tableName, JComboBox combo) {
    if (tableName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).toString().equalsIgnoreCase(tableName)) {
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
