/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import javax.swing.Icon;

/**
 * <p>Interfejs obiejktu bazy danych do u¿ytku ró¿nego
 * @author akaluza
 */
public interface IDatabaseObject {

  public String getSqlObjectName();

  public String getCatalogName();
  
  public String getSchemaName();
  
  public String getObjectName();
  
  public String getObjectType();
  
  public String getStatus();
  
  public Icon getObjectIcon();
  
}
