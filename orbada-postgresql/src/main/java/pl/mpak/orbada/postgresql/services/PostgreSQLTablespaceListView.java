package pl.mpak.orbada.postgresql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.gui.admin.TablespaceListPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PostgreSQLTablespaceListView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    return new TablespaceListPanelView(accesibilities);
  }
  
  @Override
  public boolean isDefaultView() {
    return false;
  }
  
  @Override
  public String getPublicName() {
    return stringManager.getString("PostgreSQLTablespaceListView-public-name");
  }
  
  @Override
  public String getViewId() {
    return "orbada-postgresql-tablespace-view";
  }
  
  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/tablespace.gif");
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLTablespaceListView-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaPostgreSQLPlugin.adminGroup};
  }
  
}
