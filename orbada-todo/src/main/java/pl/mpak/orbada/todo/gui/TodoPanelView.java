/*
 * TodoPanelView.java
 *
 * Created on 23 listopad 2007, 20:15
 */

package pl.mpak.orbada.todo.gui;

import java.awt.Color;
import java.awt.Component;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EventObject;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.gui.comps.table.ViewTable;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.todo.OrbadaTodoPlugin;
import pl.mpak.orbada.todo.db.Todo;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.QueryListener;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.patt.Resolvers;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class TodoPanelView extends javax.swing.JPanel implements Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("todo");

  private IViewAccesibilities accesibilities;
  private String td_sch_id;
  
  /**
   * Creates new form TodoPanelView
   * @param accesibilities
   */
  public TodoPanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }
  
  private void init() {
    menuTodo.setText(SwingUtil.setButtonText(menuTodo, stringManager.getString("TodoPanelView-menuTodo-text")));
    cmNewTask.setEnabled(false);
    cmEditTask.setEnabled(false);
    cmDeleteTask.setEnabled(false);
    cmSwitchEnableTask.setEnabled(false);
    
    accesibilities.addMenu(menuTodo);
    
    tableTodos.getQuery().setDatabase(InternalDatabase.get());
    tableTodos.getQuery().addQueryListener(new QueryListener() {
      public void beforeScroll(EventObject e) {
      }
      public void afterScroll(EventObject e) {
      }
      public void beforeOpen(EventObject e) {
      }
      public void afterOpen(EventObject e) {
        cmNewTask.setEnabled(true);
      }
      public void beforeClose(EventObject e) {
      }
      public void afterClose(EventObject e) {
        cmNewTask.setEnabled(false);
        cmEditTask.setEnabled(false);
        cmDeleteTask.setEnabled(false);
        cmSwitchEnableTask.setEnabled(false);
        cmIncPriority.setEnabled(false);
        cmDecPriority.setEnabled(false);
      }
      public void flushedPerformed(EventObject e) {
      }
      public void errorPerformed(EventObject e) {
      }
    });
    
    tableTodos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        cmEditTask.setEnabled(e.getFirstIndex() >= 0);
        cmDeleteTask.setEnabled(e.getFirstIndex() >= 0);
        cmSwitchEnableTask.setEnabled(e.getFirstIndex() >= 0);
        cmIncPriority.setEnabled(e.getFirstIndex() >= 0);
        cmDecPriority.setEnabled(e.getFirstIndex() >= 0);
        cmExportTodo.setEnabled(e.getFirstIndex() >= 0);
        cmImportTodo.setEnabled(true);
      }
    });
    
    try {
      QueryTableCellRenderer renderer = new QueryTableCellRenderer() {
        {
          setShowNullValue(false);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
          try {
            if ("F".equals(tableTodos.getQuery().fieldByName("td_enable").getString())) {
              label.setForeground(Color.DARK_GRAY);
            }
          } catch (Exception ex) {
          }
          return label;
        }
      };
      if (accesibilities.getDatabase() != null) {
        td_sch_id = accesibilities.getDatabase().getUserProperties().getProperty("schemaId");
      }
      tableTodos.addColumn(new QueryTableColumn("usr_name", stringManager.getString("user-name"), 80, renderer));
      tableTodos.addColumn(new QueryTableColumn("td_inserted", stringManager.getString("inserted"), 110, renderer));
      tableTodos.addColumn(new QueryTableColumn("td_updated", stringManager.getString("updated"), 110, renderer));
      if (td_sch_id == null) {
        tableTodos.addColumn(new QueryTableColumn("sch_name", stringManager.getString("schema"), 140, renderer));
      }
      tableTodos.addColumn(new QueryTableColumn("td_title", stringManager.getString("title"), 200, renderer));
      tableTodos.addColumn(new QueryTableColumn("td_priority", stringManager.getString("priority"), 50, renderer));
      tableTodos.addColumn(new QueryTableColumn("td_state", stringManager.getString("status"), 150, renderer));
      tableTodos.addColumn(new QueryTableColumn("td_description", stringManager.getString("description"), 600, renderer));
      if (td_sch_id == null) {
        tableTodos.getQuery().setSqlText("select todos.*, sch_name, usr_name from todos left outer join schemas on (td_sch_id = sch_id) left outer join users on (td_usr_id = usr_id) where (td_usr_id = :td_usr_id or usr_orbada = 'Y') order by td_enable desc, usr_orbada, td_priority, td_state");
        tableTodos.getQuery().paramByName("td_usr_id").setString(accesibilities.getApplication().getUserId());
      } else {
        tableTodos.getQuery().setSqlText("select todos.*, usr_name from todos left outer join users on (td_usr_id = usr_id) where (td_sch_id = :td_sch_id or usr_orbada = 'Y') order by td_enable desc, usr_orbada, td_priority, td_state");
        tableTodos.getQuery().paramByName("td_sch_id").setString(td_sch_id);
      }
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          try {
            tableTodos.getQuery().open();
            if (!tableTodos.getQuery().isEmpty()) {
              tableTodos.changeSelection(0, 0, false, false);
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
      });
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }
  }
  
  public void close() throws IOException {
    tableTodos.getQuery().close();
    accesibilities = null;
  }
  
  public void refreshQuery() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int lastRow = tableTodos.getSelectedRow();
        try {
          String td_id = null;
          if (tableTodos.getQuery().isActive() && lastRow != -1) {
            tableTodos.getQuery().getRecord(lastRow);
            td_id = tableTodos.getQuery().fieldByName("td_id").getString();
          }
          tableTodos.getQuery().refresh();
          if (td_id != null) {
            if (tableTodos.getQuery().locate("td_id", new Variant(td_id))) {
              tableTodos.changeSelection(tableTodos.getQuery().getCurrentRecord().getIndex(), tableTodos.getSelectedColumn(), false, false);
              lastRow = -1;
            }
          }
          if (lastRow != -1) {
            if (tableTodos.getRowCount() > lastRow && lastRow >= 0) {
              tableTodos.setRowSelectionInterval(lastRow, lastRow);
            } else if (tableTodos.getRowCount() > 0) {
              tableTodos.setRowSelectionInterval(0, 0);
            }
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    });
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmNewTask = new pl.mpak.sky.gui.swing.Action();
    cmEditTask = new pl.mpak.sky.gui.swing.Action();
    cmDeleteTask = new pl.mpak.sky.gui.swing.Action();
    cmSwitchEnableTask = new pl.mpak.sky.gui.swing.Action();
    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    menuTodo = new javax.swing.JMenu();
    menuRefresh = new javax.swing.JMenuItem();
    jSeparator4 = new javax.swing.JSeparator();
    menuNew = new javax.swing.JMenuItem();
    menuEdit = new javax.swing.JMenuItem();
    jSeparator8 = new javax.swing.JSeparator();
    menuExport = new javax.swing.JMenuItem();
    menuImport = new javax.swing.JMenuItem();
    jSeparator5 = new javax.swing.JSeparator();
    menuSwitchEnable = new javax.swing.JMenuItem();
    jSeparator6 = new javax.swing.JSeparator();
    menuDelete = new javax.swing.JMenuItem();
    cmIncPriority = new pl.mpak.sky.gui.swing.Action();
    cmDecPriority = new pl.mpak.sky.gui.swing.Action();
    cmExportTodo = new pl.mpak.sky.gui.swing.Action();
    cmImportTodo = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JSeparator();
    buttonNew = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonEdit = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator7 = new javax.swing.JToolBar.Separator();
    buttonExport = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonImport = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JSeparator();
    buttonSwitchEnable = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonIncPriority = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonDescPriority = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator1 = new javax.swing.JSeparator();
    buttonDelete = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableTodos = new ViewTable();
    queryTableStatusBar1 = new pl.mpak.usedb.gui.swing.QueryTableStatusBar();

    cmNewTask.setActionCommandKey("cmNewTask");
    cmNewTask.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/new16.gif"))); // NOI18N
    cmNewTask.setText(stringManager.getString("cmNewTask-text")); // NOI18N
    cmNewTask.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewTaskActionPerformed(evt);
      }
    });

    cmEditTask.setActionCommandKey("cmEditTask");
    cmEditTask.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/edit16.gif"))); // NOI18N
    cmEditTask.setText(stringManager.getString("cmEditTask-text")); // NOI18N
    cmEditTask.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditTaskActionPerformed(evt);
      }
    });

    cmDeleteTask.setActionCommandKey("cmDeleteTask");
    cmDeleteTask.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/trash.gif"))); // NOI18N
    cmDeleteTask.setText(stringManager.getString("cmDeleteTask-text")); // NOI18N
    cmDeleteTask.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteTaskActionPerformed(evt);
      }
    });

    cmSwitchEnableTask.setActionCommandKey("cmSwitchEnable");
    cmSwitchEnableTask.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/enabled.gif"))); // NOI18N
    cmSwitchEnableTask.setText(stringManager.getString("cmSwitchEnableTask-text")); // NOI18N
    cmSwitchEnableTask.setTooltip(stringManager.getString("cmSwitchEnableTask-hint")); // NOI18N
    cmSwitchEnableTask.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSwitchEnableTaskActionPerformed(evt);
      }
    });

    cmRefresh.setActionCommandKey("cmRefresh");
    cmRefresh.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/refresh16.gif"))); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    menuRefresh.setAction(cmRefresh);
    menuTodo.add(menuRefresh);
    menuTodo.add(jSeparator4);

    menuNew.setAction(cmNewTask);
    menuTodo.add(menuNew);

    menuEdit.setAction(cmEditTask);
    menuTodo.add(menuEdit);
    menuTodo.add(jSeparator8);

    menuExport.setAction(cmExportTodo);
    menuTodo.add(menuExport);

    menuImport.setAction(cmImportTodo);
    menuTodo.add(menuImport);
    menuTodo.add(jSeparator5);

    menuSwitchEnable.setAction(cmSwitchEnableTask);
    menuTodo.add(menuSwitchEnable);
    menuTodo.add(jSeparator6);

    menuDelete.setAction(cmDeleteTask);
    menuTodo.add(menuDelete);

    cmIncPriority.setActionCommandKey("cmIncPriority");
    cmIncPriority.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/arrowup.gif"))); // NOI18N
    cmIncPriority.setText(stringManager.getString("cmIncPriority-text")); // NOI18N
    cmIncPriority.setTooltip(stringManager.getString("cmIncPriority-hint")); // NOI18N
    cmIncPriority.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmIncPriorityActionPerformed(evt);
      }
    });

    cmDecPriority.setActionCommandKey("cmDecPriority");
    cmDecPriority.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/arrowdown.gif"))); // NOI18N
    cmDecPriority.setText(stringManager.getString("cmDecPriority-text")); // NOI18N
    cmDecPriority.setTooltip(stringManager.getString("cmDecPriority-hint")); // NOI18N
    cmDecPriority.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDecPriorityActionPerformed(evt);
      }
    });

    cmExportTodo.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/export_table16.gif"))); // NOI18N
    cmExportTodo.setText(stringManager.getString("cmExportTodo-text")); // NOI18N
    cmExportTodo.setTooltip(stringManager.getString("cmExportTodo-hint")); // NOI18N
    cmExportTodo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmExportTodoActionPerformed(evt);
      }
    });

    cmImportTodo.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/import_table16.gif"))); // NOI18N
    cmImportTodo.setText(stringManager.getString("cmImportTodo-text")); // NOI18N
    cmImportTodo.setTooltip(stringManager.getString("cmImportTodo-hint")); // NOI18N
    cmImportTodo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmImportTodoActionPerformed(evt);
      }
    });

    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonRefresh);

    jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jToolBar1.add(jSeparator3);

    buttonNew.setAction(cmNewTask);
    buttonNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonNew);

    buttonEdit.setAction(cmEditTask);
    buttonEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonEdit);
    jToolBar1.add(jSeparator7);

    buttonExport.setAction(cmExportTodo);
    buttonExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonExport);

    buttonImport.setAction(cmImportTodo);
    buttonImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonImport);

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jToolBar1.add(jSeparator2);

    buttonSwitchEnable.setAction(cmSwitchEnableTask);
    buttonSwitchEnable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonSwitchEnable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonSwitchEnable);

    buttonIncPriority.setAction(cmIncPriority);
    buttonIncPriority.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonIncPriority.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonIncPriority);

    buttonDescPriority.setAction(cmDecPriority);
    buttonDescPriority.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDescPriority.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonDescPriority);

    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jToolBar1.add(jSeparator1);

    buttonDelete.setAction(cmDeleteTask);
    buttonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    jToolBar1.add(buttonDelete);

    jPanel1.add(jToolBar1, java.awt.BorderLayout.WEST);

    add(jPanel1, java.awt.BorderLayout.NORTH);

    jScrollPane1.setViewportView(tableTodos);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);

    queryTableStatusBar1.setShowFieldType(false);
    queryTableStatusBar1.setShowOpenTime(false);
    queryTableStatusBar1.setTable(tableTodos);
    add(queryTableStatusBar1, java.awt.BorderLayout.SOUTH);
  }// </editor-fold>//GEN-END:initComponents

private void cmDecPriorityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDecPriorityActionPerformed
  if (tableTodos.getSelectedRow() != -1) {
    try {
      tableTodos.getQuery().getRecord(tableTodos.getSelectedRow());
      Todo todo = new Todo(tableTodos.getQuery().fieldByName("TD_ID").getString());
      todo.fieldByName("TD_PRIORITY").setValue(new Variant(tableTodos.getQuery().fieldByName("TD_PRIORITY").getLong() +1));
      todo.applyUpdate();
      refreshQuery();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmDecPriorityActionPerformed

private void cmIncPriorityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmIncPriorityActionPerformed
  if (tableTodos.getSelectedRow() != -1) {
    try {
      tableTodos.getQuery().getRecord(tableTodos.getSelectedRow());
      if (tableTodos.getQuery().fieldByName("TD_PRIORITY").getLong() > 1) {
        Todo todo = new Todo(tableTodos.getQuery().fieldByName("TD_ID").getString());
        todo.fieldByName("TD_PRIORITY").setValue(new Variant(tableTodos.getQuery().fieldByName("TD_PRIORITY").getLong() -1));
        todo.applyUpdate();
        refreshQuery();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmIncPriorityActionPerformed
  
private void cmSwitchEnableTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSwitchEnableTaskActionPerformed
  if (tableTodos.getSelectedRow() != -1) {
    try {
      tableTodos.getQuery().getRecord(tableTodos.getSelectedRow());
      Todo todo = new Todo(tableTodos.getQuery().fieldByName("TD_ID").getString());
      if ("T".equals(tableTodos.getQuery().fieldByName("TD_ENABLE").getString())) {
        todo.fieldByName("TD_ENABLE").setValue(new Variant("F"));
      }
      else {
        todo.fieldByName("TD_ENABLE").setValue(new Variant("T"));
      }
      todo.applyUpdate();
      refreshQuery();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmSwitchEnableTaskActionPerformed

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshQuery();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmDeleteTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteTaskActionPerformed
  if (tableTodos.getQuery().isActive() && tableTodos.getSelectedRow() >= 0) {
    try {
      tableTodos.getQuery().getRecord(tableTodos.getSelectedRow());
      if (MessageBox.show(this, stringManager.getString("deleting"), String.format(stringManager.getString("TodoPanelView-del-task-q"), new Object[] {tableTodos.getQuery().fieldByName("td_title").getString()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        Todo todo = new Todo();
        todo.getPrimaryKeyField().setValue(new Variant(tableTodos.getQuery().fieldByName("TD_ID").getString()));
        todo.applyDelete();
        refreshQuery();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteTaskActionPerformed

private void cmEditTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditTaskActionPerformed
  try {
    if (tableTodos.getQuery().isActive() && tableTodos.getSelectedRow() >= 0) {
      tableTodos.getQuery().getRecord(tableTodos.getSelectedRow());
      if (TodoEditDialog.showDialog(tableTodos.getQuery().fieldByName("td_id").getString(), td_sch_id) == ModalResult.OK) {
        refreshQuery();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditTaskActionPerformed

private void cmNewTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewTaskActionPerformed
  try {
    if (tableTodos.getQuery().isActive()) {
      if (TodoEditDialog.showDialog(null, td_sch_id) == ModalResult.OK) {
        refreshQuery();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewTaskActionPerformed

private void cmExportTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExportTodoActionPerformed
  try {
    if (tableTodos.getQuery().isActive() && tableTodos.getSelectedRow() >= 0) {
      tableTodos.getQuery().getRecord(tableTodos.getSelectedRow());
      Todo todo = new Todo(tableTodos.getQuery().fieldByName("td_id").getString());
      File file = FileUtil.selectFileToSave(
        this, stringManager.getString("saving-task"), Resolvers.expand("$(orbada.home)") +"/todos",
        new File(todo.fieldByName("TD_ID").getString() +".xml.todo"), 
        new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("task-files"), new String[] {".xml.todo"})});
      if (file != null) {
        todo.storeToXML(new FileOutputStream(file), "TODO");
        todo.fieldByName("TD_EXPORTED").setString("T");
        todo.applyUpdate();
        refreshQuery();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmExportTodoActionPerformed

private void cmImportTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmImportTodoActionPerformed
  if (ImportTodoDialog.showDialog()) {
    refreshQuery();
  }
}//GEN-LAST:event_cmImportTodoActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDelete;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDescPriority;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonEdit;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonExport;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonImport;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonIncPriority;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonNew;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSwitchEnable;
  private pl.mpak.sky.gui.swing.Action cmDecPriority;
  private pl.mpak.sky.gui.swing.Action cmDeleteTask;
  private pl.mpak.sky.gui.swing.Action cmEditTask;
  private pl.mpak.sky.gui.swing.Action cmExportTodo;
  private pl.mpak.sky.gui.swing.Action cmImportTodo;
  private pl.mpak.sky.gui.swing.Action cmIncPriority;
  private pl.mpak.sky.gui.swing.Action cmNewTask;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private pl.mpak.sky.gui.swing.Action cmSwitchEnableTask;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JSeparator jSeparator4;
  private javax.swing.JSeparator jSeparator5;
  private javax.swing.JSeparator jSeparator6;
  private javax.swing.JToolBar.Separator jSeparator7;
  private javax.swing.JSeparator jSeparator8;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JMenuItem menuDelete;
  private javax.swing.JMenuItem menuEdit;
  private javax.swing.JMenuItem menuExport;
  private javax.swing.JMenuItem menuImport;
  private javax.swing.JMenuItem menuNew;
  private javax.swing.JMenuItem menuRefresh;
  private javax.swing.JMenuItem menuSwitchEnable;
  private javax.swing.JMenu menuTodo;
  private pl.mpak.usedb.gui.swing.QueryTableStatusBar queryTableStatusBar1;
  private ViewTable tableTodos;
  // End of variables declaration//GEN-END:variables
  
}
