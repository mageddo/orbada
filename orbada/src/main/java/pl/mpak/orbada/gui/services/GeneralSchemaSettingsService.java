/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.services;

import java.awt.Component;
import javax.swing.Icon;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.schemas.GeneralSchemaSettingServicePanel;
import pl.mpak.orbada.plugins.providers.SchemaSettingsProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class GeneralSchemaSettingsService extends SchemaSettingsProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  @Override
  public boolean isForDriverType(String driverTypeName) {
    return true;
  }

  @Override
  public Component getSettingsComponent(String schemaId) {
    return new GeneralSchemaSettingServicePanel(schemaId);
  }

  @Override
  public String getSettingsPath() {
    return null;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getDescription() {
    return stringManager.getString("GeneralSchemaSettingsService-description");
  }

  @Override
  public String getGroupName() {
    return Consts.orbadaGroupName;
  }

}
