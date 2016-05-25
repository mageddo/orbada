package pl.mpak.usedb.gui.swing;

import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;

public class QueryComboBox extends JComboBox {
  private static final long serialVersionUID = -6129987655393376919L;

  private Query query;
  private String keyField;
  private String displayField;
  private boolean selectingItem = false;

  public QueryComboBox() {
    this(false);
  }

  public QueryComboBox(Query query) {
    this(false);
    setQuery(query);
  }

  public QueryComboBox(boolean createQuery) {
    super(new QueryComboBoxModel());
    setRenderer(new QueryListCellRenderer(null, null, null));
    if (createQuery) {
      setQuery(new Query());
    }
  }

  public void setQuery(Query query) {
    this.query = query;
    if (getModel() instanceof QueryComboBoxModel) {
      ((QueryComboBoxModel)getModel()).setQuery(query);
    }
    if (getRenderer() instanceof QueryListCellRenderer) {
      ((QueryListCellRenderer)getRenderer()).setQuery(query);
    }
    if (query != null) {
      try {
        query.setCacheData(true);
      } catch (UseDBException e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public void setSelectedItem(Object anObject) {
    Object oldSelection = selectedItemReminder;
    Object objectToSelect = anObject;
    if (oldSelection == null || !oldSelection.equals(anObject)) {
      if (anObject != null && !isEditable()) {
        boolean found = false;
        for (int i = 0; i < dataModel.getSize(); i++) {
          Object element = dataModel.getElementAt(i);
          if (element != null) {
            if (element.equals(anObject)) {
              found = true;
              objectToSelect = element;
              break;
            }
          }
        }
        if (!found) {
          return;
        }
      }

      selectingItem = true;
      dataModel.setSelectedItem(objectToSelect);
      selectingItem = false;

      if (selectedItemReminder != dataModel.getSelectedItem()) {
        selectedItemChanged();
      }
    }
    fireActionEvent();
  }

  public void contentsChanged(ListDataEvent e) {
    Object oldSelection = selectedItemReminder;
    Object newSelection = dataModel.getSelectedItem();
    if (oldSelection == null || !oldSelection.equals(newSelection)) {
      selectedItemChanged();
      if (!selectingItem) {
        fireActionEvent();
      }
    }
  }

  public String getDisplayField() {
    return displayField;
  }

  public void setDisplayField(String dsiplayField) {
    this.displayField = dsiplayField;
    if (getRenderer() instanceof QueryListCellRenderer) {
      ((QueryListCellRenderer) getRenderer()).setDisplayField(displayField);
    }
  }

  public String getKeyField() {
    return keyField;
  }

  public void setKeyField(String keyField) {
    this.keyField = keyField;
    if (getModel() instanceof QueryComboBoxModel) {
      ((QueryComboBoxModel) getModel()).setKeyField(keyField);
    }
    if (getRenderer() instanceof QueryListCellRenderer) {
      ((QueryListCellRenderer) getRenderer()).setKeyField(keyField);
    }
  }

  public Query getQuery() {
    return query;
  }

}
