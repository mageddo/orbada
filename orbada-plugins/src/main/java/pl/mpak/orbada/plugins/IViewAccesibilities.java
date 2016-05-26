/*
 * IViewAccesibilities.java
 * 
 * Created on 2007-10-27, 14:45:58
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import java.awt.Component;
import javax.swing.JMenu;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public interface IViewAccesibilities {

  public Database getDatabase();

  public IApplication getApplication();

  public void setTabTitle(String title);
  public String getTabTitle();

  public void setTabExtTooltip(String tooltip);
  public String getTabExtTooltip();

  public void setCloseEnabled(boolean enabled);
  public boolean isCloseEnabled();
  
  public IPerspectiveAccesibilities getPerspectiveAccesibilities();
  
  /**
   * <p>Pozwala dodaæ menu do systemu dla widoku. Menu bêdzie zarz¹dzane automatycznie przez
   * obiekt perspektywy. Nie trzeba go na koniec nawet zwalniaæ.
   * @param menu 
   */
  public void addMenu(JMenu menu);
  
  /**
   * <p>Pozwala dodaæ akcje do toolbar-a systemu dla widoku.<br>
   * Akcja bêdzie zarz¹dzane automatycznie przez obiekt perspektywy. 
   * Nie trzeba go na koniec nawet zwalniaæ.
   * @param action 
   */
  public void addAction(Action action);
  
  public Component getViewComponent(ViewProvider view);
  
  public Component[] getViewComponentList(ViewProvider view);

  public Component createView(ViewProvider view);
  
  public ViewProvider getViewProvider();
  
}
