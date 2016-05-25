package pl.mpak.sky.gui.swing.comp;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.text.Document;

import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.DefaultUndoManager;
import pl.mpak.sky.gui.swing.comp.actions.CmTab;
import pl.mpak.sky.gui.swing.comp.actions.CmTabBackward;

public class TextArea extends JTextArea {
  private static final long serialVersionUID = -8147101785529186527L;

  protected DefaultUndoManager undoManager;

  public TextArea() {
    super();
    init();
  }

  public TextArea(String text) {
    super(text);
    init();
  }

  public TextArea(Document doc) {
    super(doc);
    init();
  }

  public TextArea(int rows, int columns) {
    super(rows, columns);
    init();
  }

  public TextArea(String text, int rows, int columns) {
    super(text, rows, columns);
    init();
  }

  public TextArea(Document doc, String text, int rows, int columns) {
    super(doc, text, rows, columns);
    init();
  }

  private void init() {
    setComponentPopupMenu(new PopupMenuText(this) {
      private static final long serialVersionUID = 1L;
        protected void updateActions() {
          getUndoManager().getCmUndoEdit().setEnabled(TextArea.this.isEnabled() && TextArea.this.isEditable());
        }
    });

    SwingUtil.addAction(this, "cmTab", new CmTab(this));
    SwingUtil.addAction(this, "cmTabBackward", new CmTabBackward(this));
    getInputMap(JComponent.WHEN_FOCUSED).put(getUndoManager().getCmUndoEdit().getAltShortCut(), getUndoManager().getCmUndoEdit().getActionCommandKey());
    getComponentPopupMenu().insert(new JMenuItem(getUndoManager().getCmUndoEdit()), 0);
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
