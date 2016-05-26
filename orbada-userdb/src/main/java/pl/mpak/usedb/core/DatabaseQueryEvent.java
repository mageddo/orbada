package pl.mpak.usedb.core;

import java.util.EventObject;

public class DatabaseQueryEvent extends EventObject {
  private static final long serialVersionUID = 1L;

  private Query query;
  
  public DatabaseQueryEvent(Object source, Query query) {
    super(source);
    this.query = query;
  }
  
  public Query getQuery() {
    return query;
  }

}
