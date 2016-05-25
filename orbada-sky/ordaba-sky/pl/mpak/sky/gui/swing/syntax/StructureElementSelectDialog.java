/*
 * StructureElementSelectDialog.java
 *
 * Created on 25 styczeñ 2009, 14:15
 */

package pl.mpak.sky.gui.swing.syntax;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.ListRowChangeKeyListener;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CallableElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.DeclaredElement;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.SettingsUtil;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class StructureElementSelectDialog extends javax.swing.JDialog {
  private static final long serialVersionUID = 1L;

  private CodeElement root;
  private CodeElement selected;
  private int modalResult = ModalResult.NONE;
  private ArrayList<CodeElement> listFound;
  
  /** Creates new form StructureElementSelectDialog */
  public StructureElementSelectDialog(CodeElement root, CodeElement selected) {
    super(SwingUtil.getRootFrame(), true);
    this.root = root;
    this.selected = selected;
    this.listFound = new ArrayList<CodeElement>();
    initComponents();
    init();
  }
  
  public static CodeElement show(CodeElement root, CodeElement selected) {
    StructureElementSelectDialog dialog = new StructureElementSelectDialog(root, selected);
    dialog.setVisible(true);
    if (dialog.modalResult == ModalResult.OK) {
      return (CodeElement)dialog.listResults.getSelectedValue();
    } else {
      return null;
    }
  }

  private void init() {
    listResults.setModel(new FoundListModel());
    listResults.setCellRenderer(new FoundLineListCellRenderer());
    listResults.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        cmOk.setEnabled(listResults.getSelectedValue() != null);
      }
    });

    textSearch.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        search();
      }
      public void removeUpdate(DocumentEvent e) {
        search();
      }
      public void changedUpdate(DocumentEvent e) {
      }
    });
    textSearch.requestFocusInWindow();
    textSearch.addKeyListener(new ListRowChangeKeyListener(listResults));

    Properties props = SettingsUtil.get("sky", "structure-element-select-dialog"); //$NON-NLS-1$ //$NON-NLS-2$
    checkCallable.setSelected(StringUtil.toBoolean(props.getProperty("callable", "true"))); //$NON-NLS-1$ //$NON-NLS-2$
    int windowLeft = Integer.parseInt(props.getProperty("window-left", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
    int windowTop = Integer.parseInt(props.getProperty("window-top", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
    
    search();
    
    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel"); //$NON-NLS-1$
    getRootPane().getActionMap().put("cmCancel", cmCancel); //$NON-NLS-1$
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel});
    pack();
    if (windowLeft == -1 || windowTop == -1) {
      SwingUtil.centerWithinScreen(this);
    }
    else {
      setBounds(windowLeft, windowTop, getWidth(), getHeight());
    }
  }
  
  public void dispose() {
    try {
      Properties props = SettingsUtil.get("sky", "structure-element-select-dialog"); //$NON-NLS-1$ //$NON-NLS-2$

      props.setProperty("callable", checkCallable.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      props.setProperty("window-left", "" +getX()); //$NON-NLS-1$ //$NON-NLS-2$
      props.setProperty("window-top", "" +getY()); //$NON-NLS-1$ //$NON-NLS-2$
      
      SettingsUtil.store("sky", "structure-element-select-dialog"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
    super.dispose();
  }
  
  private void search(CodeElement item, String find) {
    while (item != null) {
      if (item instanceof BlockElement) {
        BlockElement block = (BlockElement)item;
        if (!checkCallable.isSelected()) {
          for (DeclaredElement e : block.getDeclaredList()) {
            if (e.getName().toUpperCase().indexOf(find) >= 0) {
              listFound.add(e);
            }
          }
        }
        for (CallableElement e : block.getCallableList()) {
          if (e.getName().toUpperCase().indexOf(find) >= 0) {
            listFound.add(e);
          }
        }
      }
      item = item.getOwner();
    }
  }
  
  private void search() {
    listFound.clear();
    String find = textSearch.getText().toUpperCase();
    search(selected != null ? selected : root, find);
    ((FoundListModel)listResults.getModel()).dataChanged();
    if (listResults.getSelectedIndex() >= listFound.size()) {
      listResults.setSelectedIndex(listFound.size() -1);
      listResults.ensureIndexIsVisible(listResults.getSelectedIndex());
    }
    else {
      CodeElement item = selected;
      while (item != null) {
        listResults.setSelectedValue(item, true);
        if (listResults.getSelectedValue() != null) {
          break;
        }
        item = item.getOwner();
      }
    }
    cmOk.setEnabled(listFound.size() > 0);
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmOk = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    textSearch = new pl.mpak.sky.gui.swing.comp.TextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    listResults = new javax.swing.JList();
    checkCallable = new javax.swing.JCheckBox();

    cmOk.setEnabled(false);
    cmOk.setText(Messages.getString("StructureElementSelectDialog.ok")); //$NON-NLS-1$
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(Messages.getString("StructureElementSelectDialog.cancel")); //$NON-NLS-1$
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(Messages.getString("StructureElementSelectDialog.search-structure")); //$NON-NLS-1$

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 24));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 24));

    jScrollPane1.setViewportView(listResults);

    checkCallable.setSelected(true);
    checkCallable.setText(Messages.getString("StructureElementSelectDialog.call-elements")); //$NON-NLS-1$
    checkCallable.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        checkCallableStateChanged(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(checkCallable)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(5, 5, 5)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(textSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(textSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(checkCallable))
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
modalResult = ModalResult.OK;
  dispose();
}//GEN-LAST:event_cmOkActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void checkCallableStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkCallableStateChanged
  search();
}//GEN-LAST:event_checkCallableStateChanged

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JCheckBox checkCallable;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList listResults;
  private pl.mpak.sky.gui.swing.comp.TextField textSearch;
  // End of variables declaration//GEN-END:variables

  private class FoundLineListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    public FoundLineListCellRenderer() {
      super();
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      setText(StructureUtil.getText((CodeElement)value));
      return this;
    }
  }
  
  private class FoundListModel extends AbstractListModel {
    private static final long serialVersionUID = 1L;
    public int getSize() {
      return listFound.size();
    }
    public Object getElementAt(int index) {
      return listFound.get(index);
    }
    public void dataChanged() {
      fireContentsChanged(this, 0, listFound.size());
    }
  }
  
}
