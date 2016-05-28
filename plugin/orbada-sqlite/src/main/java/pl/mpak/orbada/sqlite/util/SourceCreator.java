package pl.mpak.orbada.sqlite.util;

import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.sqlite.Sql;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class SourceCreator {

  private Database database;
  private SyntaxTextArea textArea;
  
  public SourceCreator(Database database) {
    this.database = database;
  }
  
  public SourceCreator(Database database, SyntaxTextArea textArea) {
    this(database);
    this.textArea = textArea;
  }
  
  /**
   * <p>Pozwala pobraæ Ÿród³o PL/SQL lub DLL i jeœli zosta³ ustawiony textArea to aktualizuje go
   * @param schemaName
   * @param objectType
   * @param objectName
   * @return pobrane Ÿród³o
   */
  public String getSource(final String databaseName, final String objectType, final String objectName) {
    final String source = createSource(databaseName, objectType, objectName);
    if (textArea != null) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          if (textArea instanceof AbsOrbadaSyntaxTextArea) {
            ((OrbadaSyntaxTextArea)textArea).setDatabaseObject(databaseName, objectType, objectName, source);
          }
          textArea.getEditorArea().setCaretPosition(0);
        }
      });
    }
    return source;
  }
  
  private String createSource(String databaseName, String objectType, String objectName) {
    Query source = database.createQuery();
    try {
      source.setSqlText(Sql.getObjectSource(databaseName, objectType.toLowerCase(), objectName));
      source.open();
      if (!source.eof()) {
        String text = source.fieldByName("sql").getString();
        source.close();
        if (!StringUtil.isEmpty(text)) {
          text = text.replaceFirst("(" +objectName +")|(\"" +objectName +")\"", database.quoteName(databaseName, objectName));
          if (StringUtil.equalsIgnoreCase(objectType, "view")) {
            text = "DROP VIEW " +database.quoteName(databaseName, objectName) +"\n/\n" +text +"\n/\n";
          }
          else if (StringUtil.equalsIgnoreCase(objectType, "trigger")) {
            text = "DROP TRIGGER " +database.quoteName(databaseName, objectName) +"\n/\n" +text +"\n/\n";
          }
          else if (StringUtil.equalsIgnoreCase(objectType, "table")) {
            text = text +"\n/\n" +createIndexSource(databaseName, objectName);
          }
        }
        return text;
      }
      return "";
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      source.close();
    }
    return "";
  }

  private String createIndexSource(String databaseName, String tableName) {
    Query source = database.createQuery();
    try {
      StringBuilder sb = new StringBuilder();
      source.setSqlText(Sql.getIndexListSource(databaseName, tableName));
      source.open();
      while (!source.eof()) {
        String text = source.fieldByName("sql").getString();
        String name = source.fieldByName("name").getString();
        if (!StringUtil.isEmpty(text)) {
          text = text.replaceFirst("(" +name +")|(\"" +name +")\"", database.quoteName(databaseName, name));
        }
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append(text);
        sb.append("\n/");
        return sb.toString();
      }
      return "";
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      source.close();
    }
    return "";
  }
  
}
