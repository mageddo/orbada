/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.gui.triggers.TriggerTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class TriggerFreezeViewService extends FreezeViewService {

  public TriggerFreezeViewService() {
    super();
  }
  
  public TriggerFreezeViewService(IViewAccesibilities accesibilities, String schemaName, String objectName) {
    super(accesibilities, schemaName, objectName);
  }

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    OrbadaTabbedPane tab = new TriggerTabbedPane(accesibilities);
    tab.refresh(null, schemaName, objectName);
    return tab;
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif");
  }

}
