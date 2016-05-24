/*
 * CopyCodeDefinition.java
 *
 * Created on 2007-11-03, 16:40:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.gui;

import pl.mpak.orbada.plugins.ISettings;

/**
 *
 * @author akaluza
 */
public class CopyCodeDefinition {
  
  private final static String storeName = "copy-code-definition";
  
  private String name;
  private String beforeAll;
  private String afterAll;
  private String addAfter;
  private String addBefore;
  private String beforeChars;
  private String chars;
  private String endLast;
  private String paramAs;
  private boolean updatable;
  
  public CopyCodeDefinition(String name) {
    this(name, "", "", "", "", "", "", "", "", true);
  }

  public CopyCodeDefinition(String name, String beforeAll, String addBefore, String addAfter, String endLast, String beforeChars, String chars, String afterAll, String paramAs) {
    this(name, beforeAll, addAfter, addBefore, beforeChars, chars, endLast, afterAll, paramAs, true);
  }
  
  public CopyCodeDefinition(String name, String beforeAll, String addBefore, String addAfter, String endLast, String beforeChars, String chars, String afterAll, String paramAs, boolean updatable) {
    this.name = name;
    this.beforeAll = beforeAll;
    this.addAfter = addAfter;
    this.addBefore = addBefore;
    this.beforeChars = beforeChars;
    this.chars = chars;
    this.endLast = endLast;
    this.afterAll = afterAll;
    this.paramAs = paramAs;
    this.updatable = updatable;
  }
  
  public String getAddAfter() {
    return addAfter;
  }
  
  public void setAddAfter(String addAfter) {
    this.addAfter = addAfter;
  }
  
  public String getAddBefore() {
    return addBefore;
  }
  
  public void setAddBefore(String addBefore) {
    this.addBefore = addBefore;
  }
  
  public String getBeforeChars() {
    return beforeChars;
  }
  
  public void setBeforeChars(String beforeChars) {
    this.beforeChars = beforeChars;
  }
  
  public String getChars() {
    return chars;
  }
  
  public void setChars(String chars) {
    this.chars = chars;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getEndLast() {
    return endLast;
  }
  
  public void setEndLast(String endLast) {
    this.endLast = endLast;
  }
  
  public boolean isUpdatable() {
    return updatable;
  }
  
  public void setUpdatable(boolean updatable) {
    this.updatable = updatable;
  }

  public String getBeforeAll() {
    return beforeAll;
  }

  public void setBeforeAll(String beforeAll) {
    this.beforeAll = beforeAll;
  }

  public String getAfterAll() {
    return afterAll;
  }

  public void setAfterAll(String afterAll) {
    this.afterAll = afterAll;
  }

  public String getParamAs() {
    return paramAs;
  }

  public void setParamAs(String paramAs) {
    this.paramAs = paramAs;
  }
  
  public void store(ISettings settings) {
    settings.setValue(storeName +"-" +name, name);
    settings.setValue(storeName +"-" +name +"-before-all", beforeAll);
    settings.setValue(storeName +"-" +name +"-after-all", afterAll);
    settings.setValue(storeName +"-" +name +"-add-after", addAfter);
    settings.setValue(storeName +"-" +name +"-add-before", addBefore);
    settings.setValue(storeName +"-" +name +"-before-chars", beforeChars);
    settings.setValue(storeName +"-" +name +"-chars", chars);
    settings.setValue(storeName +"-" +name +"-end-last", endLast);
    settings.setValue(storeName +"-" +name +"-param-as", paramAs);
  }
  
  public void load(ISettings settings) {
    name = settings.getValue(storeName +"-" +name, name);
    beforeAll = settings.getValue(storeName +"-" +name +"-before-all", beforeAll);
    afterAll = settings.getValue(storeName +"-" +name +"-after-all", afterAll);
    addAfter = settings.getValue(storeName +"-" +name +"-add-after", addAfter);
    addBefore = settings.getValue(storeName +"-" +name +"-add-before", addBefore);
    beforeChars = settings.getValue(storeName +"-" +name +"-before-chars", beforeChars);
    chars = settings.getValue(storeName +"-" +name +"-chars", chars);
    endLast = settings.getValue(storeName +"-" +name +"-end-last", endLast);
    paramAs = settings.getValue(storeName +"-" +name +"-param-as", paramAs);
  }
  
  public String toString() {
    return name;
  }
  
}
