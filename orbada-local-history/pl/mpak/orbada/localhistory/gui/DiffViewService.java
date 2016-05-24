package pl.mpak.orbada.localhistory.gui;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.orbada.localhistory.OrbadaLocalHistoryPlugin;
import pl.mpak.orbada.localhistory.db.OlhObjectRecord;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DiffViewService extends ViewProvider {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaLocalHistoryPlugin.class);

  private OlhObjectRecord olho;
  private SyntaxTextArea syntaxTextArea;
  
  public DiffViewService(OlhObjectRecord olho, SyntaxTextArea syntaxTextArea) {
    this.olho = olho;
    this.syntaxTextArea = syntaxTextArea;
  }

  public Component createView(IViewAccesibilities accesibilities) {
    return new DiffViewPanel(accesibilities, olho, syntaxTextArea);
  }
  
  public String getPublicName() {
    return olho.toString();
  }
  
  public String getViewId() {
    return null;
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/diff.gif");
  }

  public boolean isForDatabase(Database database) {
    return true;
  }

  public String getDescription() {
    return String.format(stringManager.getString("DiffViewService-description"), new Object[] {olho.toString()});
  }

  public String getGroupName() {
    return OrbadaLocalHistoryPlugin.pluginGroupName;
  }
  
}
