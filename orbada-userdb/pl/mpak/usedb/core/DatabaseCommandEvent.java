package pl.mpak.usedb.core;

import java.util.EventObject;

public class DatabaseCommandEvent extends EventObject {
  private static final long serialVersionUID = 1L;

  private Command comamnd;
  
  public DatabaseCommandEvent(Object source, Command comamnd) {
    super(source);
    this.comamnd = comamnd;
  }
  
  public Command getCommand() {
    return comamnd;
  }

}
