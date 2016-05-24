/*
 * UniversalActionProvider.java
 * 
 * Created on 2007-10-30, 20:38:37
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.providers;

import pl.mpak.orbada.plugins.providers.abs.ActionProvider;
import pl.mpak.orbada.universal.gui.ScriptResultPanel;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;

/**
 *
 * @author akaluza
 */
public abstract class UniversalActionProvider extends ActionProvider {

  protected IUniversalViewAccessibilities accessibilities;
  protected boolean commandTransformed = false;
  protected String oryginalCommand;
  protected ScriptResultPanel scriptResultPanel;
  
  public void setUniversalViewAccessibilities(IUniversalViewAccessibilities accessibilities) {
    this.accessibilities = accessibilities;
  }

  public void setCommandTransformed(boolean commandTransformed) {
    this.commandTransformed = commandTransformed;
  }

  public void setOryginalCommand(String oryginalCommand) {
    this.oryginalCommand = oryginalCommand;
  }
  
  public void setScriptResultPanel(ScriptResultPanel scriptResultPanel) {
    this.scriptResultPanel = scriptResultPanel;
  }

  public abstract boolean isForDatabase(Database database);

  public abstract boolean addToolButton();

  public abstract boolean addMenuItem();

  public abstract boolean addToEditor();
  
  public void beforeOpenQuery(Query query) {}

  public void afterOpenQuery(Query query) {}

  public void beforeCloseQuery(Query query) {}

  public void afterCloseQuery(Query query) {}

  public void queryError(Query query, Exception ex) {}

  public void beforeExecuteCommand(Command command) {}

  public void afterExecuteCommand(Command command) {}

  public void commandError(Command command, Exception ex) {}

}
