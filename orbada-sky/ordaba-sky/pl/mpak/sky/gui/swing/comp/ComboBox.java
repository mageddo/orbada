package pl.mpak.sky.gui.swing.comp;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class ComboBox extends JComboBox {
  private static final long serialVersionUID = 4612530362085927157L;

  public ComboBox() {
  }

  public ComboBox(ComboBoxModel aModel) {
    super(aModel);
  }

  public ComboBox(Object[] items) {
    super(items);
  }

  public ComboBox(Vector<?> items) {
    super(items);
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

      dataModel.setSelectedItem(objectToSelect);

      if (selectedItemReminder != dataModel.getSelectedItem()) {
        selectedItemChanged();
      }
    }
    fireActionEvent();
  }
  
  public void setEditable(boolean aFlag) {
    if (aFlag != isEditable) {
      super.setEditable(aFlag);
      if (getEditor().getEditorComponent() instanceof JTextField && ((JTextField)getEditor().getEditorComponent()).getComponentPopupMenu() == null) {
        ((JTextField)getEditor().getEditorComponent()).setComponentPopupMenu(new PopupMenuText((JTextField)getEditor().getEditorComponent()));
      }
    }
  }
  
  /**
   * <p>Zwraca text zapisany w edytorze lub wybrany z listy
   * @return
   * @see isEditable
   */
  public String getText() {
    if (isEditable()) {
      if (getEditor().getEditorComponent() instanceof JTextComponent) {
        String text = ((JTextComponent)getEditor().getEditorComponent()).getText();
        return text != null ? text : "";
      }
    }
    if (getSelectedItem() == null) {
      return null;
    }
    return getSelectedItem().toString();
  }
  
  /**
   * <p>Ustawia tekst w edytorze lub ustawia element listy
   * @param text
   * @see setEditable
   */
  public void setText(String text) {
    if (isEditable()) {
      if (getEditor().getEditorComponent() instanceof JTextComponent) {
        ((JTextComponent)getEditor().getEditorComponent()).setText(text);
      }
    }
    else {
      setSelectedItem(text);
    }
  }

}
