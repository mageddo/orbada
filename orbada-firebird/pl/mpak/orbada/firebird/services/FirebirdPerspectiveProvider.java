package pl.mpak.orbada.firebird.services;

import javax.swing.JMenu;
import javax.swing.JToolBar;
import orbada.Consts;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import pl.mpak.orbada.firebird.cm.CreateDomainAction;
import pl.mpak.orbada.firebird.cm.CreateExceptionAction;
import pl.mpak.orbada.firebird.cm.CreateProcedureAction;
import pl.mpak.orbada.firebird.cm.CreateSequenceAction;
import pl.mpak.orbada.firebird.cm.CreateTriggerAction;
import pl.mpak.orbada.firebird.gui.freezing.FreezeFactory;
import pl.mpak.orbada.firebird.gui.freezing.FreezeViewService;
import pl.mpak.orbada.plugins.IDatabaseObject;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
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
public class FirebirdPerspectiveProvider extends PerspectiveProvider {
  
  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaFirebirdPlugin.class);

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaFirebirdPlugin.firebirdDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("FirebirdPerspectiveProvider-description");
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }

  public void initialize() {
    //final String schemaName = OracleDbInfoProvider.getCurrentSchema(accesibilities.getDatabase());
    
    final JMenu wizards = new JMenu(stringManager.getString("FirebirdPerspectiveProvider-wizzards"));
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        wizards.add(new CreateProcedureAction(accesibilities.getDatabase()));
        wizards.add(new CreateTriggerAction(accesibilities.getDatabase()));
        wizards.addSeparator();
        wizards.add(new CreateSequenceAction(accesibilities.getDatabase()));
        wizards.add(new CreateDomainAction(accesibilities.getDatabase()));
        wizards.add(new CreateExceptionAction(accesibilities.getDatabase()));
      }
    });

    accesibilities.addMenu(wizards);
    
    JToolBar toolBar = new JToolBar("Firebird");
    toolBar.add(new ToolButton(OrbadaFirebirdPlugin.findObjectAction));
    toolBar.addSeparator();
    toolBar.add(new ToolButton(new CreateProcedureAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CreateTriggerAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CreateSequenceAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CreateDomainAction(accesibilities.getDatabase())));
    accesibilities.addToolBar(toolBar);
//    accesibilities.addAction(new SubmitJobAction(accesibilities.getDatabase()));
//    accesibilities.addAction(new CurrentSchemaAction(accesibilities.getDatabase()));
//    accesibilities.addAction(new IsolationLevelAction(accesibilities.getDatabase()));
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
