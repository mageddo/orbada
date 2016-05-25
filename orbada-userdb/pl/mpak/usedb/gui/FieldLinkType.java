/*
 * FieldLinkType.java
 *
 * Created on 22 styczeñ 2007, 20:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.usedb.gui;

/**
 *
 * @author akaluza
 * <p>Klasa, która s³ó¿y do okreœlania wartoœci pola i ³¹czenia jej z wartoœci¹ komponentu.
 */
public class FieldLinkType {

  public final static FieldLinkType[] Boolean_YN = new FieldLinkType[] {new FieldLinkType("Y", true), new FieldLinkType("N", false)};
  public final static FieldLinkType[] Boolean_TN = new FieldLinkType[] {new FieldLinkType("T", true), new FieldLinkType("N", false)};
  public final static FieldLinkType[] Boolean_TNN = new FieldLinkType[] {new FieldLinkType("T", true), new FieldLinkType("N", false), new FieldLinkType(null, false)};
  public final static FieldLinkType[] Boolean_TF = new FieldLinkType[] {new FieldLinkType("T", true), new FieldLinkType("F", false)};
  
  private Object fieldValue;
  private Object componentValue;
  
  public FieldLinkType(Object fieldValue, Object componentValue) {
    this.fieldValue = fieldValue;
    this.componentValue = componentValue;
  }

  public Object getFieldValue() {
    return fieldValue;
  }

  public void setFieldValue(Object fieldValue) {
    this.fieldValue = fieldValue;
  }

  public Object getComponentValue() {
    return componentValue;
  }

  public void setComponentValue(Object componentValue) {
    this.componentValue = componentValue;
  }
  
}
