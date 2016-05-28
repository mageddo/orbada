/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.gui.dfvisual;

/**
 *
 * @author akaluza
 */
public class DataFileInfo extends NameInfo {

  private String tablespace;
  private long blockSize;
  private long fileId;
  private String status;
  private String onlineStatus;
  private long freeBlocks;
  private long freeBytes;
  
  public DataFileInfo(
      String fileName, String tablespace, 
      long fileId, long blocks, long bytes, long blockSize, 
      String status, String onlineStatus,
      long freeBlocks, long freeBytes) {
    super(fileName);
    this.tablespace = tablespace;
    this.fileId = fileId;
    this.blockSize = blockSize;
    this.status = status;
    this.onlineStatus = onlineStatus;
    this.freeBlocks = freeBlocks;
    this.freeBytes = freeBytes;
    setBlocks(blocks);
    setBytes(bytes);
  }

  public long getFileId() {
    return fileId;
  }

  public void setFileId(long fileId) {
    this.fileId = fileId;
  }

  public String getTablespace() {
    return tablespace;
  }

  public void setTablespace(String tablespace) {
    this.tablespace = tablespace;
  }

  public long getBlockSize() {
    return blockSize;
  }

  public void setBlockSize(long blockSize) {
    this.blockSize = blockSize;
  }

  public String getOnlineStatus() {
    return onlineStatus;
  }

  public void setOnlineStatus(String onlineStatus) {
    this.onlineStatus = onlineStatus;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getFreeBlocks() {
    return freeBlocks;
  }

  public void setFreeBlocks(long freeBlocks) {
    this.freeBlocks = freeBlocks;
  }

  public long getFreeBytes() {
    return freeBytes;
  }

  public void setFreeBytes(long freeBytes) {
    this.freeBytes = freeBytes;
  }

}
