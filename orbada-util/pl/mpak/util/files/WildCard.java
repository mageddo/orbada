package pl.mpak.util.files;

import pl.mpak.util.StringUtil;

public class WildCard {

  /**
   * Funkcja konwertuje wild card (dos/windows) na wyra¿enie regularne "regex"
   * dla potrzeb Pattern i Match
   * * dowolny ci¹g znaków
   * ? dowolny znak
   * ; lub
   * np *.jar|*.zip
   * 
   * @param wildcard
   * @return ci¹g znaków dla Pattern
   */
  public static String getRegex(String wildcard) {    
    String result = StringUtil.replaceString(wildcard, ".", "\\.");
    result = StringUtil.replaceString(result, "*", ".*");
    result = StringUtil.replaceString(result, "?", ".");
    result = StringUtil.replaceString(result, ";", "|");
    return result;
  }

}
