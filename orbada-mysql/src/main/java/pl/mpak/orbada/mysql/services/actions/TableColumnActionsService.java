/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.services.actions;

import java.util.ArrayList;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.cm.CommentTableColumnAction;
import pl.mpak.orbada.mysql.cm.CopyTableColumnsAction;
import pl.mpak.orbada.mysql.cm.CreateForeignKeyColumnConstraintAction;
import pl.mpak.orbada.mysql.cm.CreatePrimaryKeyColumnConstraintAction;
import pl.mpak.orbada.mysql.cm.DefaultValueTableColumnAction;
import pl.mpak.orbada.mysql.cm.DropTableColumnAction;
import pl.mpak.orbada.mysql.cm.NullNotNullTableColumnAction;
import pl.mpak.orbada.mysql.cm.RenameTableColumnAction;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TableColumnActionsService extends ComponentActionsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  @Override
  public ComponentAction[] getForComponent(Database database, String actionType) {
    if (database == null || !OrbadaMySQLPlugin.driverType.equals(database.getDriverType())) {
      return null;
    }
    if (!"mysql-table-columns-actions".equals(actionType)) {
      return null;
    }

    ArrayList<ComponentAction> actions = new ArrayList<ComponentAction>();

    actions.add(new CopyTableColumnsAction());
    actions.add(ComponentAction.Separator);
    actions.add(new CommentTableColumnAction());
    actions.add(new NullNotNullTableColumnAction());
    actions.add(new DefaultValueTableColumnAction());
    actions.add(new RenameTableColumnAction());
    actions.add(ComponentAction.Separator);
    actions.add(new CreatePrimaryKeyColumnConstraintAction());
    actions.add(new CreateForeignKeyColumnConstraintAction());
    actions.add(ComponentAction.Separator);
    actions.add(new DropTableColumnAction());

    return actions.toArray(new ComponentAction[actions.size()]);
  }

  public String getDescription() {
    return stringManager.getString("TableColumnActionsService-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }

}
