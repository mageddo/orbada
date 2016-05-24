package pl.mpak.orbada.oracle.services;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.gui.dbmsoutput.DbmsOutputPanelView;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalResultDbmsOutputProvider extends UniversalActionProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    if (OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType())) {
      setText(stringManager.getString("UniversalResultDbmsOutputProvider-text"));
      setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/output.gif"));
      setActionCommandKey("UniversalResultDbmsOutputProvider");
      addActionListener(createActionListener());
      return true;
    }
    return false;
  }

  public boolean addToolButton() {
    return true;
  }

  public boolean addMenuItem() {
    return true;
  }

  public boolean addToEditor() {
    return false;
  }

  public String getDescription() {
    return stringManager.getString("UniversalResultDbmsOutputProvider-description");
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Component[] array = accessibilities.getResultTabs(DbmsOutputPanelView.class);
        if (array.length == 0) {
          DbmsOutputPanelView panel =  new DbmsOutputPanelView(accessibilities.getViewAccesibilities());
          accessibilities.addResultTab(getText(), panel);
        }
      }
    };
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

}
