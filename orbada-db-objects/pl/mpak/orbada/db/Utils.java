/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.db;

import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class Utils {

  /**
   * <p>Zastêpuje patt ograniczony $() ${} {} &lt;&gt; wartoœci¹ z value</p>
   * @param text
   * @param patt
   * @param value
   * @return
   */
  public static String replace(String text, String patt, String value) {
    text = StringUtil.replaceString(text, "$(" +patt.toLowerCase() +")", value);
    text = StringUtil.replaceString(text, "$(" +patt.toUpperCase() +")", value);
    text = StringUtil.replaceString(text, "${" +patt.toLowerCase() +"}", value);
    text = StringUtil.replaceString(text, "${" +patt.toUpperCase() +"}", value);
    text = StringUtil.replaceString(text, "{" +patt.toLowerCase() +"}", value);
    text = StringUtil.replaceString(text, "{" +patt.toUpperCase() +"}", value);
    text = StringUtil.replaceString(text, "<" +patt.toLowerCase() +">", value);
    text = StringUtil.replaceString(text, "<" +patt.toUpperCase() +">", value);
    return text;
  }

}
