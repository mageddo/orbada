/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.postgresql.gui.functions;

import java.awt.Component;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TriggerFunctionListView extends FunctionListView {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaPostgreSQLPlugin.class);

  public TriggerFunctionListView(IViewAccesibilities accesibilities) {
    super(accesibilities);
  }

  @Override
  public Component getTabbedPane() {
    return new TriggerFunctionTabbedPane(accesibilities);
  }

  @Override
  public String getPanelName() {
    return "postgresql-trigger-functions";
  }

  @Override
  public void extraSqlParameters(ParametrizedCommand qc) throws UseDBException {
    qc.paramByName("TRIGGER").setString("Y");
  }
  
}
