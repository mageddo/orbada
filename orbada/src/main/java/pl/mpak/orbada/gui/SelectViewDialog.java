package pl.mpak.orbada.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.util.Utils;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.util.Utils;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.ListRowChangeKeyListener;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class SelectViewDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private int modalResult = ModalResult.NONE;
  private PerspectivePanel perspective;
  private ArrayList<ViewListElement> viewList;
  private Color selectedColor = UIManager.getColor("Table.selectionBackground");

  public SelectViewDialog(PerspectivePanel perspective) {
    super(SwingUtil.getRootFrame());
    this.perspective = perspective;
    this.viewList = new ArrayList<ViewListElement>();
    initComponents();
    init();
  }
  
  public static ViewAccesibilities show(PerspectivePanel perspective) {
    SelectViewDialog dialog = new SelectViewDialog(perspective);
    dialog.setVisible(true);
    if (dialog.modalResult == ModalResult.OK) {
      ViewListElement item = (ViewListElement)dialog.listViews.getSelectedValue();
      if (item.accessibilities != null) {
        return item.accessibilities;
      }
      Component component = perspective.createView(item.provider, false, false);
      if (component != null) {
        return perspective.getViewAccesibilities(component);
      }
    }
    return null;
  }
  
  private void init() {
    
    listViews.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof ViewListElement) {
          ViewListElement view = (ViewListElement)value;
          String text = getViewText(view);
          if (view.accessibilities != null) {
            if (view.accessibilities.getViewComponent().isVisible() && !isSelected) {
              label.setBackground(selectedColor);
            }
          }
          label.setIcon(view.provider.getIcon());
          label.setText(text);
          label.setVerticalAlignment(JLabel.TOP);
        }
        return label;
      }
    });
    
    ViewListElement selectedView = null;
    listViews.setModel(new DefaultListModel());
    for (int i=0; i<perspective.getTabbedViews().getTabCount(); i++) {
      Component c = perspective.getTabbedViews().getComponentAt(i);
      ViewAccesibilities va = perspective.getViewAccesibilities(c);
      ViewListElement el = new ViewListElement(va.getViewProvider(), va);
      if (c.isVisible()) {
        selectedView = el;
      }
      viewList.add(el);
    }
    ViewProvider[] wpa = Utils.sortViewList(Application.get().getServiceArray(ViewProvider.class));
    if (wpa != null && wpa.length > 0) {
      for (ViewProvider vp : wpa) {
        if (vp.isForDatabase(perspective.getDatabase())) {
          if (perspective.getViewComponent(vp) == null) {
            ViewListElement el = new ViewListElement(vp, null);
            viewList.add(el);
          }
        }
      }
    }

    cmSelect.setEnabled(false);
    refreshList(selectedView);
    editFiltr.addKeyListener(new ListRowChangeKeyListener(listViews));
    editFiltr.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        refreshList(null);
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
        refreshList(null);
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
      }
    });
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        editFiltr.requestFocus();
      }
    });

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonOk);
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel, buttonCloseView});
    pack();
    SwingUtil.centerWithinScreen(this);
  }
  
  private String getViewText(ViewListElement view) {
    String text = "<html>";
    if (view.accessibilities != null) {
      text = text +"<b>" +view.accessibilities.getTabTitle() +"</b>";
    }
    else {
      text = text +view.provider.getPublicName();
    }
    if (view.accessibilities != null && view.accessibilities.getTabExtTooltip() != null) {
      text = text +" - " +view.accessibilities.getTabExtTooltip();
    }
    else if (view.provider.getDescription() != null) {
      text = text +" - " +view.provider.getDescription();
    }
    return text;
  }
  
  private void refreshList(Object selected) {
    DefaultListModel model = (DefaultListModel)listViews.getModel();
    if (selected == null) {
      selected = listViews.getSelectedValue();
    }
    model.clear();
    for (ViewListElement item : viewList) {
      if (!"".equals(editFiltr.getText())) {
        String text = getViewText(item);
        if (text != null && text.toUpperCase().indexOf(editFiltr.getText().toUpperCase()) >= 0) {
          model.addElement(item);
        }
        else if (item.provider.getPublicName().toUpperCase().indexOf(editFiltr.getText().toUpperCase()) >= 0) {
          model.addElement(item);
        }
      }
      else {
        model.addElement(item);
      }
    }
    listViews.setSelectedValue(selected, true);
    if (listViews.getSelectedIndex() == -1 && model.size() > 0) {
      listViews.setSelectedIndex(0);
    }
  }
  
  private static class ViewListElement {
    ViewProvider provider;
    ViewAccesibilities accessibilities;
    public ViewListElement(ViewProvider provider, ViewAccesibilities accessibilities) {
      this.provider = provider;
      this.accessibilities = accessibilities;
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmCancel = new pl.mpak.sky.gui.swing.Action();
    cmSelect = new pl.mpak.sky.gui.swing.Action();
    cmCloseView = new pl.mpak.sky.gui.swing.Action();
    jScrollPane1 = new javax.swing.JScrollPane();
    listViews = new javax.swing.JList();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    buttonCloseView = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    editFiltr = new pl.mpak.sky.gui.swing.comp.TextField();

    cmCancel.setActionCommandKey("cmCancel");
    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    cmSelect.setActionCommandKey("cmOk");
    cmSelect.setText(stringManager.getString("cmSelect-text")); // NOI18N
    cmSelect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectActionPerformed(evt);
      }
    });

    cmCloseView.setActionCommandKey("cmCloseView");
    cmCloseView.setText(stringManager.getString("cmClose-text")); // NOI18N
    cmCloseView.setTooltip(stringManager.getString("SelectViewDialog-cmCloseView-hiny")); // NOI18N
    cmCloseView.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseViewActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("SelectViewDialog-title")); // NOI18N
    setModal(true);

    listViews.setFixedCellHeight(20);
    listViews.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        listViewsValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(listViews);

    buttonOk.setAction(cmSelect);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCloseView.setAction(cmCloseView);
    buttonCloseView.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCloseView.setPreferredSize(new java.awt.Dimension(85, 25));

    jLabel1.setText(stringManager.getString("SelectViewDialog-filter-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 269, Short.MAX_VALUE)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(editFiltr, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(editFiltr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(buttonCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void listViewsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listViewsValueChanged
  cmSelect.setEnabled(listViews.getSelectedIndex() != -1);
  if (listViews.getSelectedValue() instanceof ViewListElement) {
    ViewListElement view = (ViewListElement)listViews.getSelectedValue();
    cmCloseView.setEnabled(view.accessibilities != null);
  }
  else {
    cmCloseView.setEnabled(false);
  }
}//GEN-LAST:event_listViewsValueChanged

  private void cmSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectActionPerformed
  modalResult = ModalResult.OK;
  dispose();
}//GEN-LAST:event_cmSelectActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseViewActionPerformed
  int index = listViews.getSelectedIndex() -1;
  ViewListElement item = (ViewListElement)listViews.getSelectedValue();
  viewList.remove(item);
  ((DefaultListModel)listViews.getModel()).removeElementAt(listViews.getSelectedIndex());
  if (item.accessibilities != null) {
    item.accessibilities.getPerspectiveAccesibilities().closeView(item.accessibilities.getViewComponent());
    if (index < ((DefaultListModel)listViews.getModel()).getSize() && index >= 0) {
      listViews.setSelectedIndex(index);
    }
  }
  listViews.requestFocusInWindow();
}//GEN-LAST:event_cmCloseViewActionPerformed
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonCloseView;
  private javax.swing.JButton buttonOk;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmCloseView;
  private pl.mpak.sky.gui.swing.Action cmSelect;
  private pl.mpak.sky.gui.swing.comp.TextField editFiltr;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList listViews;
  // End of variables declaration//GEN-END:variables
  
}
