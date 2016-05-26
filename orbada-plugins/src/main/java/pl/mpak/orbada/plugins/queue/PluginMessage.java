/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.queue;

/**
 *
 * @author akaluza
 */
public class PluginMessage {

  private String destinationId;
  private String messageId;
  private Object object;
  private boolean served;
  
  public PluginMessage(String destinationId, String messageId) {
    this(destinationId, messageId, null);
  }
  
  public PluginMessage(String destinationId, String messageId, Object object) {
    this.destinationId = destinationId;
    this.messageId = messageId;
    this.object = object;
  }

  /**
   * <p>Parametr u¿ytkownika, dodatkowy
   * @return
   */
  public Object getObject() {
    return object;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getDestinationId() {
    return destinationId;
  }
  
  public boolean isMessageId(String messageId) {
    if (this.messageId != null && messageId != null) {
      return this.messageId.equals(messageId);
    }
    return true;
  }

  public boolean isServed() {
    return served;
  }

  public void setServed(boolean served) {
    this.served = served;
  }

  public String toString() {
    return "pluginUniqueId:" +destinationId +",messageId:" +messageId +",object:" +object;
  }

}
