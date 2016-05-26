package pl.mpak.sky.gui.swing.comp;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import pl.mpak.sky.gui.swing.comp.actions.CmCopyEdit;
import pl.mpak.sky.gui.swing.comp.actions.CmCutEdit;
import pl.mpak.sky.gui.swing.comp.actions.CmPasteEdit;
import pl.mpak.sky.gui.swing.comp.actions.CmSelectAllEdit;

/**
 * Klasa TextPopupMenu s³u¿y do obs³ugi menu podrêcznego dla komponentów
 * edycyjnych (JTextComponent)
 * 
 * W kodzie wystarczy go poprostu utworzyæ
 * JTextComponent.setComponentPopupMen(new TextPopupMenu(JTextComponent));
 * podepnie siê on automatycznie
 * 
 * @author Administrator
 * 
 */
public class PopupMenuText extends PopupMenu {
  private static final long serialVersionUID = -4053506004234272374L;

  public CmCutEdit cmCut = null;
  public CmCopyEdit cmCopy = null;
  public CmPasteEdit cmPaste = null;
  public CmSelectAllEdit cmSelectAll = null;

  public PopupMenuText(JTextComponent textComponent) {
    super(textComponent);
    init();
    textComponent.addCaretListener(new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        updateActions();
      }
    });
  }

  private void init() {
    cmCut = new CmCutEdit((JTextComponent) getPopupComponent());
    cmCopy = new CmCopyEdit((JTextComponent) getPopupComponent());
    cmPaste = new CmPasteEdit((JTextComponent) getPopupComponent());
    cmSelectAll = new CmSelectAllEdit((JTextComponent) getPopupComponent());
    addMenuEntries();
  }

  private void addMenuEntries() {
    add(cmCut);
    add(cmCopy);
    add(cmPaste);
    addSeparator();
    add(cmSelectAll);
  }

}
