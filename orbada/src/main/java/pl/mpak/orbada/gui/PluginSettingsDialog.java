/*
 * PluginSettingsDialog.java
 *
 * Created on 6 grudzieñ 2007, 18:42
 */

package orbada.gui;

import java.util.StringTokenizer;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import orbada.Consts;
import orbada.core.Application;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.PerspectiveSettingsProvider;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.plugins.providers.abs.AbstractSettingsProvider;
import orbada.util.Utils;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class PluginSettingsDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private SettingsProviderTreeNode currentNode;
  private Database database;
  private ISettings settings;
  
  /** Creates new form PluginSettingsDialog */
  public PluginSettingsDialog(Database database) {
    super(SwingUtil.getRootFrame());
    this.database = database;
    initComponents();
    init();
  }
  
  public PluginSettingsDialog() {
    this(null);
  }
  
  public static void showDialog(Database database) {
    PluginSettingsDialog dialog = new PluginSettingsDialog(database);
    dialog.setVisible(true);
  }
  
  public static void showDialog() {
    PluginSettingsDialog dialog = new PluginSettingsDialog();
    dialog.setVisible(true);
  }
  
  private void init() {
    setTitle(database == null ? stringManager.getString("PluginSettingsDialog-plugin-settings") : stringManager.getString("PluginSettingsDialog-perspective-settings"));

    settings = Application.get().getSettings("orbada-settings-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", new Variant(getWidth())).getInteger(), settings.getValue("height", new Variant(getHeight())).getInteger());
    } catch (Exception ex) {
    }

    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    
    treeSettings.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    if (database == null) {
      SettingsProvider[] spa = Application.get().getServiceArray(SettingsProvider.class);
      if (spa != null) {
        for (SettingsProvider sp : spa) {
          addSettings(root, sp);
        }
      }
    }
    else {
      PerspectiveSettingsProvider[] pspa = Application.get().getServiceArray(PerspectiveSettingsProvider.class);
      if (pspa != null) {
        for (PerspectiveSettingsProvider sp : pspa) {
          if (sp.isForDatabase(database)) {
            addSettings(root, sp);
          }
        }
      }
    }
    treeSettings.setModel(new DefaultTreeModel(root));
    if (root.getChildCount() > 0) {
      treeSettings.setSelectionInterval(0, 0);
    }
    else {
      setCurrentNode(null);
    }
    treeSettings.expandRow(0);

    SwingUtil.addAction(this, cmHelp.getActionCommandKey(), cmHelp);
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel, buttonRollback, buttonApply});
    SwingUtil.centerWithinScreen(this);
  }
  
  private void addSettings(DefaultMutableTreeNode root, AbstractSettingsProvider sp) {
    DefaultMutableTreeNode node = root;
    
    StringTokenizer st = new StringTokenizer(sp.getSettingsPath(), "/\\");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      boolean found = false;
      for (int i=0; i<node.getChildCount(); i++) {
        SettingsProviderTreeNode n = (SettingsProviderTreeNode)node.getChildAt(i);
        if (token.equals(n.getUserObject().toString())) {
          node = n;
          found = true;
          break;
        }
      }
      if (!found) {
        DefaultMutableTreeNode newNode = new SettingsProviderTreeNode(null, database, token);
        node.add(newNode);
        node = newNode;
      }
    }
    ((SettingsProviderTreeNode)node).setService(sp);
  }
  
  private void setCurrentNode(SettingsProviderTreeNode node) {
    if (currentNode != null && currentNode.getComponent() != null) {
      panelSetting.remove(currentNode.getComponent());
    }
    currentNode = node;
    labelDescription.setText("<html><b>");
    if (currentNode != null && currentNode.getComponent() != null) {
      panelSetting.add(currentNode.getComponent());
      labelDescription.setText(labelDescription.getText() +currentNode.getDescription());
      iconSettings.setIcon(currentNode.getIcon());
      iconSettings.setVisible(currentNode.getIcon() != null);
    }
    else if (currentNode != null) {
      labelDescription.setText(labelDescription.getText() +currentNode.toString());
      iconSettings.setIcon(currentNode.getIcon());
      iconSettings.setVisible(currentNode.getIcon() != null);
    }
    else {
      labelDescription.setText(labelDescription.getText() +stringManager.getString("PluginSettingsDialog-no-settings"));
      iconSettings.setIcon(null);
      iconSettings.setVisible(false);
    }
    labelDescription.setText(labelDescription.getText() +"</b>");
    if (database != null) {
      labelDescription.setText(
        labelDescription.getText() +"<br>" +
        stringManager.getString("PluginSettingsDialog-for") +" <b>" +database.getDriverType() +"</b><br>" +
        database.getUrl());
    }
    cmApplySettings.setEnabled(currentNode != null && currentNode.getComponent() != null);
    cmRestoreSettings.setEnabled(currentNode != null && currentNode.getComponent() != null);
    panelSetting.revalidate();
    panelSetting.repaint();
  }
  
  private void applySettings(DefaultMutableTreeNode node) {
    if (node == null) {
      return;
    }
    if (node instanceof SettingsProviderTreeNode) {
      if (((SettingsProviderTreeNode)node).isShowed()) {
        ((SettingsProviderTreeNode)node).applySettings();
      }
    }
    for (int i=0; i<node.getChildCount(); i++) {
      applySettings((DefaultMutableTreeNode)node.getChildAt(i));
    }
  }
  
  private void cancelSettings(DefaultMutableTreeNode node) {
    if (node == null) {
      return;
    }
    if (node instanceof SettingsProviderTreeNode) {
      if (((SettingsProviderTreeNode)node).isShowed()) {
        ((SettingsProviderTreeNode)node).cancelSettings();
      }
    }
    for (int i=0; i<node.getChildCount(); i++) {
      cancelSettings((DefaultMutableTreeNode)node.getChildAt(i));
    }
  }
  
  private void close(DefaultMutableTreeNode node) {
    if (node == null) {
      return;
    }
    if (node instanceof SettingsProviderTreeNode) {
      if (((SettingsProviderTreeNode)node).isShowed()) {
        ((SettingsProviderTreeNode)node).close();
      }
    }
    for (int i=0; i<node.getChildCount(); i++) {
      close((DefaultMutableTreeNode)node.getChildAt(i));
    }
  }
  
  @Override
  public void dispose() {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeSettings.getModel().getRoot();
    close(root);
    settings.setValue("width", new Variant(getWidth()));
    settings.setValue("height", new Variant(getHeight()));
    settings.store();
    super.dispose();
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
    cmApplySettings = new pl.mpak.sky.gui.swing.Action();
    cmRestoreSettings = new pl.mpak.sky.gui.swing.Action();
    cmHelp = new pl.mpak.sky.gui.swing.Action();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    jSplitPane1 = new javax.swing.JSplitPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    treeSettings = new javax.swing.JTree();
    panelSetting = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    iconSettings = new pl.mpak.sky.gui.swing.comp.IconPanel();
    labelDescription = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    buttonRollback = new javax.swing.JButton();
    buttonApply = new javax.swing.JButton();

    cmOk.setActionCommandKey("cmOk");
    cmOk.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmOk.setText(stringManager.getString("cmOk-text")); // NOI18N
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setActionCommandKey("cmCancel");
    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    cmApplySettings.setActionCommandKey("cmApplySettings");
    cmApplySettings.setText(stringManager.getString("cmApply-text")); // NOI18N
    cmApplySettings.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmApplySettingsActionPerformed(evt);
      }
    });

    cmRestoreSettings.setActionCommandKey("cmRestoreSettings");
    cmRestoreSettings.setText(stringManager.getString("cmRestore-text")); // NOI18N
    cmRestoreSettings.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRestoreSettingsActionPerformed(evt);
      }
    });

    cmHelp.setActionCommandKey("cmHelp");
    cmHelp.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
    cmHelp.setText(stringManager.getString("cmHelp-text")); // NOI18N
    cmHelp.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmHelpActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setModal(true);

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    jSplitPane1.setBorder(null);
    jSplitPane1.setDividerLocation(200);
    jSplitPane1.setContinuousLayout(true);
    jSplitPane1.setOneTouchExpandable(true);

    jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 324));

    treeSettings.setRootVisible(false);
    treeSettings.setShowsRootHandles(true);
    treeSettings.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
      public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
        treeSettingsValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(treeSettings);

    jSplitPane1.setLeftComponent(jScrollPane1);

    panelSetting.setLayout(new java.awt.BorderLayout());

    jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));
    jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    iconSettings.setMinimumSize(new java.awt.Dimension(24, 24));
    iconSettings.setPreferredSize(new java.awt.Dimension(32, 32));

    javax.swing.GroupLayout iconSettingsLayout = new javax.swing.GroupLayout(iconSettings);
    iconSettings.setLayout(iconSettingsLayout);
    iconSettingsLayout.setHorizontalGroup(
      iconSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 32, Short.MAX_VALUE)
    );
    iconSettingsLayout.setVerticalGroup(
      iconSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 32, Short.MAX_VALUE)
    );

    jPanel1.add(iconSettings);

    labelDescription.setText("labelDescription");
    jPanel1.add(labelDescription);

    panelSetting.add(jPanel1, java.awt.BorderLayout.PAGE_START);

    jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));
    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

    buttonRollback.setAction(cmRestoreSettings);
    buttonRollback.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonRollback.setPreferredSize(new java.awt.Dimension(85, 25));
    jPanel2.add(buttonRollback);

    buttonApply.setAction(cmApplySettings);
    buttonApply.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonApply.setPreferredSize(new java.awt.Dimension(85, 25));
    jPanel2.add(buttonApply);

    panelSetting.add(jPanel2, java.awt.BorderLayout.PAGE_END);

    jSplitPane1.setRightComponent(panelSetting);

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
          .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeSettings.getModel().getRoot();
    applySettings(root);
    dispose();
  }//GEN-LAST:event_cmOkActionPerformed

  private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeSettings.getModel().getRoot();
    cancelSettings(root);
    dispose();
  }//GEN-LAST:event_cmCancelActionPerformed

  private void treeSettingsValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeSettingsValueChanged
    SettingsProviderTreeNode node = (SettingsProviderTreeNode)treeSettings.getLastSelectedPathComponent();
    setCurrentNode(node);
  }//GEN-LAST:event_treeSettingsValueChanged

  private void cmApplySettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmApplySettingsActionPerformed
    if (currentNode != null) {
      currentNode.applySettings();
    }
  }//GEN-LAST:event_cmApplySettingsActionPerformed

  private void cmRestoreSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRestoreSettingsActionPerformed
    if (currentNode != null) {
      currentNode.restoreSettings();
    }
  }//GEN-LAST:event_cmRestoreSettingsActionPerformed

  private void cmHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmHelpActionPerformed
    if (currentNode != null && currentNode.getComponent() != null) {
      Utils.gotoHelp(currentNode.getComponent());
    }
  }//GEN-LAST:event_cmHelpActionPerformed
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonApply;
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JButton buttonRollback;
  private pl.mpak.sky.gui.swing.Action cmApplySettings;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmHelp;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private pl.mpak.sky.gui.swing.Action cmRestoreSettings;
  private pl.mpak.sky.gui.swing.comp.IconPanel iconSettings;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JLabel labelDescription;
  private javax.swing.JPanel panelSetting;
  private javax.swing.JTree treeSettings;
  // End of variables declaration//GEN-END:variables
  
}
