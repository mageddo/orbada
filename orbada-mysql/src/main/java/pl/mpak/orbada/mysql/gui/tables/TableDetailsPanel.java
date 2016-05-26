/*
 * TableIndexesPanel.java
 *
 * Created on 28 paüdziernik 2007, 21:38
 */

package pl.mpak.orbada.mysql.gui.tables;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropDetailsTab;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TableDetailsPanel extends UniversalPropDetailsTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public TableDetailsPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TableDetailsPanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-table-details";
  }

  @Override
  public String getSql() {
    return Sql.getTableDetails(currentSchemaName, currentObjectName);
  }
  
}
