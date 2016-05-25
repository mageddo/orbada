package pl.mpak.util;

import java.util.ArrayList;

/**
 * Klasa pozwala pobraæ z ci¹gu znaków elementy rozdzielone od siebie
 * znakami rozdzielaj¹cymi (domyœlnie ,;).
 * Jeœli ci¹g znaków zostanie umieszczony w cudzys³owie wtedy w ci¹gu
 * mog¹ wyst¹piæ znaki oddzielaj¹ce.
 * Cudzys³ów uzyskuje sie poprzez jego podwójne wyst¹pienie w ci¹gu znaków.
 *
 * Przyk³ad
 *   CommaDelimiter cd = new CommaDelimiter("Andrzej Ka³u¿a,\"Warszawski park leœny\"");
 *   while (cd.moreTokens()) {
 *     System.out.println(cd.nextToken());
 *   }
 * 
 * @author Administrator
 *
 */
public class CommaDelimiter {
  
  public final static String DEFAULT_DELIMS = ",; ";
  
  private String text;
  private String delimiters;
  private int index;

  public CommaDelimiter() {
    super();
    setText("");
    setDelimiters(",; ");
    index = 0;
  }

  public CommaDelimiter(String text) {
    this();
    setText(text);
  }

  public CommaDelimiter(String text, String delim) {
    this(text);
    setDelimiters(delim);
  }

  /**
   * Przetwarza "text" i zwraca element z pozycji "index"
   * "index" rozpoczyna siê od wartoœci 1
   * 
   * @param text
   * @param index rozpoczyna siê od wartoœci 1 (pierwszy element z listy)
   * @return
   */
  public static String getCommaString(String text, int index, String delim) {
    CommaDelimiter cd = new CommaDelimiter(text, delim);
    String token;
    
    while (cd.moreTokens()) {
      index--;
      token = cd.nextToken();
      if (index == 0) {
        return token;
      }
    }
    return "";
  }
  
  public static String getCommaString(String text, int index) {
    return getCommaString(text, index, DEFAULT_DELIMS);
  }

  public static String[] getCommaStrings(String text) {
    return getCommaStrings(text, DEFAULT_DELIMS);
  }
  
  public static String[] getCommaStrings(String text, String delim) {
    CommaDelimiter cd = new CommaDelimiter(text, delim);
    ArrayList<String> result = new ArrayList<String>();
    
    while (cd.moreTokens()) {
      result.add(cd.nextToken());
    }
    
    return result.toArray(new String[result.size()]);
  }
  
  public static String createCommaString(String[] strings) {
    String result = "";
    for (int i=0; i<strings.length; i++) {
      String temp = strings[i];
      temp = StringUtil.replaceString(strings[i], "\"", "\"\"");
      temp = StringUtil.replaceString(temp, "\n", "\\n");
      result = StringUtil.addStringChar(result, "\"" +temp +"\"", ",");
    }
    return result;
  }
  
  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setDelimiters(String delimiters) {
    this.delimiters = delimiters;
  }

  public String getDelimiters() {
    return delimiters;
  }

  /**
   * Czy jest wiêcej elementów na liœcie
   * 
   * @return
   */
  public boolean moreTokens() {
    return index < (text == null ? 0 : text.length());
  }
  
  /**
   * Pobiera kolejny element z listy
   * 
   * @return
   */
  public String nextToken() {
    StringBuilder sb = new StringBuilder();
    
    while (index < text.length()) {
      if (text.charAt(index) == '"') {
        index++;
        while (index < text.length()) {
          if (text.charAt(index) == '"') {
            if (index +1 < text.length() && text.charAt(index +1) == '"') {
              index+=2;
              sb.append('"');
            }
            else {
              index++;
              break;
            }
          }
          else if (text.charAt(index) == '\\') {
            if (index +1 < text.length() && text.charAt(index +1) == 'n') {
              index+=2;
              sb.append('\n');
            }
            else {
              sb.append(text.charAt(index));
              index++;
              break;
            }
          }
          else {
            sb.append(text.charAt(index));
            index++;
          }
        }
      }
      else if (delimiters.indexOf(text.charAt(index)) >= 0) {
        index++;
        break;
      }
      else {
        sb.append(text.charAt(index));
        index++;
      }
    }
    
    return sb.toString();
  }
}
