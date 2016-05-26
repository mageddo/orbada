package pl.mpak.orbada.mysql.gui.freezing;

import java.awt.Component;
import javax.swing.Icon;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.mysql.gui.procedures.ProcedureTabbedPane;
import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public class ProcedureFreezeViewService extends FreezeViewService {

  public ProcedureFreezeViewService() {
    super();
  }
  
  public ProcedureFreezeViewService(IViewAccesibilities accesibilities, String schemaName, String objectName) {
    super(accesibilities, schemaName, objectName);
  }

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    OrbadaTabbedPane tab = new ProcedureTabbedPane(accesibilities);
    tab.refresh(null, databaseName, objectName);
    return tab;
  }

  @Override
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/procedure.gif");
  }

  public String getDescription() {
    return "PROCEDURE MySQL";
  }

}
