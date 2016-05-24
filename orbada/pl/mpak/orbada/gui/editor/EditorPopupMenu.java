/*
 * EditorPopupMenu.java
 *
 * Created on 2007-10-19, 20:48:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.editor;

import pl.mpak.sky.gui.swing.comp.PopupMenuText;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;

/**
 *
 * @author akaluza
 */
public class EditorPopupMenu extends PopupMenuText {
  
  public EditorPopupMenu(SyntaxTextArea textComponent) {
    super(textComponent.getEditorArea());
    init();
  }
  
  private void init() {
    
  }
  
}
