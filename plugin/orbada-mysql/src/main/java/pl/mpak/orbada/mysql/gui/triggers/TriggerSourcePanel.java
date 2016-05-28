package pl.mpak.orbada.mysql.gui.triggers;

import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.mysql.OrbadaMySQLPlugin;
import pl.mpak.orbada.mysql.util.SourceCreator;
import pl.mpak.orbada.universal.gui.tabbed.UniversalSourceTab;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class TriggerSourcePanel extends UniversalSourceTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("mysql");

  public TriggerSourcePanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("TriggerSourcePanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-trigger-source";
  }

  @Override
  public void updateBody(AbsOrbadaSyntaxTextArea textArea) {
    new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "TRIGGER", currentObjectName);
  }

  @Override
  public boolean isStorable() {
    return true;
  }
  
}
