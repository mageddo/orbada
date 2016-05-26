package pl.mpak.usedb.gui.swing.crf;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.gui.swing.QueryTableCellRendererFilter;
import pl.mpak.util.StringUtil;

/**
 * @author akaluza
 * <p>Filtr dla wartoœci logicznych</p>
 * @see StringUtil.toBoolean();
 */
public class BooleanCellRendererFilter implements QueryTableCellRendererFilter {
  
  private Color trueColor;
  private Color falseColor;
  
  public BooleanCellRendererFilter() {
    this(SwingUtil.Color.GREEN, Color.RED);
  }
  
  public BooleanCellRendererFilter(Color trueColor, Color falseColor) {
    this.trueColor = trueColor;
    this.falseColor = falseColor;
  }
  
  @Override
  public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
    ((JLabel)renderer).setHorizontalAlignment(SwingConstants.CENTER);
    if ((value instanceof Boolean && (Boolean)value) || (value != null && StringUtil.toBoolean(value.toString()))) {
      if (trueColor != null) {
        ((JLabel)renderer).setForeground(trueColor);
      }
    }
    else {
      if (falseColor != null) {
        ((JLabel)renderer).setForeground(falseColor);
      }
    }
  }
}
