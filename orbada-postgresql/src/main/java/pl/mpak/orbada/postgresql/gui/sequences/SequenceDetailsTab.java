/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.sequences;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.orbada.universal.gui.tabbed.UniversalPropDetailsTab;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SequenceDetailsTab extends UniversalPropDetailsTab {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);
  
  private String sequenceAccesible = "NO";

  public SequenceDetailsTab(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-sequence-details";
  }

  @Override
  public String getSql() {
    Query qry = getDatabase().createQuery();
    try {
      qry.setSqlText(Sql.getSequenceAccessible());
      qry.paramByName("schema_name").setString(currentSchemaName);
      qry.paramByName("sequence_name").setString(currentObjectName);
      qry.open();
      if (!qry.eof()) {
        sequenceAccesible = qry.fieldByName("accessible").getString();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      qry.close();
    }
    
    if ("NO".equalsIgnoreCase(sequenceAccesible)) {
      return "select '" +stringManager.getString("no-right-to-view", currentObjectName) +"' info";
    }
    
    return Sql.getSequenceDetails();
  }

  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    if (!"NO".equalsIgnoreCase(sequenceAccesible)) {
      qc.paramByName("schema_name").setString(currentSchemaName);
      qc.paramByName("sequence_name").setString(currentObjectName);
      qc.paramByName("&schema_name").setString(getDatabase().quoteName(currentSchemaName));
      qc.paramByName("&sequence_name").setString(getDatabase().quoteName(currentObjectName));
    }
  }
  
  @Override
  public String getTitle() {
    return stringManager.getString("details");
  }
  
}
