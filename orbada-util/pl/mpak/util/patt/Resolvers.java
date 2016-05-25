package pl.mpak.util.patt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Resolvers {
  
  private final static Resolvers instance = new Resolvers();
  
  static {
    Enumeration<Object> e = System.getProperties().keys();
    while (e.hasMoreElements()) {
      String key = e.nextElement().toString();
      Resolvers.register(new DefinedModel(key));
    }
  }
  
  private HashMap<String, ResolvableModel> modelMap;
  
  public static Resolvers getInstance() {
    return instance;
  }

  public Resolvers() {
    modelMap = new HashMap<String, ResolvableModel>();
  }
  
  public HashMap<String, ResolvableModel> getModelMap() {
    return modelMap;
  }
  
  public Iterator<String> keys() {
    return modelMap.keySet().iterator();
  }
  
  public ResolvableModel get(String key) {
    return modelMap.get(key);
  }
  
  public static void register(ResolvableModel model) {
    instance.modelMap.put(model.getModel().toUpperCase(), model);
  }
  
  public static String expand(String text) {
    return expand(text, null);
  }
  
  public static String expand(String text, Map<String, String> valueMap) {
    if (text == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    int i = 0;
    int start;
    while (i < text.length()) {
      char ch = text.charAt(i);
      if (ch == '$' && i +1 < text.length() && text.charAt(i +1) == '(') {
        start = i;
        i+=2;
        StringBuilder word = new StringBuilder(); 
        while (i < text.length() && (ch = text.charAt(i)) != ')') {
          word.append(ch);
          i++;
        }
        if (ch == ')') {
          i++;
          ResolvableModel rm = instance.modelMap.get(word.toString().toUpperCase());
          if (rm != null) {
            sb.append(rm.getResolve());
          }
          else if (valueMap != null) {
            String value = valueMap.get(word.toString());
            if (value != null) {
              sb.append(value);
            }
          }
        }
        else {
          i = start;
          sb.append(text.charAt(i));
          i++;
        }
      }
      else {
        sb.append(ch);
        i++;
      }
    }
    return sb.toString();
  }
  
  public static List<String> createValueNames(String text, boolean excludeGlobal) {
    if (text == null) {
      return null;
    }
    List<String> map = new ArrayList<String>();
    int i = 0;
    int start;
    while (i < text.length()) {
      char ch = text.charAt(i);
      if (ch == '$' && i +1 < text.length() && text.charAt(i +1) == '(') {
        start = i;
        i+=2;
        StringBuilder word = new StringBuilder(); 
        while (i < text.length() && (ch = text.charAt(i)) != ')') {
          word.append(ch);
          i++;
        }
        if (ch == ')') {
          i++;
          if (!excludeGlobal || instance.modelMap.get(word.toString().toUpperCase()) == null) {
            if (map.indexOf(word.toString()) == -1) {
              map.add(word.toString());
            }
          }
        }
        else {
          i = start;
          i++;
        }
      }
      else {
        i++;
      }
    }
    return map;
  }

}
