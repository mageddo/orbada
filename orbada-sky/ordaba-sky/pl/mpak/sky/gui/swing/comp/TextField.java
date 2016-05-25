package pl.mpak.sky.gui.swing.comp;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.Document;

import pl.mpak.sky.gui.swing.DefaultUndoManager;

public class TextField extends JTextField {
  private static final long serialVersionUID = 6458438031848959589L;

  protected DefaultUndoManager undoManager;

  public TextField() {
    super();
    init();
  }

  public TextField(String text) {
    super(text);
    init();
  }

  public TextField(int columns) {
    super(columns);
    init();
  }

  public TextField(String text, int columns) {
    super(text, columns);
    init();
  }

  public TextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    init();
  }

  private void init() {
    setComponentPopupMenu(new PopupMenuText(this) {
      private static final long serialVersionUID = 1L;
        protected void updateActions() {
          super.updateActions();
          getUndoManager().getCmUndoEdit().setEnabled(TextField.this.isEnabled() && TextField.this.isEditable());
        }
    });

    getInputMap(JComponent.WHEN_FOCUSED).put(getUndoManager().getCmUndoEdit().getAltShortCut(), getUndoManager().getCmUndoEdit().getActionCommandKey());
    getComponentPopupMenu().insert(getUndoManager().getCmUndoEdit(), 0);
    getComponentPopupMenu().insert(new JPopupMenu.Separator(), 1);
  }
  
  public void setDocument(Document document) {
    super.setDocument(document);
    getUndoManager().setDocument(document);
  }
  
  public DefaultUndoManager getUndoManager() {
    if (undoManager == null) {
      undoManager = new DefaultUndoManager(getDocument());
    }
    return undoManager;
  }
}
