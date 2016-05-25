package pl.mpak.usedb.gui.swing;

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;

import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

public class QueryComboBoxModel implements ComboBoxModel {

  private Query query;
  private String keyField;
  private Object selectedValue;

  protected EventListenerList listenerList = new EventListenerList();

  public QueryComboBoxModel() {
    this(null, null);
  }
  
  public QueryComboBoxModel(Query query, String keyField) {
    this.query = query;
    this.keyField = keyField;
//    if (query != null) {
//      try {
//        if (!query.isEmpty()) {
//          setSelectedItem(getElementAt(0));
//        }
//      } catch (Exception e) {
//        ExceptionUtil.processException(e);
//      }
//    }
  }

  public Object getSelectedItem() {
    return selectedValue;
  }

  public void setSelectedItem(Object anItem) {
    selectedValue = anItem;
  }

  public void addListDataListener(ListDataListener l) {
    listenerList.add(ListDataListener.class, l);
  }

  public Object getElementAt(int index) {
    if (query != null && query.isActive()) {
      try {
        query.getRecord(index);
        Variant o = query.fieldByName(keyField).getValue();
        return o;
      } catch (Exception e) {
        ExceptionUtil.processException(e);
        return null;
      }
    }
    return null;
  }

  public int getSize() {
    if (query != null && query.isActive()) {
      return query.getRecordCount() == -1 ? 0 : query.getRecordCount();
    }
    return 0;
  }

  public void removeListDataListener(ListDataListener l) {
    listenerList.remove(ListDataListener.class, l);
  }

  public Query getQuery() {
    return query;
  }

  public void setQuery(Query query) {
    if (this.query != query) {
      this.query = query;
    }
  }

  public String getKeyField() {
    return keyField;
  }

  public void setKeyField(String keyField) {
    this.keyField = keyField;
  }

}
