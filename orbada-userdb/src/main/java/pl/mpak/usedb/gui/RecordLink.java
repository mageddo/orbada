/*
 * QueryFieldsLink.java
 *
 * Created on 21 styczeñ 2007, 21:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.usedb.gui;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import pl.mpak.usedb.Messages;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.br.BufferedRecord;
import pl.mpak.usedb.br.BufferedRecordField;
import pl.mpak.usedb.core.CacheField;
import pl.mpak.usedb.core.CacheRecord;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.usedb.gui.linkreq.IFieldRequires;
import pl.mpak.util.Assert;
import pl.mpak.util.array.StringList;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author akaluza
 * <p>Klasa s³u¿¹ca do po³¹czenia komponentów edycyjnych z poleceniem aktualizacji
 * oraz rekordem w CachedRecord, BufferedRecord lub Query.
 */
public class RecordLink {
  
  private ArrayList<FieldLink> fieldLinkList;
  
  public RecordLink() {
    fieldLinkList = new ArrayList<FieldLink>();
  }

  public void add(FieldLink fieldLink) {
    fieldLinkList.add(fieldLink);
  }
  
  public void add(String fieldName, Component component) throws IntrospectionException {
    add(new FieldLink(fieldName, component));
  }
  
  public void add(String fieldName, Component component, String propertyName) throws IntrospectionException {
    add(new FieldLink(fieldName, component, propertyName));
  }
  
  public void add(String fieldName, Component component, String propertyName, int varType) throws IntrospectionException {
    add(new FieldLink(fieldName, component, propertyName, varType));
  }
  
  public void add(String fieldName, Component component, String propertyName, FieldLinkType[] fieldLinkTypes) throws IntrospectionException {
    add(new FieldLink(fieldName, component, propertyName, fieldLinkTypes));
  }
  
  public void add(String fieldName, Component component, IFieldRequires required) throws IntrospectionException {
    add(new FieldLink(fieldName, component, required));
  }
  
  public void add(String fieldName, Component component, String propertyName, IFieldRequires required) throws IntrospectionException {
    add(new FieldLink(fieldName, component, propertyName, required));
  }
  
  public void add(String fieldName, Component component, String propertyName, int varType, IFieldRequires required) throws IntrospectionException {
    add(new FieldLink(fieldName, component, propertyName, varType, required));
  }
  
  public void add(String fieldName, Component component, String propertyName, FieldLinkType[] fieldLinkTypes, IFieldRequires required) throws IntrospectionException {
    add(new FieldLink(fieldName, component, propertyName, fieldLinkTypes, required));
  }
  
  public void remove(String fieldName) {
    for (int i=0; i<fieldLinkList.size(); i++) {
      if (fieldName.equalsIgnoreCase(fieldLinkList.get(i).getFieldName())) {
        fieldLinkList.remove(i);
        break;
      }
    }
  }
  
  public String getComponentValue(String fieldName) throws UseDBException {
    for (int i=0; i<fieldLinkList.size(); i++) {
      FieldLink fl = fieldLinkList.get(i);
      if (fl.getFieldName().equalsIgnoreCase(fieldName)) {
        try {
          return fl.getValue().toString();
        } catch (Exception ex) {
          throw new UseDBException(ex);
        }
      }
    }
    return null;
  }
  
  /**
   * <p>Aktualizuje komponenty wartoœciami z CacheRecord.
   * @param cacheRecord
   * @throws UseDBException
   */
  public void updateComponents(CacheRecord cacheRecord) throws UseDBException {
    for (int i=0; i<fieldLinkList.size(); i++) {
      FieldLink fl = fieldLinkList.get(i);
      CacheField cf = cacheRecord.getField(fl.getFieldName());
      try {
        fl.setValue(cf.getValue().toString());
      } catch (InvocationTargetException ex) {
        ;
      } catch (Exception ex) {
        throw new UseDBException(ex);
      }
    }
  }
  
  /**
   * <p>Aktualizuje komponenty wartoœciami z Query.
   * @param query
   * @throws UseDBException
   */
  public void updateComponents(Query query) throws UseDBException {
    for (int i=0; i<fieldLinkList.size(); i++) {
      FieldLink fl = fieldLinkList.get(i);
      QueryField qf = query.fieldByName(fl.getFieldName());
      try {
        fl.setValue(qf.getString());
      } catch (InvocationTargetException ex) {
        ;
      } catch (Exception ex) {
        throw new UseDBException(ex);
      }
    }
  }
  
  /**
   * <p>Aktualizuje komponenty wartoœciami z BufferedRecord.
   * @param bufferedRecord
   * @throws UseDBException
   */
  public void updateComponents(BufferedRecord bufferedRecord) throws UseDBException {
    for (int i=0; i<fieldLinkList.size(); i++) {
      FieldLink fl = fieldLinkList.get(i);
      BufferedRecordField brf = bufferedRecord.fieldByName(fl.getFieldName());
      Assert.notNull(brf, "fl.getFieldName():" +fl.getFieldName()); //$NON-NLS-1$
      try {
        if (brf.getValue() == null) {
          fl.setValue(brf.getValue());
        }
        else {
          fl.setValue(brf.getValue().getObject());
        }
      } catch (InvocationTargetException ex) {
        ;
      } catch (Exception ex) {
        throw new UseDBException(ex);
      }
    }
  }
  
  /**
   * <p>Aktualizuje wartoœciami z komponentów CacheRecord.
   * @param cacheRecord
   * @throws UseDBException
   */
  public void updateRecord(CacheRecord cacheRecord) throws UseDBException {
    for (int i=0; i<fieldLinkList.size(); i++) {
      FieldLink fl = fieldLinkList.get(i);
      CacheField cf = cacheRecord.getField(fl.getFieldName());
      try {
        cf.setValue(new Variant(fl.getValue(), cf.getValue().getValueType()));
      } catch (Exception ex) {
        throw new UseDBException(ex);
      }
    }
  }
  
  /**
   * <p>Aktualizuje wartoœciami z komponentów do BufferedRecord.
   * @param bufferedRecord
   * @throws UseDBException
   */
  public void updateRecord(BufferedRecord bufferedRecord) throws UseDBException {
    StringList list = null;
    for (int i=0; i<fieldLinkList.size(); i++) {
      FieldLink fl = fieldLinkList.get(i);
      BufferedRecordField brf = bufferedRecord.fieldByName(fl.getFieldName());
      try {
        Object value = fl.getValue();
        if (fl.getRequired() != null && !fl.getRequired().accept(value)) {
          if (list == null) {
            list = new StringList();
          }
          list.add(fl.getRequired().getMessage(value));
        }
        else if (value == null) {
          brf.setValue(null);
        }
        else {
          if (brf.getValueType() != VariantType.varString && brf.getValueType() != VariantType.varVariant && "".equals(value.toString())) { //$NON-NLS-1$
            brf.setValue(new Variant());
          }
          else {
            brf.setValue(new Variant(value, brf.getValueType()));
          }
        }
      } catch (Exception ex) {
        throw new UseDBException(ex);
      }
    }
    if (list != null && list.size() > 0) {
      throw new UseDBException(Messages.getString("RecordLink.field-no-cond") +"\n" +list.getText()); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }
  
}
