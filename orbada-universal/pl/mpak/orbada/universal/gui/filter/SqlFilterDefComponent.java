/*
 * SqlFilterDefinitionComponent.java
 *
 * Created on 2007-11-04, 19:19:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.filter;

/**
 *
 * @author akaluza
 */
public class SqlFilterDefComponent {
  
  private boolean emptyDefinition;
  private String columnSqlName;
  private String columnPublicName;
  private int[] conditions;
  private String[] valueList;
  
  public SqlFilterDefComponent() {
    this(null, null, null, null);
  }
  
  public SqlFilterDefComponent(String sqlCode, String publicName) {
    this(sqlCode, publicName, new int[] {SqlFilterConsts.COND_USER_VALUE}, null);
  }

  public SqlFilterDefComponent(String columnSqlName, String columnPublicName, String[] valueList) {
    this(columnSqlName, columnPublicName, SqlFilterConsts.COND_ALL, valueList);
  }
  
  public SqlFilterDefComponent(String columnSqlName, String columnPublicName, int[] conditions) {
    this(columnSqlName, columnPublicName, conditions, null);
  }

  public SqlFilterDefComponent(String columnSqlName, String columnPublicName, int[] conditions, String[] valueList) {
    this.columnSqlName = columnSqlName;
    this.columnPublicName = columnPublicName;
    this.conditions = conditions == null ? new int[] {} : conditions;
    this.valueList = valueList == null ? new String[] {} : valueList;
    if (this.columnSqlName == null && this.columnPublicName == null) {
      emptyDefinition = true;
    }
  }
  
  public String toString() {
    return columnPublicName == null ? "" : columnPublicName;
  }

  public String getColumnSqlName() {
    return columnSqlName;
  }

  public String getColumnPublicName() {
    return columnPublicName;
  }

  public int[] getConditions() {
    return conditions;
  }

  public String[] getValueList() {
    return valueList;
  }

  public boolean isEmptyDefinition() {
    return emptyDefinition;
  }
  
}
