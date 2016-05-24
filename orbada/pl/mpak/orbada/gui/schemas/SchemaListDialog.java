/*
 * SchemaListDialog.java
 *
 * Created on 13 paŸdziernik 2007, 19:20
 */

package pl.mpak.orbada.gui.schemas;

import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.ConnectionFactory;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Schema;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.ImageManager;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;
import pl.mpak.usedb.core.DefaultQueryListener;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryComboBoxModel;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.usedb.gui.swing.QueryTableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskPool;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class SchemaListDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private ISettings settings;
  private Query queryDrivers;
  private Query queryHosts;
  private Query queryUsers;
  
  /** Creates new form SchemaListDialog */
  public SchemaListDialog() {
    super(SwingUtil.getRootFrame());
    initComponents();
    init();
  }
  
  public static void showDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        SchemaListDialog dialog = new SchemaListDialog();
        dialog.setVisible(true);
      }
    });
  }
  
  private void init() {
    try {
      queryDrivers = InternalDatabase.get().createQuery();
      queryDrivers.setCacheData(true);
      queryDrivers.open("select cast(null as varchar(100)) sname from dual union select distinct drv_name sname from schemas, drivers where sch_drv_id = drv_id order by 1");

      queryHosts = InternalDatabase.get().createQuery();
      queryHosts.setCacheData(true);
      queryHosts.open("select cast(null as varchar(100)) sname from dual union select distinct upper(sch_host) sname from schemas where sch_host is not null order by 1");
      
      queryUsers = InternalDatabase.get().createQuery();
      queryUsers.setCacheData(true);
      queryUsers.open("select cast(null as varchar(100)) sname from dual union select distinct upper(sch_user) sname from schemas order by 1");

      comboDriver.setModel(new QueryComboBoxModel(queryDrivers, "sname"));
      comboHost.setModel(new QueryComboBoxModel(queryHosts, "sname"));
      comboUser.setModel(new QueryComboBoxModel(queryUsers, "sname"));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    
    settings = Application.get().getSettings("orbada-schema-list-dialog");
    try {
      setBounds(0, 0, settings.getValue("width", new Variant(getWidth())).getInteger(), settings.getValue("height", new Variant(getHeight())).getInteger());
    } catch (Exception ex) {
    }
    comboDriver.setText(settings.getValue("driver-selected", (String)null));
    comboHost.setText(settings.getValue("host-selected", (String)null));
    comboUser.setText(settings.getValue("user-selected", (String)null));

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmClose");
    getRootPane().getActionMap().put("cmClose", cmClose);
    getRootPane().setDefaultButton(buttonConnect);
    
    TableRowChangeKeyListener trckl = new TableRowChangeKeyListener(tableSchemas);
    textSchHost.addKeyListener(trckl);
    textSchUser.addKeyListener(trckl);
    textSchPassword.addKeyListener(trckl);
    textSchUrl.addKeyListener(trckl);
    
    tableSchemas.getQuery().setDatabase(InternalDatabase.get());
    tableSchemas.getQuery().addQueryListener(new DefaultQueryListener() {
      public void afterOpen(EventObject e) {
        cmNew.setEnabled(true);
        cmImport.setEnabled(true);
      }
      public void afterClose(EventObject e) {
        cmNew.setEnabled(false);
        cmClone.setEnabled(false);
        cmEdit.setEnabled(false);
        cmDelete.setEnabled(false);
        cmExport.setEnabled(false);
        cmImport.setEnabled(false);
      }
    });
    
    tableSchemas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        cmConnect.setEnabled(e.getFirstIndex() >= 0);
        cmClone.setEnabled(e.getFirstIndex() >= 0);
        cmEdit.setEnabled(e.getFirstIndex() >= 0);
        cmDelete.setEnabled(e.getFirstIndex() >= 0);
        cmExport.setEnabled(e.getFirstIndex() >= 0);
        int rowIndex = tableSchemas.getSelectedRow();
        if (rowIndex >= 0 && tableSchemas.getQuery().isActive()) {
          try {
            tableSchemas.getQuery().getRecord(rowIndex);
            textSchHost.setText(tableSchemas.getQuery().fieldByName("sch_host").getString());
            textSchUser.setText(tableSchemas.getQuery().fieldByName("sch_user").getString());
            textSchPassword.setText(tableSchemas.getQuery().fieldByName("sch_password").getString());
            textSchUrl.setText(tableSchemas.getQuery().fieldByName("sch_url").getString());
            if (tableSchemas.getQuery().fieldByName("sch_usr_id").isNull()) {
              cmEdit.setEnabled(Application.get().isUserAdmin());
              cmDelete.setEnabled(Application.get().isUserAdmin());
            }
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        } else {
          textSchHost.setText("");
          textSchUser.setText("");
          textSchPassword.setText("");
          textSchUrl.setText("");
        }
      }
    });

    try {
      tableSchemas.addColumn(new QueryTableColumn("sch_public_name", stringManager.getString("SchemaListDialog-schema-name"), 130, new QueryTableCellRenderer(java.awt.Font.BOLD)));
      tableSchemas.addColumn(new QueryTableColumn("sch_user", stringManager.getString("SchemaListDialog-user"), 70));
      tableSchemas.addColumn(new QueryTableColumn("sch_selected", stringManager.getString("SchemaListDialog-selected"), 120, new QueryTableCellRenderer(SwingUtil.Color.GREEN)));
      tableSchemas.addColumn(new QueryTableColumn("drv_name", stringManager.getString("SchemaListDialog-driver"), 120, new QueryTableCellRenderer(SwingUtil.Color.NAVY)));
      tableSchemas.addColumn(new QueryTableColumn("sch_db_version", stringManager.getString("SchemaListDialog-version"), 70));
      tableSchemas.addColumn(new QueryTableColumn("sch_url", stringManager.getString("SchemaListDialog-connection-url"), 250));
      tableSchemas.getQuery().setSqlText(
        "select sch_id, sch_name, sch_public_name, drv_name, drv_type_name, drv_class_name, sch_host, sch_user, sch_password, sch_url, sch_db_version, sch_selected, sch_usr_id\n" +
        "  from schemas left outer join drivers on (sch_drv_id = drv_id)\n" +
        " where (sch_usr_id = :sch_usr_id or sch_usr_id is null)\n" +
        "   and (upper(sch_host) = :SCH_HOST or :SCH_HOST is null)\n" +
        "   and (drv_name = :DRV_NAME or :DRV_NAME is null)\n" +
        "   and (upper(sch_user) = :SCH_USER or :SCH_USER is null)\n" +
        " order by sch_selected desc, sch_name");
      refreshQuery(null);
    } catch (Exception exception) {
      ExceptionUtil.processException(exception);
    }

    int height = SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonFunctions, buttonEdit, buttonDelete, buttonDrivers});
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonConnect, buttonClose}, height);
    textSchPassword.requestFocusInWindow();
    SwingUtil.centerWithinScreen(this);
  }
  
  @Override
  public void dispose() {
    settings.setValue("driver-selected", comboDriver.getText());
    settings.setValue("host-selected", comboHost.getText());
    settings.setValue("user-selected", comboUser.getText());
    settings.setValue("width", new Variant(getWidth()));
    settings.setValue("height", new Variant(getHeight()));
    settings.store();
    if (queryDrivers != null) {
      queryDrivers.close();
    }
    if (queryHosts != null) {
      queryHosts.close();
    }
    if (queryUsers != null) {
      queryUsers.close();
    }
    tableSchemas.getQuery().close();
    super.dispose();
  }
  
  public void refreshQuery(final String sch_id) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int lastRow = tableSchemas.getSelectedRow();
        try {
          String id = sch_id;
          if (id == null && tableSchemas.getQuery().isActive() && lastRow >= 0) {
            tableSchemas.getQuery().getRecord(lastRow);
            id = tableSchemas.getQuery().fieldByName("sch_id").getString();
          }
          tableSchemas.getQuery().close();
          tableSchemas.getQuery().paramByName("sch_usr_id").setString(Application.get().getUserId());
          tableSchemas.getQuery().paramByName("SCH_HOST").setString(comboHost.getText());
          tableSchemas.getQuery().paramByName("DRV_NAME").setString(comboDriver.getText());
          tableSchemas.getQuery().paramByName("SCH_USER").setString(comboUser.getText());
          tableSchemas.getQuery().open();
          if (id != null && tableSchemas.getQuery().locate("SCH_ID", new Variant(id))) {
            tableSchemas.changeSelection(tableSchemas.getQuery().getCurrentRecord().getIndex(), 0);
          }
          else if (tableSchemas.getRowCount() > lastRow && lastRow >= 0) {
            tableSchemas.setRowSelectionInterval(lastRow, lastRow);
          } else if (tableSchemas.getRowCount() > 0) {
            tableSchemas.setRowSelectionInterval(0, 0);
          }
        } catch (Exception ex) {
          ExceptionUtil.processException(ex);
        }
      }
    });
  }

  private void focusPassword() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textSchPassword.requestFocusInWindow();
      }
    });
  }

  private void refreshFilters() {
    try {
      final String driver = comboDriver.getText();
      final String host = comboHost.getText();
      final String user = comboUser.getText();
      queryDrivers.refresh();
      queryHosts.refresh();
      queryUsers.refresh();
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          comboDriver.setModel(new QueryComboBoxModel(queryDrivers, "sname"));
          comboHost.setModel(new QueryComboBoxModel(queryHosts, "sname"));
          comboUser.setModel(new QueryComboBoxModel(queryUsers, "sname"));
          comboDriver.setSelectedItem(driver);
          comboHost.setSelectedItem(host);
          comboUser.setSelectedItem(user);
        }
      });
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

    cmClose = new pl.mpak.sky.gui.swing.Action();
    cmNew = new pl.mpak.sky.gui.swing.Action();
    cmEdit = new pl.mpak.sky.gui.swing.Action();
    cmDelete = new pl.mpak.sky.gui.swing.Action();
    cmConnect = new pl.mpak.sky.gui.swing.Action();
    cmDrivers = new pl.mpak.sky.gui.swing.Action();
    cmClone = new pl.mpak.sky.gui.swing.Action();
    cmFunctions = new pl.mpak.sky.gui.swing.Action();
    popupFunctions = new javax.swing.JPopupMenu();
    menuClone = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JSeparator();
    menuExport = new javax.swing.JMenuItem();
    menuImport = new javax.swing.JMenuItem();
    cmExport = new pl.mpak.sky.gui.swing.Action();
    cmImport = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    textSchHost = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel2 = new javax.swing.JLabel();
    textSchUser = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    textSchPassword = new javax.swing.JPasswordField();
    jLabel4 = new javax.swing.JLabel();
    textSchUrl = new pl.mpak.sky.gui.swing.comp.TextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableSchemas = new pl.mpak.orbada.gui.comps.table.ViewTable();
    jLabel5 = new javax.swing.JLabel();
    comboDriver = new pl.mpak.sky.gui.swing.comp.ComboBox();
    comboHost = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel6 = new javax.swing.JLabel();
    comboUser = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel7 = new javax.swing.JLabel();
    buttonNew = new javax.swing.JButton();
    buttonFunctions = new javax.swing.JButton();
    buttonEdit = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    buttonDrivers = new javax.swing.JButton();
    buttonConnect = new javax.swing.JButton();
    buttonClose = new javax.swing.JButton();

    cmClose.setActionCommandKey("cmClose");
    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(stringManager.getString("cmClose-text")); // NOI18N
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    cmNew.setActionCommandKey("cmNew");
    cmNew.setEnabled(false);
    cmNew.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new16.gif")); // NOI18N
    cmNew.setText(stringManager.getString("cmNew-text")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setActionCommandKey("cmEdit");
    cmEdit.setEnabled(false);
    cmEdit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/edit16.gif")); // NOI18N
    cmEdit.setText(stringManager.getString("cmEdit-text")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setActionCommandKey("cmDelete");
    cmDelete.setEnabled(false);
    cmDelete.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
    cmDelete.setText(stringManager.getString("cmDelete-text")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    cmConnect.setActionCommandKey("cmConnect");
    cmConnect.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmConnect.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/connect16.gif")); // NOI18N
    cmConnect.setText(stringManager.getString("cmConnect-text")); // NOI18N
    cmConnect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmConnectActionPerformed(evt);
      }
    });

    cmDrivers.setActionCommandKey("cmDrivers");
    cmDrivers.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/driverproperties.gif")); // NOI18N
    cmDrivers.setText(stringManager.getString("SchemaListDialog-cmDriver-text")); // NOI18N
    cmDrivers.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDriversActionPerformed(evt);
      }
    });

    cmClone.setActionCommandKey("cmClone");
    cmClone.setEnabled(false);
    cmClone.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/copy.gif")); // NOI18N
    cmClone.setText(stringManager.getString("SchemaListDialog-cmClone-text")); // NOI18N
    cmClone.setTooltip(stringManager.getString("SchemaListDialog-cmClone-hint")); // NOI18N
    cmClone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloneActionPerformed(evt);
      }
    });

    cmFunctions.setActionCommandKey("cmFunctions");
    cmFunctions.setText(stringManager.getString("SchemaListDialog-cmFunctions-text")); // NOI18N
    cmFunctions.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFunctionsActionPerformed(evt);
      }
    });

    menuClone.setAction(cmClone);
    popupFunctions.add(menuClone);
    popupFunctions.add(jSeparator1);

    menuExport.setAction(cmExport);
    popupFunctions.add(menuExport);

    menuImport.setAction(cmImport);
    popupFunctions.add(menuImport);

    cmExport.setActionCommandKey("cmExport");
    cmExport.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/export.gif")); // NOI18N
    cmExport.setText(stringManager.getString("SchemaListDialog-cmExport-text")); // NOI18N
    cmExport.setTooltip(stringManager.getString("SchemaListDialog-cmExport-hint")); // NOI18N
    cmExport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmExportActionPerformed(evt);
      }
    });

    cmImport.setActionCommandKey("cmImport");
    cmImport.setEnabled(false);
    cmImport.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/import.gif")); // NOI18N
    cmImport.setText(stringManager.getString("SchemaListDialog-cmImport-text")); // NOI18N
    cmImport.setTooltip(stringManager.getString("SchemaListDialog-cmImport-hint")); // NOI18N
    cmImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmImportActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("SchemaListDialog-title")); // NOI18N
    setModal(true);

    jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));
    jPanel1.setMaximumSize(new java.awt.Dimension(200, 32767));

    jLabel1.setLabelFor(textSchHost);
    jLabel1.setText(stringManager.getString("SchemaListDialog-host-dd")); // NOI18N

    textSchHost.setEditable(false);
    textSchHost.setFocusable(false);

    jLabel2.setLabelFor(textSchUser);
    jLabel2.setText(stringManager.getString("SchemaListDialog-user-dd")); // NOI18N

    jLabel3.setLabelFor(textSchPassword);
    jLabel3.setText(stringManager.getString("SchemaListDialog-password-dd")); // NOI18N

    jLabel4.setLabelFor(textSchUrl);
    jLabel4.setText(stringManager.getString("SchemaListDialog-url-dd")); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jLabel2)
        .addContainerGap())
      .addComponent(textSchUser, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jLabel3)
        .addContainerGap())
      .addComponent(textSchPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jLabel4)
        .addContainerGap())
      .addComponent(textSchUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jLabel1)
        .addContainerGap())
      .addComponent(textSchHost, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(textSchHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(textSchUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(textSchPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel4)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(textSchUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(180, Short.MAX_VALUE))
    );

    tableSchemas.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        tableSchemasMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(tableSchemas);

    jLabel5.setText(stringManager.getString("SchemaListDialog-driver-dd")); // NOI18N

    comboDriver.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboDriverItemStateChanged(evt);
      }
    });

    comboHost.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboHostItemStateChanged(evt);
      }
    });

    jLabel6.setText(stringManager.getString("SchemaListDialog-host-dd")); // NOI18N

    comboUser.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboUserItemStateChanged(evt);
      }
    });

    jLabel7.setText(stringManager.getString("SchemaListDialog-user-dd")); // NOI18N

    buttonNew.setAction(cmNew);
    buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNew.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonFunctions.setAction(cmFunctions);
    buttonFunctions.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonFunctions.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonEdit.setAction(cmEdit);
    buttonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEdit.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDelete.setAction(cmDelete);
    buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDelete.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDrivers.setAction(cmDrivers);
    buttonDrivers.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDrivers.setPreferredSize(new java.awt.Dimension(95, 25));

    buttonConnect.setAction(cmConnect);
    buttonConnect.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonConnect.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonClose.setAction(cmClose);
    buttonClose.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonClose.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel5)
              .addComponent(comboDriver, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel6)
              .addComponent(comboHost, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(comboUser, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
              .addComponent(jLabel7)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
            .addComponent(buttonFunctions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDrivers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel6)
                  .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                  .addComponent(comboHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(comboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
              .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDrivers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonFunctions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
private void cmConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmConnectActionPerformed
  try {
    if (tableSchemas.getSelectedRow() >= 0) {
      tableSchemas.getQuery().getRecord(tableSchemas.getSelectedRow());
      TaskPool.getTaskPool().addTask(new Task(stringManager.getString("SchemaListDialog-connecting")) {
        String url = textSchUrl.getText();
        String user = textSchUser.getText();
        String password = new String(textSchPassword.getPassword());
        String sch_id = tableSchemas.getQuery().fieldByName("sch_id").getString();
        public void run() {
          final PleaseWait wait = new PleaseWait(ImageManager.getImage("/res/orbada48.png"), getDescription());
          Application.get().startPleaseWait(wait);
          try {
            ConnectionFactory cf = ConnectionFactory.createInstance(url, user, password, sch_id);
            cf.createDatabase();
          } catch (final Exception ex) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                Application.get().stopPleaseWait(wait);
                MessageBox.show(SchemaListDialog.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
                ExceptionUtil.processException(ex);
              }
            });
          }
          finally {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                Application.get().stopPleaseWait(wait);
              }
            });
          }
        }
      });
      if ((evt.getModifiers() & InputEvent.CTRL_MASK) != InputEvent.CTRL_MASK) {
        dispose();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmConnectActionPerformed

private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
  if (tableSchemas.getQuery().isActive() && tableSchemas.getSelectedRow() >= 0) {
    try {
      tableSchemas.getQuery().getRecord(tableSchemas.getSelectedRow());
      String sch_id = tableSchemas.getQuery().fieldByName("sch_id").getString();
      String sch_name = tableSchemas.getQuery().fieldByName("SCH_NAME").getString();
      if (MessageBox.show(this,
            stringManager.getString("deleting"),
            String.format(
              stringManager.getString("SchemaListDialog-delete-schema-q"),
              new Object[] {sch_id, sch_name}),
            ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        Schema schema = new Schema(InternalDatabase.get());
        schema.getPrimaryKeyField().setValue(new Variant(sch_id));
        schema.applyDelete();
        Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageSchemaDeleted, sch_id));
        refreshFilters();
        refreshQuery(null);
        focusPassword();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDeleteActionPerformed

private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
  try {
    if (tableSchemas.getQuery().isActive() && tableSchemas.getSelectedRow() >= 0) {
      tableSchemas.getQuery().getRecord(tableSchemas.getSelectedRow());
      String sch_id = tableSchemas.getQuery().fieldByName("sch_id").getString();
      if (SchemaEditDialog.showDialog(sch_id) != null) {
        Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageSchemaUpdated, sch_id));
        refreshFilters();
        refreshQuery(sch_id);
        focusPassword();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmEditActionPerformed

private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
  try {
    if (tableSchemas.getQuery().isActive()) {
      String sch_id = SchemaEditDialog.showDialog((String)null);
      if (sch_id != null) {
        Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageSchemaInserted, sch_id));
        refreshFilters();
        refreshQuery(sch_id);
        focusPassword();
      }
    }
  } catch (Throwable ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmNewActionPerformed

private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
  dispose();
}//GEN-LAST:event_cmCloseActionPerformed

private void cmDriversActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDriversActionPerformed
  DriverListDialog.showDialog();
}//GEN-LAST:event_cmDriversActionPerformed

private void tableSchemasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSchemasMouseClicked
  if (evt.getClickCount() == 2) {
    cmConnect.performe();
  }
}//GEN-LAST:event_tableSchemasMouseClicked

private void cmCloneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloneActionPerformed
  try {
    if (tableSchemas.getQuery().isActive() && tableSchemas.getSelectedRow() >= 0) {
      tableSchemas.getQuery().getRecord(tableSchemas.getSelectedRow());
      Schema schema = new Schema(InternalDatabase.get(), tableSchemas.getQuery().fieldByName("sch_id").getString());
      String sch_id = SchemaEditDialog.showDialog(schema);
      if (sch_id != null) {
        Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageSchemaInserted, sch_id));
        refreshFilters();
        refreshQuery(sch_id);
        focusPassword();
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmCloneActionPerformed

private void comboDriverItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboDriverItemStateChanged
  if (evt.getStateChange() == ItemEvent.SELECTED) {
    refreshQuery(null);
  }
}//GEN-LAST:event_comboDriverItemStateChanged

private void comboHostItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboHostItemStateChanged
  if (evt.getStateChange() == ItemEvent.SELECTED) {
    refreshQuery(null);
  }
}//GEN-LAST:event_comboHostItemStateChanged

private void comboUserItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboUserItemStateChanged
  if (evt.getStateChange() == ItemEvent.SELECTED) {
    refreshQuery(null);
  }
}//GEN-LAST:event_comboUserItemStateChanged

private void cmFunctionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFunctionsActionPerformed
  popupFunctions.show(buttonFunctions, 0, buttonFunctions.getHeight());
}//GEN-LAST:event_cmFunctionsActionPerformed

private void cmExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExportActionPerformed
  try {
    if (tableSchemas.getQuery().isActive() && tableSchemas.getSelectedRow() >= 0) {
      File file = FileUtil.selectFileToSave(this, stringManager.getString("SchemaListDialog-save-schema"), new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("SchemaListDialog-schema-file"), new String[] {".xml.oconnschema"})});
      if (file != null) {
        tableSchemas.getQuery().getRecord(tableSchemas.getSelectedRow());
        Schema schema = new Schema(InternalDatabase.get(), tableSchemas.getQuery().fieldByName("sch_id").getString());
        schema.storeToXML(new FileOutputStream(file), null, "utf-8");
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmExportActionPerformed

private void cmImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmImportActionPerformed
  try {
    if (tableSchemas.getQuery().isActive()) {
      File file = FileUtil.selectFileToOpen(this, stringManager.getString("SchemaListDialog-open-schema"), (File)null, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("SchemaListDialog-schema-file"), new String[] {".xml.oconnschema"})});
      if (file != null) {
        Schema schema = new Schema(InternalDatabase.get());
        schema.loadFromXML(new FileInputStream(file));
        String sch_id = SchemaEditDialog.showDialog(schema);
        if (sch_id != null) {
          Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageSchemaInserted, sch_id));
          refreshFilters();
          refreshQuery(sch_id);
          focusPassword();
        }
      }
    }
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmImportActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonClose;
  private javax.swing.JButton buttonConnect;
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonDrivers;
  private javax.swing.JButton buttonEdit;
  private javax.swing.JButton buttonFunctions;
  private javax.swing.JButton buttonNew;
  private pl.mpak.sky.gui.swing.Action cmClone;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmConnect;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmDrivers;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmExport;
  private pl.mpak.sky.gui.swing.Action cmFunctions;
  private pl.mpak.sky.gui.swing.Action cmImport;
  private pl.mpak.sky.gui.swing.Action cmNew;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboDriver;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboHost;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboUser;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JMenuItem menuClone;
  private javax.swing.JMenuItem menuExport;
  private javax.swing.JMenuItem menuImport;
  private javax.swing.JPopupMenu popupFunctions;
  private pl.mpak.orbada.gui.comps.table.ViewTable tableSchemas;
  private pl.mpak.sky.gui.swing.comp.TextField textSchHost;
  private javax.swing.JPasswordField textSchPassword;
  private pl.mpak.sky.gui.swing.comp.TextField textSchUrl;
  private pl.mpak.sky.gui.swing.comp.TextField textSchUser;
  // End of variables declaration//GEN-END:variables
  
}
