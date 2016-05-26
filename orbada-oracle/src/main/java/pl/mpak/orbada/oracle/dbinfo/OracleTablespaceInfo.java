/*
 * DerbyDbViewInfo.java
 *
 * Created on 2007-11-15, 19:31:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dbinfo;

import java.math.BigInteger;
import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleTablespaceInfo extends DbObjectIdentified {
  
  private Long blockSize;
  private BigInteger initialExtent;
  private BigInteger nextExtent;
  private BigInteger minExtents;
  private BigInteger maxExtents;
  private String status;
  private String contents;
  
  public OracleTablespaceInfo(String name, OracleTablespaceListInfo owner) {
    super(name, owner);
  }
  
  public String[] getMemberNames() {
    return new String[] {"Rozmiar bloku", "Init.Ext.", "Next Ext.", "Min Ext.", "Max Ext.", "Status", "Zawartoœæ"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(blockSize),
      new Variant(initialExtent),
      new Variant(nextExtent),
      new Variant(minExtents),
      new Variant(maxExtents),
      new Variant(status),
      new Variant(contents)
    };
  }

  public Long getBlockSize() {
    return blockSize;
  }

  public void setBlockSize(Long blockSize) {
    this.blockSize = blockSize;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  public BigInteger getInitialExtent() {
    return initialExtent;
  }

  public void setInitialExtent(BigInteger initialExtent) {
    this.initialExtent = initialExtent;
  }

  public BigInteger getMinExtents() {
    return minExtents;
  }

  public void setMinExtents(BigInteger minExtents) {
    this.minExtents = minExtents;
  }

  public BigInteger getMaxExtents() {
    return maxExtents;
  }

  public void setMaxExtents(BigInteger maxExtents) {
    this.maxExtents = maxExtents;
  }

  public BigInteger getNextExtent() {
    return nextExtent;
  }

  public void setNextExtent(BigInteger nextExtent) {
    this.nextExtent = nextExtent;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public void refresh() throws Exception {
  }

}
