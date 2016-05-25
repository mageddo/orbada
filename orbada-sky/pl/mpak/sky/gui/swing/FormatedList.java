package pl.mpak.sky.gui.swing;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

import pl.mpak.util.StringUtil;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Lista JList z mo¿liwoœci¹ formatowania wiersza
 *
 */
public class FormatedList extends JList {
  private static final long serialVersionUID = -4143226773263907958L;
  
  private String formatData = null;
  private ListModel listModel = null;
  private Vector<?> listData = null;

  public FormatedList(ListModel dataModel) {
    super(dataModel);
    init();
  }

  public FormatedList(Vector<?> listData) {
    super(listData);
    this.listData = listData;
    init();
  }

  public FormatedList(String formatData, Vector<?> listData) {
    super(listData);
    this.listData = listData;
    setDataFormat(formatData);
    init();
  }

  public FormatedList() {
    super();
    init();
  }

  private void init() {
    setModel(getListModel());
  }

  /**
   * Pozwala zdefiniowaæ sposób formatowania danych zawartych w Vector<?>.getElementAt(index)
   * @param formatData
   */
  public void setDataFormat(String formatData) {
    if (!StringUtil.equals(this.formatData, formatData)) {
      this.formatData = formatData;
    }
  }

  public String getFormatData() {
    return formatData;
  }
  
  private ListModel getListModel() {
    if (listModel == null) {
      listModel = new AbstractListModel() {
        private static final long serialVersionUID = 1L;
        public int getSize() {
          return listData == null ? 0 : listData.size();
        }
        public Object getElementAt(int index) {
          if (formatData != null) {
            return listData == null ? "No data found" : String.format(formatData, ((Vector<?>)listData.elementAt(index)).toArray());
          }
          else {
            return listData == null ? "No data found" : listData.elementAt(index);
          }
        }
      };
    }
    return listModel;
  }
  
  public void refresh() {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        revalidate();
        repaint();
      }
    });
  }
  
//  public void setListData(Vector<?> listData) {
//    if (this.listData != listData) {
//      this.listData = listData;
//      if (this.listData != null && this.listData.size() > 0) {
//        setSelectedIndex(0);
//      }
//      refresh();
//    }
//  }
  
  public Vector<?> getListData() {
    return listData;
  }
  
  public void setSelectedIndex(int index) {
    super.setSelectedIndex(index);
    ensureIndexIsVisible(index);
  }
}
