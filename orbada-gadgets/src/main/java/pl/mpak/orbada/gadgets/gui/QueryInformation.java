/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gadgets.gui;

import java.io.Serializable;
import pl.mpak.orbada.gadgets.OrbadaGadgetsPlugin;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class QueryInformation implements Serializable, Cloneable {

  private StringManager stringManager = StringManagerFactory.getStringManager("gadgets");

  private transient String info = "";
  private transient long lastTime;
  private transient Database database;
  private String sqlText;
  private int interval;
  
  public QueryInformation() {
    lastTime = System.currentTimeMillis();
  }

  public QueryInformation(Database database, String sqlText, int interval) {
    this.database = database;
    this.sqlText = sqlText;
    this.interval = interval;
    lastTime = 0L;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  public Database getDatabase() {
    return database;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public String getSqlText() {
    return sqlText;
  }

  public void setSqlText(String sqlText) {
    this.sqlText = sqlText;
  }
  
  public boolean isChange() {
    return System.currentTimeMillis() >= lastTime +(interval *1000L);
  }

  public String getInfo() {
    if (System.currentTimeMillis() >= lastTime +(interval *1000L)) {
      lastTime = System.currentTimeMillis();
      StringBuffer sb = new StringBuffer();
      Query query = database.createQuery();
      try {
        query.open(sqlText);
        if (query.getFieldCount() >= 2) {
          while (!query.eof()) {
            String trAttr = null;
            if (query.findFieldByName("attr") != null) {
              trAttr = query.fieldByName("attr").getString();
            }
            sb.append("<tr" +(trAttr != null ? " " +trAttr : "") +"><td align=right>" +query.getField(0).getString() +"</td><td>" +query.getField(1).getString() +"</td></tr>\n");
            query.next();
          }
        }
        else {
          sb.append(stringManager.getString("wrong-number-of-columns-in-the-result"));
        }
      }
      catch (Exception ex) {
        sb.append(ex.getMessage());
      }
      finally {
        query.close();
      }
      info = sb.toString();
    }
    return info;
  }
  
  @Override
  public String toString() {
    return SQLUtil.removeWhiteSpaces(sqlText);
  }
  
  @Override
  public Object clone() {
    return new QueryInformation(database, sqlText, interval);
  }

}
