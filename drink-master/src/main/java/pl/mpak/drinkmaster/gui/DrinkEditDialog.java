package pl.mpak.drinkmaster.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.beans.IntrospectionException;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import pl.mpak.drinkmaster.DrinkMasterPlugin;
import pl.mpak.drinkmaster.Sql;
import pl.mpak.drinkmaster.db.DrinkCompoRecord;
import pl.mpak.drinkmaster.db.DrinkRecord;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.RecordLink;
import pl.mpak.usedb.gui.linkreq.FieldRequeiredNotNull;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.array.StringList;

/**
 *
 * @author  akaluza
 */
public class DrinkEditDialog extends javax.swing.JDialog {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(DrinkMasterPlugin.class);

  private String dnk_id;
  private Database database;
  private int modalResult = ModalResult.NONE;
  private boolean componentChange;
  private DrinkRecord record;
  private RecordLink dataLink;
  private boolean fromClipboard;
  
  public DrinkEditDialog(Database database, String dnk_id, boolean fromClipboard) throws IntrospectionException, UseDBException {
    super(SwingUtil.getRootFrame(), true);
    this.database = database;
    this.dnk_id = dnk_id;
    this.fromClipboard = fromClipboard;
    initComponents();
    init();
  }

  public static String showDialog(Database database, boolean fromClipboard) throws IntrospectionException, UseDBException {
    DrinkEditDialog dialog = new DrinkEditDialog(database, null, fromClipboard);
    dialog.setVisible(true);
    return dialog.modalResult == ModalResult.OK ? dialog.dnk_id : null;
  }
  
  public static String showDialog(Database database, String dnk_id) throws IntrospectionException, UseDBException {
    DrinkEditDialog dialog = new DrinkEditDialog(database, dnk_id, false);
    dialog.setVisible(true);
    return dialog.modalResult == ModalResult.OK ? dialog.dnk_id : null;
  }
  
  private void init() throws IntrospectionException, UseDBException {
    dataLink = new RecordLink();
    dataLink.add("DNK_NAME", textName, new FieldRequeiredNotNull(stringManager.getString("drink-name")));
    dataLink.add("DNK_MAKE_UP", textMakeUp);
    
    if (dnk_id != null) {
      record = new DrinkRecord(database, dnk_id);
      Query query = database.createQuery();
      try {
        query.setSqlText(Sql.getComponentList());
        query.paramByName("DNK_ID").setString(dnk_id);
        query.open();
        while (!query.eof()) {
          textComponents.append(query.fieldByName("dnc_component").getString() +"\n");
          query.next();
        }
      }
      catch (Exception ex) {
        throw new UseDBException(ex);
      }
      finally {
        query.close();
      }
    } else {
      record = new DrinkRecord(database);
    }
    dataLink.updateComponents(record);

    textComponents.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        componentChange = true;
      }
      public void removeUpdate(DocumentEvent e) {
        componentChange = true;
      }
      public void changedUpdate(DocumentEvent e) {
      }
    });
    
    if (dnk_id == null && fromClipboard) {
      Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
      try {
        if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          StringList sl = new StringList();
          sl.setText((String)t.getTransferData(DataFlavor.stringFlavor));
          if (sl.size() > 8) {
            int line = 0;
            while ("".equals(sl.get(line).trim())) {
              line++;
            }
            textName.setText(sl.get(line).trim());
            line++;
            while (sl.get(line).trim().startsWith("--------") || "".equals(sl.get(line).trim()) || sl.get(line).trim().toUpperCase().startsWith("SK£AD")) {
              line++;
            }
            while (!"".equals(sl.get(line).trim())) {
              StringTokenizer st = new StringTokenizer(sl.get(line).trim(), ",");
              while (st.hasMoreTokens()) {
                if (textComponents.getText().length() > 0) {
                  textComponents.append("\n");
                }
                textComponents.append(st.nextToken().trim());
              }
              line++;
            }
            while ("".equals(sl.get(line).trim()) || sl.get(line).trim().toUpperCase().startsWith("JAK WYKONAÆ:") || sl.get(line).trim().toUpperCase().startsWith("SPOSÓB PRZYRZ¥DZENIA:")) {
              line++;
            }
            while (!"".equals(sl.get(line).trim())) {
              textMakeUp.append(sl.get(line).trim());
              line++;
            }
          }
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    
    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    
    SwingUtil.centerWithinScreen(this);
  }
  
  private void updateDrink() throws Exception {
    dataLink.updateRecord(record);
    if (record.isChanged() || componentChange) {
      if (dnk_id == null) {
        record.applyInsert();
        dnk_id = record.getId();
      } else {
        record.applyUpdate();
        if (componentChange) {
          Command command = database.createCommand();
          command.setSqlText("delete from drink_compos where dnc_dnk_id = :DNK_ID");
          command.paramByName("DNK_ID").setString(dnk_id);
          command.execute();
        }
      }
      if (componentChange) {
        StringList sl = new StringList();
        sl.setText(textComponents.getText());
        for (int i=0; i<sl.size(); i++) {
          DrinkCompoRecord dcr = new DrinkCompoRecord(database);
          dcr.setDnkId(dnk_id);
          dcr.setNo(i +1L);
          dcr.setComponent(sl.get(i));
          dcr.applyInsert();
        }
      }
    }
  }
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmOk = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    jLabel2 = new javax.swing.JLabel();
    textName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textComponents = new pl.mpak.sky.gui.swing.comp.TextArea();
    jScrollPane2 = new javax.swing.JScrollPane();
    textMakeUp = new pl.mpak.sky.gui.swing.comp.TextArea();
    jLabel4 = new javax.swing.JLabel();

    cmOk.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmOk.setText(stringManager.getString("ok-amp")); // NOI18N
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cancel-amp")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("drink-edit-dialog-title")); // NOI18N

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("drink-name-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("composes-dd")); // NOI18N

    textComponents.setColumns(20);
    textComponents.setRows(5);
    textComponents.setFont(new java.awt.Font("Monospaced", 0, 14));
    jScrollPane1.setViewportView(textComponents);

    textMakeUp.setColumns(20);
    textMakeUp.setRows(5);
    textMakeUp.setFont(new java.awt.Font("Monospaced", 0, 14));
    jScrollPane2.setViewportView(textMakeUp);

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("make-dd")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel3)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel4)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  try {
    updateDrink();
    modalResult = ModalResult.OK;
    dispose();
  } catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
  }
}//GEN-LAST:event_cmOkActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private pl.mpak.sky.gui.swing.comp.TextArea textComponents;
  private pl.mpak.sky.gui.swing.comp.TextArea textMakeUp;
  private pl.mpak.sky.gui.swing.comp.TextField textName;
  // End of variables declaration//GEN-END:variables

}
