package pl.mpak.orbada.sqlscripts.services;

import javax.swing.JMenu;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.sqlscripts.OrbadaSqlScriptsPlugin;
import pl.mpak.orbada.sqlscripts.cm.SqlScriptCallAction;
import pl.mpak.orbada.sqlscripts.cm.SqlScriptDefineAction;
import pl.mpak.orbada.sqlscripts.db.SqlScriptRecord;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SqlScriptPerspectiveProvider extends PerspectiveProvider {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("sql-scripts");

  private JMenu scripts;
  
  public boolean isForDatabase(Database database) {
    return database != null;
  }

  public String getDescription() {
    return stringManager.getString("perspective-provider-description");
  }

  public String getGroupName() {
    return OrbadaSqlScriptsPlugin.pluginGroupName;
  }
  
  public void reloadSqlScripts() {
    scripts.removeAll();
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.setSqlText(  
        "select *\n" +
        "  from osqlscripts\n" +
        " where oss_usr_id = :USR_ID\n" +
        "   and oss_dtp_id = :DTP_ID\n" +
        " order by oss_name");
      query.paramByName("usr_id").setString(accesibilities.getApplication().getUserId());
      query.paramByName("dtp_id").setString(accesibilities.getDatabase().getUserProperties().getProperty("dtp_id"));
      query.open();
      while (!query.eof()) {
        SqlScriptRecord record = new SqlScriptRecord(accesibilities.getApplication().getOrbadaDatabase());
        record.updateFrom(query);
        scripts.add(new SqlScriptCallAction(accesibilities.getDatabase(), record));
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    scripts.addSeparator();
    scripts.add(new SqlScriptDefineAction(this));
  }
  
  public void initialize() {
    scripts = new JMenu();
    scripts.setText(SwingUtil.setButtonText(scripts, stringManager.getString("sql-scripts")));
    reloadSqlScripts();
    accesibilities.addMenu(scripts);
  }
  
}
