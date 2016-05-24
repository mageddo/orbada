/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.types;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.postgresql.gui.PostgreSQLDependsTab;
import pl.mpak.orbada.postgresql.services.PostgreSQLDbInfoProvider;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;

/**
 *
 * @author akaluza
 */
public class TypeDependsTab extends PostgreSQLDependsTab {

  public TypeDependsTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getSql(SqlFilter filter) {
    return Sql.getDependTypeList(filter.getSqlText(), PostgreSQLDbInfoProvider.instance.getVersion(getDatabase()));
  }
  
}
