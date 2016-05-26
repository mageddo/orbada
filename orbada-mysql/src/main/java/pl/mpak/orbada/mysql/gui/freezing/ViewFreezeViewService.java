package pl.mpak.orbada.mysql.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.mysql.gui.views.ViewTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class ViewFreezeViewService extends FreezeViewService {

  public ViewFreezeViewService() {
    super();
  }
  
  public ViewFreezeViewService(IViewAccesibilities accesibilities, String schemaName, String objectName) {
    super(accesibilities, schemaName, objectName);
  }

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    OrbadaTabbedPane tab = new ViewTabbedPane(accesibilities);
    tab.refresh(null, databaseName, objectName);
    return tab;
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/view.gif");
  }

  public String getDescription() {
    return "VIEW MySQL";
  }

}
