package pl.mpak.sky.gui.swing.comp;

import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import pl.mpak.sky.gui.swing.DefaultUndoManager;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.actions.CmTab;
import pl.mpak.sky.gui.swing.comp.actions.CmTabBackward;

public class EditorPane extends JEditorPane {
  private static final long serialVersionUID = -9169843827391119004L;

  protected DefaultUndoManager undoManager;
  protected StyledDocument document = null;

  public EditorPane() {
    super();
    init();
  }

  public EditorPane(URL initialPage) throws IOException {
    super(initialPage);
    init();
  }

  public EditorPane(String url) throws IOException {
    super(url);
    init();
  }

  public EditorPane(String type, String text) {
    super(type, text);
    init();
  }

  private void init() {
    StyledEditorKit editorKit = new StyledEditorKit();
    setEditorKit(editorKit);
    document = (StyledDocument)getDocument();
    
    setComponentPopupMenu(new PopupMenuText(this) {
      private static final long serialVersionUID = 1L;
        protected void updateActions() {
          getUndoManager().getCmUndoEdit().setEnabled(EditorPane.this.isEnabled() && EditorPane.this.isEditable());
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
