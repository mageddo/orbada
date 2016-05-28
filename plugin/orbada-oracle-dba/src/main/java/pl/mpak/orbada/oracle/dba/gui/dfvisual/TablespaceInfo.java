/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.gui.dfvisual;

/**
 *
 * @author akaluza
 */
public class TablespaceInfo extends NameInfo {

  private Long blockSize;
  private String status;
  
  public TablespaceInfo(String name, Long blockSize, String status) {
    super(name);
    this.blockSize = blockSize;
    this.status = status;
  }

  public Long getBlockSize() {
    return blockSize;
  }

  public void setBlockSize(Long blockSize) {
    this.blockSize = blockSize;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
