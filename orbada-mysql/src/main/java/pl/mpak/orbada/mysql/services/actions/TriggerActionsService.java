/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.services.actions;

import java.util.ArrayList;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.cm.DropTriggerAction;
import pl.mpak.orbada.mysql.cm.TriggerFreezeAction;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TriggerActionsService extends ComponentActionsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  @Override
  public ComponentAction[] getForComponent(Database database, String actionType) {
    if (database == null || !OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      return null;
    }
    if (!"mysql-triggers-actions".equals(actionType)) {
      return null;
    }

    ArrayList<ComponentAction> actions = new ArrayList<ComponentAction>();

    actions.add(new TriggerFreezeAction());
    actions.add(ComponentAction.Separator);
    actions.add(new DropTriggerAction());

    return actions.toArray(new ComponentAction[actions.size()]);
  }

  public String getDescription() {
    return stringManager.getString("TriggerActionsService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}
