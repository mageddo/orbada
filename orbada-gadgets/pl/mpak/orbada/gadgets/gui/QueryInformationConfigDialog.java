/*
 * QueryInformationConfigDialog.java
 *
 * Created on 13 paüdziernik 2008, 20:54
 */

package pl.mpak.orbada.gadgets.gui;

import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import pl.mpak.orbada.db.DriverType;
import pl.mpak.orbada.gadgets.OrbadaGadgetsPlugin;
import pl.mpak.orbada.gadgets.db.OgQueryInfoPerspectiveUpdater;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.TableColumn;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.id.UniqueID;

/**
 *
 * @author  akaluza
 */
public class QueryInformationConfigDialog extends javax.swing.JDialog {

  private StringManager stringManager = StringManagerFactory.getStringManager(OrbadaGadgetsPlugin.class);

  private IPerspectiveAccesibilities accesibilities;
  private DriverType driverType;
  private ArrayList<OgQueryInfoPerspectiveUpdater> list;
  private int modalResult = ModalResult.NONE;
  
  /** Creates new form QueryInformationConfigDialog */
  public QueryInformationConfigDialog(IPerspectiveAccesibilities accesibilities) throws UseDBException {
    super();
    this.accesibilities = accesibilities;
    driverType = new DriverType(accesibilities.getApplication().getOrbadaDatabase());
    this.list = new ArrayList<OgQueryInfoPerspectiveUpdater>();
    initComponents();
    init();
  }
  
  public static boolean show(IPerspectiveAccesibilities accesibilities) throws UseDBException {
    QueryInformationConfigDialog dialog = new QueryInformationConfigDialog(accesibilities);
    dialog.setVisible(true);
    return dialog.modalResult == ModalResult.OK;
  }

  private void init() throws UseDBException {
    driverType.loadRecordBy("DTP_NAME", new Variant(accesibilities.getDatabase().getDriverType()));
    tableQueryInfo.setModel(new AbstractTableModel() {
      private Class<?>[] classes = {Boolean.class, String.class, String.class, Integer.class, Integer.class};
      public int getRowCount() {
        return list.size();
      }
      public int getColumnCount() {
        return 0;
      }
      @Override
      public boolean isCellEditable(int rowIndex, int columnIndex) {
        OgQueryInfoPerspectiveUpdater i = list.get(rowIndex);
	return columnIndex == 0 || ((columnIndex == 3 || columnIndex == 4) && i.isChecked());
      }
      @Override
      public Class<?> getColumnClass(int columnIndex) {
	return classes[columnIndex];
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
          return list.get(rowIndex).isChecked();
        }
        else if (columnIndex == 1) {
          return list.get(rowIndex).getGqiName();
        }
        else if (columnIndex == 2) {
          return list.get(rowIndex).getDtpName();
        }
        else if (columnIndex == 3) {
          return list.get(rowIndex).getIntervalSec();
        }
        else if (columnIndex == 4) {
          return list.get(rowIndex).getOrder();
        }
        return null;
      }
      @Override
      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        OgQueryInfoPerspectiveUpdater i = list.get(rowIndex);
        if (columnIndex == 0) {
          if (!i.isChecked()) {
            i.setIntervalSec(60);
            i.setOrder(rowIndex);
          }
          i.setChecked((Boolean)aValue);
          fireTableRowsUpdated(rowIndex, rowIndex);
        }
        else if (columnIndex == 3) {
          i.setIntervalSec((Integer)aValue);
        }
        else if (columnIndex == 4) {
          i.setOrder((Integer)aValue);
        }
      }
    });
    tableQueryInfo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableQueryInfo.addColumn(new TableColumn(0, 30, stringManager.getString("ch")));
    tableQueryInfo.addColumn(new TableColumn(1, 250, stringManager.getString("name")));
    tableQueryInfo.addColumn(new TableColumn(2, 150, stringManager.getString("driver")));
    tableQueryInfo.addColumn(new TableColumn(3, 60, stringManager.getString("sec-interval")));
    tableQueryInfo.addColumn(new TableColumn(4, 60, stringManager.getString("order")));
    
    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    SwingUtil.centerWithinScreen(this);
    
    refresh();
  }

  private void refresh() {
    list.clear();
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.setSqlText(OgQueryInfoPerspectiveUpdater.getSql());
      query.paramByName("usr_id").setString(accesibilities.getApplication().getUserId());
      query.paramByName("pps_id").setString(accesibilities.getPerspectiveId());
      query.paramByName("dtp_id").setString(driverType.getId());
      query.open();
      while (!query.eof()) {
        OgQueryInfoPerspectiveUpdater i = new OgQueryInfoPerspectiveUpdater(query.getDatabase());
        i.updateFrom(query);
        i.setGqiName(query.fieldByName("gqi_name").getString());
        i.setDtpName(query.fieldByName("dtp_name").getString());
        i.setExists(!"".equals(i.getId()));
        list.add(i);
        query.next();
      }
    }
    catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    finally {
      query.close();
    }
    tableQueryInfo.revalidate();
  }

  private void apply() throws Exception {
    for (OgQueryInfoPerspectiveUpdater u : list) {
      if (!u.isExists() && u.isChecked()) {
        u.setPrimaryKeyValue(new Variant(new UniqueID().toString()));
        u.applyInsert();
      }
      else if (u.isExists() && !u.isChecked()) {
        u.applyDelete();
      }
      else if (u.isChanged()) {
        u.applyUpdate();
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
    jScrollPane1 = new javax.swing.JScrollPane();
    tableQueryInfo = new pl.mpak.orbada.gui.comps.table.Table();

    cmOk.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmOk.setText(stringManager.getString("cmOk-text")); // NOI18N
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("QueryInformationConfigDialog-title")); // NOI18N
    setModal(true);

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

    tableQueryInfo.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    tableQueryInfo.setRowHeight(18);
    jScrollPane1.setViewportView(tableQueryInfo);

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
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
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
    apply();
    modalResult = ModalResult.OK;
    dispose();
  } catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
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
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.orbada.gui.comps.table.Table tableQueryInfo;
  // End of variables declaration//GEN-END:variables

}
