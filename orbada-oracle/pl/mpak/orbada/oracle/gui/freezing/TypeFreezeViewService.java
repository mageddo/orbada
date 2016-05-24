/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.types.TypeTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class TypeFreezeViewService extends FreezeViewService {

  public TypeFreezeViewService() {
    super();
  }
  
  public TypeFreezeViewService(IViewAccesibilities accesibilities, String schemaName, String objectName) {
    super(accesibilities, schemaName, objectName);
  }

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    OrbadaTabbedPane tab = new TypeTabbedPane(accesibilities);
    tab.refresh(null, schemaName, objectName);
    return tab;
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/type.gif");
  }

}
