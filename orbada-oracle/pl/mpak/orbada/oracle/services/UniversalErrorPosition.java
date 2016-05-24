/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.services;

import java.sql.ParameterMetaData;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.universal.providers.UniversalActionProvider;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class UniversalErrorPosition extends UniversalActionProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);
  private int beforeStartPosition = -1;
  private int beforeEndPosition = -1;
  private String sqlText;

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaOraclePlugin.oracleDriverType.equals(database.getDriverType());
  }

  @Override
  public boolean addToolButton() {
    return false;
  }

  @Override
  public boolean addMenuItem() {
    return false;
  }

  @Override
  public boolean addToEditor() {
    return false;
  }

  public String getDescription() {
    return stringManager.getString("UniversalErrorPosition-description");
  }

  public String getGroupName() {
    return OrbadaOraclePlugin.oracleDriverType;
  }

  private void beforeSql() {
    beforeStartPosition = accessibilities.getSyntaxEditor().getStartCurrentText();
    beforeEndPosition = accessibilities.getSyntaxEditor().getEndCurrentText();
  }

  private void onError(ParametrizedCommand cmd, Exception ex) {
    if (accessibilities.getSyntaxEditor().getStartCurrentText() == beforeStartPosition &&
        accessibilities.getSyntaxEditor().getEndCurrentText() == beforeEndPosition &&
        !commandTransformed) {
      Command command = cmd.getDatabase().createCommand();
      try {
        command.setSqlText(Sql.getErrorPosition());
        command.paramByName("sql").setString(cmd.getSqlText());
        command.paramByName("result").setParamMode(ParameterMetaData.parameterModeOut, java.sql.Types.INTEGER);
        command.execute();
        if (!command.paramByName("result").getValue().isNullValue()) {
          accessibilities.getSyntaxEditor().setCaretPosition(beforeStartPosition +command.paramByName("result").getInteger(), true);
        }
      }
      catch (Exception e) {
      }
    }
  }

  @Override
  public void beforeOpenQuery(Query query) {
    beforeSql();
  }

  @Override
  public void queryError(Query query, Exception ex) {
    onError(query, ex);
  }

  @Override
  public void beforeExecuteCommand(Command command) {
    beforeSql();
  }

  @Override
  public void commandError(Command command, Exception ex) {
    onError(command, ex);
  }

}
