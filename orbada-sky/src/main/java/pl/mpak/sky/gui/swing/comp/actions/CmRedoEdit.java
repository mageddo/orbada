package pl.mpak.sky.gui.swing.comp.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.undo.CannotRedoException;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.DefaultUndoManager;

public class CmRedoEdit extends Action {
  private static final long serialVersionUID = -8981990233880328143L;

  private static ImageIcon icon = null;
  
  private javax.swing.undo.UndoManager undoManager;

  public CmRedoEdit(javax.swing.undo.UndoManager undoManager) {
    super();
    this.undoManager = undoManager;
    setText(Messages.getString("CmRedoEdit.text")); //$NON-NLS-1$
    if (icon == null) {
      icon = pl.mpak.sky.gui.swing.ImageManager.getImage("/res/redo.gif"); //$NON-NLS-1$
    }
    setSmallIcon(icon);
    setShortCut(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    setActionCommandKey("cmRedoEdit"); //$NON-NLS-1$
    addActionListener(createActionListener());
    updateRedoInfo();
  } 

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          if (undoManager instanceof DefaultUndoManager) {
            ((DefaultUndoManager)undoManager).fireRedoPerformed();
          }
          else {
            undoManager.redo();
          }
        }
        catch (CannotRedoException ex) {
          ;
        }
        updateRedoInfo();
      }
    };
  }

  public void updateRedoInfo() {
    if (undoManager.canUndo()) {
      setText(undoManager.getRedoPresentationName());
    }
    else {
      setText(Messages.getString("CmRedoEdit.text")); //$NON-NLS-1$
    }
    setEnabled(undoManager.canRedo());
  }
}
