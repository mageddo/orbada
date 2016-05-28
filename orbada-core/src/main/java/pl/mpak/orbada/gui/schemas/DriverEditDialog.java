package pl.mpak.orbada.gui.schemas;

import java.beans.IntrospectionException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.DriverClassLoaderManager;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Driver;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.RecordLink;
import pl.mpak.usedb.gui.linkreq.FieldRequeiredNotNull;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.DriverAssignableClasses;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.files.FileExtensionFilter;
import pl.mpak.util.id.UniqueID;

/**
 *
 * @author  akaluza
 */
public class DriverEditDialog extends javax.swing.JDialog {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private String drv_id;
  private int modalResult = ModalResult.NONE;
  private Driver driver;
  private RecordLink driverLink;
  
  public DriverEditDialog(String drv_id) throws IntrospectionException, UseDBException {
    super(SwingUtil.getRootFrame());
    this.drv_id = drv_id;
    initComponents();
    init();
  }
  
  public DriverEditDialog(Driver driver) throws IntrospectionException, UseDBException {
    super(SwingUtil.getRootFrame());
    this.driver = driver;
    initComponents();
    init();
  }

  public static String showDialog(String drv_id) throws IntrospectionException, UseDBException {
    DriverEditDialog dialog = new DriverEditDialog(drv_id);
    dialog.setVisible(true);
    return dialog.getModalResult() == ModalResult.OK ? dialog.drv_id : null;
  }
  
  public static String showDialog(Driver driver) throws IntrospectionException, UseDBException {
    DriverEditDialog dialog = new DriverEditDialog(driver);
    dialog.setVisible(true);
    return dialog.getModalResult() == ModalResult.OK ? dialog.drv_id : null;
  }

  private void init() throws IntrospectionException, UseDBException {
    Query query = InternalDatabase.get().createQuery();
    try {
      try {
        query.open("select dtp_name from driver_types order by dtp_name");
        comboDrvTypeName.setModel(new javax.swing.DefaultComboBoxModel(QueryUtil.queryToArray(null, query)));
      }
      catch(Exception e) {
        ExceptionUtil.processException(e);
      }
      try {
        query.open("select distinct drv_library_source from drivers order by drv_library_source");
        comboDrvLibrarySource.setModel(new javax.swing.DefaultComboBoxModel(QueryUtil.queryToArray(null, query)));
      }
      catch(Exception e) {
        ExceptionUtil.processException(e);
      }
    }
    finally {
      query.close();
    }

    listExtraLibrary.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        cmDelLibrary.setEnabled(listExtraLibrary.getSelectedIndex() >= 0);
      }
    });
    listExtraLibrary.setModel(new DefaultListModel());

    driverLink = new RecordLink();
    driverLink.add("DRV_NAME", textDrvName, new FieldRequeiredNotNull(stringManager.getString("DriverEditDialog-driver-name")));
    driverLink.add("DRV_LIBRARY_SOURCE", comboDrvLibrarySource, "selectedItem");
    driverLink.add("DRV_TYPE_NAME", comboDrvTypeName, "selectedItem");
    driverLink.add("DRV_CLASS_NAME", comboDrvClassName, "selectedItem");
    driverLink.add("DRV_URL_TEMPLATE", textDrvUrlTemplate);

    if (drv_id != null) {
      driver = new Driver(Application.get().getOrbadaDatabase(), drv_id);
    }
    else if (driver != null) {
      driver.setId(new UniqueID().toString());
      if (!Application.get().isUserAdmin() || driver.getUsrId() != null) {
        driver.setUsrId(Application.get().getUserId());
      }
    }
    else {
      driver = new Driver(Application.get().getOrbadaDatabase());
      driver.setUsrId(Application.get().getUserId());
    }
    driverLink.updateComponents(driver);
    checkPublicDriver.setEnabled(Application.get().isUserAdmin());
    checkPublicDriver.setSelected(driver.getUsrId() == null);
    extraLibToDialog();
    updateUrlList();

    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonAdd, buttonDel});
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel});

    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);

    SwingUtil.centerWithinScreen(this);
  }
  
  public int getModalResult() {
    return modalResult;
  }
  
  @Override
  public void dispose() {
    super.dispose();
  }

  private void extraLibToDialog() {
    DefaultListModel model = (DefaultListModel)listExtraLibrary.getModel();
    model.clear();
    if (!driver.fieldByName("DRV_EXTRA_LIBRARY").isNull()) {
      try {
        StringTokenizer st = new StringTokenizer(driver.fieldByName("DRV_EXTRA_LIBRARY").getString(), ";");
        while (st.hasMoreTokens()) {
          model.addElement(st.nextToken());
        }
        if (model.size() > 0) {
          listExtraLibrary.setSelectedIndex(0);
        }
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  private void dialogToExtraLib() {
    DefaultListModel model = (DefaultListModel)listExtraLibrary.getModel();
    StringBuilder sb = new StringBuilder();
    for (int i=0; i<model.size(); i++) {
      if (sb.length() > 0) {
        sb.append(";");
      }
      sb.append(model.get(i));
    }
    driver.fieldByName("DRV_EXTRA_LIBRARY").setString(sb.toString());
  }
  
  protected void refreshClassList() {
    comboDrvClassName.removeAllItems();
    
    if (comboDrvLibrarySource.getSelectedItem() == null || "".equals(comboDrvLibrarySource.getSelectedItem())) {
      return;
    }
    
    URL url = null;
    try {
      url = (new File(comboDrvLibrarySource.getSelectedItem().toString())).toURI().toURL();
    }
    catch (MalformedURLException e) {
      ExceptionUtil.processException(e);
      return;
    }
    DriverAssignableClasses cl = new DriverAssignableClasses(url);
    Class[] classes;
    try {
      classes = cl.getDriverClasses();
    }
    catch (Exception e) {
      ExceptionUtil.processException(e);
      return;
    }
    for (int i = 0; i < classes.length; ++i) {
      comboDrvClassName.addItem(classes[i].getName());
    }
  }
  
  private void updateUrlList() {
    
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
    cmOpenSource = new pl.mpak.sky.gui.swing.Action();
    cmRefreshDrivers = new pl.mpak.sky.gui.swing.Action();
    cmResolveUrl = new pl.mpak.sky.gui.swing.Action();
    cmAddLibrary = new pl.mpak.sky.gui.swing.Action();
    cmDelLibrary = new pl.mpak.sky.gui.swing.Action();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    comboDrvTypeName = new javax.swing.JComboBox();
    jButton1 = new javax.swing.JButton();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    textDrvName = new pl.mpak.sky.gui.swing.comp.TextField();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    comboDrvClassName = new javax.swing.JComboBox();
    comboDrvLibrarySource = new javax.swing.JComboBox();
    jButton2 = new javax.swing.JButton();
    textDrvUrlTemplate = new pl.mpak.sky.gui.swing.comp.TextField();
    buttonResolveUrl = new javax.swing.JButton();
    checkPublicDriver = new javax.swing.JCheckBox();
    jLabel6 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    listExtraLibrary = new javax.swing.JList();
    buttonAdd = new javax.swing.JButton();
    buttonDel = new javax.swing.JButton();

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

    cmOpenSource.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/open16.gif")); // NOI18N
    cmOpenSource.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOpenSourceActionPerformed(evt);
      }
    });

    cmRefreshDrivers.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
    cmRefreshDrivers.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshDriversActionPerformed(evt);
      }
    });

    cmResolveUrl.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/refresh16.gif")); // NOI18N
    cmResolveUrl.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmResolveUrlActionPerformed(evt);
      }
    });

    cmAddLibrary.setText(stringManager.getString("cmAddLibrary-text")); // NOI18N
    cmAddLibrary.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmAddLibraryActionPerformed(evt);
      }
    });

    cmDelLibrary.setText(stringManager.getString("cmDelLibrary-text")); // NOI18N
    cmDelLibrary.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDelLibraryActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("DriverEditDialog-title")); // NOI18N
    setModal(true);

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setLabelFor(textDrvName);
    jLabel1.setText(stringManager.getString("DriverEditDialog-name-dd")); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("DriverEditDialog-driver-source-dd")); // NOI18N

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setLabelFor(comboDrvTypeName);
    jLabel3.setText(stringManager.getString("DriverEditDialog-driver-kind-dd")); // NOI18N

    comboDrvTypeName.setEditable(true);

    jButton1.setAction(cmOpenSource);
    jButton1.setPreferredSize(new java.awt.Dimension(25, 25));

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("DriverEditDialog-class-dd")); // NOI18N

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText(stringManager.getString("DriverEditDialog-url-pattern-dd")); // NOI18N

    buttonOk.setAction(cmOk);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmCancel);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    comboDrvClassName.setEditable(true);
    comboDrvClassName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "" }));

    comboDrvLibrarySource.setEditable(true);

    jButton2.setAction(cmRefreshDrivers);
    jButton2.setMargin(new java.awt.Insets(1, 1, 1, 1));
    jButton2.setPreferredSize(new java.awt.Dimension(25, 25));

    buttonResolveUrl.setAction(cmResolveUrl);
    buttonResolveUrl.setMargin(new java.awt.Insets(1, 1, 1, 1));
    buttonResolveUrl.setPreferredSize(new java.awt.Dimension(25, 25));

    checkPublicDriver.setText(stringManager.getString("DriverEditDialog-all-user-show")); // NOI18N

    jLabel6.setText(stringManager.getString("extra-libraries-dd")); // NOI18N

    listExtraLibrary.setFixedCellHeight(18);
    jScrollPane1.setViewportView(listExtraLibrary);

    buttonAdd.setAction(cmAddLibrary);
    buttonAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonAdd.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDel.setAction(cmDelLibrary);
    buttonDel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDel.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(buttonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(buttonDel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(textDrvName, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(comboDrvLibrarySource, javax.swing.GroupLayout.Alignment.TRAILING, 0, 264, Short.MAX_VALUE)
                  .addComponent(comboDrvTypeName, 0, 264, Short.MAX_VALUE)
                  .addComponent(comboDrvClassName, 0, 264, Short.MAX_VALUE)
                  .addComponent(textDrvUrlTemplate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                  .addComponent(checkPublicDriver))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                  .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(buttonResolveUrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jLabel6))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textDrvName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(7, 7, 7)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel2)
              .addComponent(comboDrvLibrarySource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel3)
              .addComponent(comboDrvTypeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(layout.createSequentialGroup()
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jLabel4)
            .addComponent(comboDrvClassName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel5)
              .addComponent(textDrvUrlTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(checkPublicDriver))
          .addComponent(buttonResolveUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel6)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jScrollPane1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

private void cmRefreshDriversActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshDriversActionPerformed
  refreshClassList();
}//GEN-LAST:event_cmRefreshDriversActionPerformed

private void cmOpenSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOpenSourceActionPerformed
  JFileChooser fileChoose = new JFileChooser(".");
  if (comboDrvLibrarySource.getSelectedItem() != null && !comboDrvLibrarySource.getSelectedItem().equals("")) {
    fileChoose.setSelectedFile(new File(comboDrvLibrarySource.getSelectedItem().toString()));
  }
  fileChoose.addChoosableFileFilter(new FileExtensionFilter(stringManager.getString("jar-files"), new String[] { ".jar", ".zip" }));
  int returnVal = fileChoose.showOpenDialog(this);
  if (returnVal == JFileChooser.APPROVE_OPTION) {
    comboDrvLibrarySource.setSelectedItem(fileChoose.getSelectedFile().getAbsoluteFile());
    refreshClassList();
  }
}//GEN-LAST:event_cmOpenSourceActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
 try {
    driverLink.updateRecord(driver);
    if (checkPublicDriver.isSelected()) {
      driver.setUsrId(null);
    }
    else if (driver.getUsrId() == null) {
      driver.setUsrId(Application.get().getUserId());
    }
    dialogToExtraLib();
    if (driver.isChanged()) {
      if (drv_id == null) {
        driver.applyInsert();
      }
      else {
        driver.applyUpdate();
      }
      drv_id = driver.getId();
    }
    modalResult = ModalResult.OK;
    DriverClassLoaderManager.reset();
    dispose();
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
    MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), new int[] {ModalResult.OK});
  }
}//GEN-LAST:event_cmOkActionPerformed

private void cmResolveUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmResolveUrlActionPerformed
  java.awt.EventQueue.invokeLater(new Runnable() {
    public void run() {
      String url = SelectUrlTemplateDialog.show(((JTextField)comboDrvTypeName.getEditor().getEditorComponent()).getText(), ((JTextField)comboDrvClassName.getEditor().getEditorComponent()).getText());
      if (url != null) {
        textDrvUrlTemplate.setText(url);
      }
    }
  });
}//GEN-LAST:event_cmResolveUrlActionPerformed

private void cmAddLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAddLibraryActionPerformed
  DefaultListModel model = (DefaultListModel)listExtraLibrary.getModel();
  JFileChooser fileChoose = new JFileChooser(".");
  fileChoose.addChoosableFileFilter(new FileExtensionFilter(stringManager.getString("jar-files"), new String[] { ".jar", ".zip" }));
  int returnVal = fileChoose.showOpenDialog(this);
  if (returnVal == JFileChooser.APPROVE_OPTION) {
    model.addElement(fileChoose.getSelectedFile().getAbsoluteFile());
  }
}//GEN-LAST:event_cmAddLibraryActionPerformed

private void cmDelLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDelLibraryActionPerformed
  DefaultListModel model = (DefaultListModel)listExtraLibrary.getModel();
  if (listExtraLibrary.getSelectedIndex() >= 0) {
    if (MessageBox.show(this, stringManager.getString("deleting"), "Usun¹æ wybran¹ bibliotekê z listy ?", ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
      model.remove(listExtraLibrary.getSelectedIndex());
    }
  }
}//GEN-LAST:event_cmDelLibraryActionPerformed
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonAdd;
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonDel;
  private javax.swing.JButton buttonOk;
  private javax.swing.JButton buttonResolveUrl;
  private javax.swing.JCheckBox checkPublicDriver;
  private pl.mpak.sky.gui.swing.Action cmAddLibrary;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmDelLibrary;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private pl.mpak.sky.gui.swing.Action cmOpenSource;
  private pl.mpak.sky.gui.swing.Action cmRefreshDrivers;
  private pl.mpak.sky.gui.swing.Action cmResolveUrl;
  private javax.swing.JComboBox comboDrvClassName;
  private javax.swing.JComboBox comboDrvLibrarySource;
  private javax.swing.JComboBox comboDrvTypeName;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList listExtraLibrary;
  private pl.mpak.sky.gui.swing.comp.TextField textDrvName;
  private pl.mpak.sky.gui.swing.comp.TextField textDrvUrlTemplate;
  // End of variables declaration//GEN-END:variables

}
