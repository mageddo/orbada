package pl.mpak.datatext.util;

public class DataTextUtils {

  public static String fillRight(String text, char ch, int width) {
    StringBuffer sb = new StringBuffer(text);
    for (int i=text.length(); i<=width; i++) {
      sb.append(ch);
    }
    return sb.toString();
  }
  
  public static String fillLeft(String text, char ch, int width) {
    StringBuffer sb = new StringBuffer();
    for (int i=text.length(); i<=width; i++) {
      sb.append(ch);
    }
    return sb.toString() +text;
  }
  
  public static byte[] copyOf(byte[] original, int newLength) {
    byte[] copy = new byte[newLength];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
  }
  
}
