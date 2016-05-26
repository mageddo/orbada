package pl.mpak.orbada.sqlite.gui.views;

import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.sqlite.OrbadaSQLitePlugin;
import pl.mpak.orbada.sqlite.util.SourceCreator;
import pl.mpak.orbada.universal.gui.tabbed.UniversalSourceTab;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class ViewSourcePanel extends UniversalSourceTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("sqlite");

  public ViewSourcePanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("ViewSourcePanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "sqlite-view-source";
  }

  @Override
  public void updateBody(AbsOrbadaSyntaxTextArea textArea) {
    try {
      new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "VIEW", currentObjectName);
    }
    catch (Exception ex) {
      textArea.setDatabaseObject(null, null, null, "");
    }
  }

  @Override
  public boolean isStorable() {
    return true;
  }
  
}
