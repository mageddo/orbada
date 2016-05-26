/*
 * OrbadaPluginManager.java
 *
 * Created on 2007-10-17, 17:42:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.core;

import pl.mpak.orbada.Consts;
import org.apache.log4j.Logger;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.OrbadaPlugin;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.orbada.plugins.queue.PluginQueue;
import pl.mpak.plugins.CallMethod;
import pl.mpak.plugins.Plugin;
import pl.mpak.plugins.PluginFound;
import pl.mpak.plugins.PluginManager;
import pl.mpak.plugins.PluginManagerListener;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OrbadaPluginManager extends PluginManager {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private Logger logger;
  private PluginQueue queue;
  
  public OrbadaPluginManager(Logger logger) {
    super(pl.mpak.orbada.plugins.OrbadaPlugin.class);
    queue = new PluginQueue(this);
    queue.start();
    this.logger = logger;
    setLogger(logger);
    addPluginManagerListener(new PluginManagerListener() {
      @Override
      public void beginProcess(ManageProcess type, int count) {
      }
      @Override
      public void process(ManageProcess type, IPlugin plugin) {
        if (type == PluginManagerListener.ManageProcess.LOAD) {
          OrbadaPluginManager.this.logger.debug("Plugin \"" +plugin.getDescriptiveName() +"\" loaded");
        } else if (type == PluginManagerListener.ManageProcess.UNLOAD) {
          OrbadaPluginManager.this.logger.debug("Plugin \"" +plugin.getDescriptiveName() +"\" unloaded");
        } else if (type == PluginManagerListener.ManageProcess.INITIALIZE) {
          OrbadaPluginManager.this.logger.debug("Plugin \"" +plugin.getDescriptiveName() +"\" initialized");
        }
      }
      @Override
      public void endProcess(ManageProcess type) {
      }
    });
  }
  
  private void checkFoundPlugins() {
    if (InternalDatabase.get() == null) {
      return;
    }
    Query query = InternalDatabase.get().createQuery();
    try {
      try {
        query.setCloseResultAfterOpen(true);
        query.setSqlText("select plg_class_name, plg_enabled from plugins where plg_usr_id = :USR_ID");
        query.paramByName("USR_ID").setString(Application.get().getUserId());
        query.open();
        for (int i=0; i<getFoundList().size(); i++) {
          PluginFound pf = getFoundList().get(i);
          if (query.locate("plg_class_name", new Variant(pf.getClassName()))) {
            pf.setEnabled(StringUtil.toBoolean(query.fieldByName("plg_enabled").getString()));
          }
          logger.debug("Founded plugin \"" +pf.getClassName() +"\" " +(pf.isEnabled() ? "enabled" : "disabled"));
        }
      } catch(Exception ex) {
        ExceptionUtil.processException(ex);
      }
    } finally {
      query.close();
    }
  }
  
  private void updateLoadedPlugins() {
    //Logger.getLogger("orbada").info("updateLoadedPlugins:1");
    if (InternalDatabase.get() == null) {
      return;
    }
    //Logger.getLogger("orbada").info("updateLoadedPlugins:2");
    Query query = InternalDatabase.get().createQuery();
    try {
      Application.renderSplashText(stringManager.getString("OrbadaPluginManager-register-plugin-3dot"));
      try {
        Command resetCommand = InternalDatabase.get().createCommand("update plugins set plg_loaded = 'N' where plg_usr_id = :USR_ID", false);
        resetCommand.paramByName("usr_id").setString(Application.get().getUserId());
        resetCommand.execute();

        Command updateCommand = InternalDatabase.get().createCommand(
          "update plugins\n" +
          "   set plg_file_name = :plg_file_name,\n" +
          "       plg_class_name = :plg_class_name,\n" +
          "       plg_description = :plg_description,\n" +
          "       plg_author = :plg_author,\n" +
          "       plg_version = :plg_version,\n" +
          "       plg_web_site = :plg_web_site,\n" +
          "       plg_update_site = :plg_update_site\n" +
          " where plg_id = :plg_id\n" +
          "   and plg_usr_id = :USR_ID", false);
        Command insertCommand = InternalDatabase.get().createCommand(
          "insert into plugins (\n" +
          "  plg_id, plg_usr_id, plg_file_name, plg_class_name, plg_description, plg_author, plg_version,\n" +
          "  plg_web_site, plg_update_site, plg_enabled, plg_loaded)\n" +
          "values (\n" +
          "  :plg_id, :plg_usr_id, :plg_file_name, :plg_class_name, :plg_description, :plg_author, :plg_version,\n" +
          "  :plg_web_site, :plg_update_site, 'T', 'T')", false);
        Command loaded = InternalDatabase.get().createCommand("update plugins set plg_loaded = 'T' where plg_id = :plg_id and plg_usr_id = :USR_ID", false);
        query.setCloseResultAfterOpen(true);
        query.setSqlText("select plg_id, plg_version from plugins where plg_usr_id = :USR_ID");
        query.paramByName("USR_ID").setString(Application.get().getUserId());
        query.open();
        for (int i=0; i<getPluginList().size(); i++) {
          Plugin p = getPluginList().get(i);
          if (query.locate("plg_id", new Variant(p.getUniqueID()))) {
            if (p.getPlugin() instanceof OrbadaPlugin && !query.isEmpty()) {
              ((OrbadaPlugin)p.getPlugin()).setLastVersion(query.fieldByName("plg_version").getString());
            }
            loaded.paramByName("plg_id").setString(p.getUniqueID());
            loaded.paramByName("usr_id").setString(Application.get().getUserId());
            loaded.execute();
            if (!p.getVersion().equals(query.fieldByName("plg_version").getString())) {
              try {
                updateCommand.paramByName("plg_id").setString(p.getUniqueID());
                updateCommand.paramByName("plg_file_name").setString(p.getSource());
                updateCommand.paramByName("plg_class_name").setString(p.getClassName());
                updateCommand.paramByName("plg_description").setString(p.getDescriptiveName());
                updateCommand.paramByName("plg_author").setString(p.getAuthor());
                updateCommand.paramByName("plg_version").setString(p.getVersion());
                updateCommand.paramByName("plg_web_site").setString(p.getWebSite());
                updateCommand.paramByName("plg_update_site").setString(p.getUpdateSite());
                updateCommand.paramByName("usr_id").setString(Application.get().getUserId());
                updateCommand.execute();
              } catch(Exception ex) {
                ExceptionUtil.processException(ex);
              }
            }
          } else {
            try {
              insertCommand.paramByName("plg_id").setString(p.getUniqueID());
              insertCommand.paramByName("plg_usr_id").setString(Application.get().getUserId());
              insertCommand.paramByName("plg_file_name").setString(p.getSource());
              insertCommand.paramByName("plg_class_name").setString(p.getClassName());
              insertCommand.paramByName("plg_description").setString(p.getDescriptiveName());
              insertCommand.paramByName("plg_author").setString(p.getAuthor());
              insertCommand.paramByName("plg_version").setString(p.getVersion());
              insertCommand.paramByName("plg_web_site").setString(p.getWebSite());
              insertCommand.paramByName("plg_update_site").setString(p.getUpdateSite());
              insertCommand.execute();
            } catch(Exception ex) {
              ExceptionUtil.processException(ex);
            }
          }
        }
      } catch(Exception ex) {
        ExceptionUtil.processException(ex);
      }
    } finally {
      query.close();
    }
  }
  
  public void setEnabled(String id, boolean enabled) {
    Command command = InternalDatabase.get().createCommand();
    try {
      command.setSqlText("update plugins set plg_enabled = :plg_enabled where plg_id = :plg_id and plg_usr_id = :USR_ID");
      command.paramByName("plg_enabled").setString(enabled ? "T" : "N");
      command.paramByName("plg_id").setString(id);
      command.paramByName("usr_id").setString(Application.get().getUserId());
      command.execute();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  @Override
  public <T extends IPluginProvider> T[] getServiceArray(Class<T> t) {
    Application app = Application.get();
    T[] array = super.getServiceArray(t);
    for (T tt : array) {
      if (tt instanceof IOrbadaPluginProvider) {
        ((IOrbadaPluginProvider)tt).setApplication(app);
      }
    }
    return array;
  }
  
  @Override
  public void findPlugins() {
    logger.debug("Plugin searching...");
    super.findPlugins();
    checkFoundPlugins();
    logger.debug("Plugin founds Okay");
  }
  
  @Override
  public void loadPlugins() {
    Application.renderSplashText(stringManager.getString("OrbadaPluginManager-loading-plugins-3dot"));
    logger.debug("Plugin loading...");
    super.loadPlugins();
    updateLoadedPlugins();
    logger.debug("Plugin loaded Okay");
  }
  
  @Override
  public void initializePlugins() {
    logger.debug("Plugin initializing...");
    for (int i=0; i<getCount(); i++) {
      logger.debug("BEFORE:" +getPlugin(i).getUniqueID());
    }
    super.callMethodPlugins(new CallMethod() {
      @Override
      public void call(IPlugin plugin) {
        ((OrbadaPlugin)plugin).setApplication(Application.get());
      }
    });
    super.initializePlugins();
    for (int i=0; i<getCount(); i++) {
      logger.debug("AFTER:" +getPlugin(i).getUniqueID());
    }
    logger.debug("Plugin initializing: Okay");
  }
  
  @Override
  public void unloadPlugins() {
    logger.debug("Plugin unloading...");
    super.unloadPlugins();
    logger.debug("Plugin unloaded Okay");
  }

  public PluginQueue getPluginQueue() {
    return queue;
  }
  
}
