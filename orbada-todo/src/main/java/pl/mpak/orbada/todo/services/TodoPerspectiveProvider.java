package pl.mpak.orbada.todo.services;

import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.todo.OrbadaTodoPlugin;
import pl.mpak.orbada.todo.cm.NewTodoAction;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TodoPerspectiveProvider extends PerspectiveProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaTodoPlugin.class);

  public boolean isForDatabase(Database database) {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("TodoPerspectiveProvider-description");
  }

  public String getGroupName() {
    return OrbadaTodoPlugin.todoGroupName;
  }

  public void initialize() {
    accesibilities.addAction(new NewTodoAction(accesibilities.getDatabase()));
    
  }
  
}
