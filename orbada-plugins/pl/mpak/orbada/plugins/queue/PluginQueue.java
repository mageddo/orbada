/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.queue;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import pl.mpak.orbada.plugins.IProcessMessagable;
import pl.mpak.plugins.Plugin;
import pl.mpak.plugins.PluginManager;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class PluginQueue extends Thread {

  private SynchronousQueue<PluginMessage> queue;
  private PluginManager pluginManager;
  private ArrayList<IProcessMessagable> extendedMessagerList;
  
  public PluginQueue(PluginManager pluginManager) {
    super("plugin-queue");
    queue = new SynchronousQueue<PluginMessage>(true);
    this.pluginManager = pluginManager;
    setPriority(MIN_PRIORITY);
    setDaemon(true);
  }
  
  public boolean post(PluginMessage message) throws InterruptedException {
    queue.put(message);
    return true;
  }
  
  public void setExtendedMessagerList(ArrayList<IProcessMessagable> messagerList) {
    this.extendedMessagerList = messagerList;
  }
  
  @Override
  public void run() {
    while (true) {
      try {
        PluginMessage message = queue.take();
        if (message != null) {
          synchronized (pluginManager.getPluginList()) {
            for (Plugin plugin : pluginManager.getPluginList()) {
              if ((message.getDestinationId() == null || message.getDestinationId().equals(plugin.getUniqueID())) && plugin.getPlugin() instanceof IProcessMessagable) {
                try {
                  ((IProcessMessagable)plugin.getPlugin()).processMessage(message);
                }
                catch (Throwable ex) {
                  ExceptionUtil.processException(ex);
                }
                if (message.getDestinationId() != null || message.isServed()) {
                  break;
                }
              }
            }
          }
          if (extendedMessagerList != null && !message.isServed()) {
            synchronized (extendedMessagerList) {
              for(IProcessMessagable pm :  extendedMessagerList) {
                pm.processMessage(message);
                if (message.isServed()) {
                  break;
                }
              }
            }
          }
        }
      } catch (InterruptedException ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

}
