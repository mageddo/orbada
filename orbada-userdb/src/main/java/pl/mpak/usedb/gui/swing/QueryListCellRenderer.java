package pl.mpak.usedb.gui.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

public class QueryListCellRenderer extends DefaultListCellRenderer {
  private static final long serialVersionUID = -3321325590237721266L;

  private Query query;
  private String displayField;
  private String keyField;
  
  public QueryListCellRenderer() {
    this(null, null, null);
  }
  
  public QueryListCellRenderer(Query query, String keyField, String displayField) {
    this.query = query;
    this.displayField = displayField;
    this.keyField = keyField;
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    JLabel renderer = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (query != null && value != null && query.isActive()) {
      try {
        if (query.locate(keyField, new Variant(value)) && query.fieldByName(displayField).getValue() != null) {
          renderer.setText(query.fieldByName(displayField).getValue().toString());
        }
        else {
          renderer.setText(" ");
        }
      } catch (Exception e) {
        ExceptionUtil.processException(e);
      }
    }
    return renderer;
  }

  public String getDisplayField() {
    return displayField;
  }

  public void setDisplayField(String displayField) {
    this.displayField = displayField;
  }

  public String getKeyField() {
    return keyField;
  }

  public void setKeyField(String keyField) {
    this.keyField = keyField;
  }

  public Query getQuery() {
    return query;
  }

  public void setQuery(Query query) {
    this.query = query;
  }
  
}
