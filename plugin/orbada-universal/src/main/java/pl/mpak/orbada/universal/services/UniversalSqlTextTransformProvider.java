/*
 * DerbyDbSqlTextTransformProvider.java
 * 
 * Created on 2007-10-31, 20:10:41
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.services;

import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 * <p>Klasa dostawcza, s³u¿y do transformacji tabeli lub schematu.tabeli w zapytanie
 * select * from [schemat.]tabela
 * @author akaluza
 */
public class UniversalSqlTextTransformProvider extends pl.mpak.orbada.universal.providers.UniversalSqlTextTransformProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  public boolean isForDatabase(Database database) {
    return database != null;
  }

  public String transformSqlText(Database database, String sqlText) {
    if (sqlText.indexOf(' ') == -1) {
      DatabaseInfoProvider instance = null;
      DatabaseInfoProvider[] dip = application.getServiceArray(DatabaseInfoProvider.class);
      if (dip != null && dip.length > 0) {
        for (int i=0; i<dip.length; i++) {
          if (dip[i].isForDatabase(database)) {
            instance = dip[i];
            break;
          }
        }
      }
      if (instance != null) {
        String[] tables = StringUtil.unionList(instance.getUserTables(database), instance.getPublicTables(database));
        if (tables != null) {
          if (StringUtil.equalAnyOfString(sqlText, tables, true)) {
            return "select * from " +sqlText;
          }
          else if (sqlText.indexOf('.') >= 0) {
            String schema = sqlText.substring(0, sqlText.indexOf('.'));
            String table = sqlText.substring(sqlText.indexOf('.') +1);
            String[] schemas = instance.getSchemas(database);
            tables = instance.getPublicTables(database);
            if (StringUtil.equalAnyOfString(table, tables, true) && StringUtil.equalAnyOfString(schema, schemas, true)) {
              return "select * from " +sqlText;
            }
          }
        }
      }
    }
    return null;
  }

  public String getDescription() {
    return stringManager.getString("UniversalSqlTextTransformProvider-description");
  }

  public String getGroupName() {
    return OrbadaUniversalPlugin.universalGroupName;
  }
  

}
