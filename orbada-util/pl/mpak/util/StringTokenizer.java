package pl.mpak.util;

public class StringTokenizer extends java.util.StringTokenizer {

  private String skipDelim;
  
  public StringTokenizer(String str) {
    super(str);
  }

  public StringTokenizer(String str, String delim) {
    super(str, delim);
  }

  public StringTokenizer(String str, String delim, boolean returnDelims) {
    super(str, delim, returnDelims);
  }

  public StringTokenizer(String str, String delim, String skipDelim) {
    super(str, delim +skipDelim, true);
    this.skipDelim = skipDelim;
  }
  
  private boolean isSkipDelim(String token) {
    if ("".equals(token)) {
      return false;
    }
    return skipDelim.indexOf(token.charAt(0)) != -1;
  }
  
  public String nextToken() {
    String token = super.nextToken();
    if (skipDelim != null) {
      while (isSkipDelim(token) && hasMoreTokens()) {
        token = super.nextToken();
      }
    }
    return token;
  }

}
