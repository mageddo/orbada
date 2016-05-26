package pl.mpak.usedb.gui.swing;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import pl.mpak.usedb.core.QueryField;

public class QueryTableColumn extends TableColumn {
  private static final long serialVersionUID = 4800474547270230883L;

  private String fieldName = null;
  private QueryField queryField = null;

  public QueryTableColumn(QueryField tableField) {
    this(tableField, tableField.getDisplayName());
  }

  public QueryTableColumn(QueryField tableField, String title) {
    super(tableField.getIndex() -1, tableField.getWidth());
    this.queryField = tableField; 
    fieldName = tableField.getFieldName();
    setIdentifier(fieldName);
    setHeaderValue(title);
    setCellRenderer(new QueryTableCellRenderer());
  }

  public QueryTableColumn(String fieldName, String title, int width) {
    super(-1, width);
    this.fieldName = fieldName;
    setIdentifier(fieldName);
    setHeaderValue(title);
    setCellRenderer(new QueryTableCellRenderer());
  }
  
  public QueryTableColumn(String fieldName, String title, int width, TableCellRenderer cellRenderer) {
    super(-1, width);
    this.fieldName = fieldName;
    setIdentifier(fieldName);
    setHeaderValue(title);
    setCellRenderer(cellRenderer);
  }
  
  void setField(QueryField tableField) {
    this.queryField = tableField;
    setModelIndex(this.queryField.getIndex() -1);
  }

  public QueryField getField() {
    return queryField;
  }
  
  public String getFieldName() {
    return queryField == null ? fieldName : queryField.getFieldName();
  }

  public void setShowNullValue(boolean showNullValue) {
    if (getCellRenderer() instanceof QueryTableCellRenderer) {
      ((QueryTableCellRenderer)getCellRenderer()).setShowNullValue(showNullValue);
    }
  }

  public boolean isShowNullValue() {
    if (getCellRenderer() instanceof QueryTableCellRenderer) {
      return ((QueryTableCellRenderer)getCellRenderer()).isShowNullValue();
    }
    return false;
  }
  
}
