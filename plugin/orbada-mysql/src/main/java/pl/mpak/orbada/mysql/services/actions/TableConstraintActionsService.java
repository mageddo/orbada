/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.services.actions;

import java.util.ArrayList;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.cm.CreateForeignKeyTableConstraintAction;
import pl.mpak.orbada.mysql.cm.CreatePrimaryKeyTableConstraintAction;
import pl.mpak.orbada.mysql.cm.CreateUniqueTableConstraintAction;
import pl.mpak.orbada.mysql.cm.DropTableConstraintAction;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableConstraintActionsService extends ComponentActionsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  @Override
  public ComponentAction[] getForComponent(Database database, String actionType) {
    if (database == null || !OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      return null;
    }
    if (!"mysql-table-constraints-actions".equals(actionType)) {
      return null;
    }

    ArrayList<ComponentAction> actions = new ArrayList<ComponentAction>();

    actions.add(new CreatePrimaryKeyTableConstraintAction());
    actions.add(new CreateForeignKeyTableConstraintAction());
    actions.add(new CreateUniqueTableConstraintAction());
    actions.add(ComponentAction.Separator);
    actions.add(new DropTableConstraintAction());

    return actions.toArray(new ComponentAction[actions.size()]);
  }

  public String getDescription() {
    return stringManager.getString("TableConstraintActionsService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}
