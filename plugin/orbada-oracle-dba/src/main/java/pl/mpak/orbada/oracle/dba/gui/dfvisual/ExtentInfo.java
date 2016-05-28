/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.gui.dfvisual;

/**
 *
 * @author akaluza
 */
public class ExtentInfo {

  private String tablespaceName;
  private String ownerName;
  private String segmentName;
  private String segmentType;
  private String partitionName;
  private long fileId;
  private long blockId;
  private long blocks;
  private boolean toRemove;
  
  public ExtentInfo(
    String tablespaceName, String ownerName, String segmentName, String segmentType, String partitionName, 
    long fileId, long blockId, long blocks) {
    this.tablespaceName = tablespaceName;
    this.ownerName = ownerName;
    this.segmentName = segmentName;
    this.segmentType = segmentType;
    this.partitionName = partitionName;
    this.fileId = fileId;
    this.blockId = blockId;
    this.blocks = blocks;
  }

  public long getBlockId() {
    return blockId;
  }

  public long getBlocks() {
    return blocks;
  }

  public long getFileId() {
    return fileId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getPartitionName() {
    return partitionName;
  }

  public String getSegmentType() {
    return segmentType;
  }

  public String getTablespaceName() {
    return tablespaceName;
  }

  public String getSegmentName() {
    return segmentName;
  }

  public boolean isToRemove() {
    return toRemove;
  }

  public void setToRemove() {
    this.toRemove = true;
  }

  public void updateInfo(NameInfo info, long blockSize) {
    info.addBlocks(blocks);
    info.addBytes(blocks *blockSize);
  }
}
