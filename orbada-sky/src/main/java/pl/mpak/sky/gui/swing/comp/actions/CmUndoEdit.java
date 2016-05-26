package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.undo.CannotUndoException;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.DefaultUndoManager;

public class CmUndoEdit extends Action {
  private static final long serialVersionUID = 8586560331890360236L;

  private static ImageIcon icon = null;
  
  private javax.swing.undo.UndoManager undoManager;

  public CmUndoEdit(javax.swing.undo.UndoManager undoManager) {
    super();
    this.undoManager = undoManager;
    setText(Messages.getString("CmUndoEdit.text")); //$NON-NLS-1$
    if (icon == null) {
      icon = pl.mpak.sky.gui.swing.ImageManager.getImage("/res/undo.gif"); //$NON-NLS-1$
    }
    setSmallIcon(icon);
    setShortCut(KeyEvent.VK_Z, KeyEvent.CTRL_MASK);
    setAltShortCut(KeyEvent.VK_BACK_SPACE, KeyEvent.ALT_MASK);
    setActionCommandKey("cmUndoEdit"); //$NON-NLS-1$
    addActionListener(createActionListener());
    updateUndoInfo();
  } 

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          if (undoManager instanceof DefaultUndoManager) {
            ((DefaultUndoManager)undoManager).fireUndoPerformed();
          }
          else {
            undoManager.undo();
          }
        }
        catch (CannotUndoException ex) {
          ;
        }
        updateUndoInfo();
      }
    };
  }
  
  public void updateUndoInfo() {
    if (undoManager.canUndo()) {
      setText(undoManager.getUndoPresentationName());
    }
    else {
      setText(Messages.getString("CmUndoEdit.text")); //$NON-NLS-1$
    }
    setEnabled(undoManager.canUndo());
  }

}
