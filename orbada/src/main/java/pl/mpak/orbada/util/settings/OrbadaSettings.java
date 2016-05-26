/*
 * OrbadaSettings.java
 *
 * Created on 2007-10-13, 13:01:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package orbada.util.settings;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.StringTokenizer;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public abstract class OrbadaSettings implements ISettings {
  
  private String groupName;
  private String schemaId;
  
  public OrbadaSettings() {
  }
  
  public abstract void load();
  
  public String getGroupName() {
    return groupName;
  }
  
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  
  public String getSchemaId() {
    return schemaId;
  }
  
  public void setSchemaId(String schemaId) {
    this.schemaId = schemaId;
  }
  
  @Override
  public void setValue(String name, String value) {
    setValue(name, new Variant(value));
  }
  
  @Override
  public void setValue(String name, Long value) {
    setValue(name, new Variant(value));
  }
  
  @Override
  public void setValue(String name, Boolean value) {
    setValue(name, new Variant(value));
  }
  
  @Override
  public void setValue(String name, Color value) {
    setValue(name, new Variant(value == null ? null : value.getRGB()));
  }
  
  @Override
  public void setValue(String name, Date value) {
    setValue(name, new Variant(value == null ? null : value.getTime()));
  }
  
  private String getFontText(Font value) {
    String fontText = null;
    if (value != null) {
      fontText = value.getFontName() +"," +value.getSize() +"," +value.getStyle();
    }
    return fontText;
  }
  
  private Font getFontFromText(String fontText) {
    if (fontText == null) {
      return null;
    }
    StringTokenizer st = new StringTokenizer(fontText, ",");
    String name = null;
    int size = 0;
    int style = 0;
    if (st.hasMoreTokens()) {
      name = st.nextToken();
    }
    else {
      return null;
    }
    try {
      if (st.hasMoreTokens()) {
        size = Integer.parseInt(st.nextToken());
      }
      if (st.hasMoreTokens()) {
        style = Integer.parseInt(st.nextToken());
      }
    }
    catch (NumberFormatException ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
    return new Font(name, style, size);
  }
  
  @Override
  public void setValue(String name, Font value) {
    setValue(name, new Variant(getFontText(value)));
  }
  
  @Override
  public String getValue(String name, String defaultValue) {
    return getValue(name, new Variant(defaultValue)).toString();
  }
  
  @Override
  public Long getValue(String name, Long defaultValue) {
    Variant v = getValue(name, new Variant(defaultValue));
    if (v.isNullValue()) {
      return null;
    }
    try {
      return v.getLong();
    } catch(Exception e) {
      return defaultValue;
    }
  }
  
  @Override
  public Boolean getValue(String name, Boolean defaultValue) {
    Variant v = getValue(name, new Variant(defaultValue));
    if (v.isNullValue()) {
      return null;
    }
    try {
      return v.getBoolean();
    } catch(Exception e) {
      return defaultValue;
    }
  }
  
  @Override
  public Color getValue(String name, Color defaultValue) {
    Variant v = getValue(name, new Variant(defaultValue == null ? null : defaultValue.getRGB()));
    if (v.isNullValue()) {
      return null;
    }
    try {
      return new Color(v.getInteger());
    } catch(Exception e) {
      return defaultValue;
    }
  }
  
  @Override
  public Date getValue(String name, Date defaultValue) {
    Variant v = getValue(name, new Variant(defaultValue == null ? null : defaultValue.getTime()));
    if (v.isNullValue()) {
      return null;
    }
    try {
      return new Date(v.getLong());
    } catch(Exception e) {
      return defaultValue;
    }
  }
  
  @Override
  public Font getValue(String name, Font defaultValue) {
    Variant v = getValue(name, new Variant(defaultValue == null ? null : getFontText(defaultValue)));
    if (v.isNullValue()) {
      return null;
    }
    try {
      return getFontFromText(v.getString());
    } catch(Exception e) {
      return defaultValue;
    }
  }
  
}
