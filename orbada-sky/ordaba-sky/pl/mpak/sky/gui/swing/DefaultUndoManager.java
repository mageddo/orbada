package pl.mpak.sky.gui.swing;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoableEdit;

import pl.mpak.sky.gui.swing.comp.actions.CmRedoEdit;
import pl.mpak.sky.gui.swing.comp.actions.CmUndoEdit;

public class DefaultUndoManager extends javax.swing.undo.UndoManager {
  private static final long serialVersionUID = 4279327905532581224L;

  private Document document = null;
  private transient UndoableEditListener undoableEditListener = null;

  private CmUndoEdit cmUndoEdit; 
  private CmRedoEdit cmRedoEdit; 
  
  public DefaultUndoManager() {
    super();
    init();
  }
  
  public DefaultUndoManager(Document document) {
    super();
    init();
    setDocument(document);
  }
  
  private void init() {
    setLimit(2000);

    undoableEditListener = new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent e) {
        addEdit(e.getEdit());
        getCmUndoEdit().updateUndoInfo();
        getCmRedoEdit().updateRedoInfo();
      }
    };
  }
  
  public void dispose() {
    setDocument(null);
  }

  public void setDocument(Document document) {
    if (this.document != document) {
      if (this.document != null) {
        this.document.removeUndoableEditListener(undoableEditListener);
      }
      this.document = document;
      if (this.document != null) {
        this.document.addUndoableEditListener(undoableEditListener);
      }
    }
  }

  public Document getDocument() {
    return document;
  }

  public CmUndoEdit getCmUndoEdit() {
    if (cmUndoEdit == null) {
      cmUndoEdit = new CmUndoEdit(this);
    }
    return cmUndoEdit;
  }
  
  public CmRedoEdit getCmRedoEdit() {
    if (cmRedoEdit == null) {
      cmRedoEdit = new CmRedoEdit(this);
    }
    return cmRedoEdit;
  }

  public void fireUndoPerformed() {
    UndoableEdit edit = editToBeUndone();
    undo();
    if (edit != null) {
      undoPerformed(edit);
    }
  }
  
  public void fireRedoPerformed() {
    UndoableEdit edit = editToBeUndone();
    redo();
    if (edit != null) {
      redoPerformed(edit);
    }
  }
  
  public void undoPerformed(UndoableEdit edit) {
    
  }
  
  public void redoPerformed(UndoableEdit edit) {
    
  }
}
