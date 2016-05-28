/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.gui.util.helpers;

import java.util.Date;

/**
 *
 * @author akaluza
 */
public class TableInfo {

  private String databaseName;
  private String name;
  private String tableType;
  private String engine;
  private String rowFormat;
  private Date created;
  private Date updated;
  private String comment;

  public TableInfo() {
  }

  public TableInfo(String databaseName, String name, String tableType, String engine, String rowFormat, Date created, Date updated, String comment) {
    this.databaseName = databaseName;
    this.name = name;
    this.tableType = tableType;
    this.engine = engine;
    this.rowFormat = rowFormat;
    this.created = created;
    this.updated = updated;
    this.comment = comment;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public String getRowFormat() {
    return rowFormat;
  }

  public void setRowFormat(String rowFormat) {
    this.rowFormat = rowFormat;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTableType() {
    return tableType;
  }

  public void setTableType(String tableType) {
    this.tableType = tableType;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  @Override
  public String toString() {
    return name;
  }
  
}
