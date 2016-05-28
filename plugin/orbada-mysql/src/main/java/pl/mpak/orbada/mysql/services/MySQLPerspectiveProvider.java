package pl.mpak.orbada.mysql.services;

import javax.swing.JMenu;
import javax.swing.JToolBar;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.gui.freezing.FreezeFactory;
import pl.mpak.orbada.mysql.gui.freezing.FreezeViewService;
import pl.mpak.orbada.plugins.IDatabaseObject;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class MySQLPerspectiveProvider extends PerspectiveProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaMySQLPlugin.driverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("MySQLPerspectiveProvider-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }
  
  public void initialize() {
    final JMenu mysql = new JMenu(OrbadaMySQLPlugin.driverType);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        mysql.add(OrbadaMySQLPlugin.findObjectAction);

        ComponentActionProvider[] capa = application.getServiceArray(ComponentActionProvider.class);
        if (capa != null && capa.length > 0) {
          boolean first = true;
          for (ComponentActionProvider cap : capa) {
            if (cap.isForComponent(accesibilities.getDatabase(), OrbadaMySQLPlugin.specjalMySQLActions)) {
              cap.setDatabase(accesibilities.getDatabase());
              if (first) {
                mysql.addSeparator();
              }
              mysql.add(cap);
              first = false;
            }
          }
        }
      }
    });
    accesibilities.addMenu(mysql);

    JToolBar toolBar = new JToolBar(OrbadaMySQLPlugin.driverType);
    toolBar.add(new ToolButton(OrbadaMySQLPlugin.findObjectAction));
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
