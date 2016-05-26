package pl.mpak.usedb.gui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import pl.mpak.util.variant.Variant;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Domyœlny renderer komórki dla tabel Sky
 *
 */
public class QueryTableCellRenderer implements TableCellRenderer {
  private static final long serialVersionUID = 6321261514437274202L;

  protected JLabel rendererLabel = new JLabel();
  protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  protected Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
  protected static Color focusForeground = UIManager.getColor("Table.focusCellForeground");
  protected static Color focusBackground = UIManager.getColor("Table.focusCellBackground");
  public static Color selectionBackground = UIManager.getColor("Table.selectionBackground") == null ? new Color(204, 204, 255) : UIManager.getColor("Table.selectionBackground");
  public int evenRowShift = UIManager.get("ColorizedTable.eventRowShift") == null ? 15 : UIManager.getInt("ColorizedTable.eventRowShift");
  public int focusColumnShift = UIManager.get("ColorizedTable.focusColumnShift") == null ? 30 : UIManager.getInt("ColorizedTable.focusColumnShift");
  protected Color focusedBackground = new Color(
      Math.min(Math.max(0, selectionBackground.getRed() -focusColumnShift), 255), 
      Math.min(Math.max(0, selectionBackground.getGreen() -focusColumnShift), 255), 
      Math.min(Math.max(0, selectionBackground.getBlue() -focusColumnShift), 255));
  
  public static Color dateColor = new java.awt.Color(0, 0x80, 0);
  public static Color stringColor = UIManager.getColor("Table.foreground");
  public static Color numberColor = Color.RED;
  public static Color nullColor = new Color(192, 192, 192);
  public static Color boolColor = Color.CYAN;
  
  public static String nullValue = "{null}";

  protected Color fontColor = null;
  protected String format = null;
  protected Font font = null;
  protected int fontStyle;
  protected QueryTableCellRendererFilter cellRendererFilter = null;
  protected int textAlign = -1;
  
  protected boolean colorized = false;
  public static boolean colorizedCells = true;
  
  private boolean showNullValue = true;
  
  private Variant tempVariant = new Variant();
  private Color evenRowColor = null;

  public QueryTableCellRenderer() {
    super();
    rendererLabel.setOpaque(true);
    rendererLabel.setVerticalAlignment(JLabel.CENTER);
    this.colorized = colorizedCells;
  }

  /**
   * Ten konstruktor pozwala ustaliæ format tekstu komórki
   * Jest to format tekstowy i dotyczy raczej formatu html jaki jest dostêpny w Swing
   * 
   * Przyk³ad: "&lt;html&gt;&lt;b&gt;<b>%s</b>&lt;/b&gt;"
   * 
   * @param format
   */
  public QueryTableCellRenderer(String format) {
    this();
    this.format = format;
  }

  /**
   * Pozwala zdefiniowaæ jeden sta³y kolor czcionki
   * 
   * @param fontColor
   */
  public QueryTableCellRenderer(Color fontColor) {
    this();
    this.fontColor = fontColor;
  }

  /**
   * Pozwala w³¹czyæ kolorowanie wg typu danych:
   * o Date    - zmienna dateColor
   * o Number  - zmienna numberColor
   * o Boolean - zmienna boolColor
   * o null    - zmienna nullColor, jeœli wartoœæ w/w typ jest null
   * 
   * @param colorized
   */
  public QueryTableCellRenderer(boolean colorized) {
    this();
    this.colorized = colorized;
  }

  /**
   * W tym konstruktorze mo¿na okreœliæ dodatkow¹ akcjê renderowania zgodnie z
   * SkyTableCellRendererFilter
   * 
   * @param cellRendererFilter
   * @see QurtyTableCellRendererFilter
   */
  public QueryTableCellRenderer(QueryTableCellRendererFilter cellRendererFilter) {
    this();
    this.cellRendererFilter = cellRendererFilter;
  }

  /**
   * Pozwala ustaliæ sta³¹ czcionkê dla wybranej kolumny, domyœlnie JTable.getFont() 
   * 
   * @param font
   */
  public QueryTableCellRenderer(Font font) {
    this();
    this.font = font;
  }

  /**
   * Pozwala ustaliæ sta³¹ czcionkê dla wybranej kolumny, domyœlnie JTable.getFont() 
   * 
   * @param font
   */
  public QueryTableCellRenderer(int fontStyle) {
    this();
    this.fontStyle = fontStyle;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    if (value instanceof Variant) {
      value = ((Variant)value).getValue();
    }
    if (isSelected) {
      rendererLabel.setForeground(table.getSelectionForeground());
      if (hasFocus) {
        rendererLabel.setBackground(focusedBackground);
      }
      else {
        rendererLabel.setBackground(selectionBackground);
      }
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
    if (value instanceof Double || value instanceof Float || value instanceof BigDecimal || value instanceof Date) {
      tempVariant.setValue(value);
      rendererLabel.setText(tempVariant.toString());
    }
    else {
      rendererLabel.setText((value == null ? "" : value.toString()));
    }

    if (textAlign >= 0) {
      rendererLabel.setHorizontalAlignment(textAlign);
    }
    if (value instanceof Date) {
      if (textAlign < 0) {
        rendererLabel.setHorizontalAlignment(JLabel.CENTER);
      }
    }
    else if (value instanceof Number) {
      if (textAlign < 0) {
        rendererLabel.setHorizontalAlignment(JLabel.RIGHT);
      }
    }
    else if (value instanceof Image) {
      rendererLabel.setHorizontalAlignment(JLabel.CENTER);
      rendererLabel.setIcon(new ImageIcon((Image)value));
      rendererLabel.setText("");
    }
    else if (value instanceof Icon) {
      rendererLabel.setHorizontalAlignment(JLabel.CENTER);
      rendererLabel.setIcon((Icon)value);
      rendererLabel.setText("");
    }
    else {
      if (textAlign < 0) {
        rendererLabel.setHorizontalAlignment(JLabel.LEFT);
      }
    }

    if (format != null) {
      rendererLabel.setText(String.format(format, new Object[] { rendererLabel.getText() }));
    }
    if (value == null && showNullValue) {
      rendererLabel.setForeground(nullColor);
      rendererLabel.setIcon(null);
      rendererLabel.setText("");
    }
    else if (fontColor != null) {
      rendererLabel.setForeground(fontColor);
    }
    else if (colorized) {
      if (value instanceof Number) {
        rendererLabel.setForeground(numberColor);
      }
      else if (value instanceof Date) {
        rendererLabel.setForeground(dateColor);
      }
      else if (value instanceof Boolean) {
        rendererLabel.setForeground(boolColor);
      }
      else if (value instanceof String) {
        if (stringColor != null) {
          rendererLabel.setForeground(stringColor);
        }
      }
    }
    if (font != null) {
      rendererLabel.setFont(font);
    }
    else {
      if (fontStyle != -1) {
        font = table.getFont().deriveFont(fontStyle);
        rendererLabel.setFont(font);
      }
      else {
        rendererLabel.setFont(table.getFont());
      }
    }
    
    if (cellRendererFilter != null) {
      cellRendererFilter.cellRendererPerformed(table, rendererLabel, value, isSelected, hasFocus);
    }

    if (value == null && showNullValue) {
      rendererLabel.setText(nullValue);
    }
    if (!isSelected && table instanceof QueryTable && evenRowShift > 0 && (row +1) % 2 == 0) {
      Color c = rendererLabel.getBackground();
      c = new Color(
          Math.min(Math.max(c.getRed() -evenRowShift, 0), 255), 
          Math.min(Math.max(c.getGreen() -evenRowShift, 0), 255), 
          Math.min(Math.max(c.getBlue() -evenRowShift, 0), 255));
      rendererLabel.setBackground(c);
    }

    return rendererLabel;
  }

  public void setFontColor(Color fontColor) {
    this.fontColor = fontColor;
  }

  public Color getFontColor() {
    return fontColor;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getFormat() {
    return format;
  }

  public Font getFont() {
    return font;
  }
  
  public void setFont(Font font) {
    this.font = font;
  }
  
  public boolean getColorized() {
    return colorized;
  }
  
  public void setColorized(boolean colorized) {
    this.colorized = colorized;
  }
  
  /**
   * Ustawia wyrównanie tekstu
   * Wartoœci¹ mo¿e byæ jedna ze sta³ych SwingConstants
   * LEFT, RIGHT, CENTER lub -1 jako domyœlne wyrównanie
   * @param textAlign
   */
  public void setTextAlign(int textAlign) {
    this.textAlign = textAlign;
  }
  
  public int getTextAlign() {
    return textAlign;
  }

  public void setShowNullValue(boolean showNullValue) {
    this.showNullValue = showNullValue;
  }

  public boolean isShowNullValue() {
    return showNullValue;
  }
}
