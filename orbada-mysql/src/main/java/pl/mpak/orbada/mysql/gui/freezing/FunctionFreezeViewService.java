package pl.mpak.orbada.mysql.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.mysql.gui.functions.FunctionTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class FunctionFreezeViewService extends FreezeViewService {

  public FunctionFreezeViewService() {
    super();
  }
  
  public FunctionFreezeViewService(IViewAccesibilities accesibilities, String schemaName, String objectName) {
    super(accesibilities, schemaName, objectName);
  }

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    OrbadaTabbedPane tab = new FunctionTabbedPane(accesibilities);
    tab.refresh(null, databaseName, objectName);
    return tab;
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/function.gif");
  }

  public String getDescription() {
    return "FUNCTION MySQL";
  }

}
