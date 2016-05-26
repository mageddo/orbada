/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.jsondataview.services;

import java.awt.Component;
import orbada.Consts;
import pl.mpak.orbada.plugins.providers.ViewValueProvider;
import pl.mpak.orbada.jsondataview.OrbadaJsonDataViewPlugin;
import pl.mpak.orbada.jsondataview.gui.ViewJsonDataPanel;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class JsonDataViewService extends ViewValueProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJsonDataViewPlugin.class);

  @Override
  public Component createComponent(Object value) {
    return new ViewJsonDataPanel(value);
  }

  public String getDescription() {
    return stringManager.getString("JsonDataViewService-description");
  }

  public String getGroupName() {
    return Consts.orbadaToolsGroupName;
  }

}
