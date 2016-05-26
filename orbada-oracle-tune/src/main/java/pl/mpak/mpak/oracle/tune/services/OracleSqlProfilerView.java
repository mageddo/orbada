/*
 * DerbyDbObjectsView.java
 * 
 * Created on 2007-10-28, 16:42:01
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.mpak.oracle.tune.services;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.mpak.oracle.tune.OrbadaOracleTunePlugin;
import pl.mpak.mpak.oracle.tune.Sql;
import pl.mpak.mpak.oracle.tune.gui.profiler.RunsPanel;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OracleSqlProfilerView extends ViewProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("oracle-tune");

  public Component createView(IViewAccesibilities accesibilities) {
    Query query = accesibilities.getDatabase().createQuery();
    try {
      query.open(Sql.getProfilerTableExists());
      if (!query.eof() && query.fieldByName("cnt").getInteger() >= 3) {
        return new RunsPanel(accesibilities);
      }
      MessageBox.show(getPublicName(), stringManager.getString("OracleSqlProfilerView-no-objects-info"));
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return null;
  }
  
  public String getPublicName() {
    return stringManager.getString("OracleSqlProfilerView-public-name");
  }
  
  public String getViewId() {
    return "orbada-oracle-tune-profiler-view";
  }
  
  public Icon getIcon() {
    return pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/profiler.gif");
  }

  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  public String getDescription() {
    return stringManager.getString("OracleSqlProfilerView-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }
  
  @Override
  public String[] getSubmenu() {
    return new String[] {OrbadaOracleTunePlugin.tuneGroup};
  }

}
