/*
 * PluginManagerDialog.java
 *
 * Created on 5 styczeñ 2008, 17:31
 */

package pl.mpak.orbada.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.gui.comps.table.Table;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.gui.comps.table.Table;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.plugins.Plugin;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.TableColumn;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author  akaluza
 */
public class PluginManagerDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private ISettings settings;
  private InstalledTableModel installedModel;
  private Package[] packageArray;

  /** Creates new form PluginManagerDialog */
  public PluginManagerDialog() {
    super(SwingUtil.getRootFrame());
    initComponents();
    init();
  }
  
  public static void showDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        PluginManagerDialog dialog = new PluginManagerDialog();
        dialog.setVisible(true);
      }
    });
  }

  private void init() {
    settings = Application.get().getSettings("plugin-manager-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", (long)getWidth()).intValue(), settings.getValue("height", (long)getHeight()).intValue());
    } catch (Exception ex) {
    }
    splitInstalled.setDividerLocation(settings.getValue("split-installed-divider", (long)splitInstalled.getDividerLocation()).intValue());

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmClose");
    getRootPane().getActionMap().put("cmClose", cmClose);

    //textDescription.setStyledDocument(new javax.swing.text.html.HTMLDocument());
    tableInstalled.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updateInstalledInfo();
      }
    });
    tableInstalled.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableInstalled.setModel(installedModel = new InstalledTableModel());
    tableInstalled.addColumn(new TableColumn(0, 200, stringManager.getString("PluginManagerDialog-name")));
    tableInstalled.addColumn(new TableColumn(1, 100, stringManager.getString("PluginManagerDialog-categories")));
    tableInstalled.addColumn(new TableColumn(2, 25, stringManager.getString("PluginManagerDialog-active")));
    tableInstalled.addColumn(new TableColumn(3, 25, stringManager.getString("PluginManagerDialog-loaded")));
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        preparePluginList();
      }
    });
    packageArray = Package.getPackages();
    Arrays.sort(packageArray, new Comparator<Package>() {
      public int compare(Package o1, Package o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    tablePackages.setModel(new AbstractTableModel() {
      public int getRowCount() {
        return packageArray.length;
      }
      public int getColumnCount() {
        return 0;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
          case 0: return packageArray[rowIndex].getName();
          case 1: return packageArray[rowIndex].getSpecificationTitle();
          case 2: return packageArray[rowIndex].getSpecificationVendor();
          case 3: return packageArray[rowIndex].getSpecificationVersion();
          case 4: return packageArray[rowIndex].getImplementationTitle();
          case 5: return packageArray[rowIndex].getImplementationVendor();
          case 6: return packageArray[rowIndex].getImplementationVersion();
        }
        return null;
      }
    });
    tablePackages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tablePackages.addColumn(new TableColumn(0, 150, stringManager.getString("PluginManagerDialog-name")));
    tablePackages.addColumn(new TableColumn(1, 150, stringManager.getString("PluginManagerDialog-spec-title")));
    tablePackages.addColumn(new TableColumn(2, 150, stringManager.getString("PluginManagerDialog-spec-producer")));
    tablePackages.addColumn(new TableColumn(3, 70, stringManager.getString("PluginManagerDialog-spec-version")));
    tablePackages.addColumn(new TableColumn(4, 150, stringManager.getString("PluginManagerDialog-impl-title")));
    tablePackages.addColumn(new TableColumn(5, 150, stringManager.getString("PluginManagerDialog-impl-producer")));
    tablePackages.addColumn(new TableColumn(6, 70, stringManager.getString("PluginManagerDialog-impl-version")));
    
    SwingUtil.centerWithinScreen(this);
  }
  
  @Override
  public void dispose() {
    settings.setValue("split-installed-divider", (long)splitInstalled.getDividerLocation());
    settings.setValue("width", (long)getWidth());
    settings.setValue("height", (long)getHeight());
    settings.store();
    super.dispose();
  }
  
  private void preparePluginList() {
    Query query = InternalDatabase.get().createQuery();
    try {
      query.setSqlText("select * from plugins where plg_usr_id = :plg_usr_id order by plg_id");
      query.paramByName("plg_usr_id").setString(Application.get().getUserId());
      query.open();
      while (!query.eof()) {
        Plugin plugin = Application.get().getPluginManager().getPluginByUniqueID(query.fieldByName("plg_id").getString());
        if (plugin != null) {
          installedModel.pluginList.add(new InstalledPlugin(
            query.fieldByName("plg_id").getString(),
            query.fieldByName("plg_description").getString(),
            StringUtil.toBoolean(query.fieldByName("plg_enabled").getString()), 
            StringUtil.toBoolean(query.fieldByName("plg_loaded").getString()), 
            plugin));
        }
        else {
          installedModel.pluginList.add(new InstalledPlugin(
            query.fieldByName("plg_id").getString(),
            query.fieldByName("plg_description").getString(),
            StringUtil.toBoolean(query.fieldByName("plg_enabled").getString()), 
            StringUtil.toBoolean(query.fieldByName("plg_loaded").getString()), 
            plugin));
        }
        query.next();
      }
    }
    catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    finally {
      query.close();
    }
    tableInstalled.revalidate();
  }
  
  private void updateInstalledInfo() {
    if (tableInstalled.getSelectedRow() != -1) {
      InstalledPlugin ip = installedModel.pluginList.get(tableInstalled.getSelectedRow());
      StringBuffer sb = new StringBuffer();
      sb.append("<html>");
      sb.append("<style>");
      sb.append("  body { font-family: Tahoma, Arial, serif; font-size: 9px; }");
      sb.append("</style>");
      sb.append("<head></head>");
      sb.append("<body>");
      if (ip.plugin != null) {
        sb.append("<font size=\"+1\">").append(ip.plugin.getDescriptiveName()).append("</font><br>");
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-unique-id-dd")).append(" </b>").append(ip.plugin.getUniqueID()).append("<br><hr>");
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-categories-dd")).append(" </b>").append(ip.plugin.getCategory()).append("<br>");
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-version-dd")).append(" </b>").append(ip.plugin.getVersion()).append("<br>");
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-author-dd")).append(" </b>").append(ip.plugin.getAuthor()).append("<br>");
        String webPage = ip.plugin.getWebSite() == null ? Application.get().getProperty(Consts.orbadaWebPage, "") : ip.plugin.getWebSite();
        String updatePage = ip.plugin.getUpdateSite() == null ? Application.get().getProperty(Consts.orbadaUpdatePage, "") : ip.plugin.getUpdateSite();
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-property-rights-dd")).append(" </b>").append(ip.plugin.getCopyrights()).append("<br>");
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-root-page-dd")).append(" </b><a href=\"").append(webPage).append("\">").append(webPage).append("</a><br>");
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-update-page-dd")).append(" </b><a href=\"").append(updatePage).append("\">").append(updatePage).append("</a><br>");
        sb.append("<br><b>").append(stringManager.getString("PluginManagerDialog-description-dd")).append("</b><br>").append(ip.plugin.getDescription());
        String licence = ip.plugin.getLicence();
        if (StringUtil.isEmpty(licence)) {
          File file = new File(Resolvers.expand("$(user.dir)/doc/licences/orbada-licence.txt"));
          if (file.exists()) {
            FileInputStream stream;
            try {
              stream = new FileInputStream(file);
              licence = pl.mpak.util.StreamUtil.stream2String(stream);
              textLicense.setText(licence);
            } catch (Exception ex) {
              textLicense.setText(ex.getMessage());
              ExceptionUtil.processException(ex);
            }
          }
          else {
            textLicense.setText(stringManager.getString("PluginManagerDialog-no-licence"));
          }
        }
        else {
          textLicense.setText(licence);
        }
      }
      else {
        sb.append("<font size=\"+1\">").append(ip.name).append("</font><br>");
        sb.append("<b>").append(stringManager.getString("PluginManagerDialog-unique-id-dd")).append(" </b>").append(ip.uniqueId).append("<br><hr>");
        textLicense.setText("");
      }
      sb.append("</body>");
      sb.append("</html>");
      textDescription.setText(sb.toString());
      textLicense.setCaretPosition(0);
      textDescription.setCaretPosition(0);
    }
  }
  
  class InstalledPlugin {
    String uniqueId;
    String name;
    boolean loaded;
    boolean enabled;
    Plugin plugin;
    public InstalledPlugin(String uniqueId, String name, boolean enabled, boolean loaded, Plugin plugin) {
      this.uniqueId = uniqueId;
      this.name = name;
      this.loaded = loaded;
      this.enabled = enabled;
      this.plugin = plugin;
    }
  }

  class InstalledTableModel extends AbstractTableModel {
    
    ArrayList<InstalledPlugin> pluginList;
    Icon greenDot;
    Icon redDot;
    
    public InstalledTableModel() {
      pluginList = new ArrayList<InstalledPlugin>();
      greenDot = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/green_sdot.png");
      redDot = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/red_sdot.png");
    }

    public int getRowCount() {
      return pluginList.size();
    }

    public int getColumnCount() {
      return 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
        case 0: return String.class;
        case 1: return String.class;
        case 2: return Icon.class;
        case 3: return Icon.class;
      }
      return null;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
      InstalledPlugin ip = pluginList.get(rowIndex);
      switch (columnIndex) {
        case 0:
          return ip.plugin != null ? ip.plugin.getDescriptiveName() : ip.name;
        case 1:
          return ip.plugin != null ? ip.plugin.getCategory() : "???";
        case 2:
          return (ip.enabled ? greenDot : redDot);
        case 3:
          return (ip.loaded ? greenDot : redDot);
      }
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

    cmClose = new pl.mpak.sky.gui.swing.Action();
    cmSwitchActive = new pl.mpak.sky.gui.swing.Action();
    jTabbedPane1 = new javax.swing.JTabbedPane();
    splitInstalled = new javax.swing.JSplitPane();
    jPanel3 = new javax.swing.JPanel();
    jScrollPane3 = new javax.swing.JScrollPane();
    tableInstalled = new Table();
    buttonDeactivate = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel();
    jTabbedPane2 = new javax.swing.JTabbedPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    textDescription = new javax.swing.JEditorPane();
    jScrollPane4 = new javax.swing.JScrollPane();
    textLicense = new javax.swing.JTextPane();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tablePackages = new Table();
    jButton1 = new javax.swing.JButton();

    cmClose.setActionCommandKey("cmClose");
    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(stringManager.getString("cmClose-text")); // NOI18N
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    cmSwitchActive.setActionCommandKey("cmSwitchActive");
    cmSwitchActive.setText(stringManager.getString("PluginManagerDialog-cmSwitchActive-text")); // NOI18N
    cmSwitchActive.setTooltip(stringManager.getString("PluginManagerDialog-cmSwitchActive-hint")); // NOI18N
    cmSwitchActive.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSwitchActiveActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("PluginManagerDialog-title")); // NOI18N
    setModal(true);

    jTabbedPane1.setFocusable(false);

    splitInstalled.setBorder(null);
    splitInstalled.setDividerLocation(350);
    splitInstalled.setContinuousLayout(true);
    splitInstalled.setOneTouchExpandable(true);

    tableInstalled.setShowHorizontalLines(false);
    jScrollPane3.setViewportView(tableInstalled);

    buttonDeactivate.setAction(cmSwitchActive);
    buttonDeactivate.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDeactivate.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
          .addComponent(buttonDeactivate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(buttonDeactivate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    splitInstalled.setLeftComponent(jPanel3);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jTabbedPane2.setFocusable(false);

    textDescription.setContentType("text/html");
    textDescription.setEditable(false);
    textDescription.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
      public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
        textDescriptionHyperlinkUpdate(evt);
      }
    });
    jScrollPane1.setViewportView(textDescription);

    jTabbedPane2.addTab(stringManager.getString("PluginManagerDialog-plugin-description"), jScrollPane1); // NOI18N

    textLicense.setEditable(false);
    textLicense.setFont(new java.awt.Font("Courier New", 0, 12));
    jScrollPane4.setViewportView(textLicense);

    jTabbedPane2.addTab(stringManager.getString("PluginManagerDialog-licence"), jScrollPane4); // NOI18N

    jPanel1.add(jTabbedPane2, java.awt.BorderLayout.CENTER);

    splitInstalled.setRightComponent(jPanel1);

    jTabbedPane1.addTab(stringManager.getString("PluginManagerDialog-installed"), splitInstalled); // NOI18N

    tablePackages.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jScrollPane2.setViewportView(tablePackages);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
        .addContainerGap())
    );

    jTabbedPane1.addTab(stringManager.getString("PluginManagerDialog-loaded-packages"), jPanel2); // NOI18N

    jButton1.setAction(cmClose);
    jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
    jButton1.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
    dispose();
}//GEN-LAST:event_cmCloseActionPerformed

  private void cmSwitchActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSwitchActiveActionPerformed
    if (tableInstalled.getSelectedRow() != -1) {
      InstalledPlugin ip = installedModel.pluginList.get(tableInstalled.getSelectedRow());
      Application.get().getPluginManager().setEnabled(ip.uniqueId, !ip.enabled);
      ip.enabled = !ip.enabled;
      tableInstalled.repaint();
    }
}//GEN-LAST:event_cmSwitchActiveActionPerformed

private void textDescriptionHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_textDescriptionHyperlinkUpdate
  try {
    if (evt.getEventType() == EventType.ACTIVATED) {
      Desktop.getDesktop().browse(evt.getURL().toURI());
    }
  } catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_textDescriptionHyperlinkUpdate
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonDeactivate;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmSwitchActive;
  private javax.swing.JButton jButton1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JScrollPane jScrollPane4;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JTabbedPane jTabbedPane2;
  private javax.swing.JSplitPane splitInstalled;
  private Table tableInstalled;
  private Table tablePackages;
  private javax.swing.JEditorPane textDescription;
  private javax.swing.JTextPane textLicense;
  // End of variables declaration//GEN-END:variables
  
}
