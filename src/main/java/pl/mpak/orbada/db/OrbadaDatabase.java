/*
 * OrbadaDatabase.java
 *
 * Created on 2007-10-23, 20:43:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import java.sql.SQLException;
import java.util.Date;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.providers.IDatabaseProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OrbadaDatabase extends Database {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private SchemaSession schemaSession;
  
  public OrbadaDatabase() {
    super();
  }
  
  @Override
  public void connect() throws SQLException {
    super.connect();
  }
  
  void createSession(String schId) {
    schemaSession = new SchemaSession(InternalDatabase.get());
    schemaSession.setStartTime(new Date().getTime());
    schemaSession.setOsesId(Application.get().getOrbadaSessionId());
    schemaSession.setSchId(schId);
    schemaSession.setUser(getUserName());
    schemaSession.setUrl(getUrl());
    try {
      schemaSession.applyInsert();
      getUserProperties().setProperty("schema-session-id", schemaSession.getId());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  @Override
  public void disconnect() throws SQLException {
    if (!this.equals(InternalDatabase.get())) {
      IDatabaseProvider[] dpa = Application.get().getServiceArray(IDatabaseProvider.class);
      for (IDatabaseProvider dp : dpa) {
        if (dp.isForDatabase(this)) {
          dp.beforeDisconnect(this);
        }
      }
    }
    else if (InternalDatabase.get().getUrl().toUpperCase().indexOf("jdbc:hsqldb:file:".toUpperCase()) != -1) {
      try {
        InternalDatabase.get().executeCommand("shutdown compact");
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    super.disconnect();
    if (schemaSession != null) {
      schemaSession.setEndTime(new Date().getTime());
      try {
        schemaSession.applyUpdate();
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  @Override
  public void commit() throws SQLException {
    super.commit();
    Application.get().postPluginMessage(new PluginMessage(Consts.orbadaSystemPluginId, "status-text", stringManager.getString("OrbadaDatabase-changes-accepted")));
    Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageTransactionStateChange, "commit"));
  }

  @Override
  public void rollback() throws SQLException {
    super.rollback();
    Application.get().postPluginMessage(new PluginMessage(Consts.orbadaSystemPluginId, "status-text", stringManager.getString("OrbadaDatabase-changes-rollbacks")));
    Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageTransactionStateChange, "rollback"));
  }
  
  @Override
  public void startTransaction() throws SQLException {
    super.startTransaction();
    Application.get().postPluginMessage(new PluginMessage(Consts.orbadaSystemPluginId, "status-text", stringManager.getString("OrbadaDatabase-changes-start_transaction")));
    Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageTransactionStateChange, "start"));
  }
  
  public void executeCommandNoException(String sqlText) {
    try {
      createCommand(sqlText, true);
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

}