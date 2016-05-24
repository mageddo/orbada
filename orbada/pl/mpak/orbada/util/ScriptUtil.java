/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.usedb.script.SimpleSQLScript;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class ScriptUtil {

  public static boolean executeInternalScript(InputStream stream) {
    try {
      return executeInternalScript(StreamUtil.stream2String(stream));
    } catch (IOException ex) {
      ExceptionUtil.processException(ex);
      return false;
    }
  }
  
  public static boolean executeInternalScript(String sqlScript) {
    sqlScript = StringUtil.replaceString(sqlScript, "$(clob)", Application.get().getProperty("data.type.clob", "CLOB"));
    sqlScript = StringUtil.replaceString(sqlScript, "$(blob)", Application.get().getProperty("data.type.blob", "BLOB"));
    SimpleSQLScript script = new SimpleSQLScript(InternalDatabase.get());
    if (!script.executeScript(sqlScript)) {
      Logger.getLogger("error-logger").error(script.getErrors()); 
      return false;
    }
    return true;
  }
  
}
