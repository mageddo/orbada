package pl.mpak.usedb.gui.swing.crf;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import pl.mpak.usedb.Messages;
import pl.mpak.util.StringUtil;

/**
 * @author akaluza
 * <p>Filtr dla wartoœci logicznych Tak/Nie</p>
 * @see StringUtil.toBoolean();
 */
public class YesNoCellRendererFilter extends BooleanCellRendererFilter {
  
  public YesNoCellRendererFilter() {
    super();
  }
  
  public YesNoCellRendererFilter(Color trueColor, Color falseColor) {
    super(trueColor, falseColor);
  }
  
  @Override
  public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
    super.cellRendererPerformed(table, renderer, value, isSelected, hasFocus);
    if (value instanceof Boolean) {
      if ((Boolean)value) {
        ((JLabel)renderer).setText(Messages.getString("bool-yes"));
      }
      else {
        ((JLabel)renderer).setText(Messages.getString("bool-no"));
      }
    }
    else {
      if (StringUtil.toBoolean((String)value)) {
        ((JLabel)renderer).setText(Messages.getString("bool-yes"));
      }
      else {
        ((JLabel)renderer).setText(Messages.getString("bool-no"));
      }
    }
  }
}
