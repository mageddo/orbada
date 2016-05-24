/*
 * SyntaxTextAreaActionProvider.java
 * 
 * Created on 2007-10-30, 20:26:10
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.event.ActionEvent;
import pl.mpak.orbada.plugins.providers.abs.ActionProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

/**
 *
 * @author akaluza
 */
public abstract class SyntaxEditorActionProvider extends ActionProvider {

  private SyntaxEditor syntaxEditor;
  
  public void setSyntaxEditr(SyntaxEditor syntaxEditor) {
    this.syntaxEditor = syntaxEditor;
  }

  protected SyntaxEditor getSyntaxEditor(ActionEvent event) {
    if (syntaxEditor == null) {
      return (SyntaxEditor)getParent(event, SyntaxEditor.class);
    }
    return syntaxEditor;
  }

}
