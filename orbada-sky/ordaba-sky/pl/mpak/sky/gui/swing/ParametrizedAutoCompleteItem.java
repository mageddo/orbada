package pl.mpak.sky.gui.swing;

import java.util.ArrayList;

import javax.swing.Icon;

public class ParametrizedAutoCompleteItem extends AutoCompleteItem {

  private ArrayList<Parameter> paramList = new ArrayList<Parameter>();
  private int commaCount = -1;

  public ParametrizedAutoCompleteItem(String word, String type, Icon icon) {
    super(word, type, icon);
  }

  public ParametrizedAutoCompleteItem(String word, String type) {
    super(word, type, (String)null);
  }

  public ParametrizedAutoCompleteItem(String word) {
    super(word);
  }

  public ParametrizedAutoCompleteItem() {
    super();
  }

  public static String commentString(String text) {
    if (text == null) {
      return "";
    }
    return " <span style=\"color:#606060\">" +text +"</span>";
  }

  public static String dataTypeString(String text) {
    if (text == null) {
      return "";
    }
    return " <span style=\"color:#008000\">" +text +"</span>";
  }

  public void add(String name, String type) {
    paramList.add(new Parameter(name, type));
  }

  public int getSize() {
    return paramList.size();
  }

  public int getCommaCount() {
    return commaCount;
  }

  public void setCommaCount(int commaCount) {
    if (this.commaCount != commaCount) {
      this.commaCount = commaCount;
      updateDisplayText();
    }
  }

  @Override
  public String getDisplayText() {
    if (super.getDisplayText() == null) {
      updateDisplayText();
    }
    return super.getDisplayText();
  }

  @Override
  public String selectedText(String replacingText, String nextChar) {
    if (commaCount >= 0) {
      if (commaCount < paramList.size()) {
        return paramList.get(commaCount).getName();
      }
      return replacingText;
    }
    return super.selectedText(replacingText, nextChar);
  }

  private void updateDisplayText() {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    if (commaCount >= 0) {
      sb.append(commentString(getWord()));
    }
    else {
      sb.append("<b>" +getWord() +"</b>");
    }
    sb.append(" (");
    for (int i=0; i<paramList.size(); i++) {
      Parameter p = paramList.get(i);
      if (i > 0) {
        sb.append(", ");
      }
      if (commaCount >= 0) {
        if (i == commaCount) {
          sb.append("<b>");
          sb.append(p.getName());
          sb.append(dataTypeString(p.getType()));
          sb.append("</b>");
        }
        else {
          sb.append(commentString(p.getName()));
          sb.append(commentString(p.getType()));
        }
      }
      else {
        sb.append(p.getName());
        sb.append(dataTypeString(p.getType()));
      }
    }
    sb.append(")");
    if (getReturnDataType() != null) {
      sb.append(dataTypeString(getReturnDataType()));
    }
    if (getIcon() == null && getType() != null) {
      sb.append(" (" +getType() +")");
    }
    setDisplayText(sb.toString());
  }
  
  public final class Parameter {
    private String name;
    private String type;

    public Parameter(String name, String type) {
      this.name = name;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }
    
  }
  
}
