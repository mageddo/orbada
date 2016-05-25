package pl.mpak.sky;

import java.util.HashMap;

import pl.mpak.util.StringUtil;

public class SkySetting {
  
  public final static String CmCopyEdit_AsHtmlToo = "CmCopyEdit.as-html-too";
  public final static String CmTab_TabMoveSelected = "CmTab.tab-move-selected";
  public final static String CmTab_TabAsSpaces = "CmTab.tab-as-spaces";
  public final static String CmAutoComplete_AutoCompleteDot = "CmAutoComplete.auto-complete-dot";
  public final static String CmAutoComplete_AutoCompleteActiveChars = "CmAutoComplete.auto-complete-active-chars";
  public final static String CmAutoComplete_AutoCompleteInactiveChars = "CmAutoComplete.auto-complete-inactive-chars";
  public final static String AutoCompleteText_InsertionText = "AutoCompleteText.insertion-text";
  public final static String AutoCompleteText_AutomaticSingle = "AutoCompleteText.automatic-single";
  public final static String SyntaxEditor_StructureAutoComplete = "SyntaxEditor.structure-auto-complete";
  public final static String SyntaxEditor_StructureAutoCompleteVariables = "SyntaxEditor.structure-auto-complete-variables";
  public final static String SyntaxEditor_CurrentTextTrimWhitespaces = "SyntaxEditor.current-text-trim-whitespaces";
  public final static String SyntaxEditor_SmartEnd = "SyntaxEditor.smart-end";
  public final static String SyntaxEditor_SmartHome = "SyntaxEditor.smart-home";
  public final static String SyntaxEditor_TabToSpaceCount = "SyntaxEditor.tab-to-space-count";
  public final static String SyntaxEditor_PreDefinedSnippets = "SyntaxEditor.predefined-snippets";
  
  /**
   * <p>Opcja musi byæ ustawiona przed utworzeniem jakiejkolwiek akcji 
   */
  public final static String Action_MacOsCommandKey = "Action.mac-os-command-key";
  
  public final static String Default_CmAutoComplete_AutoCompleteActiveChars = ".";
  public final static String Default_CmAutoComplete_AutoCompleteInactiveChars = ",;";
  public final static boolean Default_Action_MacOsCommandKey = true;
  public final static int Default_SyntaxEditor_TabToSpaceCount = 2;

  private static HashMap<String, Object> settings = new HashMap<String, Object>();
  
  public static String getString(String name) {
    return getString(name, null);
  }
  
  public static String getString(String name, String defaultValue) {
    Object value = settings.get(name);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof String) {
      return (String)value;
    }
    return value.toString();
  }
  
  public static void setString(String name, String value) {
    settings.put(name, value);
  }

  public static Integer getInteger(String name) {
    return getInteger(name, null);
  }
  
  public static Integer getInteger(String name, Integer defaultValue) {
    Object value = settings.get(name);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Integer) {
      return (Integer)value;
    }
    return Integer.parseInt(value.toString());
  }
  
  public static void setInteger(String name, Integer value) {
    settings.put(name, value);
  }

  public static Long getLong(String name) {
    return getLong(name, null);
  }
  
  public static Long getLong(String name, Long defaultValue) {
    Object value = settings.get(name);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Long) {
      return (Long)value;
    }
    return Long.parseLong(value.toString());
  }
  
  public static void setLong(String name, Long value) {
    settings.put(name, value);
  }

  public static Boolean getBoolean(String name) {
    return getBoolean(name, null);
  }
  
  public static Boolean getBoolean(String name, Boolean defaultValue) {
    Object value = settings.get(name);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Boolean) {
      return (Boolean)value;
    }
    return StringUtil.toBoolean(value.toString());
  }
  
  public static void setBoolean(String name, Boolean value) {
    settings.put(name, value);
  }

}
