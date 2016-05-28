/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.gui.dfvisual;

import java.awt.Color;
import pl.mpak.orbada.oracle.dba.gui.GuiUtil;

/**
 *
 * @author akaluza
 */
public class NameInfo implements Comparable<NameInfo> {

  public final static Color backgroundColor = Color.LIGHT_GRAY;
  
  private String name;
  private long blocks;
  private long bytes;
  
  protected Color color;
  protected Color grayedColor;
  protected Color lightedColor;

  public NameInfo(String name) {
    this.name = name;
    setColor(GuiUtil.randomColor());
    clear();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getBlocks() {
    return blocks;
  }

  public void setBlocks(long blocks) {
    this.blocks = blocks;
  }

  public void addBlocks(long blocks) {
    this.blocks += blocks;
  }

  public long getBytes() {
    return bytes;
  }

  public void setBytes(long bytes) {
    this.bytes = bytes;
  }

  public void addBytes(long bytes) {
    this.bytes += bytes;
  }

  public Color getColor() {
    return color;
  }

  public Color getGrayedColor() {
    return grayedColor;
  }

  public Color getLightedColor() {
    return lightedColor;
  }

  public void setColor(Color color) {
    this.color = color;
    if (this.color != null) {
      int r = (backgroundColor.getRed() +this.color.getRed()) /2;
      int g = (backgroundColor.getGreen() +this.color.getGreen()) /2;
      int b = (backgroundColor.getBlue() +this.color.getBlue()) /2;
      grayedColor = new Color(r, g, b);
      r = Math.min(this.color.getRed() +40, 255);
      g = Math.min(this.color.getGreen() +40, 255);
      b = Math.min(this.color.getBlue() +40, 255);
      lightedColor = new Color(r, g, b);
    }
  }
  
  public void clear() {
    this.blocks = 0L;
    this.bytes = 0L;
  }
  
  @Override
  public String toString() {
    return name;
  }

  public int compareTo(NameInfo o) {
    return name.compareTo(o.getName());
  }

}
