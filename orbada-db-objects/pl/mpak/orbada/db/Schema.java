/*
 * Schema.java
 *
 * Created on 2007-10-13, 19:01:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class Schema extends DefaultBufferedRecord {
  
  private Icon icon;
  private Driver driver;

  public Schema(Database database) {
    super(database, "SCHEMAS", "SCH_ID");
    add("SCH_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("SCH_USR_ID", VariantType.varString, true);
    add("SCH_NAME", VariantType.varString, true);
    add("SCH_DRV_ID", VariantType.varString, true);
    add("SCH_HOST", VariantType.varString, true);
    add("SCH_DATABASE", VariantType.varString, true);
    add("SCH_USER", VariantType.varString, true);
    add("SCH_PASSWORD", VariantType.varString, true);
    add("SCH_PORT", VariantType.varInteger, true);
    add("SCH_URL", VariantType.varString, true);
    add("SCH_AUTO_COMMIT", VariantType.varString, true);
    add("SCH_SELECTED", VariantType.varTimestamp, true);
    add("SCH_DB_VERSION", VariantType.varString, true);
    add("SCH_ICON", VariantType.varBinary, true);
    add("SCH_PROPERTIES", VariantType.varString, true);
    add("SCH_USER_PROPERTIES", VariantType.varString, true);
    add("SCH_PUBLIC_NAME", VariantType.varString, true);
  }
  
  public Schema(Database database, String sch_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(sch_id));
  }
  
  public String getSchId() {
    try {
      return fieldByName("sch_id").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }

  public void setSchId(String id) {
    fieldByName("SCH_ID").setString(id);
  }
  
  public String getUsrId() {
    if (fieldByName("SCH_USR_ID").isNull()) {
      return null;
    }
    return fieldByName("SCH_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("SCH_USR_ID").setString(id);
  }
  
  public String getHost() {
    if (fieldByName("SCH_HOST").isNull()) {
      return null;
    }
    return fieldByName("SCH_HOST").getValue().toString();
  }

  public void setHost(String host) {
    fieldByName("SCH_HOST").setString(host);
  }

  public Integer getPort() {
    if (fieldByName("SCH_PORT").isNull()) {
      return null;
    }
    try {
      return fieldByName("SCH_PORT").getInteger();
    } catch (VariantException ex) {
      return null;
    }
  }

  public void setPort(Integer port) {
    fieldByName("SCH_PORT").setInteger(port);
  }

  public void setPort(String port) {
    fieldByName("SCH_PORT").setString(port);
  }

  public String getUser() {
    if (fieldByName("SCH_USER").isNull()) {
      return null;
    }
    return fieldByName("SCH_USER").getValue().toString();
  }

  public void setUser(String user) {
    fieldByName("SCH_USER").setString(user);
  }

  public String getPassword() {
    if (fieldByName("SCH_PASSWORD").isNull()) {
      return null;
    }
    return fieldByName("SCH_PASSWORD").getValue().toString();
  }

  public void setPassword(String password) {
    fieldByName("SCH_PASSWORD").setString(password);
  }

  public String getDatabaseName() {
    if (fieldByName("SCH_DATABASE").isNull()) {
      return null;
    }
    return fieldByName("SCH_DATABASE").getValue().toString();
  }

  public void setDatabaseName(String database) {
    fieldByName("SCH_DATABASE").setString(database);
  }

  public String getDrvId() {
    try {
      return fieldByName("SCH_DRV_ID").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }

  public void setDrvId(String id) {
    fieldByName("SCH_DRV_ID").setString(id);
    driver = null;
  }

  public String getUrl() {
    try {
      return fieldByName("SCH_URL").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }

  public void setUrl(String url) {
    fieldByName("SCH_URL").setString(url);
  }

  public String getPropertiesAsString() {
    if (fieldByName("SCH_PROPERTIES").isNull()) {
      return null;
    }
    try {
      return fieldByName("SCH_PROPERTIES").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }
  
  public Properties getProperties() {
    if (fieldByName("SCH_PROPERTIES").isNull()) {
      return null;
    }
    try {
      StringReader reader = new StringReader(fieldByName("SCH_PROPERTIES").getString());
      Properties result = new Properties();
      result.load(reader);
      reader.close();
      return result;
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }

  public void setProperties(String props) {
    fieldByName("SCH_PROPERTIES").setString(props);
  }

  public void setProperties(Properties props) {
    if (props != null) {
      StringWriter writer = new StringWriter();
      try {
        props.store(writer, null);
        fieldByName("SCH_PROPERTIES").setString(writer.toString());
        writer.close();
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
        fieldByName("SCH_PROPERTIES").setValue(null);
      }
    }
    else {
      fieldByName("SCH_PROPERTIES").setValue(null);
    }
  }
  
  public String getUserPropertiesAsString() {
    if (fieldByName("SCH_USER_PROPERTIES").isNull()) {
      return null;
    }
    try {
      return fieldByName("SCH_USER_PROPERTIES").getString();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }

  public Properties getUserProperties() {
    if (fieldByName("SCH_USER_PROPERTIES").isNull()) {
      return null;
    }
    try {
      StringReader reader = new StringReader(fieldByName("SCH_USER_PROPERTIES").getString());
      Properties result = new Properties();
      result.load(reader);
      reader.close();
      return result;
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }

  public void setUserProperties(String props) {
    fieldByName("SCH_USER_PROPERTIES").setString(props);
  }

  public void setUserProperties(Properties props) {
    if (props != null) {
      StringWriter writer = new StringWriter();
      try {
        props.store(writer, null);
        fieldByName("SCH_USER_PROPERTIES").setString(writer.toString());
        writer.close();
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
        fieldByName("SCH_USER_PROPERTIES").setValue(null);
      }
    }
    else {
      fieldByName("SCH_USER_PROPERTIES").setValue(null);
    }
  }

  public Icon getIcon() {
    if (icon != null) {
      return icon;
    }
    if (!fieldByName("sch_icon").getValue().isNullValue()) {
      try {
        icon = new ImageIcon(fieldByName("sch_icon").getValue().getBinary());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      return icon;
    }
    return null;
  }
  
  public Driver getDriver() throws UseDBException {
    if (driver == null) {
      driver = new Driver(getDatabase(), fieldByName("SCH_DRV_ID").getValue().toString());
    }
    return driver;
  }

  @Override
  public void applyInsert() throws Exception {
    fieldByName("SCH_PUBLIC_NAME").setString(getPublicNameResolved());
    super.applyInsert();
  }

  @Override
  public void applyUpdate() throws Exception {
    fieldByName("SCH_PUBLIC_NAME").setString(getPublicNameResolved());
    super.applyUpdate();
  }

  public String replacePatts(String templateUrl) {
    String text = templateUrl;
    text = Utils.replace(text, "host", fieldByName("SCH_HOST").getValue().toString());
    if (!fieldByName("SCH_PORT").isNull()) {
      try {
        text = Utils.replace(text, "port", fieldByName("SCH_PORT").getValue().cast(VariantType.varInteger).toString());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    text = Utils.replace(text, "database", fieldByName("SCH_DATABASE").getValue().toString());
    text = Utils.replace(text, "sid", fieldByName("SCH_DATABASE").getValue().toString());
    text = Utils.replace(text, "db", fieldByName("SCH_DATABASE").getValue().toString());
    text = Utils.replace(text, "user", fieldByName("SCH_USER").getValue().toString());
    text = Utils.replace(text, "username", fieldByName("SCH_USER").getValue().toString());
    text = text.replaceAll("\\[.*\\]", "");
    return text;
  }
  
  public String getPublicNameResolved() {
    String text = replacePatts(fieldByName("SCH_NAME").getValue().toString());
    try {
      if (!fieldByName("sch_drv_id").getValue().isNullValue()) {
        text = getDriver().replacePatts(text);
      }
    } catch (UseDBException ex) {
      ExceptionUtil.processException(ex);
    }
    return text;
  }

  public String getPublicNameResolved(String userName) {
    String text = fieldByName("SCH_NAME").getValue().toString();
    text = Utils.replace(text, "user", userName);
    text = Utils.replace(text, "username", userName);
    text = replacePatts(text);
    try {
      if (!fieldByName("sch_drv_id").getValue().isNullValue()) {
        text = getDriver().replacePatts(text);
      }
    } catch (UseDBException ex) {
      ExceptionUtil.processException(ex);
    }
    return text;
  }

}
