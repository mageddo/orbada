package pl.mpak.orbada.imports.csv.gui;

import javax.swing.DefaultComboBoxModel;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class TableComboBoxModel extends DefaultComboBoxModel {
  
  private Database database;
  
  public TableComboBoxModel(Database database) {
    super();
    this.database = database;
    init();
  }
  
  private void init() {
    try {
      Query query = database.createQuery();
      query.setResultSet(database.getMetaData().getTables(null, null, null, new String[] {"TABLE", "VIEW"}));
      while (!query.eof()) {
        addElement(database.quoteName(query.fieldByName("table_schem").getString(), query.fieldByName("table_name").getString()));
        query.next();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
}
