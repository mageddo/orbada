/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.xmldataview.services;

import java.awt.Component;
import orbada.Consts;
import pl.mpak.orbada.plugins.providers.ViewValueProvider;
import pl.mpak.orbada.xmldataview.OrbadaXmlDataViewPlugin;
import pl.mpak.orbada.xmldataview.gui.ViewXmlDataPanel;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class XmlDataViewService extends ViewValueProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaXmlDataViewPlugin.class);

  @Override
  public Component createComponent(Object value) {
    return new ViewXmlDataPanel(value);
  }

  public String getDescription() {
    return stringManager.getString("XmlDataViewService-description");
  }

  public String getGroupName() {
    return Consts.orbadaToolsGroupName;
  }

}
