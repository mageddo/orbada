package pl.mpak.orbada.postgresql.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.gui.sequences.SequenceListView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PostgreSQLSequenceListView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    return new SequenceListView(accesibilities);
  }
  
  @Override
  public String getPublicName() {
    return stringManager.getString("PostgreSQLSequenceListView-public-name");
  }
  
  @Override
  public String getViewId() {
    return "orbada-postgresql-sequence-view";
  }
  
  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/sequence.gif");
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public boolean isDefaultView() {
    return false;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLSequenceListView-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }
  
}
