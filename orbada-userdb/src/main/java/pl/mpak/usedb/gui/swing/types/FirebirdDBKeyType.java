package pl.mpak.usedb.gui.swing.types;

import java.io.Serializable;

import pl.mpak.util.HexUtils;

public class FirebirdDBKeyType implements Serializable {

  private static final long serialVersionUID = 4115033550330144707L;

  private byte[] bytes;
  
  public FirebirdDBKeyType() {
  }

  public FirebirdDBKeyType(byte[] bytes) {
    this.bytes = bytes;
  }
  
  public byte[] getBytes() {
    return bytes;
  }
  
  public String toString() {
    byte[] b1 = {bytes[3], bytes[2], bytes[1], bytes[0]};
    byte[] b2 = {bytes[7], bytes[6], bytes[5], bytes[4]};
    return HexUtils.convert(b1).toUpperCase() +":" +HexUtils.convert(b2).toUpperCase();
  }

}
