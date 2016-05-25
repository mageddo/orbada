package pl.mpak.usedb.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.EventObject;

import pl.mpak.usedb.core.Database;

public class SimpleSQLScript extends SQLScript {

  private StringBuffer errors;
  
  public SimpleSQLScript(Database database) {
    super(database);
    errors = new StringBuffer();
    addScriptListener(new ScriptListener() {
      public void beforeScript(EventObject e) {
        errors.setLength(0);
      }
      public void beforeCommand(EventObject e) {
      }
      public void afterCommand(EventObject e) {
      }
      public void beforeQuery(EventObject e) {
      }
      public void afterQuery(EventObject e) {
      }
      public boolean errorOccured(ErrorScriptEventObject e) {
        if (errors.length() == 0) {
          errors.append("\n");
        }
        errors.append(((Exception)e.getSource()).getMessage());
        errors.append("\n" +e.getSqlText());
        return false;
      }
      public void afterScript(EventObject e) {
      }
    });
  }
  
  public String getErrors() {
    return errors.toString();
  }
  
  public boolean executeScript(String sqlScript) {
    try {
      execute(new BufferedReader(new StringReader(sqlScript)));
      if (errors.length() == 0) {
        return true;
      }
      return false;
    }
    catch (IOException ex) {
      if (errors.length() == 0) {
        errors.append("\n");
      }
      errors.append(ex.getMessage());
      return false;
    }
  }

}
