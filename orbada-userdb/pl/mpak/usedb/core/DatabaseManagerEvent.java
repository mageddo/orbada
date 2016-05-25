package pl.mpak.usedb.core;

import java.util.EventObject;

public class DatabaseManagerEvent extends EventObject {
  private static final long serialVersionUID = 7189152078057595501L;

  private Database database;
  
  public DatabaseManagerEvent(Object source, Database database) {
    super(source);
    this.database = database;
  }

  public Database getDatabase() {
    return database;
  }
  
}
