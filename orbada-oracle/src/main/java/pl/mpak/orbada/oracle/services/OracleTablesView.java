package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.tables.TablesPanelView;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleTablesView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  public Component createView(IViewAccesibilities accesibilities) {
    return new TablesPanelView(accesibilities);
  }
  
  @Override
  public boolean isDefaultView() {
    return true;
  }
  
  public String getPublicName() {
    return stringManager.getString("OracleTablesView-public-name");
  }
  
  public String getViewId() {
    return "orbada-oracle-tables-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/table.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("OracleTablesView-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaOraclePlugin.dataGroup};
  }
  
}
