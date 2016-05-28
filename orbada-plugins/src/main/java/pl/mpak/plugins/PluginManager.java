package pl.mpak.plugins;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;
import pl.mpak.plugins.spi.IPlugin;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.util.Assert;
import pl.mpak.util.AssignableClasses;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringUtil;
import pl.mpak.util.files.PatternFileFilter;
import pl.mpak.util.files.PatternFolderFilter;
import pl.mpak.util.files.WildCard;

public class PluginManager {

  private ArrayList<Plugin> pluginList = new ArrayList<Plugin>();
  private EventListenerList pluginManagerListenerList = new EventListenerList();
  private Class<? extends IPlugin> pluginClass;
  private ArrayList<PluginFound> foundList = new ArrayList<PluginFound>();
  private LinkedList<Class<? extends IPluginProvider>> providerList = new LinkedList<Class<? extends IPluginProvider>>();
  private HashMap<Class<? extends IPluginProvider>, IPluginProvider> sharedProviderList = new HashMap<Class<? extends IPluginProvider>, IPluginProvider>();
  private Logger logger = Logger.getLogger(PluginManager.class);
  
  public enum PluginManagerEvent {
    BEGIN_PROCESS,
    PROCESS,
    END_PROCESS
  }
  
  public PluginManager(Class<? extends IPlugin> pluginClass) {
    this.pluginClass = pluginClass;
  }

  public PluginManager() {
    this(IPlugin.class);
  }

  public void addPluginManagerListener(PluginManagerListener listener) {
    synchronized (pluginManagerListenerList) {
      pluginManagerListenerList.add(PluginManagerListener.class, listener);
    }
  }
  
  public void removePluginManagerListener(PluginManagerListener listener) {
    synchronized (pluginManagerListenerList) {
      pluginManagerListenerList.remove(PluginManagerListener.class, listener);
    }
  }
  
  public void firePluginManagerListener(PluginManagerEvent event, PluginManagerListener.ManageProcess type, int count, IPlugin plugin) {
    synchronized (pluginManagerListenerList) {
      PluginManagerListener[] listeners = pluginManagerListenerList.getListeners(PluginManagerListener.class);
      for (int i=0; i<listeners.length; i++) {
        switch(event) {
          case BEGIN_PROCESS:
            listeners[i].beginProcess(type, count);
            break;
          case END_PROCESS:
            listeners[i].endProcess(type);
            break;
          case PROCESS:
            listeners[i].process(type, plugin);
            break;
        }
      }
    }
  }
  
  public ArrayList<Plugin> getPluginList() {
    return pluginList;
  }
  
  public int getCount() {
    synchronized (pluginList) {
      return pluginList.size();
    }
  }

  public Plugin getPlugin(int index) {
    synchronized (pluginList) {
      return pluginList.get(index);
    }
  }
  
  public void add(Plugin plugin) {
    synchronized (pluginList) {
      pluginList.add(plugin);
    }
  }
  
  public Plugin remove(int index) {
    synchronized (pluginList) {
      Plugin plugin = pluginList.remove(index);
      return plugin;
    }
  }
  
  public boolean remove(Plugin plugin) {
    synchronized (pluginList) {
      int index = pluginList.indexOf(plugin);
      if (index >= 0) {
        return remove(index) != null;
      }
      return false;
    }
  }
  
  public Plugin getPluginByUniqueID(String uniqueID) {
    synchronized (pluginList) {
      for (int i=0; i<getCount(); i++) {
        if (getPlugin(i).getUniqueID().equals(uniqueID)) {
          return getPlugin(i);
        }
      }
      return null;
    }
  }
  
  private void findFiles(String path, ArrayList<File> fileList) {
    File pathFile = new File(path);
    
    String[] list = pathFile.list(new PatternFolderFilter(WildCard.getRegex("*"))); //$NON-NLS-1$
    for (int i=0; i<list.length; i++) {
      if (!"lib".equalsIgnoreCase(list[i])) { //$NON-NLS-1$
        findFiles(path +"/" +list[i], fileList); //$NON-NLS-1$
      }
    }
    list = pathFile.list(new PatternFileFilter(WildCard.getRegex("*.jar|*.zip"))); //$NON-NLS-1$
    
    for (int i=0; i<list.length; i++) {
      fileList.add(new File(path +"/" +list[i])); //$NON-NLS-1$
    }
  }
  
  private void searchPlugins(String path) {
    if (logger != null) {
      logger.debug("searchPlugins:start"); //$NON-NLS-1$
    }
    File floders = new File(path);
    floders.mkdirs();

    ArrayList<File> fileList = new ArrayList<File>();
    findFiles(path, fileList);
    if (logger != null) {
      logger.debug(Arrays.toString(fileList.toArray()));
    }
    
    try {
      URL[] urls = new URL[fileList.size()];
      for (int i=0; i<fileList.size(); i++) {
        urls[i] = fileList.get(i).toURI().toURL();
        if (logger != null) {
          logger.debug("searchPlugins:urls[i]:" +urls[i]); //$NON-NLS-1$
        }
      }
      
      AssignableClasses classLoader = new AssignableClasses(urls, getClass().getClassLoader());
      Class<?>[] classes = classLoader.getAssignableClasses(pluginClass);
      for (int c=0; c<classes.length; c++) {
        try {
          if (classes[c] == null || classes[c].getResource("") == null) { //$NON-NLS-1$
            throw new PluginException(Messages.getString("PluginManager.problem-plugin-find")); //$NON-NLS-1$
          }
          foundList.add(new PluginFound(classes[c].getResource("").toString(), classes[c], classLoader)); //$NON-NLS-1$
          firePluginManagerListener(PluginManagerEvent.PROCESS, PluginManagerListener.ManageProcess.FOUND, -1, null);
        }
        catch(Exception e) {
          ExceptionUtil.processException(e);
        }
      }
    }
    catch (Exception e) {
      ExceptionUtil.processException(e);
    }
    if (logger != null) {
      logger.debug("searchPlugins:end"); //$NON-NLS-1$
    }
  }
  
  public void findPlugins() {
    findPlugins("plugins"); //$NON-NLS-1$
  }
  
  /**
   * <p>Wyszukuje wtyczki w podanym katalogu.
   * @param pluginPath
   */
  public void findPlugins(final String pluginPath) {
    Assert.notEmpty(pluginPath);
    File pluginPathFile = new File(pluginPath);
    if(!pluginPathFile.exists() || (pluginPathFile.list() != null && pluginPathFile.list().length == 0)){
      final File resourcesPlugin = new File(ClassLoader.getSystemClassLoader()
          .getResource(pluginPath).getFile());
      if(resourcesPlugin.exists() && resourcesPlugin.isDirectory()) {
        pluginPathFile = resourcesPlugin;
      }
    }

    foundList.clear();
    firePluginManagerListener(PluginManagerEvent.BEGIN_PROCESS, PluginManagerListener.ManageProcess.FOUND, -1, null);
    try {
      if (logger != null) {
        logger.debug("PluginPath:" + pluginPathFile); //$NON-NLS-1$
      }
      searchPlugins(pluginPathFile.getAbsolutePath());
    }
    finally {
      firePluginManagerListener(PluginManagerEvent.END_PROCESS, PluginManagerListener.ManageProcess.FOUND, -1, null);
    }
  }
  
  /**
   * <p>£aduje wszystkie odnalezione wtyczki
   */
  public void loadPlugins() {
    if (logger != null) {
      logger.debug("loadPlugins:start"); //$NON-NLS-1$
    }
    firePluginManagerListener(PluginManagerEvent.BEGIN_PROCESS, PluginManagerListener.ManageProcess.LOAD, foundList.size(), null);
    try {
      for (int i=0; i<foundList.size(); i++) {
        try {
          PluginFound found = foundList.get(i);
          if (logger != null) {
            logger.debug("loadPlugins:" +found.getFileName()); //$NON-NLS-1$
          }
          if (found.isEnabled()) {
            Plugin plugin = new Plugin(found.getFileName(), found.getClazz(), found.getClassLoader());
            add(plugin);
            firePluginManagerListener(PluginManagerEvent.PROCESS, PluginManagerListener.ManageProcess.LOAD, -1, plugin.getPlugin());
          }  
        }
        catch (Exception e) {
          ExceptionUtil.processException(e);
        }
      }
    }
    finally {
      firePluginManagerListener(PluginManagerEvent.END_PROCESS, PluginManagerListener.ManageProcess.LOAD, -1, null);
      if (logger != null) {
        logger.debug("loadPlugins:end"); //$NON-NLS-1$
      }
    }
  }
  
  public void unloadPlugins() {
    firePluginManagerListener(PluginManagerEvent.BEGIN_PROCESS, PluginManagerListener.ManageProcess.UNLOAD, getCount(), null);
    try {
      synchronized (pluginList) {
        while (getCount() > 0) {
          Plugin plugin = getPlugin(0);
          IPlugin iplugin = null;
          try {
            iplugin = plugin.getPlugin();
            
            Class<IPluginProvider>[] pp = plugin.getProviderArray();
            if (pp != null && pp.length > 0) {
              for (int j=0; j<pp.length; j++) {
                providerList.remove(pp[j]);
              }
            }
            
            plugin.unloadPlugin();
          }
          catch (Exception e) {
            ExceptionUtil.processException(e);
          }
          remove(0);
          firePluginManagerListener(PluginManagerEvent.PROCESS, PluginManagerListener.ManageProcess.UNLOAD, -1, iplugin);
        }
      }
    }
    finally {
      firePluginManagerListener(PluginManagerEvent.END_PROCESS, PluginManagerListener.ManageProcess.UNLOAD, -1, null);
    }
  }
  
  public void initializePlugins() {
    firePluginManagerListener(PluginManagerEvent.BEGIN_PROCESS, PluginManagerListener.ManageProcess.INITIALIZE, getCount(), null);
    try {
      synchronized (pluginList) {
        List<IPlugin> list = new ArrayList<IPlugin>();
        for (int i=0; i<getCount(); i++) {
          list.add(getPlugin(i).getPlugin());
        }
        
        // sprawdzamy potrzebne wtyczki i zale¿noœci
        for (int i=0; i<getCount(); i++) {
          getPlugin(i).requires(list);
        }
        // sortujemy tak aby te zale¿ne znalaz³y siê na koñcu
        Collections.sort(pluginList, new Comparator<Plugin>() {
          @Override
          public int compare(Plugin o1, Plugin o2) {
            String[] d = o2.getDepends();
            if (d != null && d.length > 0) {
              if (StringUtil.anyOfString(o1.getUniqueID(), d) >= 0) {
                return -1;
              }
            }
            return 1;
          }
        });
        for (int i=0; i<getCount(); i++) {
          Plugin plugin = getPlugin(i);
          try {
            if (plugin.isCanUsePlugin()) {
              plugin.initialize();
              Class<IPluginProvider>[] pp = plugin.getProviderArray();
              if (pp != null && pp.length > 0) {
                for (Class<IPluginProvider> p : pp) {
                  addProvider(p);
                }
              }
              firePluginManagerListener(PluginManagerEvent.PROCESS, PluginManagerListener.ManageProcess.INITIALIZE, -1, plugin.getPlugin());
            }
          }
          catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
      }
    }
    finally {
      firePluginManagerListener(PluginManagerEvent.END_PROCESS, PluginManagerListener.ManageProcess.INITIALIZE, -1, null);
    }
  }
  
  /**
   * <p>Pozwala dodaæ us³ugodawcê poza mechanizmem ³adowania automatycznego
   * @param providerClass
   */
  public void addProvider(Class<? extends IPluginProvider> providerClass) {
    providerList.add(providerClass);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends IPluginProvider> T[] getServiceArray(Class<T> t) {
    ArrayList<T> list = new ArrayList<T>(50); 
    Iterator<Class<? extends IPluginProvider>> i = providerList.iterator();
    while (i.hasNext()) {
      Class<? extends IPluginProvider> c = i.next();
      if (t.isAssignableFrom(c)) {
        T pp = (T)sharedProviderList.get(c);
        if (pp != null) {
          list.add(pp);
        }
        else {
          try {
            pp = (T)c.newInstance();
            if (pp.isSharedProvider()) {
              sharedProviderList.put(c, pp);
            }
            list.add(pp);
          } catch (InstantiationException e) {
            ExceptionUtil.processException(e);
          } catch (IllegalAccessException e) {
            ExceptionUtil.processException(e);
          } catch (NoClassDefFoundError e) {
            ExceptionUtil.processException(e);
          }
        }
      }
    }
    T[] result = (T[])Array.newInstance(t, list.size());
    return list.toArray(result);
  }

  public void callMethodPlugins(CallMethod call) {
    for (int i=0; i<getCount(); i++) {
      try {
        call.call(getPlugin(i).getPlugin());
      }
      catch (Exception e) {
        ExceptionUtil.processException(e);
      }
    }
  }

  public ArrayList<PluginFound> getFoundList() {
    return foundList;
  }

  public org.apache.log4j.Logger getLogger() {
    return logger;
  }

  public void setLogger(org.apache.log4j.Logger logger) {
    this.logger = logger;
  }

}
