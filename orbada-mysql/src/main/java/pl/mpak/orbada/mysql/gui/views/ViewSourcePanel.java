package pl.mpak.orbada.mysql.gui.views;

import orbada.gui.comps.AbsOrbadaSyntaxTextArea;
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
public class ViewSourcePanel extends UniversalSourceTab {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaMySQLPlugin.class);

  public ViewSourcePanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }
  
  public String getTitle() {
    return stringManager.getString("ViewSourcePanel-title");
  }
  
  @Override
  public String getPanelName() {
    return "mysql-view-source";
  }

  @Override
  public void updateBody(AbsOrbadaSyntaxTextArea textArea) {
    new SourceCreator(getDatabase(), textArea).getSource(currentSchemaName, "VIEW", currentObjectName);
  }

  @Override
  public boolean isStorable() {
    return true;
  }
  
}
