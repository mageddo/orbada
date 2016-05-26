/*
 * OrbadaSQLSyntaxDocument.java
 *
 * Created on 2007-10-27, 15:39:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.gui.comps;

import java.sql.ResultSet;

import orbada.Consts;
import orbada.core.Application;
import orbada.db.OrbadaDatabase;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxStyle;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskPool;

/**
 *
 * @author akaluza
 */
public class OrbadaSQLSyntaxDocument extends SQLSyntaxDocument {
  
  private Database database;
  private volatile boolean inited;
  
  public OrbadaSQLSyntaxDocument() {
    this(null);
  }
  
  public OrbadaSQLSyntaxDocument(Database database) {
    super();
    setDatabase(database);
  }
  
  public Database getDatabase() {
    return database;
  }
  
  private void revalidateEditor() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (getTextComponent() != null) {
          getTextComponent().revalidate();
          getTextComponent().repaint();
        }
      }
    });
  }
  
  private void addKeywords(String[] list, int type) {
    if (list != null && list.length > 0) {
      if (list.length == 1) {
        addKeyWord(list[0], type);
      }
      else {
        for (String s : list) {
          addKeyWord(s, type);
        }
      }
      if (getTextComponent() != null) {
        revalidateEditor();
      }
    }
  }
  
  public void refreshKeyWords() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        refreshKeyWords(false);
      }
    });
  }

  public void refreshKeyWords(final boolean resetInfo) {
    inited = true;
    boolean found = false;
    clearKeyWords();
    if (database != null && Application.get() != null) {
      DatabaseInfoProvider[] dip = Application.get().getServiceArray(DatabaseInfoProvider.class);
      if (dip != null && dip.length > 0) {
        for (int i=0; i<dip.length; i++) {
          if (dip[i].isForDatabase(database)) {
            if (resetInfo) {
              dip[i].resetDatabaseInfo(database);
            }
            found = true;
            final DatabaseInfoProvider dis = dip[i];
            TaskPool.getTaskPool("sql-syntax-colector").addTask(new Task("Orbada SQL Syntax Document Info Refresh") {
              Database runDatabase = database;
              {
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getDataTypes(runDatabase), DATA_TYPE);
                }
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getKeywords(runDatabase), KEYWORD);
                }
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getOperators(runDatabase), OPERATOR);
                }
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getSqlFunctions(runDatabase), SQL_FUNCTION);
                }
                revalidateEditor();
              }
              public void run() {
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getExceptions(runDatabase), ERROR);
                }
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getSchemas(runDatabase), SYSTEM_OBJECT);
                }
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getPublicTables(runDatabase), SYSTEM_OBJECT);
                }
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getUserTables(runDatabase), TABLE);
                }
                if (runDatabase.isConnected()) {
                  addKeywords(dis.getUserFunctions(runDatabase), USER_FUNCTION);
                }
              }
            });
          }
        }
      }
      if (!found) {
        defaultAll();
        ISettings localSettings = Application.get().getSettings(((OrbadaDatabase)database).getUserProperties().getProperty("schemaId"), Consts.orbadaSettings);
        if ((localSettings.getValue(Consts.useGlobalSettingsDisableLoadSqlSyntaxInfo, true) && !Application.get().getSettings().getValue(Consts.disableLoadSqlSyntaxInfo, false)) ||
            (!localSettings.getValue(Consts.useGlobalSettingsDisableLoadSqlSyntaxInfo, true) && !localSettings.getValue(Consts.disableLoadSqlSyntaxInfo, false))) {
          TaskPool.getTaskPool("sql-syntax-colector").addTask(new Task("Loading Objects For SQL Syntax Document") {
            public void run() {
              setPercenExecution(0);
              Query query = database.createQuery();
              try {
                query.setResultSet(database.getMetaData().getTables(null, database.getUserName() == null ? null : database.getUserName().toUpperCase(), null, new String[] {"TABLE", "VIEW"}));
                String[] tableList = QueryUtil.queryToArray("{TABLE_NAME}", query);
                setPercenExecution(getPercenExecution() +15);
                addKeywords(tableList, TABLE);
                setPercenExecution(getPercenExecution() +15);
                query.close();
                query.setResultSet(database.getMetaData().getTables(null, null, null, new String[] {"SYSTEM TABLE", "SYNONYM"}));
                tableList = QueryUtil.queryToArray("{TABLE_NAME}", query);
                setPercenExecution(getPercenExecution() +15);
                addKeywords(tableList, SYSTEM_OBJECT);
                setPercenExecution(getPercenExecution() +15);
                query.close();
                ResultSet rs = database.getMetaData().getProcedures(null, database.getUserName() == null ? null : database.getUserName().toUpperCase(), null);
                if (rs != null) {
                  query.setResultSet(rs);
                  String[] procList = QueryUtil.queryToArray("{PROCEDURE_NAME}", query);
                  addKeywords(procList, USER_FUNCTION);
                }
                setPercenExecution(getPercenExecution() +15);
                setPercenExecution(getPercenExecution() +15);
                query.close();
                if (database.getMetaData().supportsSchemasInTableDefinitions()) {
                  query.setResultSet(database.getMetaData().getSchemas());
                  String[] schemaList = QueryUtil.queryToArray("{TABLE_SCHEM}", query);
                  addKeywords(schemaList, SYSTEM_OBJECT);
                }
                setPercenExecution(getPercenExecution() +15);
                addKeywords(new String[] {"AND", "ALL", "ANY", "BETWEEN", "IN", "IS", "LIKE", "NOT", "OR", "EXISTS"}, OPERATOR);
              }
              catch (Exception ex) {
                ExceptionUtil.processException(ex);
              }
              finally {
                query.close();
                setPercenExecution(100);
              }
              revalidateEditor();
            }
          });
        }
        revalidateEditor();
      }
    }
    else {
      defaultAll();
      revalidateEditor();
    }
  }
  
  public void setDatabase(Database database) {
    if (this.database != database) {
      this.database = database;
      refreshKeyWords();
    }
  }

  void externalInitKeywords() {
    if (!inited) {
      defaultAll();
      revalidateEditor();
    }
  }
  
  public static void loadSettings(SyntaxDocument doc) {
    ISettings settings = Application.get().getSettings(Consts.sqlSyntaxSettings);
    for (SyntaxStyle style : doc.getStyleMap().values()) {
      String ss = settings.getValue(style.getName(), "");
      if (!"".equals(ss)) {
        try {
          style.fromString(ss);
        }
        catch (Exception ex) {
          ;
        }
      }
    }
  }
  
  public static void storeSettings(SyntaxDocument doc) {
    ISettings settings = Application.get().getSettings(Consts.sqlSyntaxSettings);
    for (SyntaxStyle style : doc.getStyleMap().values()) {
      settings.setValue(style.getName(), style.toString());
    }
    settings.store();
  }
  
}
