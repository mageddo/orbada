/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.localhistory.services;

import java.util.EventObject;
import javax.swing.JMenu;
import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.gui.comps.SyntaxDatabaseObjectListener;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.cm.LastChangeObjectDescriptionAction;
import pl.mpak.orbada.localhistory.cm.LocalHistoryObjectAction;
import pl.mpak.orbada.localhistory.core.SchemaObjects;
import pl.mpak.orbada.localhistory.db.OlhObjectRecord;
import pl.mpak.orbada.plugins.providers.OrbadaSyntaxTextAreaProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class LocalHistorySyntaxService extends OrbadaSyntaxTextAreaProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("local-history");

  private JMenu menu;
  
  public String getDescription() {
    return stringManager.getString("LocalHistorySyntaxService-description");
  }

  public String getGroupName() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }

  @Override
  public void setSyntaxTextArea(SyntaxTextArea syntaxTextArea) {
    super.setSyntaxTextArea(syntaxTextArea);
    if (syntaxTextArea instanceof AbsOrbadaSyntaxTextArea) {
      ((AbsOrbadaSyntaxTextArea)syntaxTextArea).addDatabaseObjectListener(new SyntaxDatabaseObjectListener() {
        public void beforeChanged(EventObject e) {
        }
        public void afterChanged(EventObject e) {
          updateSource((AbsOrbadaSyntaxTextArea)e.getSource());
        }
        public void beforeStoreObject(EventObject e) {
        }
        public void afterStoreObject(EventObject e) {
          updateSource((AbsOrbadaSyntaxTextArea)e.getSource());
        }
        public void storeObjectError(EventObject e) {
        }
      });
      menu = new JMenu(stringManager.getString("LocalHistorySyntaxService-menu"));
      menu.setEnabled(false);
      menu.add(new LocalHistoryObjectAction((AbsOrbadaSyntaxTextArea)syntaxTextArea));
      menu.add(new LastChangeObjectDescriptionAction((AbsOrbadaSyntaxTextArea)syntaxTextArea));
      syntaxTextArea.getEditorArea().getComponentPopupMenu().add(menu);
    }
  }

  private void updateSource(final AbsOrbadaSyntaxTextArea syntaxTextArea) {
    SchemaObjects sh = LocalHistoryDatabaseService.getSchemaObjects(syntaxTextArea.getDatabase());
    if (sh != null && sh.isTurnedOn() && !StringUtil.equals(syntaxTextArea.getObjectName(), "") && !StringUtil.equals(syntaxTextArea.getObjectType(), "")) {
      menu.setEnabled(true);
      String source = syntaxTextArea.getText();
      String schemaName = syntaxTextArea.getSchemaName();
      String objectType = syntaxTextArea.getObjectType();
      String objectName = syntaxTextArea.getObjectName();
      if (!"".equals(source)) {
        OlhObjectRecord olho = sh.getObject(schemaName, objectType, objectName);
        if (olho == null) {
          sh.putObject(schemaName, objectType, objectName, source);
        }
        else if (!source.equals(olho.getSource())) {
          sh.putObject(schemaName, objectType, objectName, source);
        }
      }
    }
    else {
      menu.setEnabled(false);
    }
  }
  
}
