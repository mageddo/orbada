package pl.mpak.orbada.oracle.services;

import javax.swing.JMenu;
import javax.swing.JToolBar;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.cm.CompileAllObjectsAction;
import pl.mpak.orbada.oracle.cm.CopyTableAction;
import pl.mpak.orbada.oracle.cm.CopyViewAsTableAction;
import pl.mpak.orbada.oracle.cm.CreateDbLinkAction;
import pl.mpak.orbada.oracle.cm.CreateExternalTableAction;
import pl.mpak.orbada.oracle.cm.CreateFunctionAction;
import pl.mpak.orbada.oracle.cm.CreateIndexAction;
import pl.mpak.orbada.oracle.cm.CreateJavaSourceAction;
import pl.mpak.orbada.oracle.cm.CreateNormalTableAction;
import pl.mpak.orbada.oracle.cm.CreateObjectTypeAction;
import pl.mpak.orbada.oracle.cm.CreatePackageAction;
import pl.mpak.orbada.oracle.cm.CreateProcedureAction;
import pl.mpak.orbada.oracle.cm.CreateSequenceAction;
import pl.mpak.orbada.oracle.cm.CreateSynonymAction;
import pl.mpak.orbada.oracle.cm.CreateTableTriggerAction;
import pl.mpak.orbada.oracle.cm.CreateTableTypeAction;
import pl.mpak.orbada.oracle.cm.CreateTemporaryTableAction;
import pl.mpak.orbada.oracle.cm.CreateTriggerPKColumnAction;
import pl.mpak.orbada.oracle.cm.CreateVarrayTypeAction;
import pl.mpak.orbada.oracle.cm.CreateViewTriggerAction;
import pl.mpak.orbada.oracle.cm.CurrentSchemaAction;
import pl.mpak.orbada.oracle.cm.CursorSharingAction;
import pl.mpak.orbada.oracle.cm.DdlWaitForLocksAction;
import pl.mpak.orbada.oracle.cm.IsolationLevelAction;
import pl.mpak.orbada.oracle.cm.PlSqlCcFlagsAction;
import pl.mpak.orbada.oracle.cm.PlSqlCodeTypeAction;
import pl.mpak.orbada.oracle.cm.PlSqlDebugAction;
import pl.mpak.orbada.oracle.cm.PlSqlOptimizeLevelAction;
import pl.mpak.orbada.oracle.cm.PlSqlWarningsAction;
import pl.mpak.orbada.oracle.cm.QueryRewriteEnabledAction;
import pl.mpak.orbada.oracle.cm.QueryRewriteIntegrityAction;
import pl.mpak.orbada.oracle.cm.SqlTraceAction;
import pl.mpak.orbada.oracle.cm.StarTransformationEnabledAction;
import pl.mpak.orbada.oracle.cm.StatisticsLevelAction;
import pl.mpak.orbada.oracle.cm.SubmitJobAction;
import pl.mpak.orbada.oracle.gui.freezing.FreezeFactory;
import pl.mpak.orbada.oracle.gui.freezing.FreezeViewService;
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
public class OraclePerspectiveProvider extends PerspectiveProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("OraclePerspectiveProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
  
  public void initialize() {
    //final String schemaName = OracleDbInfoProvider.getCurrentSchema(accesibilities.getDatabase());
    
    final JMenu wizards = new JMenu(stringManager.getString("wizzards"));
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        wizards.add(new CreateNormalTableAction(accesibilities.getDatabase()));
        wizards.add(new CreateExternalTableAction(accesibilities.getDatabase()));
        wizards.add(new CreateTemporaryTableAction(accesibilities.getDatabase()));
        wizards.addSeparator();
        wizards.add(new CopyTableAction(accesibilities.getDatabase()));
        wizards.add(new CopyViewAsTableAction(accesibilities.getDatabase()));
        wizards.add(new SubmitJobAction(accesibilities.getDatabase()));
        wizards.addSeparator();
        wizards.add(new CreateTriggerPKColumnAction(accesibilities.getDatabase()));
        wizards.add(new CreateTableTriggerAction(accesibilities.getDatabase()));
        wizards.add(new CreateViewTriggerAction(accesibilities.getDatabase()));
        wizards.addSeparator();
        wizards.add(new CreateIndexAction(accesibilities.getDatabase()));
        wizards.addSeparator();
        wizards.add(new CreateSequenceAction(accesibilities.getDatabase()));
        wizards.add(new CreateSynonymAction(accesibilities.getDatabase()));
        wizards.add(new CreateDbLinkAction(accesibilities.getDatabase()));
        wizards.addSeparator();
        wizards.add(new CreateObjectTypeAction(accesibilities.getDatabase()));
        wizards.add(new CreateTableTypeAction(accesibilities.getDatabase()));
        wizards.add(new CreateVarrayTypeAction(accesibilities.getDatabase()));
        wizards.add(new CreatePackageAction(accesibilities.getDatabase()));
        wizards.add(new CreateJavaSourceAction(accesibilities.getDatabase()));
        wizards.add(new CreateProcedureAction(accesibilities.getDatabase()));
        wizards.add(new CreateFunctionAction(accesibilities.getDatabase()));
        wizards.addSeparator();
        wizards.add(new CompileAllObjectsAction(accesibilities.getDatabase()));
        
        ComponentActionProvider[] capa = application.getServiceArray(ComponentActionProvider.class);
        if (capa != null && capa.length > 0) {
          boolean first = true;
          for (ComponentActionProvider cap : capa) {
            if (cap.isForComponent(accesibilities.getDatabase(), OrbadaOraclePlugin.specjalOracleWizardsActions)) {
              cap.setDatabase(accesibilities.getDatabase());
              if (first) {
                wizards.addSeparator();
              }
              wizards.add(cap);
              first = false;
            }
          }
        }
      }
    });

    final JMenu oracle = new JMenu(OrbadaOraclePlugin.oracleDriverType);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        oracle.add(OrbadaOraclePlugin.findObjectAction);
        oracle.add(OrbadaOraclePlugin.callObjectAction);
        oracle.addSeparator();
        oracle.add(new PlSqlDebugAction(accesibilities.getDatabase()));
        oracle.add(new PlSqlCodeTypeAction(accesibilities.getDatabase()));
        oracle.add(new PlSqlOptimizeLevelAction(accesibilities.getDatabase()));
        oracle.add(new PlSqlWarningsAction(accesibilities.getDatabase()));
        oracle.add(new PlSqlCcFlagsAction(accesibilities.getDatabase()));
        oracle.addSeparator();
        oracle.add(new QueryRewriteEnabledAction(accesibilities.getDatabase()));
        oracle.add(new QueryRewriteIntegrityAction(accesibilities.getDatabase()));
        oracle.add(new CursorSharingAction(accesibilities.getDatabase()));
        oracle.add(new DdlWaitForLocksAction(accesibilities.getDatabase()));
        oracle.add(new SqlTraceAction(accesibilities.getDatabase()));
        oracle.add(new StarTransformationEnabledAction(accesibilities.getDatabase()));
        oracle.add(new StatisticsLevelAction(accesibilities.getDatabase()));
        oracle.addSeparator();
        oracle.add(new CurrentSchemaAction(accesibilities.getDatabase()));
        oracle.add(new IsolationLevelAction(accesibilities.getDatabase()));
        
        ComponentActionProvider[] capa = application.getServiceArray(ComponentActionProvider.class);
        if (capa != null && capa.length > 0) {
          boolean first = true;
          for (ComponentActionProvider cap : capa) {
            if (cap.isForComponent(accesibilities.getDatabase(), OrbadaOraclePlugin.specjalOracleActions)) {
              cap.setDatabase(accesibilities.getDatabase());
              if (first) {
                oracle.addSeparator();
              }
              oracle.add(cap);
              first = false;
            }
          }
        }
      }
    });

    accesibilities.addMenu(oracle);
    accesibilities.addMenu(wizards);
    
    JToolBar toolBar = new JToolBar(OrbadaOraclePlugin.oracleDriverType);
    toolBar.add(new ToolButton(OrbadaOraclePlugin.findObjectAction));
    toolBar.add(new ToolButton(OrbadaOraclePlugin.callObjectAction));
    toolBar.addSeparator();
    toolBar.add(new ToolButton(new CreateNormalTableAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CopyTableAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CreateTableTriggerAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new SubmitJobAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CreateSequenceAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CreateSynonymAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CreateDbLinkAction(accesibilities.getDatabase())));
    toolBar.addSeparator();
    toolBar.add(new ToolButton(new CurrentSchemaAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new IsolationLevelAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new CompileAllObjectsAction(accesibilities.getDatabase())));
    toolBar.add(new ToolButton(new PlSqlDebugAction(accesibilities.getDatabase())));
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
