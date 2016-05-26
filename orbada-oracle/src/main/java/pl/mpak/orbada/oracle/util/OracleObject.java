/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.sql.Timestamp;
import javax.swing.Icon;
import pl.mpak.orbada.plugins.IDatabaseObject;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.SQLUtil;

/**
 *
 * @author akaluza
 */
public class OracleObject implements IDatabaseObject, Transferable {

  private Database database;
  private String schemaName;
  private String objectName;
  private String objectType;
  private Timestamp created;
  private String status;
  
  public OracleObject() {
    
  }
  
  public OracleObject(Database database, String schemaName, String objectName, String objectType, Timestamp created, String status) {
    this.database = database;
    this.schemaName = schemaName;
    this.objectName = objectName;
    this.objectType = objectType;
    this.created = created;
    this.status = status;
  }
  
  public OracleObject(Query query) throws Exception {
    this(
      query.getDatabase(),
      query.fieldByName("schema_name").getString(),
      query.fieldByName("object_name").getString(),
      query.fieldByName("object_type").getString(),
      query.fieldByName("created").getTimestamp(),
      query.fieldByName("status").getString()
      );
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }
  
  public String getSqlObjectName() {
    return SQLUtil.createSqlName(schemaName, objectName);
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public String getCatalogName() {
    return null;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
  public Icon getObjectIcon() {
    return OracleUtil.getObjectIcon(objectType);
  }

  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] {DataFlavor.stringFlavor};
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.isFlavorTextType();
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (database.getUserProperties().getProperty("schema-name", database.getUserName().toUpperCase()).equals(getSchemaName())) {
      return getObjectName();
    }
    return getSqlObjectName();
  }

}
