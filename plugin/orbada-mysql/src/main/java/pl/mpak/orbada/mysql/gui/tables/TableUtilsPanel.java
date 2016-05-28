/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.mysql.gui.tables;

import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropOnDemandTab;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableUtilsPanel extends UniversalPropOnDemandTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TableUtilsPanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "mysql-table-utils";
  }

  @Override
  public String getSchemaColumnName() {
    return null;
  }

  @Override
  public String getObjectColumnName() {
    return null;
  }

  @Override
  public String getPropColumnName() {
    return null;
  }

  @Override
  public QueryTableColumn[] getTableColumns() {
    return null;
  }

  public String getTitle() {
    return stringManager.getString("TableUtilsPanel-title");
  }
  
  @Override
  protected boolean executeSql() {
    return true;
  }
  
}
