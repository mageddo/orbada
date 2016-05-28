/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.universal.gui.tabbed;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;

/**
 *
 * @author akaluza
 */
public abstract class UniversalPropOnDemandTab extends UniversalPropTab {

  private String sqlText;
  
  public UniversalPropOnDemandTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public void reopen(String sqlText) {
    this.sqlText = sqlText;
    refreshTask();
  }
  
  public String getSql(SqlFilter filter) {
    return sqlText;
  }
  
  public SqlFilterDefComponent[] getFilterDefComponent() {
    return null;
  }
  
  @Override
  public void refresh(String catalogName, String schemaName, String objectName) {
    sqlText = null;
    super.refresh(catalogName, schemaName, objectName);
  }
  
}
