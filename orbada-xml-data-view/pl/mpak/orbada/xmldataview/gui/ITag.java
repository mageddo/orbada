/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.xmldataview.gui;

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

  public void addData(String d) {
    if (data == null) {
      setData(d);
    } else {
      data += d;
    }
  }

  @Override
  public String toString() {
    return name + ": " + (data == null ? "" : " (" + data + ")");
  }
}
