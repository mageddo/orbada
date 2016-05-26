/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.filter;

import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DefinedFilterComponent {
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  private String name;
  private SqlFilterComponent[] filterComponent;

  public DefinedFilterComponent(SqlFilterComponent[] filterComponent) {
    this.filterComponent = filterComponent;
    this.name = stringManager.getString("DefinedFilterComponent-name");
  }
  
  public DefinedFilterComponent(String name) {
    this.name = name;
    filterComponent = new SqlFilterComponent[5];
    for (int i=0; i<filterComponent.length; i++) {
      filterComponent[i] = new SqlFilterComponent();
    }
  }

  public SqlFilterComponent[] getFilterComponent() {
    return filterComponent;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

}
