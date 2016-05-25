package pl.mpak.sky.gui.swing.syntax;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.StringTokenizer;

public class SyntaxStyle implements Serializable {
  private static final long serialVersionUID = 2606207345339540060L;

  private String name = null;
  
  private Color foreground = null;
  private boolean bold = false;
  private boolean italic = false;
  private boolean underline = false;
  private boolean strickeout = false;
  private boolean enabled = true;
  
  private Font font = null;
  
  public SyntaxStyle() {
    super();
    setForeground(Color.BLACK);
  }

  public SyntaxStyle(String name) {
    this();
    setName(name);
  }

  public SyntaxStyle(String name, Color foreground) {
    this(name);
    setForeground(foreground);
  }

  public SyntaxStyle(String name, Color foreground, boolean bold, boolean italic) {
    this(name, foreground);
    setBold(bold);
    setItalic(italic);
  }

  public SyntaxStyle(
      String name, Color foreground, 
      boolean bold, boolean italic,
      boolean underline, boolean strickout) {
    this(name, foreground, bold, italic);
    setUnderline(underline);
    setStrickeout(strickout);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setForeground(Color foreground) {
    if (this.foreground != foreground) {
      this.foreground = foreground;
      font = null;
    }
  }

  public Color getForeground() {
    return foreground;
  }

  public Color getForeground(int alpha) {
    return new Color(foreground.getRed(), foreground.getGreen(), foreground.getBlue(), alpha);
  }

  public void setBold(boolean bold) {
    if (this.bold != bold) {
      this.bold = bold;
      font = null;
    }
  }

  public boolean isBold() {
    return bold;
  }

  public void setItalic(boolean italic) {
    if (this.italic != italic) {
      this.italic = italic;
      font = null;
    }
  }

  public boolean isItalic() {
    return italic;
  }
  
  public boolean isPlain() {
    return !(bold || italic);
  }
  
  public Font getFont(Font source) {
    if (font == null) {
      font = new Font(
          source.getFamily(),
          (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0),
          source.getSize());
    }
    return font;
  }
  
  /**
   * Pozwala zaktualizowaæ styl obiektu graficznego dla potrzeb odrysowania elementu 
   * na ekranie.
   * @param g
   * @param source
   */
  public void updateGraphics(Graphics g) {
    if (foreground != null) {
      g.setColor(foreground);
    }
    g.setFont(getFont(g.getFont()));
  }
  
  public void updateGraphics(Graphics g, int add) {
    updateGraphics(g);
    if (foreground != null) {
      g.setColor(pl.mpak.sky.gui.swing.SwingUtil.addColor(g.getColor(), add, add, add));
    }
  }
  
  public void updateGraphics(Graphics g, Color foreground) {
    updateGraphics(g);
    if (foreground != null) {
      g.setColor(foreground);
    }
  }
  
  public void setUnderline(boolean underline) {
    this.underline = underline;
  }

  public boolean isUnderline() {
    return underline;
  }

  public void setStrickeout(boolean strickeout) {
    this.strickeout = strickeout;
  }

  public boolean isStrickeout() {
    return strickeout;
  }

  public String toString() {
    return 
      "[name=" +name +",foreground=" +(foreground == null ? "null" : foreground.getRGB()) +
      (italic ? ",italic" : "") +
      (bold ? ",bold" : "") +
      (underline ? ",underline" : "") +
      (strickeout ? ",strickeout" : "") +
      (enabled ? ",enabled" : "") + "]";
  }
  
  public void fromString(String value) {
    if (value != null && value.length() >= 2) {
      if (value.charAt(0) == '[' && value.charAt(value.length() -1) == ']') {
        value = value.substring(1, value.length() -1);
      }
      boolean italicPresent = false;
      boolean boldPresent = false;
      boolean underlinePresent = false;
      boolean strickeoutPresent = false;
      boolean enabledPresent = false;
      StringTokenizer st = new StringTokenizer(value, ",");
      while (st.hasMoreTokens()) {
        String token = st.nextToken().trim(); 
        if (token.startsWith("foreground=")) {
          String color = token.substring(token.indexOf('=') +1).trim();
          foreground = new Color(Integer.parseInt(color));
        }
        else if ("italic".equals(token)) {
          italicPresent = true;
        }
        else if ("bold".equals(token)) {
          boldPresent = true;
        }
        else if ("underline".equals(token)) {
          underlinePresent = true;
        }
        else if ("strickeout".equals(token)) {
          strickeoutPresent = true;
        }
        else if ("enabled".equals(token)) {
          enabledPresent = true;
        }
      }
      italic = italicPresent;
      bold = boldPresent;
      underline = underlinePresent;
      strickeout = strickeoutPresent;
      enabled = enabledPresent;
      font = null;
    }
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
  
}
