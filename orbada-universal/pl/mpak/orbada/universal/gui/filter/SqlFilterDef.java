/*
 * SqlFilterDefinition.java
 *
 * Created on 2007-11-04, 19:15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.filter;

import java.util.ArrayList;
import javax.swing.table.TableColumn;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.Assert;

/**
 * <p>Zawiera definicjê uniwersalnego filtra SQL
 * @author akaluza
 */
public class SqlFilterDef {
  
  private ArrayList<SqlFilterDefComponent> cmponentList;
  
  public SqlFilterDef(QueryTable queryTable) {
    this();
    for (int i=0; i<queryTable.getColumnModel().getColumnCount(); i++) {
      TableColumn tc = queryTable.getColumnModel().getColumn(i);
      if (tc instanceof QueryTableColumn) {
        QueryTableColumn col = (QueryTableColumn)tc;
        add(new SqlFilterDefComponent(col.getFieldName(), col.getHeaderValue().toString(), new String[] {}));
      }
    }
  }
  
  public SqlFilterDef(SqlFilterDefComponent[] components) {
    this();
    Assert.notNull(components);
    for (int i=0; i<components.length; i++) {
      add(components[i]);
    }
  }
  
  public SqlFilterDef() {
    cmponentList = new ArrayList<SqlFilterDefComponent>();
    add(new SqlFilterDefComponent());
  }
  
  public void add(SqlFilterDefComponent component) {
    cmponentList.add(component);
  }
  
  public int getCount() {
    return cmponentList.size();
  }
  
  public SqlFilterDefComponent get(int index) {
    return cmponentList.get(index);
  }
  
  public SqlFilterDefComponent getByColumn(String columnName) {
    if (columnName != null) {
      for (int i=0; i<cmponentList.size(); i++) {
        if (columnName.equalsIgnoreCase(cmponentList.get(i).getColumnSqlName())) {
          return cmponentList.get(i);
        }
      }
    }
    return cmponentList.get(0);
  }
  
}
