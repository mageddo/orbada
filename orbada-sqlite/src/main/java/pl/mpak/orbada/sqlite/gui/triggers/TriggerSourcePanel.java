package pl.mpak.orbada.sqlite.gui.triggers;

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
public class TriggerSourcePanel extends UniversalSourceTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSQLitePlugin.class);

  public TriggerSourcePanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TriggerSourcePanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "sqlite-trigger-source";
  }

  @Override
  public void updateBody(AbsOrbadaSyntaxTextArea textArea) {
    try {
      new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "TRIGGER", currentObjectName);
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
