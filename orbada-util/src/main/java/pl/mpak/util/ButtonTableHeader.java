package pl.mpak.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Aby u¿yæ tej klasy nale¿y pos³u¿yæ siê konstrukcj¹ 
 * 
 * new JTable() {
 *   protected JTableHeader createDefaultTableHeader() {
 *     return new ButtonTableHeader(columnModel);
 *   }
 * }
 * 
 * Czemu ? Nie wiem... ale tylko tak dzia³a
 *
 */
public class ButtonTableHeader extends JTableHeader {
  private static final long serialVersionUID = -1L;

  private static ImageIcon ascendingIcon;
  private static ImageIcon descendingIcon;
  private boolean buttonPressed = false;
  private int pressedColumnIndex = -1;
  private int sortedColumnIndex = -1;
  private Order ordered = Order.NONE;
  private boolean dragged = false;
  private HeaderListener headerListener = new HeaderListener();
  private boolean noneOrderAllow = true;
  
  public ButtonTableHeader() {
    super();
    init();
  }

  public ButtonTableHeader(TableColumnModel cm) {
    super(cm);
    init();
  }

  private void init() {
    if (ascendingIcon == null) {
      ascendingIcon = new ImageIcon(getClass().getResource("/pl/mpak/util/res/ascending.gif"));
    }
    if (descendingIcon == null) {
      descendingIcon = new ImageIcon(getClass().getResource("/pl/mpak/util/res/descending.gif"));
    }
    setDefaultRenderer(new ButtonTableHeaderRenderer(getFont()));
  }
  
  public void setFont(Font font) {
    super.setFont(font);
    if (getDefaultRenderer() instanceof ButtonTableHeaderRenderer) {
      ((ButtonTableHeaderRenderer)getDefaultRenderer()).getButton().setFont(font);
    }
  }
  
  public void setTable(JTable table) {
    if (this.table != null && (this.table instanceof SortableTable || this.table.getModel() instanceof SortableTable)) {
      removeMouseListener(headerListener);
      removeMouseMotionListener(headerListener);
    }
    
    super.setTable(table);
    
    if (this.table != null && (this.table instanceof SortableTable || this.table.getModel() instanceof SortableTable)) {
      addMouseListener(headerListener);
      addMouseMotionListener(headerListener);
    }
  }
  
  public void sort(int columnModelIndex, Order ordered, int modifiers) {
    this.ordered = ordered;
    sortedColumnIndex = columnModelIndex;
    SortableTable st = null;
    if (table instanceof SortableTable) {
      st = (SortableTable)table;
    }
    else if (table.getModel() instanceof SortableTable) {
      st = (SortableTable)table.getModel();
    }
    if (st != null) {
      int rowIndex = table.getSelectedRow();
      int columnIndex = table.getSelectedColumn();
      table.changeSelection(-1, columnIndex, false, false);
      st.sortByColumn(sortedColumnIndex, ordered, modifiers);
      if (table.getModel() instanceof AbstractTableModel) {
        ((AbstractTableModel)table.getModel()).fireTableDataChanged();
      }
      table.changeSelection(rowIndex, columnIndex, false, false);
    }
  }
  
  public void resetOrder() {
    ordered = Order.NONE;
    sortedColumnIndex = -1;
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        repaint();
      }
    });
  }
  
  public boolean isNoneOrderAllow() {
    return noneOrderAllow;
  }

  public void setNoneOrderAllow(boolean noneOrderAllow) {
    this.noneOrderAllow = noneOrderAllow;
  }

  /**
   * Zwraca indeks modelu kolumny posortowanej
   * @return
   */
  public int getSortedColumnIndex() {
    return sortedColumnIndex;
  }

  class HeaderListener extends MouseAdapter implements MouseMotionListener {
    public void mousePressed(MouseEvent e) {
      buttonPressed = true;
      pressedColumnIndex = table.convertColumnIndexToModel(columnAtPoint(e.getPoint()));
      if (pressedColumnIndex != sortedColumnIndex) {
        ordered = Order.NONE;
      }
      repaint();
    }

    public void mouseReleased(MouseEvent e) {
      buttonPressed = false;
      if (!dragged) {
        sortedColumnIndex = -1;

        int index = table.convertColumnIndexToView(pressedColumnIndex);
        if (pressedColumnIndex > -1 && (index < table.getModel().getColumnCount() || index < table.getColumnModel().getColumnCount())) {
          if (ordered == Order.NONE) {
            ordered = Order.DESCENDING;
          }
          else if (ordered == Order.DESCENDING) {
            ordered = Order.ASCENDING;
          }
          else if (noneOrderAllow) {
            ordered = Order.NONE;
          }
          else {
            ordered = Order.ASCENDING;
          }
          sort(pressedColumnIndex, ordered, e.getModifiersEx());
        }
        repaint();
      }
      dragged = false;
    }

    public void mouseDragged(MouseEvent e) {
      dragged = true;
      if (buttonPressed) {
        buttonPressed = false;
        repaint();
      }
    }

    public void mouseMoved(MouseEvent e) {
      dragged = false;
    }

  }
  
  protected class ButtonTableHeaderRenderer implements TableCellRenderer {
    private JLabel button;

    public ButtonTableHeaderRenderer() {
      button = new JLabel();
      button.setPreferredSize(new java.awt.Dimension(76,18));
      button.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      //button.setBorder(new EmptyBorder(1, 1, 1, 1));
      button.setLayout(new BorderLayout());
      //button.setMargin(new Insets(2, 2, 2, 2));
      button.setHorizontalAlignment(SwingConstants.LEADING);
    }

    public ButtonTableHeaderRenderer(Font font) {
      this();
      button.setFont(font);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      int modelIndex = table.convertColumnIndexToModel(column);
      
      if (value == null) {
        value = "";
      }
      
//      if (buttonPressed && modelIndex == pressedColumnIndex) {
//        button.getModel().setArmed(true);
//        button.getModel().setPressed(true);
//      }
//      else {
//        button.getModel().setArmed(false);
//        button.getModel().setPressed(false);
//      }
      
      if (ordered == Order.ASCENDING && modelIndex == sortedColumnIndex) {
        button.setIcon(ascendingIcon);
      }
      else if (ordered == Order.DESCENDING && modelIndex == sortedColumnIndex) {
        button.setIcon(descendingIcon);
      }
      else {
        button.setIcon(null);
      }

      button.setText(value.toString());
      
      return button;
    }
    
    public JLabel getButton() {
      return button;
    }
  }

}
