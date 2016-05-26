package orbada.gui.services;

import java.awt.Component;
import java.io.File;
import javax.swing.Icon;
import orbada.Consts;
import orbada.gui.TextFileViewPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TextFileViewService extends ViewProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private File file;

  public TextFileViewService(File file) {
    this.file = file;
  }

  @Override
  public Component createView(IViewAccesibilities accesibilities) {
    return new TextFileViewPanel(accesibilities, file);
  }
  
  @Override
  public String getPublicName() {
    return String.format("Plik \"%s\"", new Object[] {file.getName()});
  }
  
  @Override
  public String getViewId() {
    return null;
  }
  
  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public boolean isForDatabase(Database database) {
    return true;
  }

  @Override
  public String getDescription() {
    return file.getPath();
  }

  @Override
  public String getGroupName() {
    return "Narzêdzia";
  }
  
}
