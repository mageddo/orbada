/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.services.actions;

import java.util.ArrayList;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.cm.CopyTableColumnsAction;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ViewColumnActionsService extends ComponentActionsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  @Override
  public ComponentAction[] getForComponent(Database database, String actionType) {
    if (database == null || !OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      return null;
    }
    if (!"mysql-view-columns-actions".equals(actionType)) {
      return null;
    }

    ArrayList<ComponentAction> actions = new ArrayList<ComponentAction>();

    actions.add(new CopyTableColumnsAction());

    return actions.toArray(new ComponentAction[actions.size()]);
  }

  public String getDescription() {
    return stringManager.getString("ViewColumnActionsService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}
