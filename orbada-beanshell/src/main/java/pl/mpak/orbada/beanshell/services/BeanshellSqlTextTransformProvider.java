/*
 * BeanshellSqlTextTransformProvider.java
 * 
 * Created on 2007-10-31, 21:02:27
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.beanshell.services;

import bsh.EvalError;
import pl.mpak.orbada.beanshell.BeanShellInterpreter;
import pl.mpak.orbada.beanshell.OrbadaBeanshellPlugin;
import pl.mpak.orbada.universal.providers.UniversalSqlTextTransformProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class BeanshellSqlTextTransformProvider extends UniversalSqlTextTransformProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("beanshell");

  private BeanShellInterpreter interpreter;
  
  public BeanshellSqlTextTransformProvider() {
  }
  
  public boolean isForDatabase(Database database) {
    return true;
  }

  public String transformSqlText(Database database, String sqlText) {
    if (interpreter == null) {
      interpreter = new BeanShellInterpreter(database);
    }
    int start = sqlText.toUpperCase().indexOf("BEANSHELL");
    if (start >= 0) {
      while (start >= 0) {
        boolean found = false;
        int end = start +9;
        while (end < sqlText.length()) {
          char ch = sqlText.charAt(end);
          if (!Character.isSpaceChar(ch)) {
            break;
          }
          end++;
        }
        if (end < sqlText.length() && sqlText.charAt(end) == '{') {
          int rounds = 0;
          while (end < sqlText.length()) {
            char ch = sqlText.charAt(end);
            if (ch == '{') {
              rounds++;
            }
            else if (ch == '}') {
              if (--rounds == 0) {
                end++;
                found = true;
                break;
              }
            }
            else if (ch == '"') {
              end++;
              while (end < sqlText.length()) {
                if (sqlText.charAt(end) == '"' && sqlText.charAt(end -1) != '\\') {
                  break;
                }
                end++;
              }
            }
            end++;
          }
          if (found) {
            try {
              String toEval = sqlText.substring(start +9, end);
              toEval = toEval.substring(toEval.indexOf('{') +1, toEval.lastIndexOf('}'));
              Object result = interpreter.eval(toEval);
              if (start == 0) {
                if (result != null) {
                  MessageBox.show(stringManager.getString("result"), result.toString(), ModalResult.OK);
                }
                return "";
              }
              else if (result != null) {
                String newSqlText = sqlText.substring(0, start);
                newSqlText = newSqlText +result.toString();
                sqlText = newSqlText +sqlText.substring(end);
              }
            } catch (EvalError ex) {
              MessageBox.show(stringManager.getString("beanshell-error"), ex.getMessage(), ModalResult.OK);
              return "";
            }
          }
        }
        if (!found) {
          start = sqlText.toUpperCase().indexOf("BEANSHELL", start +9);
        }
        else {
          start = sqlText.toUpperCase().indexOf("BEANSHELL");
        }
      }
      return sqlText;
    }
    return null;
  }

  public String getDescription() {
    return stringManager.getString("BeanshellSqlTextTransformProvider-description");
  }

  public String getGroupName() {
    return OrbadaBeanshellPlugin.beanshellGroupName;
  }


}
