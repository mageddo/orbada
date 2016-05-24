/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.triggers;

import java.util.HashMap;
import pl.mpak.orbada.gui.ITabObjectUserData;
import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.util.SourceCreator;
import pl.mpak.orbada.universal.gui.tabbed.UniversalSourceTab;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class TriggerSourceTab extends UniversalSourceTab implements ITabObjectUserData {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);
  
  private HashMap<String, Variant> mapValues;

  public TriggerSourceTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-trigger-source";
  }

  @Override
  public void userData(HashMap<String, Variant> values) {
    mapValues = values;
  }
  
  @Override
  public void updateBody(AbsOrbadaSyntaxTextArea textArea) {
    try {
      if (mapValues != null) {
        new SourceCreator(getDatabase(), textArea).getSource(
          currentSchemaName, "TRIGGER", 
          currentObjectName,
          mapValues.get("table_name").getString());
      }
      else {
        textArea.setDatabaseObject(null, null, null, "");
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  @Override
  public boolean isStorable() {
    return true;
  }

  @Override
  public String getTitle() {
    return stringManager.getString("source");
  }

}
