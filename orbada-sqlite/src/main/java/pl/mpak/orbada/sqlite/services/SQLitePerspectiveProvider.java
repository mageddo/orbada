package pl.mpak.orbada.sqlite.services;

import javax.swing.JMenu;
import javax.swing.JToolBar;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.plugins.IDatabaseObject;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.cm.DatabaseVacuumAction;
import pl.mpak.orbada.sqlite.cm.PragmaAutoVacuumAction;
import pl.mpak.orbada.sqlite.cm.PragmaCacheSizeAction;
import pl.mpak.orbada.sqlite.cm.PragmaCaseSensitiveLikeAction;
import pl.mpak.orbada.sqlite.cm.PragmaLockingModeAction;
import pl.mpak.orbada.sqlite.cm.PragmaMaxPageCountAction;
import pl.mpak.orbada.sqlite.gui.freezing.FreezeFactory;
import pl.mpak.orbada.sqlite.gui.freezing.FreezeViewService;
import pl.mpak.orbada.sqlite.services.actions.AttachDatabaseAction;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SQLitePerspectiveProvider extends PerspectiveProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaSQLitePlugin.driverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("SQLitePerspectiveProvider-description");
  }

  public String getGroupName() {
    return OrbadaSQLitePlugin.driverType;
  }
  
  public void initialize() {
    final AttachDatabaseAction attachDatabaseAction = new AttachDatabaseAction(accesibilities.getDatabase());
    final DatabaseVacuumAction databaseVacuumAction = new DatabaseVacuumAction(accesibilities.getDatabase());

    final JMenu sqLite = new JMenu(OrbadaSQLitePlugin.driverType);
    final JToolBar toolBar = new JToolBar(OrbadaSQLitePlugin.driverType);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        sqLite.add(attachDatabaseAction);
        sqLite.add(databaseVacuumAction);
        sqLite.addSeparator();
        sqLite.add(new PragmaCacheSizeAction(accesibilities.getDatabase()));
        sqLite.add(new PragmaCaseSensitiveLikeAction(accesibilities.getDatabase()));
        sqLite.add(new PragmaMaxPageCountAction(accesibilities.getDatabase()));
        sqLite.add(new PragmaAutoVacuumAction(accesibilities.getDatabase()));
        sqLite.add(new PragmaLockingModeAction(accesibilities.getDatabase()));

        toolBar.add(new ToolButton(attachDatabaseAction));
        toolBar.add(new ToolButton(databaseVacuumAction));
      }
    });
    accesibilities.addMenu(sqLite);
    accesibilities.addToolBar(toolBar);
  }

  @Override
  public void processMessage(PluginMessage message) {
    if (getAccesibilities() != null &&
        getAccesibilities().getDatabase() != null &&
        getAccesibilities().getDatabase().getUniqueID().equals(message.getDestinationId()) &&
        message.isMessageId(Consts.globalMessageFreezeObject)) {
      final IDatabaseObject object = (IDatabaseObject)message.getObject();
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          FreezeViewService service = new FreezeFactory().createInstance(object.getObjectType(), StringUtil.nvl(object.getSchemaName(), object.getCatalogName()), object.getObjectName());
          IPerspectiveAccesibilities accessibilities = application.getActivePerspective();
          if (service != null && accessibilities != null) {
            accessibilities.createView(service);
          }
        }
      });
      message.setServed(true);
    }
  }
  
}
