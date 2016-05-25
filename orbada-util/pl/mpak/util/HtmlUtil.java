package pl.mpak.util;

import java.awt.Color;

public class HtmlUtil {

  public static String prepareText(String text) {
    text = StringUtil.replaceString(text, "&", "&amp;");
    text = StringUtil.replaceString(text, "\"", "&quot;");
    text = StringUtil.replaceString(text, "<", "&lt;");
    text = StringUtil.replaceString(text, ">", "&gt;");
    return text;
  }
  
  public static String prepareForUrl(String text) {
    if (text == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<text.length(); i++) {
      switch (text.charAt(i)) {
        case 10: sb.append("%0A"); break;
        case 13: sb.append("%0D"); break;
        case '$': sb.append("%24"); break;
        case '&': sb.append("%26"); break;
        case '+': sb.append("%2B"); break;
        case ',': sb.append("%2C"); break;
        case '/': sb.append("%2F"); break;
        case ':': sb.append("%3A"); break;
        case ';': sb.append("%3B"); break;
        case '=': sb.append("%3D"); break;
        case '?': sb.append("%3F"); break;
        case '@': sb.append("%40"); break;
        case ' ': sb.append("%20"); break;
        case '\"': sb.append("%22"); break;
        case '<': sb.append("%3C"); break;
        case '>': sb.append("%3E"); break;
        case '#': sb.append("%23"); break;
        case '%': sb.append("%25"); break;
        case '{': sb.append("%7B"); break;
        case '}': sb.append("%7D"); break;
        case '|': sb.append("%7C"); break;
        case '\\': sb.append("%5C"); break;
        case '^': sb.append("%5E"); break;
        case '~': sb.append("%7E"); break;
        case '[': sb.append("%5B"); break;
        case ']': sb.append("%5D"); break;
        case '(': sb.append("%28"); break;
        case ')': sb.append("%29"); break;
        case '`': sb.append("%60"); break;
        default: sb.append(text.charAt(i));
      }
    }
    return sb.toString();
  }
  
  public static String htmlColor(Color c) {
    String r = Integer.toHexString(c.getRed());
    r = (r.length() == 1 ? "0" +r : r);
    String g = Integer.toHexString(c.getGreen());
    g = (g.length() == 1 ? "0" +g : g);
    String b = Integer.toHexString(c.getBlue());
    b = (b.length() == 1 ? "0" +b : b);
    return "#" +r +g +b;
  }
  
}
