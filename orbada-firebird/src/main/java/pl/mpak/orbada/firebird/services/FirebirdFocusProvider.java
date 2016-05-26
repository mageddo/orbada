package pl.mpak.orbada.firebird.services;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import pl.mpak.orbada.firebird.OrbadaFirebirdPlugin;
import orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.providers.GlobalFocusProvider;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

/**
 *
 * @author akaluza
 */
public class FirebirdFocusProvider extends GlobalFocusProvider {

  public void focusGained(JComponent comp) {
    if (comp instanceof SyntaxEditor) {
      SyntaxEditor editor = (SyntaxEditor)comp;
      OrbadaSyntaxTextArea sqlArea = (OrbadaSyntaxTextArea)SwingUtil.getOwnerComponent(OrbadaSyntaxTextArea.class, editor);
      if (sqlArea != null && sqlArea.getDatabase() != null) {
        OrbadaFirebirdPlugin.findObjectAction.setEnabled(true);
        OrbadaFirebirdPlugin.findObjectAction.setDatabase(sqlArea.getDatabase());
        OrbadaFirebirdPlugin.findObjectAction.setSyntaxEditor(editor);
      }
      else {
        OrbadaFirebirdPlugin.findObjectAction.setEnabled(false);
        OrbadaFirebirdPlugin.findObjectAction.setDatabase(null);
        OrbadaFirebirdPlugin.findObjectAction.setSyntaxEditor(null);
      }
    }
    else if (!(comp instanceof JRootPane) /*&& !(comp instanceof AbstractButton)*/) {
      OrbadaFirebirdPlugin.findObjectAction.setEnabled(false);
      OrbadaFirebirdPlugin.findObjectAction.setDatabase(null);
      OrbadaFirebirdPlugin.findObjectAction.setSyntaxEditor(null);
    }
  }

  public void focusLost(JComponent comp) {
  }

  public String getDescription() {
    return "Firebird Global Focus Provider";
  }

  public String getGroupName() {
    return OrbadaFirebirdPlugin.firebirdDriverType;
  }


}
