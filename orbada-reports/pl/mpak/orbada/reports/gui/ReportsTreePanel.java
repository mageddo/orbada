/*
 * ReportsTreePanel.java
 *
 * Created on 30 paüdziernik 2008, 17:34
 */

package pl.mpak.orbada.reports.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.io.IOException;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.reports.Sql;
import pl.mpak.orbada.reports.db.ReportGroupList;
import pl.mpak.orbada.reports.db.ReportRecord;
import pl.mpak.orbada.reports.db.ReportsGroupRecord;
import pl.mpak.orbada.reports.gui.nodes.ReportGroupTreeNodeInfo;
import pl.mpak.orbada.reports.gui.nodes.ReportTreeNodeInfo;
import pl.mpak.orbada.reports.gui.nodes.RootGroupTreeNodeInfo;
import pl.mpak.orbada.reports.gui.nodes.TreeNodeWillExpand;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class ReportsTreePanel extends javax.swing.JPanel implements Closeable {

  private IPerspectiveAccesibilities accesibilities;
  private boolean viewClosing = false;
  private Action runAction;

  /** Creates new form ReportsTreePanel */
  public ReportsTreePanel(IPerspectiveAccesibilities accesibilities, Action runAction) {
    this.accesibilities = accesibilities;
    this.runAction = runAction;
    initComponents();
    init();
  }

  private void init() {
    if (runAction != null) {
      buttonRun.setAction(runAction);
    }
    else {
      buttonRun.setVisible(false);
    }
    treeReports.setCellRenderer(new ReportsTreeCellRenderer());
    SwingUtil.addAction(treeReports, cmCopy);
    reloadTree();
    
    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader()); 
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.open("select * from orep_groups where orepg_usr_id is null order by orepg_id");
      ReportGroupList groupList = new ReportGroupList();
      groupList.createFromQuery(query);
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
      Thread.currentThread().setContextClassLoader(oldCL); 
    }
  }
  
  public JTree getTree() {
    return treeReports;
  }
  
  private void reloadTree() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    root.add(addNodeWillExpand(new DefaultMutableTreeNode(new RootGroupTreeNodeInfo(
      "<html>" +java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("html_report_for_driver") +" " +getDatabase().getDriverType(), 
      getDatabase().getUserProperties().getProperty("dtp_id"), null, null, false))));
    root.add(addNodeWillExpand(new DefaultMutableTreeNode(new RootGroupTreeNodeInfo(
      "<html>" +java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("html_shared_reports"), 
      null, null, null, true))));
    root.add(addNodeWillExpand(new DefaultMutableTreeNode(new RootGroupTreeNodeInfo(
      "<html>" +java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("html_user_reports") +" " +accesibilities.getApplication().getUserName(), 
      null, null, accesibilities.getApplication().getUserId(), false))));
    treeReports.setModel(new DefaultTreeModel(root));
    if (root.getChildCount() > 0) {
      treeReports.setSelectionInterval(0, 0);
    }
  }
  
  private DefaultMutableTreeNode addNodeWillExpand(DefaultMutableTreeNode node) {
    node.add(new TreeNodeWillExpand());
    return node;
  }
  
  private boolean nodeWillExpand(DefaultMutableTreeNode node) {
    if (!node.isLeaf() && node.getChildCount() == 1) {
      return node.getChildAt(0) instanceof TreeNodeWillExpand;
    }
    return false;
  }

  public Database getDatabase() {
    return accesibilities.getDatabase();
  }
  
  public void close() throws IOException {
    viewClosing = true;
    accesibilities = null;
  }
  
  private void loadGlobalGroups(DefaultMutableTreeNode parent, String dtp_id, String orepg_id) {
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.setSqlText(Sql.getGlobalGroupList(null));
      query.paramByName("DTP_ID").setString(dtp_id);
      query.paramByName("OREPG_ID").setString(orepg_id);
      query.open();
      while (!query.eof()) {
        ReportsGroupRecord group = new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase());
        group.updateFrom(query);
        parent.add(addNodeWillExpand(new DefaultMutableTreeNode(new ReportGroupTreeNodeInfo(group))));
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }
  
  private void loadSharedGroups(DefaultMutableTreeNode parent, String dtp_id, String sch_id, String usr_id) {
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.setSqlText(Sql.getSharedGroupList(null));
      query.paramByName("DTP_ID").setString(dtp_id);
      query.paramByName("SCH_ID").setString(sch_id);
      query.paramByName("USR_ID").setString(usr_id);
      query.open();
      while (!query.eof()) {
        ReportsGroupRecord group = new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase());
        group.updateFrom(query);
        parent.add(addNodeWillExpand(new DefaultMutableTreeNode(new ReportGroupTreeNodeInfo(group))));
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }
  
  private void loadUserGroups(DefaultMutableTreeNode parent, String dtp_id, String sch_id, String usr_id) {
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.setSqlText(Sql.getUserGroupList(null));
      query.paramByName("DTP_ID").setString(dtp_id);
      query.paramByName("SCH_ID").setString(sch_id);
      query.paramByName("USR_ID").setString(usr_id);
      query.open();
      while (!query.eof()) {
        ReportsGroupRecord group = new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase());
        group.updateFrom(query);
        parent.add(addNodeWillExpand(new DefaultMutableTreeNode(new ReportGroupTreeNodeInfo(group))));
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }
  
  private void loadGroupsByGroup(DefaultMutableTreeNode parent, String dtp_id, String orepg_id) {
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.setSqlText(Sql.getGroupByGroupList(null));
      query.paramByName("DTP_ID").setString(dtp_id);
      query.paramByName("OREPG_ID").setString(orepg_id);
      query.open();
      while (!query.eof()) {
        ReportsGroupRecord group = new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase());
        group.updateFrom(query);
        parent.add(addNodeWillExpand(new DefaultMutableTreeNode(new ReportGroupTreeNodeInfo(group))));
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
  }
  
  private void loadReports(DefaultMutableTreeNode parent, String orepg_id) {
    Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
    try {
      query.setSqlText(Sql.getReportList(null));
      query.paramByName("OREPG_ID").setString(orepg_id);
      query.open();
      while (!query.eof()) {
        ReportRecord report = new ReportRecord(accesibilities.getApplication().getOrbadaDatabase());
        report.updateFrom(query);
        parent.add(new DefaultMutableTreeNode(new ReportTreeNodeInfo(report)));
        query.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
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

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    cmDelete = new pl.mpak.sky.gui.swing.Action();
    cmEdit = new pl.mpak.sky.gui.swing.Action();
    cmNewGroup = new pl.mpak.sky.gui.swing.Action();
    cmNewReport = new pl.mpak.sky.gui.swing.Action();
    menuSelectedNode = new javax.swing.JPopupMenu();
    menuNewGroup = new javax.swing.JMenuItem();
    menuNewReport = new javax.swing.JMenuItem();
    menuEdit = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JSeparator();
    menuCopy = new javax.swing.JMenuItem();
    jSeparator5 = new javax.swing.JSeparator();
    menuDelete = new javax.swing.JMenuItem();
    cmFreezeReport = new pl.mpak.sky.gui.swing.Action();
    cmCopy = new pl.mpak.sky.gui.swing.Action();
    jPanel10 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    buttonNewGroup = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonNewReport = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonEdit = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    buttonRun = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFreezeReport = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator3 = new javax.swing.JToolBar.Separator();
    buttonDelete = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    treeReports = new javax.swing.JTree();

    cmRefresh.setActionCommandKey("cmRefresh");
    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports"); // NOI18N
    cmRefresh.setText(bundle.getString("refresh")); // NOI18N
    cmRefresh.setTooltip(bundle.getString("refresh_tooltip")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    cmDelete.setActionCommandKey("cmDelete");
    cmDelete.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDelete.setText(bundle.getString("delete_report_grup")); // NOI18N
    cmDelete.setTooltip(bundle.getString("delete_report_group_tooltip")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    cmEdit.setActionCommandKey("cmEdit");
    cmEdit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEdit.setText(bundle.getString("edit_report_group")); // NOI18N
    cmEdit.setTooltip(bundle.getString("edit_report_group_tooltip")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmNewGroup.setActionCommandKey("cmNewGroup");
    cmNewGroup.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_group.gif")); // NOI18N
    cmNewGroup.setText(bundle.getString("add_group")); // NOI18N
    cmNewGroup.setTooltip(bundle.getString("add_group_tooltip")); // NOI18N
    cmNewGroup.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewGroupActionPerformed(evt);
      }
    });

    cmNewReport.setActionCommandKey("cmNewReport");
    cmNewReport.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_report.gif")); // NOI18N
    cmNewReport.setText(bundle.getString("add_report")); // NOI18N
    cmNewReport.setTooltip(bundle.getString("add_report_tooltip")); // NOI18N
    cmNewReport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewReportActionPerformed(evt);
      }
    });

    menuNewGroup.setAction(cmNewGroup);
    menuSelectedNode.add(menuNewGroup);

    menuNewReport.setAction(cmNewReport);
    menuSelectedNode.add(menuNewReport);

    menuEdit.setAction(cmEdit);
    menuSelectedNode.add(menuEdit);
    menuSelectedNode.add(jSeparator1);

    menuCopy.setAction(cmCopy);
    menuSelectedNode.add(menuCopy);
    menuSelectedNode.add(jSeparator5);

    menuDelete.setAction(cmDelete);
    menuSelectedNode.add(menuDelete);

    cmFreezeReport.setActionCommandKey("cmFreezeReport");
    cmFreezeReport.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
    cmFreezeReport.setText(bundle.getString("run_and_freeze")); // NOI18N
    cmFreezeReport.setTooltip(bundle.getString("run_and_freeze_tooltip")); // NOI18N
    cmFreezeReport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeReportActionPerformed(evt);
      }
    });

    cmCopy.setActionCommandKey("cmCopy");
    cmCopy.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
    cmCopy.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/copy.gif")); // NOI18N
    cmCopy.setText(bundle.getString("copy")); // NOI18N
    cmCopy.setTooltip(bundle.getString("copy_sql_command_tooltip")); // NOI18N
    cmCopy.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCopyActionPerformed(evt);
      }
    });

    setLayout(new java.awt.BorderLayout());

    jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);
    toolBar.add(jSeparator4);

    buttonNewGroup.setAction(cmNewGroup);
    buttonNewGroup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonNewGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonNewGroup);

    buttonNewReport.setAction(cmNewReport);
    buttonNewReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonNewReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonNewReport);

    buttonEdit.setAction(cmEdit);
    buttonEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonEdit);
    toolBar.add(jSeparator2);

    buttonRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRun);

    buttonFreezeReport.setAction(cmFreezeReport);
    buttonFreezeReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonFreezeReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonFreezeReport);
    toolBar.add(jSeparator3);

    buttonDelete.setAction(cmDelete);
    buttonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonDelete);

    jPanel10.add(toolBar);

    add(jPanel10, java.awt.BorderLayout.NORTH);

    treeReports.setRootVisible(false);
    treeReports.setShowsRootHandles(true);
    treeReports.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
      public void treeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
      }
      public void treeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
        treeReportsTreeWillExpand(evt);
      }
    });
    treeReports.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        treeReportsMouseClicked(evt);
      }
    });
    treeReports.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
      public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
        treeReportsValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(treeReports);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  reloadTree();
}//GEN-LAST:event_cmRefreshActionPerformed

private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeReports.getLastSelectedPathComponent();
  DefaultTreeModel model = (DefaultTreeModel)treeReports.getModel();
  try {
    if (node.getUserObject() instanceof ReportGroupTreeNodeInfo) {
      ReportGroupTreeNodeInfo info = (ReportGroupTreeNodeInfo)node.getUserObject();
      if (MessageBox.show(this, java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("deleting"), java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("delete_selected_group") +info.getName(), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        ReportsGroupRecord group = new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase());
        group.setPrimaryKeyValue(new Variant(info.getReportsGroup().getId()));
        group.applyDelete();
        model.removeNodeFromParent(node);
      }
    }
    else if (node.getUserObject() instanceof ReportTreeNodeInfo) {
      ReportTreeNodeInfo info = (ReportTreeNodeInfo)node.getUserObject();
      if (MessageBox.show(this, java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("deleting"), java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("delete_report") +info.getName(), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        ReportRecord report = new ReportRecord(accesibilities.getApplication().getOrbadaDatabase());
        report.setPrimaryKeyValue(new Variant(info.getReport().getId()));
        report.applyDelete();
        model.removeNodeFromParent(node);
      }
    }
  } catch (Exception ex) {
    MessageBox.show(this, java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmDeleteActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeReports.getLastSelectedPathComponent();
  DefaultTreeModel model = (DefaultTreeModel)treeReports.getModel();
  try {
    TreePath selectedPath = treeReports.getLeadSelectionPath();
    treeReports.expandPath(selectedPath);
    if (node.getUserObject() instanceof ReportGroupTreeNodeInfo) {
      ReportGroupTreeNodeInfo info = (ReportGroupTreeNodeInfo)node.getUserObject();
      String id = ReportGroupEditDialog.showDialog(accesibilities.getApplication().getOrbadaDatabase(), info.getReportsGroup().getId());
      if (id != null) {
        node.setUserObject(new ReportGroupTreeNodeInfo(new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase(), id)));
        model.nodeStructureChanged(node);
      }
    }
    else if (node.getUserObject() instanceof ReportTreeNodeInfo) {
      ReportTreeNodeInfo info = (ReportTreeNodeInfo)node.getUserObject();
      boolean shared = false;
      if (node.getParent() instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode)node.getParent()).getUserObject() instanceof ReportGroupTreeNodeInfo) {
        shared = ((ReportGroupTreeNodeInfo)((DefaultMutableTreeNode)node.getParent()).getUserObject()).getReportsGroup().isShared();
      }
      String id = ReportEditDialog.showDialog(getDatabase(), accesibilities.getApplication().getOrbadaDatabase(), info.getReport().getId(), null, shared);
      if (id != null) {
        node.setUserObject(new ReportTreeNodeInfo(new ReportRecord(accesibilities.getApplication().getOrbadaDatabase(), id)));
        model.nodeStructureChanged(node);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmNewGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewGroupActionPerformed
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeReports.getLastSelectedPathComponent();
  DefaultTreeModel model = (DefaultTreeModel)treeReports.getModel();
  try {
    TreePath selectedPath = treeReports.getLeadSelectionPath();
    treeReports.expandPath(selectedPath);
    if (node.getUserObject() instanceof RootGroupTreeNodeInfo) {
      RootGroupTreeNodeInfo info = (RootGroupTreeNodeInfo)node.getUserObject();
      String id = ReportGroupEditDialog.showDialog(accesibilities.getApplication().getOrbadaDatabase(), null, info.getDtpId(), info.getSchId(), info.getUsrId(), info.isShared());
      if (id != null) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ReportGroupTreeNodeInfo(new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase(), id)));
        model.insertNodeInto(newNode, node, node.getChildCount());
        //treeReports.expandPath(selectedPath);
        TreePath newPath = new TreePath(newNode.getPath());
        treeReports.scrollPathToVisible(newPath);
        treeReports.setSelectionPath(newPath);
      }
    }
    else if (node.getUserObject() instanceof ReportGroupTreeNodeInfo) {
      ReportGroupTreeNodeInfo info = (ReportGroupTreeNodeInfo)node.getUserObject();
      String id = ReportGroupEditDialog.showDialog(accesibilities.getApplication().getOrbadaDatabase(), info.getReportsGroup().getId(), info.getReportsGroup().getDtpId(), info.getReportsGroup().getSchId(), info.getReportsGroup().getUsrId(), info.getReportsGroup().isShared());
      if (id != null) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ReportGroupTreeNodeInfo(new ReportsGroupRecord(accesibilities.getApplication().getOrbadaDatabase(), id)));
        model.insertNodeInto(newNode, node, node.getChildCount());
        //treeReports.expandPath(selectedPath);
        TreePath newPath = new TreePath(newNode.getPath());
        treeReports.scrollPathToVisible(newPath);
        treeReports.setSelectionPath(newPath);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewGroupActionPerformed

private void cmNewReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewReportActionPerformed
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeReports.getLastSelectedPathComponent();
  DefaultTreeModel model = (DefaultTreeModel)treeReports.getModel();
  try {
    TreePath selectedPath = treeReports.getLeadSelectionPath();
    treeReports.expandPath(selectedPath);
    if (node.getUserObject() instanceof ReportGroupTreeNodeInfo) {
      ReportGroupTreeNodeInfo info = (ReportGroupTreeNodeInfo)node.getUserObject();
      String id = ReportEditDialog.showDialog(getDatabase(), accesibilities.getApplication().getOrbadaDatabase(), null, info.getReportsGroup().getId(), info.getReportsGroup().isShared());
      if (id != null) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ReportTreeNodeInfo(new ReportRecord(accesibilities.getApplication().getOrbadaDatabase(), id)));
        model.insertNodeInto(newNode, node, node.getChildCount());
        TreePath newPath = new TreePath(newNode.getPath());
        treeReports.scrollPathToVisible(newPath);
        treeReports.setSelectionPath(newPath);
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewReportActionPerformed

private void cmFreezeReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeReportActionPerformed
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeReports.getLastSelectedPathComponent();
  if (node != null) {
    ReportRecord report = ((ReportTreeNodeInfo)node.getUserObject()).getReport();
    accesibilities.createView(new ReportViewService(report));
  }
}//GEN-LAST:event_cmFreezeReportActionPerformed

private void treeReportsTreeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_treeReportsTreeWillExpand
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)evt.getPath().getLastPathComponent();
  if (node != null && nodeWillExpand(node)) {
    node.removeAllChildren();
    if (node.getUserObject() instanceof RootGroupTreeNodeInfo) {
      RootGroupTreeNodeInfo info = (RootGroupTreeNodeInfo)node.getUserObject();
      if (info.isShared() && info.getDtpId() == null && info.getSchId() == null && info.getUsrId() == null) {
        node.add(addNodeWillExpand(new DefaultMutableTreeNode(new RootGroupTreeNodeInfo(
          "<html>" +java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("html_report_for_driver") +" " +getDatabase().getDriverType(), 
          getDatabase().getUserProperties().getProperty("dtp_id"), null, accesibilities.getApplication().getUserId(), true))));
        node.add(addNodeWillExpand(new DefaultMutableTreeNode(new RootGroupTreeNodeInfo(
          "<html>" +java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("html_reports_for_schema") +" " +getDatabase().getPublicName(), 
          getDatabase().getUserProperties().getProperty("dtp_id"), getDatabase().getUserProperties().getProperty("schemaId"), accesibilities.getApplication().getUserId(), true))));
      }
      else if (!info.isShared() && info.getDtpId() == null && info.getSchId() == null) {
        node.add(addNodeWillExpand(new DefaultMutableTreeNode(new RootGroupTreeNodeInfo(
          "<html>" +java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("html_report_for_driver") +" " +getDatabase().getDriverType(), 
          getDatabase().getUserProperties().getProperty("dtp_id"), null, accesibilities.getApplication().getUserId(), false))));
        node.add(addNodeWillExpand(new DefaultMutableTreeNode(new RootGroupTreeNodeInfo(
          "<html>" +java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("html_reports_for_schema") +" " +getDatabase().getPublicName(), 
          getDatabase().getUserProperties().getProperty("dtp_id"), getDatabase().getUserProperties().getProperty("schemaId"), accesibilities.getApplication().getUserId(), false))));
      }
      else if (info.getSchId() == null && info.getUsrId() == null) {
        loadGlobalGroups(node, info.getDtpId(), null);
      }
      else if (info.isShared()) {
        loadSharedGroups(node, info.getDtpId(), info.getSchId(), info.getUsrId());
      }
      else {
        loadUserGroups(node, info.getDtpId(), info.getSchId(), info.getUsrId());
      }
    }
    else if (node.getUserObject() instanceof ReportGroupTreeNodeInfo) {
      ReportGroupTreeNodeInfo info = (ReportGroupTreeNodeInfo)node.getUserObject();
      loadGroupsByGroup(node, info.getReportsGroup().getDtpId(), info.getReportsGroup().getId());
      loadReports(node, info.getReportsGroup().getId());
    }
  }
}//GEN-LAST:event_treeReportsTreeWillExpand

private void treeReportsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeReportsMouseClicked
  if (evt.getButton() == MouseEvent.BUTTON3 && evt.getClickCount() == 1) {
    TreePath path = treeReports.getPathForLocation(evt.getX(), evt.getY());
    if (path != null) {
      treeReports.setSelectionPath(path);
      menuSelectedNode.show(treeReports, evt.getX(), evt.getY());
    }
  }
  else if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
    if (runAction != null && runAction.isEnabled()) {
      runAction.performe();
    }
    else if (cmFreezeReport.isEnabled()) {
      cmFreezeReport.performe();
    }
  }
}//GEN-LAST:event_treeReportsMouseClicked

private void treeReportsValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeReportsValueChanged
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeReports.getLastSelectedPathComponent();
  if (node != null) {
    if (node.getUserObject() instanceof RootGroupTreeNodeInfo) {
      RootGroupTreeNodeInfo info = (RootGroupTreeNodeInfo)node.getUserObject();
      cmNewGroup.setEnabled(info.getDtpId() != null || info.getSchId() != null);
      if (!accesibilities.getApplication().isUserAdmin() && info.getUsrId() == null) {
        cmNewGroup.setEnabled(false);
      }
    }
    else if (node.getUserObject() instanceof ReportGroupTreeNodeInfo) {
      ReportGroupTreeNodeInfo info = (ReportGroupTreeNodeInfo)node.getUserObject();
      cmNewGroup.setEnabled(!info.getReportsGroup().isShared());
    }
    else {
      cmNewGroup.setEnabled(false);
    }
    cmEdit.setEnabled(!(node.getUserObject() instanceof RootGroupTreeNodeInfo));
    cmDelete.setEnabled(!(node.getUserObject() instanceof RootGroupTreeNodeInfo));
    cmNewReport.setEnabled(node.getUserObject() instanceof ReportGroupTreeNodeInfo);
    if (runAction != null) {
      runAction.setEnabled(node.getUserObject() instanceof ReportTreeNodeInfo);
    }
    cmFreezeReport.setEnabled(node.getUserObject() instanceof ReportTreeNodeInfo);
    cmCopy.setEnabled(node.getUserObject() instanceof ReportTreeNodeInfo);
  }
  else {
    cmNewGroup.setEnabled(false);
    cmEdit.setEnabled(false);
    cmDelete.setEnabled(false);
    cmNewReport.setEnabled(false);
    if (runAction != null) {
      runAction.setEnabled(false);
    }
    cmFreezeReport.setEnabled(false);
    cmCopy.setEnabled(false);
  }
}//GEN-LAST:event_treeReportsValueChanged

private void cmCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCopyActionPerformed
  DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeReports.getLastSelectedPathComponent();
  if (node != null) {
    if (node.getUserObject() instanceof ReportTreeNodeInfo) {
      ReportTreeNodeInfo info = (ReportTreeNodeInfo)node.getUserObject();
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection data = new StringSelection(info.getReport().getSql());
      clipboard.setContents(data, data);
    }
  }
}//GEN-LAST:event_cmCopyActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonDelete;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonEdit;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreezeReport;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonNewGroup;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonNewReport;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRun;
  private pl.mpak.sky.gui.swing.Action cmCopy;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmFreezeReport;
  private pl.mpak.sky.gui.swing.Action cmNewGroup;
  private pl.mpak.sky.gui.swing.Action cmNewReport;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JPanel jPanel10;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JToolBar.Separator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JSeparator jSeparator5;
  private javax.swing.JMenuItem menuCopy;
  private javax.swing.JMenuItem menuDelete;
  private javax.swing.JMenuItem menuEdit;
  private javax.swing.JMenuItem menuNewGroup;
  private javax.swing.JMenuItem menuNewReport;
  private javax.swing.JPopupMenu menuSelectedNode;
  private javax.swing.JToolBar toolBar;
  private javax.swing.JTree treeReports;
  // End of variables declaration//GEN-END:variables

}
