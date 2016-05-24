package pl.mpak.orbada.firebird.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.firebird.gui.functions.FunctionTabbedPane;
import pl.mpak.orbada.gui.OrbadaTabbedPane;
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
    tab.refresh(null, schemaName, objectName);
    return tab;
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/function.gif");
  }

}
