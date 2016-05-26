/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.triggers;

import java.util.HashMap;
import orbada.gui.ITabObjectUserData;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropDetailsTab;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class TriggerDetailsTab extends UniversalPropDetailsTab implements ITabObjectUserData {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  private HashMap<String, Variant> mapValues;

  public TriggerDetailsTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-trigger-details";
  }

  @Override
  public String getSql() {
    return Sql.getTriggerDetail();
  }

  @Override
  public void userData(HashMap<String, Variant> values) {
    mapValues = values;
  }
  
  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    try {
      if (mapValues != null) {
        qc.paramByName("schema_name").setString(currentSchemaName);
        qc.paramByName("trigger_name").setString(currentObjectName);
        qc.paramByName("table_name").setString(mapValues.get("table_name").getString());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  @Override
  public String getTitle() {
    return stringManager.getString("details");
  }

}
