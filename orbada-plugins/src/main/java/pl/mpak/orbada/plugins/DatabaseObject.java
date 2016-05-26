/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import javax.swing.Icon;
import pl.mpak.usedb.util.SQLUtil;

/**
 *
 * @author akaluza
 */
public class DatabaseObject implements IDatabaseObject {

  private String catalogName;
  private String schemaName;
  private String objectName;
  private String objectType;
  private String status;

  public DatabaseObject(String catalogName, String schemaName, String objectName, String objectType, String status) {
    this.catalogName = catalogName;
    this.schemaName = schemaName;
    this.objectName = objectName;
    this.objectType = objectType;
    this.status = status;
  }

  public DatabaseObject(String schemaName, String objectName, String objectType) {
    this(null, schemaName, objectName, objectType, null);
  }

  public String getSqlObjectName() {
    return SQLUtil.createSqlName(catalogName, schemaName, objectName);
  }

  public String getCatalogName() {
    return catalogName;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public String getObjectName() {
    return objectName;
  }

  public String getObjectType() {
    return objectType;
  }

  public String getStatus() {
    return status;
  }

  public Icon getObjectIcon() {
    return null;
  }

}
