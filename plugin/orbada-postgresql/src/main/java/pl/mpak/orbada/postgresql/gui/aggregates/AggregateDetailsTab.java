/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.aggregates;

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
public class AggregateDetailsTab extends UniversalPropDetailsTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public AggregateDetailsTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-aggregate-details";
  }

  @Override
  public String getSql() {
    return Sql.getAggregateDetail();
  }

  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("schema_name").setString(currentSchemaName);
    qc.paramByName("aggregate_name").setString(currentObjectName);
  }
  
  @Override
  public String getTitle() {
    return stringManager.getString("details");
  }
  
}
