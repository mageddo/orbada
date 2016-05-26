package pl.mpak.orbada.mysql.gui.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.mysql.gui.util.helpers.EngineInfo;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class EngineComboBoxModel extends DefaultComboBoxModel {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);
  
  private Database database;
  
  public EngineComboBoxModel(Database database) {
    super();
    this.database = database;
    init();
  }
  
  public void change() {
    removeAllElements();
    try {
      Query query = database.createQuery(Sql.getEngineList());
      try {
        while (!query.eof()) {
          addElement(new EngineInfo(
            query.fieldByName("ENGINE").getString(),
            query.fieldByName("SUPPORT").getString(),
            query.fieldByName("COMMENT").getString(),
            StringUtil.toBoolean(query.fieldByName("TRANSACTIONS").getString()),
            StringUtil.toBoolean(query.fieldByName("XA").getString()),
            StringUtil.toBoolean(query.fieldByName("SAVEPOINTS").getString())
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
  
  public void select(String engineName, JComboBox combo) {
    if (engineName != null) {
      for (int i=0; i<getSize(); i++) {
        if (getElementAt(i).toString().equalsIgnoreCase(engineName)) {
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
