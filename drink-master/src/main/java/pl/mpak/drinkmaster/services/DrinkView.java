package pl.mpak.drinkmaster.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.drinkmaster.DrinkMasterPlugin;
import pl.mpak.drinkmaster.gui.DrinkViewPanel;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DrinkView extends ViewProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("drink-master");

  public Component createView(IViewAccesibilities accesibilities) {
    return new DrinkViewPanel(accesibilities);
  }
  
  public String getPublicName() {
    return stringManager.getString("drinks");
  }
  
  public String getViewId() {
    return "drink-master-view";
  }
  
  public Icon getIcon() {
    return new javax.swing.ImageIcon(getClass().getResource("/res/drink.gif"));
  }

  public boolean isForDatabase(Database database) {
    return true;
  }

  public String getDescription() {
    return stringManager.getString("drink-view-provider-description");
  }

  public String getGroupName() {
    return DrinkMasterPlugin.pluginGroupName;
  }
  
}
