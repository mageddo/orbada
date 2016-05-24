package pl.mpak.orbada.mysql.services;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import pl.mpak.orbada.gui.comps.OrbadaJavaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.plugins.providers.GlobalFocusProvider;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class MySQLFocusProvider extends GlobalFocusProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public void focusGained(JComponent comp) {
    if (comp instanceof SyntaxEditor) {
      SyntaxEditor editor = (SyntaxEditor)comp;
      OrbadaSyntaxTextArea sqlArea = (OrbadaSyntaxTextArea)SwingUtil.getOwnerComponent(OrbadaSyntaxTextArea.class, editor);
      if (sqlArea != null && sqlArea.getDatabase() != null) {
        OrbadaMySQLPlugin.findObjectAction.setEnabled(true);
        OrbadaMySQLPlugin.findObjectAction.setDatabase(sqlArea.getDatabase());
        OrbadaMySQLPlugin.findObjectAction.setSyntaxEditor(editor);
      }
      else {
        OrbadaJavaSyntaxTextArea javaArea = (OrbadaJavaSyntaxTextArea)SwingUtil.getOwnerComponent(OrbadaJavaSyntaxTextArea.class, editor);
        if (javaArea != null && javaArea.getDatabase() != null) {
          OrbadaMySQLPlugin.findObjectAction.setEnabled(true);
          OrbadaMySQLPlugin.findObjectAction.setDatabase(javaArea.getDatabase());
          OrbadaMySQLPlugin.findObjectAction.setSyntaxEditor(editor);
        }
        else {
          OrbadaMySQLPlugin.findObjectAction.setEnabled(false);
          OrbadaMySQLPlugin.findObjectAction.setDatabase(null);
          OrbadaMySQLPlugin.findObjectAction.setSyntaxEditor(null);
        }
      }
    }
    else if (!(comp instanceof JRootPane) /*&& !(comp instanceof AbstractButton)*/) {
      OrbadaMySQLPlugin.findObjectAction.setEnabled(false);
      OrbadaMySQLPlugin.findObjectAction.setDatabase(null);
      OrbadaMySQLPlugin.findObjectAction.setSyntaxEditor(null);
    }
  }

  public void focusLost(JComponent comp) {
  }

  public String getDescription() {
    return stringManager.getString("MySQLFocusProvider-description");
  }

  public String getGroupName() {
    return OrbadaMySQLPlugin.driverType;
  }


}
