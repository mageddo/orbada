/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.gui.util.helpers;

/**
 *
 * @author akaluza
 */
public class DatabaseInfo {

  private String name;

  public DatabaseInfo() {
  }

  public DatabaseInfo(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
