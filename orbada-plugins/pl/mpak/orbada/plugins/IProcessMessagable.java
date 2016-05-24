/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import pl.mpak.orbada.plugins.queue.PluginMessage;

/**
 *
 * @author akaluza
 */
public interface IProcessMessagable {

  public void processMessage(PluginMessage message);
  
}
