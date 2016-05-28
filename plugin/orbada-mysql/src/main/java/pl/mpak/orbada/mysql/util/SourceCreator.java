/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.util;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.mysql.Sql;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
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
   * <p>Pozwala pobraæ Ÿród³o i jeœli zosta³ ustawiony textArea to aktualizuje go
   * @param databaseName
   * @param objectType FUNCTION, PROCEDURE, etc ale mo¿e te¿ byæ CALL czyli wywo³anie funkcji/procedury
   * @param objectName w przypadku CALL i pakietu/typu powinien zawieraæ pe³n¹ nazwê wraz z nazw¹ pakietu/typu
   * @return
   */
  public String getSource(final String databaseName, final String objectType, final String objectName) {
    final String source;
    final PleaseWait wait;
    if (textArea != null) {
      wait = PleaseWait.createSqlWait();
      Application.get().startPleaseWait(wait);
    }
    else {
      wait = null;
    }
    try {
      if (!StringUtil.equals(objectName, "")) {
        if ("TRIGGER".equalsIgnoreCase(objectType)) {
          source = getTrigger(databaseName, objectName);
        }
        else if ("TABLE".equalsIgnoreCase(objectType)) {
          source = getTable(databaseName, objectName);
        }
        else if ("VIEW".equalsIgnoreCase(objectType)) {
          source = getView(databaseName, objectName);
        }
        else if ("PROCEDURE".equalsIgnoreCase(objectType)) {
          source = getProcedure(databaseName, objectName);
        }
        else if ("FUNCTION".equalsIgnoreCase(objectType)) {
          source = getFunction(databaseName, objectName);
        }
        else {
          source = "";
        }
      }
      else {
        source = "";
      }
    }
    finally {
      if (wait != null) {
        Application.get().stopPleaseWait(wait);
      }
    }
    if (textArea != null) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          if (textArea instanceof AbsOrbadaSyntaxTextArea) {
            ((AbsOrbadaSyntaxTextArea)textArea).setDatabaseObject(databaseName, objectType, objectName, source);
          }
          textArea.getEditorArea().setCaretPosition(0);
        }
      });
    }
    return source;
  }

  private String getTrigger(final String databaseName, final String objectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getTriggersSource(databaseName, objectName));
      query.open();
      if (!query.eof()) {
        return 
          "DROP TRIGGER " +database.quoteName(databaseName, objectName) +"\n/\n" +
          query.fieldByName("SQL Original Statement").getString() +"\n/";
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getTable(final String databaseName, final String objectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getTableSource(databaseName, objectName));
      query.open();
      if (!query.eof()) {
        return query.fieldByName("Create Table").getString();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getView(final String databaseName, final String objectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getViewSource(databaseName, objectName));
      query.open();
      if (!query.eof()) {
        StringBuilder source = new StringBuilder(query.fieldByName("Create View").getString());
        if (StringUtil.charCount(source.toString(), '\n') == 0) {
          int index = SQLUtil.indexOf(source.toString(), "create ", 0, true);
          if (index == 0) {
            source.delete(index, "create".length());
            source.insert(index, "CREATE OR REPLACE");
          }
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), "select ", index, true)) != -1) {source.insert(index, "\n"); index += 2;}
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), " from ", index, true)) != -1) {source.insert(index, "\n "); index += 3;}
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), " where ", index, true)) != -1) {source.insert(index, "\n"); index += 2;}
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), " union ", index, true)) != -1) {source.insert(index, "\n"); index += 2;}
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), " and ", index, true)) != -1) {source.insert(index, "\n  "); index += 4;}
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), " group by ", index, true)) != -1) {source.insert(index, "\n"); index += 2;}
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), "having ", index, true)) != -1) {source.insert(index, "\n"); index += 2;}
          index = 0;
          while ((index = SQLUtil.indexOf(source.toString(), " order by ", index, true)) != -1) {source.insert(index, "\n"); index += 2;}
        }
        return source.toString();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getProcedure(final String databaseName, final String objectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getProcedureSource(databaseName, objectName));
      query.open();
      if (!query.eof()) {
        return
          "DROP PROCEDURE IF EXISTS " +database.quoteName(databaseName, objectName) +"\n/\n" +
          query.fieldByName("Create Procedure").getString() +"\n/";
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

  private String getFunction(final String databaseName, final String objectName) {
    Query query = database.createQuery();
    try {
      query.setSqlText(Sql.getFunctionSource(databaseName, objectName));
      query.open();
      if (!query.eof()) {
        return
          "DROP FUNCTION IF EXISTS " +database.quoteName(databaseName, objectName) +"\n/\n" +
          query.fieldByName("Create Function").getString() +"\n/";
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return "";
  }

}
