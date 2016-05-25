package pl.mpak.sky.gui.swing.syntax;

import pl.mpak.util.StringUtil;
import pl.mpak.util.array.StringList;
import pl.mpak.util.patt.Resolvers;

public class Snippet {

  private boolean active = true;
  private String name;
  private String code;
  private String expandedCode;
  private String codeAtCursor;
  private boolean immediate;
  private int offset;
  private int length;
  private boolean preDefined;
  
  public Snippet() {
  }
  
  public Snippet(String name, String code, boolean immediate) {
    this.name = name;
    this.code = code;
    this.immediate = immediate;
  }
  
  public Snippet(String name, String code, boolean immediate, boolean preDefined) {
    this(name, code, immediate);
    this.preDefined = preDefined;
  }
  
  protected void updateCode(int padding) {
    offset = 0;
    if (code != null) {
      expandedCode = Resolvers.expand(code);
      if (padding > 0) {
        StringList sl = new StringList();
        sl.setText(expandedCode);
        for (int i=1; i<sl.size(); i++) {
          sl.set(i, StringUtil.padLeft("", padding) +sl.get(i));
        }
        expandedCode = sl.getText();
      }
      int index = expandedCode.indexOf('|');
      if (index >= 0) {
        offset = index;
        expandedCode = expandedCode.replace("|", "");
        codeAtCursor = expandedCode.substring(offset);
        length = expandedCode.length();
      }
    }
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    if (!StringUtil.equals(this.code, code)) {
      this.code = code;
    }
  }

  protected String getExpandedCode(int padding) {
    updateCode(padding);
    return expandedCode;
  }

  public boolean isImmediate() {
    return immediate;
  }

  public void setImmediate(boolean immediate) {
    this.immediate = immediate;
  }

  protected boolean isPreDefined() {
    return preDefined;
  }

  protected void setPreDefined(boolean preDefined) {
    this.preDefined = preDefined;
  }

  /**
   * Before call this function call getExpandedCode()
   * @return
   */
  public int getOffset() {
    return offset;
  }

  protected String getCodeAtCursor() {
    return codeAtCursor;
  }

  protected int getLength() {
    return length;
  }

}
