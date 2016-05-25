package pl.mpak.usedb.gui.swing.crf;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;

/**
 * @author akaluza
 * <p>Filtr listy wartoœci i odpowiadaj¹cych im kolorów</p>
 */
public class ColorListCellRendererFilter implements QueryTableCellRendererFilter {

  private String[] values;
  private Color[] colors;
  
  public ColorListCellRendererFilter(String[] values, Color[] colors) {
    this.values = values;
    this.colors = colors;
  }

  @Override
  public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
    for (int i=0; i<values.length; i++) {
      if (values[i].equals((String)value) && colors[i] != null) {
        ((JLabel)renderer).setForeground(colors[i]);
      }
    }
  }

}
