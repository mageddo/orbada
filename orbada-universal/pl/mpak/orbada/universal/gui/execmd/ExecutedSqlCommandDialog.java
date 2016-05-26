/*
 * ExecutedSqlCommand.java
 *
 * Created on 21 paüdziernik 2007, 14:22
 */

package pl.mpak.orbada.universal.gui.execmd;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import orbada.gui.comps.OrbadaSyntaxTextArea;
import orbada.gui.comps.table.DataTable;
import orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.HtmlUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskPool;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ExecutedSqlCommandDialog extends javax.swing.JDialog {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaUniversalPlugin.class);

  private int modalResult = ModalResult.NONE;
  private String sqlText;
  private String selectedFileId;
  private Database database;
  private String schemaId;
  private ISettings settings;
  
  /** Creates new form ExecutedSqlCommand 
   * @param database 
   */
  public ExecutedSqlCommandDialog(Database database) {
    super(SwingUtil.getRootFrame());
    this.database = database;
    this.schemaId = database.getUserProperties().getProperty("schemaId");
    initComponents();
    init();
  }
  
  public static String showDialog(Database database) {
    ExecutedSqlCommandDialog dialog = new ExecutedSqlCommandDialog(database);
    dialog.setVisible(true);
    if (dialog.modalResult == ModalResult.OK) {
      return dialog.sqlText;
    }
    return null;
  }
  
  private void init() {
    settings = Application.get().getSettings("orbada-execmd-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", new Variant(800)).getInteger(), settings.getValue("height", new Variant(500)).getInteger());
    } catch (Exception ex) {
    }
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonInsert);
    
    textSqlCommand.getEditorArea().setEditable(false);
    textSqlCommand.setDatabase(database);
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textFind.requestFocusInWindow();
        split.setDividerLocation(0.75f);
      }
    });
    tableSqlCommands.getQuery().setDatabase(ExecutedSqlManager.get().getDatabase());
    tableSqlCommands.addColumn(
      new QueryTableColumn("cmd_source", stringManager.getString("sql-command"), 700,new QueryTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, row, hasFocus, hasFocus, row, row);
        try {
          String sqlText = HtmlUtil.prepareText(SQLUtil.removeWhiteSpaces(value.toString()));
          label.setForeground(stringColor);
          label.setText("<html>" +sqlText);
          label.setHorizontalAlignment(JLabel.LEFT);
          label.setVerticalAlignment(JLabel.TOP);
          if (isSelected) {
            label.setBackground(selectionBackground);
          }
        } catch (Throwable ex) {
          ExceptionUtil.processException(ex);
          label.setText("<html>" +value.toString());
        }
        return label;
      }
    }));
    tableSqlCommands.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    tableSqlCommands.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int rowIndex = tableSqlCommands.getSelectedRow();
        if (rowIndex >= 0 && tableSqlCommands.getQuery().isActive()) {
          try {
            tableSqlCommands.getQuery().getRecord(rowIndex);
            selectedFileId = tableSqlCommands.getQuery().fieldByName("cmd_file_id").getString();
            sqlText = tableSqlCommands.getQuery().fieldByName("cmd_source").getString();
            textSqlCommand.setText(sqlText);
            textSqlCommand.getEditorArea().setCaretPosition(0);
            labelInfo.setText(String.format(stringManager.getString("ExecutedSqlCommandDialog-sql-command-info"),
              new Object[] {
              tableSqlCommands.getQuery().fieldByName("cmd_executed").getTimestamp(), 
              tableSqlCommands.getQuery().fieldByName("cmd_selected").getTimestamp(), 
              tableSqlCommands.getQuery().fieldByName("cmd_executed_count").getInteger()
            }));
            cmInsert.setEnabled(true);
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        } else {
          cmInsert.setEnabled(false);
        }
      }
    });
    tableSqlCommands.setRowHeight(70);
    try {
      tableSqlCommands.getQuery().setSqlText(
        "select * from execmds\n" +
        " where cmd_usr_id = :cmd_usr_id\n" +
        "   and (cmd_sch_id = :cmd_sch_id or cmd_sch_id is null)\n" +
        "   and (cmd_source like '%'||:cmd_source||'%' or cmd_source like '%'||upper(:cmd_source)||'%' or :cmd_source is null)\n" +
        " order by cmd_selected desc");
      refresh();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    textFind.addKeyListener(new TableRowChangeKeyListener(tableSqlCommands));
    SwingUtil.addAction(textFind, cmSearch);
    
    SwingUtil.centerWithinScreen(this);
  }
  
  @Override
  public void dispose() {
    settings.setValue("width", new Variant(getWidth()));
    settings.setValue("height", new Variant(getHeight()));
    settings.store();
    tableSqlCommands.getQuery().close();
    super.dispose();
  }
  
  private void refresh() {
    try {
      tableSqlCommands.getQuery().close();
      tableSqlCommands.getQuery().paramByName("cmd_usr_id").setString(Application.get().getUserId());
      tableSqlCommands.getQuery().paramByName("cmd_sch_id").setString(schemaId);
      tableSqlCommands.getQuery().paramByName("cmd_source").setString(textFind.getText());
      tableSqlCommands.getQuery().open();
      if (!tableSqlCommands.getQuery().isEmpty()) {
        tableSqlCommands.changeSelection(0, tableSqlCommands.getSelectedColumn(), false, false);
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
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
    cmInsert = new pl.mpak.sky.gui.swing.Action();
    cmSearch = new pl.mpak.sky.gui.swing.Action();
    buttonCancel = new javax.swing.JButton();
    buttonInsert = new javax.swing.JButton();
    labelInfo = new javax.swing.JLabel();
    textFind = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel1 = new javax.swing.JLabel();
    toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
    split = new javax.swing.JSplitPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSqlCommands = new DataTable();
    textSqlCommand = new OrbadaSyntaxTextArea();

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    cmInsert.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmInsert.setText(stringManager.getString("cmInsert-text")); // NOI18N
    cmInsert.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmInsertActionPerformed(evt);
      }
    });

    cmSearch.setActionCommandKey("cmSearch");
    cmSearch.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
    cmSearch.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_object16.gif")); // NOI18N
    cmSearch.setText(stringManager.getString("cmSearch-text")); // NOI18N
    cmSearch.setTooltip(stringManager.getString("cmSearch-hint")); // NOI18N
    cmSearch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSearchActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("ExecutedSqlCommandDialog-title")); // NOI18N
    setModal(true);

    buttonCancel.setAction(cmCancel);
    buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

    buttonInsert.setAction(cmInsert);
    buttonInsert.setPreferredSize(new java.awt.Dimension(75, 23));

    labelInfo.setText(" ");

    jLabel1.setText(stringManager.getString("search-dd")); // NOI18N

    toolButton1.setAction(cmSearch);
    toolButton1.setPreferredSize(new java.awt.Dimension(24, 24));

    split.setBorder(null);
    split.setDividerLocation(200);
    split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);

    jScrollPane1.setViewportView(tableSqlCommands);

    split.setLeftComponent(jScrollPane1);
    split.setRightComponent(textSqlCommand);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(split, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonInsert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textFind, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(toolButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(toolButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(split, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonInsert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(labelInfo))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void cmInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmInsertActionPerformed
  TaskPool.getTaskPool().addTask(new Task() {
    public void run() {
      ExecutedSqlManager.get().updateSelected(selectedFileId);
    }
  });
  modalResult = ModalResult.OK;
  dispose();
}//GEN-LAST:event_cmInsertActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSearchActionPerformed
  refresh();
}//GEN-LAST:event_cmSearchActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonInsert;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmInsert;
  private pl.mpak.sky.gui.swing.Action cmSearch;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JLabel labelInfo;
  private javax.swing.JSplitPane split;
  private DataTable tableSqlCommands;
  private pl.mpak.sky.gui.swing.comp.TextField textFind;
  private OrbadaSyntaxTextArea textSqlCommand;
  private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
  // End of variables declaration//GEN-END:variables
  
}
