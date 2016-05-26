package pl.mpak.orbada.beanshell.services;

import java.util.ArrayList;
import pl.mpak.orbada.beanshell.OrbadaBeanshellPlugin;
import pl.mpak.orbada.beanshell.db.BshActionRecord;
import pl.mpak.orbada.beanshell.services.cm.BshComponentAction;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IProcessMessagable;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.task.Task;

/**
 *
 * @author akaluza
 */
public class BshComponentActionsProvider extends ComponentActionsProvider implements IProcessMessagable {

  private final StringManager stringManager = StringManagerFactory.getStringManager("beanshell");

  private ArrayList<BshActionRecord> actionList;
  private boolean inited = false;
  
  public BshComponentActionsProvider() {
    actionList = new ArrayList<BshActionRecord>();
  }
  
  @Override
  public void setApplication(IApplication application) {
    this.application = application;
    this.application.registerRequestMessager(this);
  }
  
  private void reloadActions() {
    synchronized (actionList) {
      actionList.clear();
      Query query = application.getOrbadaDatabase().createQuery();
      try {
        query.setSqlText("select * from bshactions where (bsha_usr_id is null or bsha_usr_id = :USR_ID)");
        query.paramByName("USR_ID").setString(application.getUserId());
        query.open();
        while (!query.eof()) {
          BshActionRecord action = new BshActionRecord(application.getOrbadaDatabase());
          action.updateFrom(query);
          actionList.add(action);
          query.next();
        }
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
    }
  }
  
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  public String getDescription() {
    return stringManager.getString("BshComponentActionsProvider-description");
  }

  public String getGroupName() {
    return OrbadaBeanshellPlugin.beanshellGroupName;
  }

  public void processMessage(PluginMessage message) {
    if (message.isMessageId(OrbadaBeanshellPlugin.bshActionsReloadMessage)) {
      application.getOrbadaDatabase().getTaskPool().addTask(new Task() {
        public void run() {
          reloadActions();
        }
      });
    }
  }

  @Override
  public ComponentAction[] getForComponent(Database database, String actionType) {
    ArrayList<ComponentAction> actions = new ArrayList<ComponentAction>();
    if (!inited) {
      reloadActions();
      inited = true;
    }
    synchronized (actionList) {
      for (BshActionRecord action : actionList) {
        if (actionType.equals(action.getKey()) && (StringUtil.isEmpty(action.getDtpId()) || action.getDtpId().equals(database.getUserProperties().get("dtp_id")))) {
          actions.add(new BshComponentAction(action));
        }
      }
    }
    if (actions.size() > 0) {
      return actions.toArray(new BshComponentAction[actions.size()]);
    }
    return null;
  }


}
