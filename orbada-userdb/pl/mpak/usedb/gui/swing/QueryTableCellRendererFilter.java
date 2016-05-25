package pl.mpak.usedb.gui.swing;

import java.awt.Component;

import javax.swing.JTable;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Pozwala wykonaæ dodatkow¹ akcjê przy renderowaniu komórki tabeli 
 *
 * Przyk³¹d:
 *   public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus) {
 *      if (StringUtil.nvl((String)value, "").equals("VALID")) {
 *        ((JLabel)renderer).setForeground(new Color(0, 0x80, 0));
 *      }
 *      else if (StringUtil.nvl((String)value, "").equals("INVALID")) {
 *        ((JLabel)renderer).setForeground(Color.RED);
 *      }
 *   }   
 */
public interface QueryTableCellRendererFilter {
  
  public void cellRendererPerformed(JTable table, Component renderer, Object value, boolean isSelected, boolean hasFocus);
  
}
