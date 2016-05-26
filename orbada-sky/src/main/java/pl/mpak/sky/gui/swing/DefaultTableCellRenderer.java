package pl.mpak.sky.gui.swing;

import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class DefaultTableCellRenderer implements TableCellRenderer {

  protected JLabel rendererLabel = new JLabel();
  protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  protected Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
  protected Color focusForeground = UIManager.getColor("Table.focusCellForeground");
  protected Color focusBackground = UIManager.getColor("Table.focusCellBackground");

  public DefaultTableCellRenderer() {
    super();
    rendererLabel.setOpaque(true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      rendererLabel.setForeground(table.getSelectionForeground());
      rendererLabel.setBackground(table.getSelectionBackground());
    }
    else {
      rendererLabel.setForeground(table.getForeground());
      rendererLabel.setBackground(table.getBackground());
    }

    if (hasFocus) {
      rendererLabel.setBorder(focusBorder);
      if (!isSelected && table.isCellEditable(row, column)) {
        rendererLabel.setForeground(focusForeground);
        rendererLabel.setBackground(focusBackground);
      }
    }
    else {
      rendererLabel.setBorder(noFocusBorder);
    }

    rendererLabel.setText((value == null ? "" : value).toString());

    if (value instanceof Date) {
      rendererLabel.setText(DateFormat.getDateTimeInstance().format(value));
      rendererLabel.setHorizontalAlignment(JLabel.CENTER);
    }
    else if (value instanceof Number) {
      rendererLabel.setHorizontalAlignment(JLabel.RIGHT);
    }
    else {
      rendererLabel.setHorizontalAlignment(JLabel.LEFT);
    }

    return rendererLabel;
  }

}
