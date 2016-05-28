/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import java.sql.Timestamp;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDbLinkInfo extends DbObjectIdentified {
  
  private String userName;
  private String host;
  private Timestamp created;
  
  public OracleDbLinkInfo(String name, OracleDbLinkListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"U¿ytkownik", "Serwer docelowy", "Utworzony"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(userName), 
      new Variant(host), 
      new Variant(created)
    };
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void refresh() throws Exception {
  }

}
