/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.db;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author akaluza
 */
public class OlhObjectRecord extends DefaultBufferedRecord {

  public OlhObjectRecord(Database database) {
    super(database, "OLHOBJECTS", "OLHO_ID");
    add("OLHO_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("OLHO_SCH_ID", VariantType.varString, true);
    add("OLHO_CREATED", VariantType.varTimestamp, true);
    add("OLHO_OBJECT_SCHEMA", VariantType.varString, true);
    add("OLHO_OBJECT_TYPE", VariantType.varString, true);
    add("OLHO_OBJECT_NAME", VariantType.varString, true);
    add("OLHO_SOURCE", VariantType.varString, true);
    add("OLHO_DESCRIPTION", VariantType.varString, true);
  }
  
  public OlhObjectRecord(Database database, String osm_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(osm_id));
  }
  
  public String getId() {
    return fieldByName("OLHO_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("OLHO_ID").setString(id);
  }
  
  public String getSchId() {
    return fieldByName("OLHO_SCH_ID").getValue().toString();
  }
  
  public void setSchId(String id) {
    fieldByName("OLHO_SCH_ID").setString(id);
  }
  
  public Long getCreated() {
    try {
      return fieldByName("OLHO_CREATED").getLong();
    } catch (VariantException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
  public void setCreated(Long created) {
    fieldByName("OLHO_CREATED").setLong(created);
  }
  
  public void setCreated(String created) {
    fieldByName("OLHO_CREATED").setString(created);
  }
  
  public String getObjectSchema() {
    return fieldByName("OLHO_OBJECT_SCHEMA").getValue().toString();
  }
  
  public void setObjectSchema(String schemaName) {
    fieldByName("OLHO_OBJECT_SCHEMA").setString(schemaName);
  }
  
  public String getObjectType() {
    return fieldByName("OLHO_OBJECT_TYPE").getValue().toString();
  }
  
  public void setObjectType(String objectType) {
    fieldByName("OLHO_OBJECT_TYPE").setString(objectType);
  }
  
  public String getObjectName() {
    return fieldByName("OLHO_OBJECT_NAME").getValue().toString();
  }
  
  public void setObjectName(String objectName) {
    fieldByName("OLHO_OBJECT_NAME").setString(objectName);
  }
  
  public String getSource() {
    return fieldByName("OLHO_SOURCE").getValue().toString();
  }
  
  public void setSource(String source) {
    fieldByName("OLHO_SOURCE").setString(source);
  }
  
  public String getDescription() {
    return fieldByName("OLHO_DESCRIPTION").getValue().toString();
  }
  
  public void setDescription(String description) {
    fieldByName("OLHO_DESCRIPTION").setString(description);
  }
  
  @Override
  public void applyInsert() throws Exception {
    if (!fieldByName("OLHO_CREATED").isChanged()) {
      fieldByName("OLHO_CREATED").setValue(new Variant(System.currentTimeMillis()));
    }
    super.applyInsert();
  }
  
  @Override
  public String toString() {
    return getObjectType() +" " +SQLUtil.createSqlName(getObjectSchema(), getObjectName());
  }
  
}
