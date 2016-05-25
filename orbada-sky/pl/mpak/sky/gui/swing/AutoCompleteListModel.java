package pl.mpak.sky.gui.swing;

import javax.swing.AbstractListModel;

public class AutoCompleteListModel extends AbstractListModel {
  private static final long serialVersionUID = -190798185361026744L;

  private AutoCompleteItem[] delegate;

  public AutoCompleteListModel() {
  }

  public void clear() {
    if (delegate != null) {
      int end = delegate.length - 1;
      delegate = null;
      if (end >= 0) {
        fireIntervalRemoved(this, 0, end);
      }
    }
  }

  public Object getElementAt(int index) {
    if (delegate != null) {
      return delegate[index];
    }
    return null;
  }

  public int getSize() {
    if (delegate != null) {
      return delegate.length;
    }
    return 0;
  }

  public void setContents(AutoCompleteItem[] contents) {
    clear();
    if (contents != null && contents.length > 0) {
      delegate = contents;
      fireIntervalAdded(this, 0, contents.length);
    }
    else {
      delegate = null;
    }
  }
  
}
