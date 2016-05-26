/*
 * DbFileInfo.java
 *
 * Created on 2007-11-13, 20:13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.derbydb.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 * <p>Pliki jar, dokumenty, biblioteki (library)
 * @author akaluza
 */
public class DerbyDbFileInfo extends DbObjectIdentified {
  
  private String fileName;
  
  public DerbyDbFileInfo(String name, DerbyDbFileListInfo owner) {
    super(name, owner);
    setRefreshed(true);
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String[] getMemberNames() {
    return new String[] {"File name"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {new Variant(getFileName())};
  }

  public void refresh() throws Exception {
  }

}
