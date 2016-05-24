package pl.mpak.orbada.postgresql.services;

import javax.swing.JMenu;
import javax.swing.JToolBar;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class PostgreSQLPerspectiveProvider extends PerspectiveProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLPerspectiveProvider-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }
  
  @Override
  public void initialize() {
//    final AttachDatabaseAction attachDatabaseAction = new AttachDatabaseAction(accesibilities.getDatabase());
//    final DatabaseVacuumAction databaseVacuumAction = new DatabaseVacuumAction(accesibilities.getDatabase());

    final JMenu postgresql = new JMenu(OrbadaPostgreSQLPlugin.driverType);
    final JToolBar toolBar = new JToolBar(OrbadaPostgreSQLPlugin.driverType);
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
//        sqLite.add(attachDatabaseAction);
//        sqLite.add(databaseVacuumAction);
//        sqLite.addSeparator();
//        sqLite.add(new PragmaCacheSizeAction(accesibilities.getDatabase()));
//        sqLite.add(new PragmaCaseSensitiveLikeAction(accesibilities.getDatabase()));
//        sqLite.add(new PragmaMaxPageCountAction(accesibilities.getDatabase()));
//        sqLite.add(new PragmaAutoVacuumAction(accesibilities.getDatabase()));
//        sqLite.add(new PragmaLockingModeAction(accesibilities.getDatabase()));
//
//        toolBar.add(new ToolButton(attachDatabaseAction));
//        toolBar.add(new ToolButton(databaseVacuumAction));
      }
    });
    //accesibilities.addMenu(postgresql);
    accesibilities.addToolBar(toolBar);
  }

  @Override
  public void processMessage(PluginMessage message) {
    if (getAccesibilities() != null &&
        getAccesibilities().getDatabase() != null &&
        getAccesibilities().getDatabase().getUniqueID().equals(message.getDestinationId()) &&
        message.isMessageId(Consts.globalMessageFreezeObject)) {
//      final IDatabaseObject object = (IDatabaseObject)message.getObject();
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
//          FreezeViewService service = new FreezeFactory().createInstance(object.getObjectType(), StringUtil.nvl(object.getSchemaName(), object.getCatalogName()), object.getObjectName());
//          IPerspectiveAccesibilities accessibilities = application.getActivePerspective();
//          if (service != null && accessibilities != null) {
//            accessibilities.createView(service);
//          }
        }
      });
      message.setServed(true);
    }
  }
  
}
