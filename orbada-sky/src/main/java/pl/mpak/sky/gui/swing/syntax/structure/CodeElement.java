package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pl.mpak.sky.gui.swing.syntax.SyntaxDocument.Token;

public class CodeElement {
  
  private int index;
  private String codeName;
  private int startOffset;
  private int endOffset;
  private CodeElement owner;
  private Token token;
  
  private CodeElementList<KeywordElement> keywords;

  public CodeElement(CodeElement owner, String codeName) {
    super();
    this.owner = owner;
    this.codeName = codeName;
  }
  
  protected void finalize() throws Throwable {
    super.finalize();
  }

  public String getCodeName() {
    return codeName;
  }

  public void setCodeName(String codeName) {
    this.codeName = codeName;
  }
  
  public String getDisplayCodeName() {
    return codeName;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public CodeElement getOwner() {
    return owner;
  }
  
  public void changeOwner(CodeElement owner) {
    this.owner = owner;
  }

  public void setStartOffset(int startOffset) {
    this.startOffset = startOffset;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public void setEndOffset(int length) {
    this.endOffset = length;
  }
  
  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Token getToken() {
    return token;
  }

  public void setToken(Token token) {
    this.token = token;
  }
  
  public void setTokenStyle(int styleId) {
    if (token != null) {
      token.styleId = styleId;
    }
  }

  public void setTokenWaveUnderline(int waveStyleId) {
    if (token != null) {
      token.waveStyleId = waveStyleId;
    }
  }

  public CodeElement getElementAt(int offset) {
    if (offset >= startOffset && offset <= endOffset) {
      return this;
    }
    return null;
  }
  
  public CodeElement getElementAt(CodeElement[] inElements, int offset) {
    CodeElement result;
    for (CodeElement e : inElements) {
      if (e != null) {
        if ((result = e.getElementAt(offset)) != null) {
          return result;
        }
      }
    }
    if (offset >= startOffset && offset <= endOffset) {
      return this;
    }
    return null;
  }
  
  public BlockElement getRootBlock() {
    CodeElement e = getOwner();
    if (e == null && this instanceof BlockElement) {
      return (BlockElement)this;
    }
    while (e != null) {
      if (e.getOwner() == null && e instanceof BlockElement) {
        return (BlockElement)e;
      }
      e = e.getOwner();
    }
    return null;
  }

  public CodeElement[] find(Class<?> clazz) {
    if (clazz.isInstance(this)) {
      return new CodeElement[] {this};
    }
    return new CodeElement[] {};
  }

  public CodeElement[] find(CodeElement[] inElements, Class<?> clazz) {
    Set<CodeElement> set = new HashSet<CodeElement>();
    for (CodeElement e : inElements) {
      if (e != null) {
        set.addAll(Arrays.asList(e.find(clazz)));
      }
    }
    if (clazz.isInstance(this)) {
      set.add(this);
    }
    return set.toArray(new CodeElement[set.size()]);
  }
  
  public CodeElement[] getOwners(Class<?> clazz) {
    ArrayList<CodeElement> list = new ArrayList<CodeElement>();
    CodeElement c = getOwner();
    while (c != null) {
      if (clazz.isInstance(c)) {
        list.add(c);
      }
      c = c.getOwner();
    }
    return list.toArray(new CodeElement[list.size()]);
  }

  public CodeElement getOwner(Class<?> clazz) {
    CodeElement c = getOwner();
    while (c != null) {
      if (clazz.isInstance(c)) {
        return c;
      }
      c = c.getOwner();
    }
    return null;
  }

  public CodeElement getOwner(Class<?> clazz, Class<?> stopClass) {
    return getOwner(clazz, new Class<?>[] {stopClass});
  }

  public CodeElement getOwner(Class<?> clazz, Class<?>[] stopClass) {
    CodeElement c = getOwner();
    while (c != null) {
      if (stopClass != null) {
        if (stopClass.length == 1 && stopClass[0].isInstance(c)) {
          return null;
        }
        else if (stopClass.length > 1) {
          for (Class<?> cs : stopClass) {
            if (cs.isInstance(c)) {
              return null;
            }
          }
        }
      }
      if (clazz.isInstance(c)) {
        return c;
      }
      c = c.getOwner();
    }
    return null;
  }

  public CodeElementList<KeywordElement> getKeywords() {
    if (keywords == null) {
      keywords = new CodeElementList<KeywordElement>(this);
    }
    return keywords;
  }
  
  public KeywordElement addKeyword(KeywordElement e) {
    if (getKeywords().add(e)) {
      return e;
    }
    return null;
  }

  public void setKeywords(CodeElementList<KeywordElement> keywords) {
    this.keywords = keywords;
  }
  
  public String keywordsToSource(int level, String ifEmpty) {
    if (keywords != null) {
      StringBuilder sb = new StringBuilder();
      for(KeywordElement e : keywords) {
        if (sb.length() > 0) {
          sb.append(" ");
        }
        sb.append(e.toSource(level));
      }
      return sb.toString();
    }
    return ifEmpty;
  }

  public String toString() {
    return "codeName=" +codeName +",startOffset=" +startOffset +",endOffset=" +endOffset;
  }
  
  public String toSource(int level) {
    return "";
  }
  
}
