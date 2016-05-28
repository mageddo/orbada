/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlite;

import pl.mpak.usedb.util.SQLUtil;

/**
 *
 * @author akaluza
 */
public class Sql {

  public static String getCatalogList() {
    return "PRAGMA DATABASE_LIST";
  }

  public static String getTableList(String database) {
    return "select name, '" +database +"' database from " +SQLUtil.createSqlName(database, "sqlite_master") +" where type = 'table' order by name";
  }
  
  public static String getViewList(String database) {
    return "select name, '" +database +"' database from " +SQLUtil.createSqlName(database, "sqlite_master") +" where type = 'view' order by name";
  }

  public static String getTriggerList(String database) {
    return "select name, tbl_name, '" +database +"' database from " +SQLUtil.createSqlName(database, "sqlite_master") +" where type = 'trigger' order by name";
  }

  public static String getColumnList(String database, String table) {
    return "PRAGMA " +SQLUtil.createSqlName(database, "TABLE_INFO") +"(" +SQLUtil.createSqlName(table) +")";
  }

  public static String getIndexList(String database, String table) {
    return "PRAGMA " +SQLUtil.createSqlName(database, "INDEX_LIST") +"(" +SQLUtil.createSqlName(table) +")";
  }

  public static String getReferencesList(String database, String table) {
    return "PRAGMA " +SQLUtil.createSqlName(database, "FOREIGN_KEY_LIST") +"(" +SQLUtil.createSqlName(table) +")";
  }

  public static String getTableTriggerList(String database, String table) {
    return "select name, tbl_name, '" +database +"' database from " +SQLUtil.createSqlName(database, "sqlite_master") +" where type = 'trigger' and tbl_name = '" +table +"' order by name";
  }

  public static String getObjectSource(String database, String type, String object) {
    return "select sql from " +SQLUtil.createSqlName(database, "sqlite_master") +" where type = '" +type +"' and name = '" +object +"'";
  }

  public static String getIndexListSource(String database, String table) {
    return "select name, sql from " +SQLUtil.createSqlName(database, "sqlite_master") +" where type = 'index' and tbl_name = '" +table +"' and sql is not null";
  }

}
