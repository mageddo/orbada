/*
 * BeanShellEditorView.java
 * 
 * Created on 2007-11-08, 19:03:05
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.todo.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.todo.OrbadaTodoPlugin;
import pl.mpak.orbada.todo.gui.TodoPanelView;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TodoView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("todo");

  public Component createView(IViewAccesibilities accesibilities) {
    return new TodoPanelView(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("TodoView-public-name");
  }
  
  public String getViewId() {
    return "orbada-todo-view";
  }
  
  public Icon getIcon() {
    return new javax.swing.ImageIcon(getClass().getResource("/res/icons/tasks16.gif"));
  }

  public boolean isForDatabase(Database database) {
//    if (database == null) {
//      return true;
//    }
    return true;
  }

  public String getDescription() {
    return stringManager.getString("TodoView-description");
  }

  public String getGroupName() {
    return OrbadaTodoPlugin.todoGroupName;
  }
  
}
