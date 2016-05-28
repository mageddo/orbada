package pl.mpak.orbada.mysql.gui.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.gui.util.helpers.DatabaseInfo;
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
public class DatabaseComboBoxModel extends DefaultComboBoxModel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");
  
  private Database database;
  
  public DatabaseComboBoxModel(Database database) {
    super();
    this.database = database;
    init();
  }
  
  public void change() {
    removeAllElements();
    try {
      Query query = database.createQuery(Sql.getDatabaseList());
      try {
        while (!query.eof()) {
          addElement(new DatabaseInfo(query.fieldByName("schema_name").getString()));
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
  
  public void select(String databaseName, JComboBox combo) {
    if (databaseName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).toString().equalsIgnoreCase(databaseName)) {
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
