/*
 * FieldLink.java
 *
 * Created on 21 styczeñ 2007, 20:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.usedb.gui;

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import pl.mpak.usedb.gui.linkreq.IFieldRequires;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 */
public class FieldLink {
  
  private String fieldName;
  private Component component;
  private String propertyName;
  private int varType = VariantType.varUnassigned;
  private FieldLinkType[] fieldLinkTypes;
  private Method getter;
  private Method setter;
  private IFieldRequires required;
  
  public FieldLink() {
  }

  public FieldLink(String fieldName, Component component) throws IntrospectionException {
    this(fieldName, component, "text");
  }

  public FieldLink(String fieldName, Component component, String propertyName) throws IntrospectionException {
    this(fieldName, component, propertyName, (FieldLinkType[])null);
  }

  public FieldLink(String fieldName, Component component, String propertyName, int varType) throws IntrospectionException {
    this(fieldName, component, propertyName, (FieldLinkType[])null);
    this.varType = varType;
  }

  public FieldLink(String fieldName, Component component, String propertyName, FieldLinkType[] fieldLinkTypes) throws IntrospectionException {
    this();
    this.fieldName = fieldName;
    this.component = component;
    this.propertyName = propertyName;
    if (fieldLinkTypes != null) {
      this.fieldLinkTypes = Arrays.copyOf(fieldLinkTypes, fieldLinkTypes.length);
    }
    initMethods();
  }

  public FieldLink(String fieldName, Component component, IFieldRequires required) throws IntrospectionException {
    this(fieldName, component);
    this.required = required;
  }

  public FieldLink(String fieldName, Component component, String propertyName, IFieldRequires required) throws IntrospectionException {
    this(fieldName, component, propertyName);
    this.required = required;
  }

  public FieldLink(String fieldName, Component component, String propertyName, int varType, IFieldRequires required) throws IntrospectionException {
    this(fieldName, component, propertyName, varType);
    this.required = required;
  }

  public FieldLink(String fieldName, Component component, String propertyName, FieldLinkType[] fieldLinkTypes, IFieldRequires required) throws IntrospectionException {
    this(fieldName, component, propertyName, fieldLinkTypes);
    this.required = required;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) throws IntrospectionException {
    if (StringUtil.equals(this.propertyName, propertyName)) {
      this.propertyName = propertyName;
      initMethods();
    }
  }

  public Component getComponent() {
    return component;
  }

  public void setComponent(Component component) throws IntrospectionException {
    if (this.component != component) {
      this.component = component;
      initMethods();
    }
  }
  
  private void initMethods() throws IntrospectionException {
    if (component != null) {
      BeanInfo info;
      info = Introspector.getBeanInfo(component.getClass(), Object.class);
  
      PropertyDescriptor[] pd = info.getPropertyDescriptors();
      for (int i=0; i<pd.length; i++) {
        if (pd[i].getName().equals(propertyName)) {
          getter = pd[i].getReadMethod();
          setter = pd[i].getWriteMethod();
          break;
        }
      }
      
      if (getter == null && setter == null) {
        throw new IntrospectionException("Can't find getter and/or setter for property \"" +propertyName +"\".");
      }
    }
  }
  
  public void setValue(Object value) throws IllegalAccessException, InvocationTargetException, IllegalArgumentException, VariantException, IOException {
    if (setter != null) {
      if (fieldLinkTypes != null) {
        for (int i=0; i<fieldLinkTypes.length; i++) {
          if (StringUtil.equals(value, fieldLinkTypes[i].getFieldValue())) {
            setter.invoke(component, new Object[] {fieldLinkTypes[i].getComponentValue()});
            break;
          }
        }
      }
      else {
        if (value == null) {
          setter.invoke(component, new Object[] {null});
        }
        else if (varType == VariantType.varUnassigned) {
          setter.invoke(component, new Object[] {new Variant(value).getString()});
        }
        else {
          Variant v = new Variant(value);
          try {
            v.cast(varType);
          } catch (Exception e) {
            throw new InvocationTargetException(e);
          }
          setter.invoke(component, new Object[] {v.getValue().toString()});
        }
      }
    }
  }
  
  public Object getValue() throws IllegalAccessException, InvocationTargetException {
    if (getter != null) {
      if (fieldLinkTypes != null) {
        Object value = getter.invoke(component, (Object[])null);
        for (int i=0; i<fieldLinkTypes.length; i++) {
          if (StringUtil.equals(fieldLinkTypes[i].getComponentValue(), value)) {
            return fieldLinkTypes[i].getFieldValue() == null ? "" : fieldLinkTypes[i].getFieldValue();
          }
        }
        return value;
      }
      else {
        Object o = getter.invoke(component, (Object[])null);
        return o == null ? null : o.toString();
      }
    }
    return null;
  }

  public IFieldRequires getRequired() {
    return required;
  }

  public void setRequired(IFieldRequires required) {
    this.required = required;
  }
  
}
