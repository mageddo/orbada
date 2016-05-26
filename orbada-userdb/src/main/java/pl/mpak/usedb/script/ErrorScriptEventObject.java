package pl.mpak.usedb.script;

import java.util.EventObject;

public class ErrorScriptEventObject extends EventObject {
  private static final long serialVersionUID = -7163590134295543366L;

  private String sqlText;
  
  public ErrorScriptEventObject(Object source, String sqlText) {
    super(source);
    this.sqlText = sqlText;
  }

  public String getSqlText() {
    return sqlText;
  }

}
