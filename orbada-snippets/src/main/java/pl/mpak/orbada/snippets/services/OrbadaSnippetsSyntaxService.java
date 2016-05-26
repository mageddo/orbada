/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.snippets.services;

import pl.mpak.orbada.plugins.providers.OrbadaSyntaxTextAreaProvider;
import pl.mpak.orbada.snippets.OrbadaSnippetsPlugin;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OrbadaSnippetsSyntaxService extends OrbadaSyntaxTextAreaProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager("snippets");
  
  @Override
  public String getDescription() {
    return stringManager.getString("OrbadaSnippetsSyntaxService-description");
  }

  @Override
  public String getGroupName() {
    return OrbadaSnippetsPlugin.pluginGroupName;
  }
  
  @Override
  public void setSyntaxTextArea(final SyntaxTextArea syntaxTextArea) {
    super.setSyntaxTextArea(syntaxTextArea);
    if (syntaxTextArea != null) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          OrbadaSnippetsPlugin.getSnippetsManager().refreshSnippets(syntaxTextArea);
        }
      });
    }
  }
  
}
