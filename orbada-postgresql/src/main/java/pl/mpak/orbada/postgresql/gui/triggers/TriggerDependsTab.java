/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.triggers;

import java.util.HashMap;
import pl.mpak.orbada.gui.ITabObjectUserData;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.gui.PostgreSQLDependsTab;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class TriggerDependsTab extends PostgreSQLDependsTab implements ITabObjectUserData {

  private HashMap<String, Variant> mapValues;

  public TriggerDependsTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getDependTriggerList(filter.getSqlText(), PostgreSQLDbInfoProvider.instance.getVersion(getDatabase()));
  }
  
  @Override
  public void userData(HashMap<String, Variant> values) {
    mapValues = values;
  }
  
  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    super.extraSqlParameters(qc);
    try {
      if (mapValues != null) {
        qc.paramByName("table_name").setString(mapValues.get("table_name").getString());
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
}
