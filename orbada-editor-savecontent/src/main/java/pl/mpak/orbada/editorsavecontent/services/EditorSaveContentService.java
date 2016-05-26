/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.editorsavecontent.services;

import pl.mpak.orbada.editorsavecontent.OrbadaEditorSaveContentPlugin;
import pl.mpak.orbada.editorsavecontent.cm.EditorSaveContentAction;
import pl.mpak.orbada.plugins.providers.OrbadaSyntaxTextAreaProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class EditorSaveContentService extends OrbadaSyntaxTextAreaProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaEditorSaveContentPlugin.class);

  public String getDescription() {
    return stringManager.getString("EditorSaveContentService.description");
  }

  public String getGroupName() {
    return OrbadaEditorSaveContentPlugin.pluginGroupName;
  }
  
  @Override
  public void setSyntaxTextArea(SyntaxTextArea syntaxTextArea) {
    super.setSyntaxTextArea(syntaxTextArea);
    if (syntaxTextArea instanceof SyntaxTextArea) {
      syntaxTextArea.getEditorArea().getComponentPopupMenu().add(new EditorSaveContentAction(syntaxTextArea));
    }
  }

}
