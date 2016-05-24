/*
 * IPerspectiveAccesibilities.java
 * 
 * Created on 2007-11-11, 18:59:46
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public interface IPerspectiveAccesibilities {

  public Database getDatabase();
  
  public IApplication getApplication();
  
  /**
   * <p>Pozwala dodaæ menu do systemu dla perspektywy. Menu bêdzie zarz¹dzane automatycznie przez
   * obiekt perspektywy. Nie trzeba go na koniec nawet zwalniaæ.
   * @param menu 
   */
  public void addMenu(JMenu menu);
  
  /**
   * <p>Pozwala dodaæ akcje do toolbar-a systemu dla perspektywy.<br>
   * Akcja bêdzie zarz¹dzane automatycznie przez obiekt perspektywy. 
   * Nie trzeba go na koniec nawet zwalniaæ.
   * @param action 
   */
  public void addAction(Action action);
  
  /**
   * <p>Pozwala dodaæ toolbar do listy toolbar-ów systemu dla perspektywy.<br>
   * Toolbar bêdzie zarz¹dzany automatycznie przez perspektywê.<br>
   * Nie trzeba go zwalniaæ na koniec.
   * @param toolBar
   */
  public void addToolBar(JToolBar toolBar);
  
  /**
   * <p> Pozwala dodaæ status bar perspektywy<br>
   * Perspektywa bêdzie zarz¹dana automatycznie. Nie trzeba go na koniec zwalniaæ.
   * @param statusBar
   */
  public void addStatusBar(JComponent statusBar);
  
  public Component getViewComponent(ViewProvider view);
  
  public Component createView(ViewProvider view);
  
  public void closeView(Component component);
  
  public void setSelectedView(Component c);
  
  public String getPerspectiveId();
  
}
