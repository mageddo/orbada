/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.views;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropDetailsTab;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ViewDetailsTab extends UniversalPropDetailsTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public ViewDetailsTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-view-details";
  }

  @Override
  public String getSql() {
    return Sql.getViewDetail();
  }

  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("schema_name").setString(currentSchemaName);
    qc.paramByName("view_name").setString(currentObjectName);
  }
  
  @Override
  public String getTitle() {
    return stringManager.getString("details");
  }
  
}
