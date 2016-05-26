package pl.mpak.util;

/**
 * @author akaluza
 * <p>Funkcje encode i decode z tej klasy z³u¿¹ odpowiednio do kodowania i
 * dekodowania ci¹gu znaków w systemie 60-ym.
 */
public class Hex60 {
  
  private static final String charsWord2Hex60 = "0123456789abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUW"; 

  private static String word2Hex60Alg( int value ) {
    if (value < 57) {
      return "" +charsWord2Hex60.charAt(value);
    }
    return word2Hex60Alg( value / 57 ) +charsWord2Hex60.charAt(value % 57);
  }
  
  private static String word2Hex60( int value ) {
    String result = word2Hex60Alg( value );
    if (result.length() == 1) {
      result = "X" +result;
    }
    else if (result.length() == 2) {
      result = "Y" +result;
    }
    return result;
  }

  private static int hex602WordAlg( String value ) {
    int result = 0;
    int m = 1;
    for (int i = value.length() -1; i >= 0; i--) {
      result = result +charsWord2Hex60.indexOf(value.charAt(i)) *m;
      m *= 57;
    }
    return result;
  }
  
  private static int hex602Word( String value ) {
    if (value.length() == 0) {
      return 0;
    }
    else if (value.charAt(0) == 'X') {
      return hex602WordAlg( "" +value.charAt(1) );
    }
    else if (value.charAt(0) == 'Y') {
      return hex602WordAlg( value.substring(1, 3) );
    }
    return hex602WordAlg( value );
  }
  
  public static String encode(String text) {
    if (text == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    int i=0;
    while (i<text.length()) {
      if (i == text.length() -1) {
        sb.append(word2Hex60(text.charAt(i)));
        i++;
      }
      else if (text.charAt(i) >= 256) {
        sb.append("Z" +word2Hex60(text.charAt(i)));
        i++;
      }
      else {
        sb.append(word2Hex60(text.charAt(i +1) *256 +text.charAt(i)));
        i+=2;
      }
    }
    return sb.toString();
  }
  
  public static String decode(String text) {
    if (text == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    int i=0;
    while (i<text.length()) {
      boolean pnt = text.charAt(i) == 'Z';
      if (pnt) {
        i++;
      }
      int sh = text.charAt(i) == 'X' ? 2 : 3;
      int ch = hex602Word( text.substring(i, i +sh) );
      if (pnt) {
        sb.append((char)ch);
      }
      else {
        sb.append((char)(ch % 256));
        if (ch / 256 > 0) {
          sb.append((char)(ch / 256));
        }
      }
      i+= sh;
    }
    return sb.toString();
  }
  
}
