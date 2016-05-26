package pl.mpak.orbada.mysql.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.mysql.gui.triggers.TriggerTabbedPane;
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
    tab.refresh(null, databaseName, objectName);
    return tab;
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif");
  }

  public String getDescription() {
    return "TRIGGER MySQL";
  }

}
