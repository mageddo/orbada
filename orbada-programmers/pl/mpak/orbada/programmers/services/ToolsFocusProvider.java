/*
 * ToolsFocusProvider.java
 * 
 * Created on 2007-11-03, 13:41:22
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.programmers.services;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import pl.mpak.orbada.plugins.providers.GlobalFocusProvider;
import pl.mpak.orbada.programmers.OrbadaProgrammersPlugin;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ToolsFocusProvider extends GlobalFocusProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaProgrammersPlugin.class);

  public void focusGained(JComponent comp) {
    if (comp instanceof SyntaxEditor) {
      SyntaxEditor editor = (SyntaxEditor)comp;
      OrbadaProgrammersPlugin.cmCopyCode.setEnabled(true);
      OrbadaProgrammersPlugin.cmPasteCode.setEnabled(editor.isEditable());
      OrbadaProgrammersPlugin.cmCopyCode.setEditor(editor);
      OrbadaProgrammersPlugin.cmPasteCode.setEditor(editor);
    }
    else if (!(comp instanceof JRootPane) /*&& !(comp instanceof AbstractButton)*/) {
      OrbadaProgrammersPlugin.cmCopyCode.setEditor(null);
      OrbadaProgrammersPlugin.cmCopyCode.setEnabled(false);
      OrbadaProgrammersPlugin.cmPasteCode.setEditor(null);
      OrbadaProgrammersPlugin.cmPasteCode.setEnabled(false);
    }
  }

  public void focusLost(JComponent comp) {
  }

  public String getDescription() {
    return stringManager.getString("ToolsFocusProvider-description");
  }

  public String getGroupName() {
    return OrbadaProgrammersPlugin.programmersGroupName;
  }


}
