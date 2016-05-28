/*
 * SqlFilterComponent.java
 *
 * Created on 2007-11-05, 20:02:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.filter;

/**
 *
 * @author akaluza
 */
public class SqlFilterComponent {
  
  private boolean turnedOn;
  private String operator;
  private String columnName;
  private String condition;
  private String value;
  
  public SqlFilterComponent() {
    this.operator = "";
  }
  
  public String getOperator() {
    return operator;
  }
  
  public void setOperator(String operator) {
    this.operator = operator;
  }
  
  public boolean isTurnedOn() {
    return turnedOn;
  }
  
  public void setTurnedOn(boolean turnedOn) {
    this.turnedOn = turnedOn;
  }
  
  public String getColumnName() {
    return columnName;
  }
  
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
  
  public String getCondition() {
    return condition;
  }
  
  public void setCondition(String condition) {
    this.condition = condition;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
}
