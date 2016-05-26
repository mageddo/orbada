/*
 * SyntaxTextAreaActionProvider.java
 * 
 * Created on 2007-10-30, 20:26:10
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;

/**
 * <p>Serwis udostêpniaj¹cy syntaxTextArea. Wywo³ywany jest przy ka¿dym tworzeniu edytora.<br>
 * Wywy³ywane jest dla OrbadaJavaSyntaxTextArea czyli dla edytora SQL.
 * @author akaluza
 */
public abstract class OrbadaJavaSyntaxTextAreaProvider implements IOrbadaPluginProvider {

  protected IApplication application;
  protected SyntaxTextArea syntaxTextArea;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return false;
  }
  
  /**
   * <p>Dopiero tutaj mo¿na zainicjowaæ np listenery edytora
   * @param syntaxTextArea
   */
  public void setSyntaxTextArea(SyntaxTextArea syntaxTextArea) {
    this.syntaxTextArea = syntaxTextArea;
  }

  protected SyntaxTextArea getSyntaxTextArea() {
    return syntaxTextArea;
  }

}
