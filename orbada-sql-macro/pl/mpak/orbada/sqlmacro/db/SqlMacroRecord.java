/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.sqlmacro.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.DefaultBufferedRecord;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author akaluza
 */
public class SqlMacroRecord extends DefaultBufferedRecord {

  private Pattern pattern;
  
  public SqlMacroRecord(Database database) {
    super(database, "OSQLMACROS", "OSM_ID");
    add("OSM_ID", new Variant(new UniqueID().toString()), VariantType.varString, false);
    add("OSM_USR_ID", VariantType.varString, true);
    add("OSM_NAME", VariantType.varString, true);
    add("OSM_REGEXP", VariantType.varString, true);
    add("OSM_RESOLVE", VariantType.varString, true);
    add("OSM_ORDER", VariantType.varLong, true);
    add("OSM_DTP_ID", VariantType.varString, true);
  }
  
  public SqlMacroRecord(Database database, String osm_id) throws UseDBException {
    this(database);
    loadRecord(new Variant(osm_id));
  }
  
  public String getId() {
    return fieldByName("OSM_ID").getValue().toString();
  }
  
  public void setId(String id) {
    fieldByName("OSM_ID").setString(id);
  }
  
  public String getUsrId() {
    if (fieldByName("OSM_USR_ID").isNull()) {
      return null;
    }
    return fieldByName("OSM_USR_ID").getValue().toString();
  }
  
  public void setUsrId(String id) {
    fieldByName("OSM_USR_ID").setString(id);
  }
  
  public String getName() {
    return fieldByName("OSM_NAME").getValue().toString();
  }
  
  public void setName(String name) {
    fieldByName("OSM_NAME").setString(name);
  }
  
  public String getRegexp() {
    return fieldByName("OSM_REGEXP").getValue().toString();
  }
  
  public void setRegexp(String regexp) {
    fieldByName("OSM_REGEXP").setString(regexp);
  }
  
  public String getResolve() {
    return fieldByName("OSM_RESOLVE").getValue().toString();
  }
  
  public void setResolve(String resolve) {
    fieldByName("OSM_RESOLVE").setString(resolve);
  }
  
  public Long getOrder() {
    if (fieldByName("OSM_ORDER").isNull()) {
      return null;
    }
    try {
      return fieldByName("OSM_ORDER").getLong();
    }
    catch (Exception ex) {
      return null;
    }
  }
  
  public void setOrder(Long order) {
    fieldByName("OSM_ORDER").setLong(order);
  }
  
  public String getDtpId() {
    if (fieldByName("OSM_DTP_ID").isNull()) {
      return null;
    }
    return fieldByName("OSM_DTP_ID").getValue().toString();
  }

  public void setDtpId(String id) {
    fieldByName("OSM_DTP_ID").setString(id);
  }

  public String resolve(String sqlText) {
    try {
      if (pattern == null) {
        pattern = Pattern.compile(getRegexp(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
      }
      Matcher mat = pattern.matcher(sqlText);
      if (mat.matches()) {
        String toResolve = Resolvers.expand(getResolve());
        return mat.replaceAll(toResolve);
      }
    }
    catch (PatternSyntaxException ex) {
      ExceptionUtil.processException(ex);
    }
    return null;
  }
  
}
