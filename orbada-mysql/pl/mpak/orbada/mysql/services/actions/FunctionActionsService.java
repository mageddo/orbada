/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.services.actions;

import pl.mpak.orbada.mysql.cm.DropFunctionAction;
import java.util.ArrayList;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.cm.CreateFunctionAction;
import pl.mpak.orbada.mysql.cm.FunctionFreezeAction;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FunctionActionsService extends ComponentActionsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  @Override
  public ComponentAction[] getForComponent(Database database, String actionType) {
    if (database == null || !OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      return null;
    }
    if (!"mysql-functions-actions".equals(actionType)) {
      return null;
    }

    ArrayList<ComponentAction> actions = new ArrayList<ComponentAction>();

    actions.add(new CreateFunctionAction());
    actions.add(new FunctionFreezeAction());
    actions.add(ComponentAction.Separator);
    actions.add(new DropFunctionAction());

    return actions.toArray(new ComponentAction[actions.size()]);
  }

  public String getDescription() {
    return stringManager.getString("FunctionActionsService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}
