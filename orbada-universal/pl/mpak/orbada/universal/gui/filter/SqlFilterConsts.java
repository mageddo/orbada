/*
 * SqlFilterConsts.java
 * 
 * Created on 2007-11-04, 19:23:52
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.filter;

/**
 *
 * @author akaluza
 */
public class SqlFilterConsts {

  public final static int COND_USER_VALUE = 0;
  public final static int COND_EQUAL = 1;
  public final static int COND_LESS = 2;
  public final static int COND_GREAT = 3;
  public final static int COND_LE = 4;
  public final static int COND_GE = 5;
  public final static int COND_NOT_EQUAL = 6;
  public final static int COND_LIKE = 7;
  public final static int COND_NOT_LIKE = 8;
  public final static int COND_IN = 9;
  public final static int COND_NOT_IN = 10;
  public final static int COND_IS_NULL = 11;
  public final static int COND_IS_NOT_NULL = 12;
  private final static int COND_LAST = 13;

  public final static int[] COND_ALL = {
    COND_EQUAL, COND_LESS, COND_GREAT, COND_LE, COND_GE, COND_NOT_EQUAL, 
    COND_LIKE, COND_NOT_LIKE, COND_IN, COND_NOT_IN, COND_IS_NULL, COND_IS_NOT_NULL
  };
  
  public final static String[] COND_STR_ALL = {
    "USER", "=", "<", ">", "<=", ">=", "<>", "LIKE", "NOT LIKE", "IN", "NOT IN", "IS NULL", "IS NOT NULL"
  };
  
  public static String condToStr(int cond) {
    if (cond >= COND_LAST) {
      return "???";
    }
    else {
      return COND_STR_ALL[cond];
    }
  }
  
  public static int strToCond(String cond) {
    for (int i=0; i<COND_STR_ALL.length; i++) {
      if (COND_STR_ALL[i].equals(cond)) {
        return i;
      }
    }
    return -1;
  }
  
  public static String[] getConditionList(int[] condList) {
    String[] strs = new String[condList.length];
    
    for (int i=0; i<condList.length; i++) {
      strs[i] = condToStr(condList[i]);
    }
    
    return strs;
  }
  
}
