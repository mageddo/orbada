package pl.mpak.orbada.gui.schemas;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverPropertyInfo;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import pl.mpak.orbada.db.DriverClassLoaderManager;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.ConnectionFactory;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.Driver;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Schema;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.orbada.plugins.providers.SchemaSettingsProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.FieldLinkType;
import pl.mpak.usedb.gui.RecordLink;
import pl.mpak.usedb.gui.linkreq.FieldRequeiredNotNull;
import pl.mpak.usedb.gui.swing.QueryComboBoxModel;
import pl.mpak.usedb.gui.swing.QueryListCellRenderer;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.FileUtil;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskPool;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;
import pl.mpak.waitdlg.WaitDialog;

/**
 *
 * @author  akaluza
 */
public class SchemaEditDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private String sch_id;
  private int modalResult = ModalResult.NONE;
  private Schema schema;
  private RecordLink schemaLink;
  private Query queryDrivers;
  private ArrayList<SchemaSetting> settinsList = new ArrayList<SchemaSetting>();
  
  public SchemaEditDialog(String sch_id) throws IntrospectionException, UseDBException {
    super(SwingUtil.getRootFrame());
    this.sch_id = sch_id;
    initComponents();
    init();
  }
  
  public SchemaEditDialog(Schema schema) throws IntrospectionException, UseDBException {
    super(SwingUtil.getRootFrame());
    this.schema = schema;
    initComponents();
    init();
  }

  public static String showDialog(String sch_id) throws IntrospectionException, UseDBException {
    SchemaEditDialog dialog = new SchemaEditDialog(sch_id);
    dialog.setVisible(true);
    if (dialog.getModalResult() == ModalResult.OK) {
      return dialog.sch_id;
    }
    return null;
  }
  
  public static String showDialog(Schema schema) throws IntrospectionException, UseDBException {
    SchemaEditDialog dialog = new SchemaEditDialog(schema);
    dialog.setVisible(true);
    if (dialog.getModalResult() == ModalResult.OK) {
      return dialog.sch_id;
    }
    return null;
  }

  private void init() throws IntrospectionException, UseDBException {
    queryDrivers = InternalDatabase.get().createQuery();
    try {
      queryDrivers.setCacheData(true);
      queryDrivers.setSqlText("select drv_id, drv_name, drv_type_name, drv_url_template, drv_class_name from drivers where drv_usr_id = :drv_usr_id or drv_usr_id is null order by drv_name, drv_id");
      queryDrivers.paramByName("drv_usr_id").setString(Application.get().getUserId());
      queryDrivers.open();
      comboSchDrvId.setModel(new QueryComboBoxModel(queryDrivers, "drv_id"));
      comboSchDrvId.setRenderer(new QueryListCellRenderer(queryDrivers, "drv_id", "drv_name"));
      comboSchDrvId.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            try {
              if (queryDrivers.locate("drv_id", new Variant(e.getItem()))) {
                labelDrvUrlTemplate.setText(queryDrivers.fieldByName("drv_url_template").getString());
                setSettingsVisible(queryDrivers.fieldByName("drv_type_name").getString());
              }
              else {
                labelDrvUrlTemplate.setText(" ");
              }
            } catch (Exception ex) {
              ExceptionUtil.processException(ex);
            }
          }  
        }
      });
    } catch(Exception e) {
      ExceptionUtil.processException(e);
    }
    
    schemaLink = new RecordLink();
    schemaLink.add("SCH_NAME", textSchName, new FieldRequeiredNotNull(stringManager.getString("SchemaEditDialog-schema-name")));
    schemaLink.add("SCH_DRV_ID", comboSchDrvId, "selectedItem");
    schemaLink.add("SCH_HOST", textSchHost);
    schemaLink.add("SCH_USER", textSchUser);
    schemaLink.add("SCH_PASSWORD", textSchPassword);
    schemaLink.add("SCH_DATABASE", textSchDatabase);
    schemaLink.add("SCH_PORT", textSchPort, "text", VariantType.varInteger);
    schemaLink.add("SCH_URL", textSchUrl);
    schemaLink.add("SCH_AUTO_COMMIT", checkSchAutoCommit, "selected", FieldLinkType.Boolean_TN);
    schemaLink.add("SCH_PROPERTIES", textProperties);
    schemaLink.add("SCH_USER_PROPERTIES", textUserProperties);
    
    if (sch_id != null) {
      schema = new Schema(InternalDatabase.get(), sch_id);
    }
    else if (schema != null) {
      schema.fieldByName("sch_id").setString(new UniqueID().toString());
      schema.fieldByName("sch_selected").setValue(null);
      if (!Application.get().isUserAdmin() || schema.getUsrId() != null) {
        schema.setUsrId(Application.get().getUserId());
      }
    }
    else {
      schema = new Schema(InternalDatabase.get());
      schema.setUsrId(Application.get().getUserId());
      schema.fieldByName("SCH_NAME").setString("{user}@{host}");
    }
    // musi byæ przed schemaLink.updateComponents
    SchemaSettingsProvider[] sspa = Application.get().getServiceArray(SchemaSettingsProvider.class);
    if (sspa != null) {
      for (SchemaSettingsProvider ssp : sspa) {
        addSettings(ssp);
      }
    }

    schemaLink.updateComponents(schema);
    checkPublicSchema.setEnabled(Application.get().isUserAdmin());
    checkPublicSchema.setSelected(schema.getUsrId() == null);
    
    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);

    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel, buttonTest, buttonIcon, buttonAddJdbcProperty, buttonAddUserProperty});
    updateIcon();
    pack();
    SwingUtil.centerWithinScreen(this);
  }
  
  private void addSettings(SchemaSettingsProvider ssp) {
    Component c = ssp.getSettingsComponent(schema.getSchId());
    settinsList.add(new SchemaSetting(ssp, c));
  }

  private void setSettingsVisible(String driverType) {
    for (SchemaSetting ss : settinsList) {
      if (ss.settings.isForDriverType(driverType)) {
        tabbetSettings.addTab(ss.settings.getDescription(), ss.settings.getIcon(), ss.component);
      }
      else {
        tabbetSettings.remove(ss.component);
      }
    }
  }

  private void applySettings() {
    for (SchemaSetting ss : settinsList) {
      if (ss.component instanceof ISettingsComponent) {
        ISettingsComponent isc = (ISettingsComponent)ss.component;
        try {
          if (ss.component.isVisible()) {
            isc.applySettings();
          }
          else {
            isc.cancelSettings();
          }
        }
        catch (Throwable e) {
          ExceptionUtil.processException(e);
          MessageBox.show(this, stringManager.getString("error"), e.getMessage(), ModalResult.OK, MessageBox.ERROR);
        }
      }
    }
  }

  private void cancelSettings() {
    for (SchemaSetting ss : settinsList) {
      if (ss.component instanceof ISettingsComponent) {
        ISettingsComponent isc = (ISettingsComponent)ss.component;
        try {
          isc.cancelSettings();
        }
        catch (Throwable e) {
          ExceptionUtil.processException(e);
          MessageBox.show(this, stringManager.getString("error"), e.getMessage(), ModalResult.OK, MessageBox.ERROR);
        }
      }
    }
  }

  public int getModalResult() {
    return modalResult;
  }
  
  private void updateIcon() {
    if (schema.fieldByName("sch_icon").getValue().isNullValue()) {
      cmSelectIcon.setSmallIcon(null);
    }
    else {
      try {
        cmSelectIcon.setSmallIcon(new ImageIcon(schema.fieldByName("sch_icon").getValue().getBinary()));
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
  
  @Override
  public void dispose() {
    settinsList.clear();
    queryDrivers.close();
    super.dispose();
  }
  
  private void updateSchema() throws Exception {
    schemaLink.updateRecord(schema);
    if (checkPublicSchema.isSelected()) {
      schema.setUsrId(null);
    }
    else if (schema.getUsrId() == null) {
      schema.setUsrId(Application.get().getUserId());
    }
    if (schema.isChanged()) {
      if (sch_id == null) {
        schema.applyInsert();
        sch_id = schema.getSchId();
      } else {
        schema.applyUpdate();
      }
    }
    applySettings();
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
    cmRefreshUrl = new pl.mpak.sky.gui.swing.Action();
    cmSelectIcon = new pl.mpak.sky.gui.swing.Action();
    cmTest = new pl.mpak.sky.gui.swing.Action();
    cmAddJdbcProperty = new pl.mpak.sky.gui.swing.Action();
    cmAddUserProperties = new pl.mpak.sky.gui.swing.Action();
    tabbetSettings = new javax.swing.JTabbedPane();
    jPanel2 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    textSchName = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel2 = new javax.swing.JLabel();
    comboSchDrvId = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel3 = new javax.swing.JLabel();
    textSchHost = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    textSchUser = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel5 = new javax.swing.JLabel();
    textSchPassword = new javax.swing.JPasswordField();
    jLabel6 = new javax.swing.JLabel();
    textSchDatabase = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel7 = new javax.swing.JLabel();
    textSchPort = new pl.mpak.sky.gui.swing.comp.TextField();
    labelDrvUrlTemplate = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    textSchUrl = new pl.mpak.sky.gui.swing.comp.TextField();
    jButton1 = new javax.swing.JButton();
    checkSchAutoCommit = new javax.swing.JCheckBox();
    checkPublicSchema = new javax.swing.JCheckBox();
    jPanel3 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textProperties = new pl.mpak.sky.gui.swing.comp.TextArea();
    jLabel10 = new javax.swing.JLabel();
    buttonAddJdbcProperty = new javax.swing.JButton();
    jPanel1 = new javax.swing.JPanel();
    buttonAddUserProperty = new javax.swing.JButton();
    jLabel11 = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    textUserProperties = new pl.mpak.sky.gui.swing.comp.TextArea();
    buttonTest = new javax.swing.JButton();
    buttonIcon = new javax.swing.JButton();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();

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

    cmRefreshUrl.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefreshUrl.setTooltip(stringManager.getString("SchemaEditDialog-cmRefreshUrl-hint")); // NOI18N
    cmRefreshUrl.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshUrlActionPerformed(evt);
      }
    });

    cmSelectIcon.setActionCommandKey("cmSelectIcon");
    cmSelectIcon.setText(stringManager.getString("SchemaEditDialog-cmSelectIcon-text")); // NOI18N
    cmSelectIcon.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmSelectIconActionPerformed(evt);
      }
    });

    cmTest.setText(stringManager.getString("SchemaEditDialog-cmTest-text")); // NOI18N
    cmTest.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmTestActionPerformed(evt);
      }
    });

    cmAddJdbcProperty.setActionCommandKey("cmAddJdbcProperty");
    cmAddJdbcProperty.setText(stringManager.getString("SchemaEditDialog-cmAddJdbcProperty-text")); // NOI18N
    cmAddJdbcProperty.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddJdbcPropertyActionPerformed(evt);
      }
    });

    cmAddUserProperties.setActionCommandKey("cmAddUserProperties");
    cmAddUserProperties.setText(stringManager.getString("SchemaEditDialog-cmAddUserProperties-text")); // NOI18N
    cmAddUserProperties.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddUserPropertiesActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("SchemaEditDialog-title")); // NOI18N
    setModal(true);

    tabbetSettings.setFocusable(false);

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setLabelFor(textSchName);
    jLabel1.setText(stringManager.getString("SchemaEditDialog-schema-name-dd")); // NOI18N

    textSchName.setToolTipText(stringManager.getString("SchemaEditDilalog-sch_name-hint")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setLabelFor(comboSchDrvId);
    jLabel2.setText(stringManager.getString("SchemaEditDialog-driver-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setLabelFor(textSchHost);
    jLabel3.setText(stringManager.getString("SchemaEditDialog-host-dd")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setLabelFor(textSchUser);
    jLabel4.setText(stringManager.getString("SchemaEditDialog-user-dd")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setLabelFor(textSchPassword);
    jLabel5.setText(stringManager.getString("SchemaEditDialog-password-dd")); // NOI18N

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setLabelFor(textSchDatabase);
    jLabel6.setText(stringManager.getString("SchemaEditDialog-database-dd")); // NOI18N

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setLabelFor(textSchPort);
    jLabel7.setText(stringManager.getString("SchemaEditDialog-port-dd")); // NOI18N

    labelDrvUrlTemplate.setText(" ");

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setLabelFor(textSchUrl);
    jLabel8.setText(stringManager.getString("SchemaEditDialog-url-dd")); // NOI18N

    jButton1.setAction(cmRefreshUrl);

    checkSchAutoCommit.setText(stringManager.getString("SchemaEditDialog-auto-commit")); // NOI18N

    checkPublicSchema.setText(stringManager.getString("SchemaEditDialog-all-show-schema")); // NOI18N

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
          .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
          .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
          .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
          .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
          .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(checkPublicSchema)
          .addComponent(labelDrvUrlTemplate, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
          .addComponent(comboSchDrvId, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
          .addComponent(textSchPort, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkSchAutoCommit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addComponent(textSchUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(textSchName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
          .addComponent(textSchHost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
            .addComponent(textSchPassword, javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textSchUser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(textSchDatabase, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textSchName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(comboSchDrvId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textSchHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textSchUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(textSchPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(textSchDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(textSchPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel7))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(labelDrvUrlTemplate)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(textSchUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel8)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkSchAutoCommit)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkPublicSchema)
        .addContainerGap(55, Short.MAX_VALUE))
    );

    tabbetSettings.addTab(stringManager.getString("SchemaEditDialog-general"), jPanel2); // NOI18N

    textProperties.setColumns(20);
    textProperties.setRows(5);
    textProperties.setFont(new java.awt.Font("Courier New", 0, 12));
    jScrollPane1.setViewportView(textProperties);

    jLabel10.setText(stringManager.getString("SchemaEditDialog-jdbc-param-info-ext")); // NOI18N

    buttonAddJdbcProperty.setAction(cmAddJdbcProperty);
    buttonAddJdbcProperty.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
            .addComponent(buttonAddJdbcProperty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel10)
          .addComponent(buttonAddJdbcProperty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    tabbetSettings.addTab(stringManager.getString("SchemaEditDialog-jdbc-params"), jPanel3); // NOI18N

    buttonAddUserProperty.setAction(cmAddUserProperties);
    buttonAddUserProperty.setPreferredSize(new java.awt.Dimension(85, 25));

    jLabel11.setText(stringManager.getString("SchemaEditDialog-other-params-info-ext")); // NOI18N

    textUserProperties.setColumns(20);
    textUserProperties.setRows(5);
    textUserProperties.setFont(new java.awt.Font("Courier New", 0, 12));
    jScrollPane2.setViewportView(textUserProperties);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(buttonAddUserProperty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel11)
          .addComponent(buttonAddUserProperty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    tabbetSettings.addTab(stringManager.getString("SchemaEditDialog-other-params"), jPanel1); // NOI18N

    buttonTest.setAction(cmTest);
    buttonTest.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonTest.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonIcon.setAction(cmSelectIcon);
    buttonIcon.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonIcon.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(tabbetSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tabbetSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

private void cmRefreshUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshUrlActionPerformed
  try {
    updateSchema();
    textSchUrl.setText(schema.replacePatts(labelDrvUrlTemplate.getText()));
  } catch (Exception ex) {
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmRefreshUrlActionPerformed
  
private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  cancelSettings();
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  try {
    updateSchema();
    modalResult = ModalResult.OK;
    dispose();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
  }
}//GEN-LAST:event_cmOkActionPerformed

private void cmSelectIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectIconActionPerformed
  if (!schema.fieldByName("sch_icon").getValue().isNullValue()) {
    switch (MessageBox.show(this, stringManager.getString("SchemaEditDialog-icon"), stringManager.getString("SchemaEditDialog-icon-selected-q"), ModalResult.YESNOCANCEL, MessageBox.QUESTION)) {
      case ModalResult.CANCEL:
        return;
      case ModalResult.YES: {
        schema.fieldByName("sch_icon").setValue(null);
        updateIcon();
        return;
      }
    }
  }
  File file = FileUtil.selectFileToOpen(this, new FileExtensionFilter[] {new FileExtensionFilter(stringManager.getString("SchemaEditDialog-pic-file"), new String[] {".jpg", ".jpeg", ".gif", ".png"})});
  if (file != null) {
    try {
      schema.fieldByName("sch_icon").setValue(new Variant(StreamUtil.stream2Array(new FileInputStream(file))));
      updateIcon();
    } catch (IOException ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
    }
  }
}//GEN-LAST:event_cmSelectIconActionPerformed

private void cmTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmTestActionPerformed
    try {
      schemaLink.updateRecord(schema);
      TaskPool.getTaskPool().addTask(new Task(stringManager.getString("SchemaEditDialog-connection-testing")) {
        String url = textSchUrl.getText();
        String user = textSchUser.getText();
        String password = new String(textSchPassword.getPassword());
        @Override
        public void run() {
          WaitDialog.showWaitDialog(getDescription(), 100);
          try {
            ConnectionFactory cf = ConnectionFactory.createInstance(url, user, password, schema);
            Database database = cf.createDatabase(true);
            database.close();
            WaitDialog.hideWaitDialog();
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                MessageBox.show(SchemaEditDialog.this, SchemaEditDialog.stringManager.getString("SchemaEditDialog-success"), SchemaEditDialog.stringManager.getString("SchemaEditDialog-test-success"), ModalResult.OK, MessageBox.INFORMATION);
              }
            });
          } catch (final Throwable ex) {
            WaitDialog.hideWaitDialog();
            ExceptionUtil.processException(ex);
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                MessageBox.show(SchemaEditDialog.this, SchemaEditDialog.stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
              }
            });
          }
        }
      });
    } catch (UseDBException ex) {
      MessageBox.show(SchemaEditDialog.this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
}//GEN-LAST:event_cmTestActionPerformed

private void cmAddJdbcPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddJdbcPropertyActionPerformed
  try {
    schemaLink.updateRecord(schema);
    Driver driver = new Driver(InternalDatabase.get(), schema.getDrvId());
    java.sql.Driver sqlDriver = DriverClassLoaderManager.getDriver(
      driver.fieldByName("DRV_LIBRARY_SOURCE").getValue().toString(),
      driver.fieldByName("DRV_EXTRA_LIBRARY").getValue().toString(),
      driver.fieldByName("drv_class_name").getValue().toString());
    DriverPropertyInfo[] dpi = sqlDriver.getPropertyInfo(schema.replacePatts(driver.fieldByName("drv_url_template").getString()), new Properties());
    String[] propList = new String[dpi.length];
    for (int i=0; i<dpi.length; i++) {
      propList[i] = dpi[i].name;
    }
    String property = PropertyDialog.show(propList);
    if (property != null) {
      if (textProperties.getText().length() > 0) {
        textProperties.append("\n");
      }
      textProperties.append(property);
    }
  }
  catch (Exception ex) {
    MessageBox.show(SchemaEditDialog.this, stringManager.getString("error"), String.format(stringManager.getString("SchemaEditDialog-problem-on-load-driver"), new Object[] {ex.getMessage()}), ModalResult.OK, MessageBox.ERROR);
  }
}//GEN-LAST:event_cmAddJdbcPropertyActionPerformed

private void cmAddUserPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddUserPropertiesActionPerformed
  String property = PropertyDialog.show(Database.useDbParameters);
  if (property != null) {
    if (textUserProperties.getText().length() > 0) {
      textUserProperties.append("\n");
    }
    textUserProperties.append(property);
  }
}//GEN-LAST:event_cmAddUserPropertiesActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonAddJdbcProperty;
  private javax.swing.JButton buttonAddUserProperty;
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonIcon;
  private javax.swing.JButton buttonOk;
  private javax.swing.JButton buttonTest;
  private javax.swing.JCheckBox checkPublicSchema;
  private javax.swing.JCheckBox checkSchAutoCommit;
  private pl.mpak.sky.gui.swing.Action cmAddJdbcProperty;
  private pl.mpak.sky.gui.swing.Action cmAddUserProperties;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private pl.mpak.sky.gui.swing.Action cmRefreshUrl;
  private pl.mpak.sky.gui.swing.Action cmSelectIcon;
  private pl.mpak.sky.gui.swing.Action cmTest;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboSchDrvId;
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JLabel labelDrvUrlTemplate;
  private javax.swing.JTabbedPane tabbetSettings;
  private pl.mpak.sky.gui.swing.comp.TextArea textProperties;
  private pl.mpak.sky.gui.swing.comp.TextField textSchDatabase;
  private pl.mpak.sky.gui.swing.comp.TextField textSchHost;
  private pl.mpak.sky.gui.swing.comp.TextField textSchName;
  private javax.swing.JPasswordField textSchPassword;
  private pl.mpak.sky.gui.swing.comp.TextField textSchPort;
  private pl.mpak.sky.gui.swing.comp.TextField textSchUrl;
  private pl.mpak.sky.gui.swing.comp.TextField textSchUser;
  private pl.mpak.sky.gui.swing.comp.TextArea textUserProperties;
  // End of variables declaration//GEN-END:variables
  
  private final class SchemaSetting {
    private SchemaSettingsProvider settings;
    private Component component;

    public SchemaSetting(SchemaSettingsProvider settings, Component component) {
      this.settings = settings;
      this.component = component;
    }

  }

}
