/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.services.actions;

import java.util.ArrayList;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.cm.CommentTableAction;
import pl.mpak.orbada.mysql.cm.CopyTableAsTableAction;
import pl.mpak.orbada.mysql.cm.DropTableAction;
import pl.mpak.orbada.mysql.cm.OrderByTableAction;
import pl.mpak.orbada.mysql.cm.RenameTableAction;
import pl.mpak.orbada.mysql.cm.TableFreezeAction;
import pl.mpak.orbada.mysql.cm.TruncateTableAction;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableActionsService extends ComponentActionsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  @Override
  public ComponentAction[] getForComponent(Database database, String actionType) {
    if (database == null || !OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      return null;
    }
    if (!"mysql-tables-actions".equals(actionType)) {
      return null;
    }

    ArrayList<ComponentAction> actions = new ArrayList<ComponentAction>();

    actions.add(new TableFreezeAction());
    actions.add(new CommentTableAction());
    actions.add(new CopyTableAsTableAction());
    actions.add(new OrderByTableAction());
    actions.add(ComponentAction.Separator);
    actions.add(new RenameTableAction());
    actions.add(ComponentAction.Separator);
    actions.add(new TruncateTableAction());
    actions.add(new DropTableAction());

    return actions.toArray(new ComponentAction[actions.size()]);
  }

  public String getDescription() {
    return stringManager.getString("TableActionsService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}
