/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.tools;

import java.util.HashMap;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Tool;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ToolList {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private HashMap<String, Tool> toolMap;
  
  public ToolList() {
    toolMap = new HashMap<String, Tool>();
    reload();
  }
  
  public void reload() {
    toolMap.clear();
    if (InternalDatabase.get() != null) { 
      Query query = InternalDatabase.get().createQuery();
      try {
        query.setSqlText("select * from tools where to_usr_id = :to_usr_id");
        query.paramByName("to_usr_id").setString(Application.get().getUserId());
        query.open();
        while (!query.eof()) {
          Tool tool = new Tool(InternalDatabase.get());
          tool.updateFrom(query);
          toolMap.put(tool.getCommand().toUpperCase(), tool);
          query.next();
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
    }
  }
  
  public Tool get(String command) {
    return toolMap.get(command.toUpperCase());
  }
  
  public Tool[] toArray() {
    return toolMap.values().toArray(new Tool[toolMap.size()]);
  }
  
  public void exec(String command, Object[] args) {
    Tool tool = get(command);
    if (tool != null) {
      tool.exec(Application.get().getMainFrame().getActiveDatabase(), args);
    }
    else {
      MessageBox.show(Application.get().getMainFrame(), stringManager.getString("ToolList-program"), String.format(stringManager.getString("ToolList-no-program-to-call"), new Object[] {command}), ModalResult.OK, MessageBox.ERROR);
    }
  }
  
}
