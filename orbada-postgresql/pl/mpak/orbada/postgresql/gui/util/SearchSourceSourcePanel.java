/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.util;

import java.util.HashMap;
import pl.mpak.orbada.gui.ITabObjectUserData;
import pl.mpak.orbada.gui.comps.AbsOrbadaSyntaxTextArea;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.util.SourceCreator;
import pl.mpak.orbada.universal.gui.tabbed.UniversalSourceTab;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class SearchSourceSourcePanel extends UniversalSourceTab implements ITabObjectUserData {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);
  
  private HashMap<String, String> sourceMap;
  protected String lastSchemaName = "";
  protected String lastObjectName = "";
  private HashMap<String, Variant> mapValues;
  
  public SearchSourceSourcePanel(IViewAccesibilities accesibilities) {
    super(accesibilities);
    sourceMap = new HashMap<String, String>();
  }

  @Override
  public String getPanelName() {
    return "postgresql-search-source";
  }

  @Override
  protected boolean isToolbar() {
    return false;
  }
  
  @Override
  public void refresh(String catalogName, String schemaName, String objectName) {
    if (!currentSchemaName.equals(schemaName) || !currentObjectName.equals(objectName) || requestRefresh) {
      super.refresh(catalogName, schemaName, objectName);
    }
    else if (SwingUtil.isVisible(this)) {
      updateBody(getTextArea());
    }
  }

  @Override
  public void userData(HashMap<String, Variant> values) {
    mapValues = values;
  }

  @Override
  public void updateBody(AbsOrbadaSyntaxTextArea textArea) {
    if (!StringUtil.isEmpty(currentObjectName) && mapValues != null) {
      if (!StringUtil.equals(currentSchemaName, lastSchemaName)) {
        sourceMap.clear();
        lastSchemaName = currentSchemaName;
      }
      String objectType = null;
      String objectOwner = null;
      int line = 0;
      try {
        objectType = mapValues.get("object_type").getString();
        objectOwner = mapValues.get("object_owner").getString();
        line = mapValues.get("line").getInteger();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      String source = null;
      if (!StringUtil.equals(currentObjectName, lastObjectName)) {
        source = sourceMap.get(currentObjectName);
        if (source == null) {
          source = new SourceCreator(accesibilities.getDatabase()).getSource(currentSchemaName, objectType, currentObjectName, objectOwner);
          sourceMap.put(currentObjectName, source);
        }
        lastObjectName = currentObjectName;
        getTextArea().setDatabaseObject(currentSchemaName, objectType, currentObjectName, source);
      }
      if ("VIEW".equalsIgnoreCase(objectType)) {
        line++;
      }
      else if ("RULE".equalsIgnoreCase(objectType)) {
        line+=2;
      }
      else if ("TRIGGER".equalsIgnoreCase(objectType)) {
        line+=2;
      }
      // +2 -- DROP COMMAND
      gotoPoint(line +2, 1);
    }
  }

  @Override
  public boolean isStorable() {
    return false;
  }

  @Override
  public String getTitle() {
    return null;
  }

}
