/*
 * BeanShellInterpreter.java
 *
 * Created on 2007-11-09, 20:01:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class BeanShellInterpreter extends Interpreter {
  
  private Database database;
  private IApplication application;
  
  public BeanShellInterpreter(Database database) {
    super();
    this.database = database;
    this.application = Application.get();
    init();
  }
  
  private void init() {
    try {
      set("self", this);
      set("database", database);
      set("application", application);
      eval("addClassPath(self.getClass().getResource(\"/pl/mpak/orbada/beanshell/commands/\"))");
      eval("importCommands(\"\");");
    } catch (EvalError ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void clearResult() {
    
  }
  
}
