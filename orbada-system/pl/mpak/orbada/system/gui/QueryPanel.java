/*
 * TaskPanel.java
 *
 * Created on 12 listopad 2007, 18:30
 */

package pl.mpak.orbada.system.gui;

import java.awt.Color;
import java.sql.SQLException;
import java.util.Date;
import pl.mpak.orbada.system.OrbadaSystemPlugin;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class QueryPanel extends javax.swing.JPanel {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaSystemPlugin.class);
  private Query query;
  private Color orgColor;
  
  /** 
   * Creates new form TaskPanel 
   * @param task 
   */
  public QueryPanel(Query query) {
    this.query = query;
    initComponents();
    init();
  }
  
  private void init() {
    orgColor = getBackground();
    setFocusable(true);
  }

  public void update() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        updateStatus();
      }
    });
  }
  
  public void updateStatus() {
    labelDatabase.setText(query.getDatabase().getPublicName());
    textQuery.setText(query.getSqlText());
    textQuery.setCaretPosition(0);
    setBackground(orgColor);
    switch (query.getState()) {
      case CHECKING: labelStatus.setText(stringManager.getString("checking")); break;
      case CHECKED: labelStatus.setText(stringManager.getString("checked")); break;
      case OPENING: {
          labelStatus.setText(stringManager.getString("opening"));
          setBackground(SwingUtil.addColor(orgColor, 120, -10, -10));
        }
        break;
      case OPENED: 
        labelStatus.setText(String.format(stringManager.getString("opened-records"), new Object[] {query.getRecordCount(), new Variant(new Date(query.getOpenTime())), StringUtil.formatTime(query.getOpeningTime())}));
        break;
      case CLOSING: labelStatus.setText(stringManager.getString("closing")); break;
      case CLOSE: labelStatus.setText(stringManager.getString("closed")); break;
      default: labelStatus.setText("???");
    }
    cmCancel.setEnabled(
      query.getState() == Query.State.OPENING ||
      query.getState() == Query.State.OPENED);
    if (query.getState() == Query.State.OPENING) {
      cmCancel.setText(stringManager.getString("cancel"));
    }
    else if (query.getState() == Query.State.OPENED) {
      cmCancel.setText(stringManager.getString("close"));
    }
  }
  
  public void close() {
  }
  
  public Query getQuery() {
    return query;
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmCancel = new pl.mpak.sky.gui.swing.Action();
    jButton1 = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    textQuery = new javax.swing.JTextArea();
    labelDatabase = new javax.swing.JLabel();
    labelStatus = new javax.swing.JLabel();

    cmCancel.setActionCommandKey("cmCancel"); // NOI18N
    cmCancel.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icon/stop10.gif"))); // NOI18N
    cmCancel.setText(stringManager.getString("cancel")); // NOI18N
    cmCancel.setTooltip(""); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));

    jButton1.setAction(cmCancel);
    jButton1.setMargin(new java.awt.Insets(1, 1, 1, 1));

    textQuery.setColumns(20);
    textQuery.setEditable(false);
    textQuery.setFont(new java.awt.Font("Courier New", 0, 11));
    textQuery.setRows(5);
    jScrollPane1.setViewportView(textQuery);

    labelDatabase.setText("labelDatabase"); // NOI18N

    labelStatus.setText("labelStatus"); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(labelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton1))
          .addComponent(labelDatabase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(labelDatabase)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jButton1)
          .addComponent(labelStatus))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  try {
    if (query.getState() == Query.State.OPENING)  {
      query.cancel();
    }
    else if (query.getState() == Query.State.OPENED) {
      query.close();
    }
  } catch (SQLException ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCancelActionPerformed
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private javax.swing.JButton jButton1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JLabel labelDatabase;
  private javax.swing.JLabel labelStatus;
  private javax.swing.JTextArea textQuery;
  // End of variables declaration//GEN-END:variables
  
}
