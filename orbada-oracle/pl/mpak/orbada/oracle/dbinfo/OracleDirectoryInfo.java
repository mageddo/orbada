/*
 * DerbyDbIndexInfo.java
 *
 * Created on 2007-11-15, 20:33:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleDirectoryInfo extends DbObjectIdentified {
  
  private String directoryOwner;
  private String directoryPath;
  
  public OracleDirectoryInfo(String name, OracleDirectoryListInfo owner) {
    super(name, owner);
  }

  public String[] getMemberNames() {
    return new String[] {"Owner", "Œcie¿ka"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(directoryOwner),
      new Variant(directoryPath)
    };
  }

  public String getDirectoryPath() {
    return directoryPath;
  }

  public void setDirectoryPath(String directoryPath) {
    this.directoryPath = directoryPath;
  }

  public String getDirectoryOwner() {
    return directoryOwner;
  }

  public void setDirectoryOwner(String directoryOwner) {
    this.directoryOwner = directoryOwner;
  }

  public void refresh() throws Exception {
  }

}
