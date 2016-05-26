/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.jsondataview.gui;

import javax.swing.Icon;

/**
 *
 * @author akaluza
 */
public class ITag {

  private String name;
  private String data;
  private Icon icon;
  private String tipText;

  public ITag(String n) {
    name = n;
  }

  public ITag(String n, String data) {
    this(n);
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public void setData(String d) {
    data = d;
  }

  public String getData() {
    return data;
  }

  public String getToolTipText() {
    return tipText;
  }

  public Icon getIcon() {
    return icon;
  }

  @Override
  public String toString() {
    return name + ": " + (data == null ? "" : " (" + data + ")");
  }
}
