/*
 * SimpleSelectDialog.java
 *
 * Created on 9 paüdziernik 2007, 16:30
 */

package pl.mpak.orbada.gui.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.ListRowChangeKeyListener;
import pl.mpak.util.StringUtil;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskPool;

/**
 *
 * @author  akaluza
 */
public class SimpleSelectDialog extends javax.swing.JDialog {
  
  private String formatData = null;
  private ArrayList<?> listData = null;
  private ArrayList<?> showListData = null;
  private ListModel listModel;
  private KeyListener keyListener;
  
  private int modalResult = ModalResult.NONE;
  
  public interface CallBack {
    public void selected(Object o);
  }
  
  public static Object select(Window owner, int x, int y, Vector<?> listData, int selected, CallBack callBack) {
    return select(owner, x, y, null, listData, selected, callBack);
  }
  
  public static Object select(Window owner, int x, int y, String formatData, Vector<?> listData, int selected, CallBack callBack) {
    return select(new SimpleSelectDialog(owner, formatData, listData, selected), x, y, callBack);
  }
  
  public static Object select(Window owner, int x, int y, ArrayList<?> listData, int selected, CallBack callBack) {
    return select(owner, x, y, null, listData, selected, callBack);
  }
  
  public static Object select(Window owner, int x, int y, String formatData, ArrayList<?> listData, int selected, CallBack callBack) {
    return select(new SimpleSelectDialog(owner, formatData, listData, selected), x, y, callBack);
  }
  
  private static Object select(final SimpleSelectDialog dialog, int x, int y, final CallBack callBack) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    if (x +dialog.getWidth() > screenSize.getWidth()) {
      x -= (x +dialog.getWidth() -screenSize.getWidth());
    }
    if (y +dialog.getHeight() > screenSize.getHeight()) {
      y -= (y +dialog.getHeight() -screenSize.getHeight());
    }
    dialog.setBounds(x, y, dialog.getWidth(), dialog.getHeight());
    dialog.setVisible(true);

    if (!dialog.isModal() && callBack != null) {
      if (dialog.getModalResult() == ModalResult.OK) {
        callBack.selected(dialog.getSelectedValue());
      }
      TaskPool.getTaskPool().addTask(new Task() {
        @Override
        public void run() {
          while (dialog.isVisible()) {
            try {
              Thread.sleep(10);
            } catch (InterruptedException interruptedException) {
            }
          }
          if (dialog.getModalResult() == ModalResult.OK) {
            callBack.selected(dialog.getSelectedValue());
          }
        }
      });
    } else {
      if (dialog.getModalResult() == ModalResult.OK) {
        return dialog.getSelectedValue();
      }
    }
    return null;
  }
  
  /** Creates new form SimpleSelectDialog
   * @param window
   * @param formatData
   * @param listData
   * @param selected
   */
  public SimpleSelectDialog(Window window, String formatData, Vector<?> listData, int selected) {
    this(window, formatData, new ArrayList(listData), selected);
  }
  
  public SimpleSelectDialog(Window window, String formatData, ArrayList<?> listData, int selected) {
    super(window);
    this.listData = listData;
    this.showListData = listData;
    this.formatData = formatData;
    
    initComponents();
    init(selected);
    setModal(window == null);
  }
  
  private void init(int selected) {
    keyListener = new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
          if (showListData.size() > 0) {
            modalResult = ModalResult.OK;
            dispose();
          }
          evt.consume();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
          modalResult = ModalResult.CANCEL;
          dispose();
          evt.consume();
        }
      }
    };
    
    listModel = new AbstractListModel() {
      private static final long serialVersionUID = 1L;
      @Override
      public int getSize() {
        return showListData == null ? 0 : showListData.size();
      }
      @Override
      public Object getElementAt(int index) {
        if (formatData != null) {
          return showListData == null ? "No data found" : String.format(formatData, ((Vector<?>)showListData.get(index)).toArray());
        } else {
          return showListData == null ? "No data found" : showListData.get(index);
        }
      }
    };
    listSearchResult.setModel(listModel);
    listSearchResult.addKeyListener(keyListener);
    listSearchResult.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        listSearchResult.ensureIndexIsVisible(listSearchResult.getSelectedIndex());
      }
    });
    
    textFieldSearch.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateList();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
        updateList();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
      }
    });
    textFieldSearch.addKeyListener(keyListener);
    textFieldSearch.addKeyListener(new ListRowChangeKeyListener(listSearchResult));
    
    updateList();
    setPreferredSize(listSearchResult.getPreferredSize());
    
    pack();
    Dimension d = getSize();
    if (d.width > 400) {
      d.width = 400;
    } else if (d.width < 100) {
      d.width = 130;
    } else {
      d.width += 30;
    }
    if (d.height > 300) {
      d.height = 300;
    } else if (d.height < 100) {
      d.height = 130;
    } else {
      d.height += 30;
    }
    setSize(d);
    setSelectedIndex(selected);
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        listSearchResult.ensureIndexIsVisible(listSearchResult.getSelectedIndex());
      }
    });
  }
  
  private void updateList() {
    if (StringUtil.equals(textFieldSearch.getText(), "")) {
      showListData = listData;
    } else {
      showListData = (ArrayList<?>)listData.clone();
      int i = 0;
      while (i < showListData.size()) {
        if (showListData.get(i).toString().toUpperCase().indexOf(textFieldSearch.getText().toUpperCase()) == -1) {
          showListData.remove(i);
        } else {
          i++;
        }
      }
      if (showListData != null && showListData.size() > 0) {
        setSelectedIndex(0);
      }
    }
    listSearchResult.revalidate();
    listSearchResult.repaint();
  }
  
  public void setSelectedIndex(int index) {
    listSearchResult.setSelectedIndex(index);
    listSearchResult.ensureIndexIsVisible(index);
  }
  
  public int getModalResult() {
    return modalResult;
  }
  
  public Object getSelectedValue() {
    if (listSearchResult.getSelectedIndex() >= 0) {
      return showListData.get(listSearchResult.getSelectedIndex());
    } else {
      return null;
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    panelContent = new javax.swing.JPanel();
    textFieldSearch = new javax.swing.JTextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    listSearchResult = new javax.swing.JList();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setBounds(new java.awt.Rectangle(4, 4, 4, 4));
    setUndecorated(true);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowDeactivated(java.awt.event.WindowEvent evt) {
        formWindowDeactivated(evt);
      }
    });

    panelContent.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

    listSearchResult.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    listSearchResult.setFixedCellHeight(18);
    listSearchResult.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        listSearchResultMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(listSearchResult);

    javax.swing.GroupLayout panelContentLayout = new javax.swing.GroupLayout(panelContent);
    panelContent.setLayout(panelContentLayout);
    panelContentLayout.setHorizontalGroup(
      panelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(textFieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
    );
    panelContentLayout.setVerticalGroup(
      panelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panelContentLayout.createSequentialGroup()
        .addComponent(textFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(panelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(panelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void listSearchResultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listSearchResultMouseClicked
  if (evt.getButton() == MouseEvent.BUTTON1) {
    modalResult = ModalResult.OK;
    dispose();
  }
}//GEN-LAST:event_listSearchResultMouseClicked

private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
  if (isVisible()) {
    dispose();
  }
}//GEN-LAST:event_formWindowDeactivated

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList listSearchResult;
  private javax.swing.JPanel panelContent;
  private javax.swing.JTextField textFieldSearch;
  // End of variables declaration//GEN-END:variables
  
}
