package pl.mpak.orbada.oracle.services;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import pl.mpak.orbada.gui.comps.OrbadaJavaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.plugins.providers.GlobalFocusProvider;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleFocusProvider extends GlobalFocusProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle");

  public void focusGained(JComponent comp) {
    if (comp instanceof SyntaxEditor) {
      SyntaxEditor editor = (SyntaxEditor)comp;
      OrbadaSyntaxTextArea sqlArea = (OrbadaSyntaxTextArea)SwingUtil.getOwnerComponent(OrbadaSyntaxTextArea.class, editor);
      if (sqlArea != null && sqlArea.getDatabase() != null) {
        OrbadaOraclePlugin.findObjectAction.setEnabled(true);
        OrbadaOraclePlugin.findObjectAction.setDatabase(sqlArea.getDatabase());
        OrbadaOraclePlugin.findObjectAction.setSyntaxEditor(editor);
        OrbadaOraclePlugin.callObjectAction.setEnabled(true);
        OrbadaOraclePlugin.callObjectAction.setDatabase(sqlArea.getDatabase());
        OrbadaOraclePlugin.callObjectAction.setSyntaxEditor(editor);
      }
      else {
        OrbadaJavaSyntaxTextArea javaArea = (OrbadaJavaSyntaxTextArea)SwingUtil.getOwnerComponent(OrbadaJavaSyntaxTextArea.class, editor);
        if (javaArea != null && javaArea.getDatabase() != null) {
          OrbadaOraclePlugin.findObjectAction.setEnabled(true);
          OrbadaOraclePlugin.findObjectAction.setDatabase(javaArea.getDatabase());
          OrbadaOraclePlugin.findObjectAction.setSyntaxEditor(editor);
          OrbadaOraclePlugin.callObjectAction.setEnabled(true);
          OrbadaOraclePlugin.callObjectAction.setDatabase(javaArea.getDatabase());
          OrbadaOraclePlugin.callObjectAction.setSyntaxEditor(editor);
        }
        else {
          OrbadaOraclePlugin.findObjectAction.setEnabled(false);
          OrbadaOraclePlugin.findObjectAction.setDatabase(null);
          OrbadaOraclePlugin.findObjectAction.setSyntaxEditor(null);
          OrbadaOraclePlugin.callObjectAction.setEnabled(false);
          OrbadaOraclePlugin.callObjectAction.setDatabase(null);
          OrbadaOraclePlugin.callObjectAction.setSyntaxEditor(null);
        }
      }
    }
    else if (!(comp instanceof JRootPane) /*&& !(comp instanceof AbstractButton)*/) {
      OrbadaOraclePlugin.findObjectAction.setEnabled(false);
      OrbadaOraclePlugin.findObjectAction.setDatabase(null);
      OrbadaOraclePlugin.findObjectAction.setSyntaxEditor(null);
      OrbadaOraclePlugin.callObjectAction.setEnabled(false);
      OrbadaOraclePlugin.callObjectAction.setDatabase(null);
      OrbadaOraclePlugin.callObjectAction.setSyntaxEditor(null);
    }
  }

  public void focusLost(JComponent comp) {
  }

  public String getDescription() {
    return stringManager.getString("OracleFocusProvider-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }


}
