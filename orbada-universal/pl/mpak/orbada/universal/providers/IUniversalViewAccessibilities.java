/*
 * IUniversalViewAccessibilities.java
 * 
 * Created on 2007-10-30, 20:53:44
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.providers;

import java.awt.Component;
import javax.swing.JComponent;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

/**
 *
 * @author akaluza
 */
public interface IUniversalViewAccessibilities {

  public IViewAccesibilities getViewAccesibilities();
  
  public SyntaxEditor getSyntaxEditor();
  
  public void addResultTab(String title, JComponent component);
  
  public Component[] getResultTabs(Class<? extends Component> clazz);
  
  public void setSelectedTab(Component component);

  public void setTabTooltip(Component component, String tooltip);

  public void setTabTitle(Component component, String title);
  
}
