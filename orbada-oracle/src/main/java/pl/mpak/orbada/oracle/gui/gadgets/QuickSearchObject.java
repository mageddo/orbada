/*
 * QuickSearchObject.java
 *
 * Created on 14 wrzesieñ 2008, 21:05
 */

package pl.mpak.orbada.oracle.gui.gadgets;

import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.Sql;
import pl.mpak.orbada.oracle.gui.freezing.FreezeFactory;
import pl.mpak.orbada.oracle.gui.freezing.FreezeViewService;
import pl.mpak.orbada.oracle.util.DragSourceOracleObject;
import pl.mpak.orbada.oracle.util.OracleObject;
import pl.mpak.orbada.plugins.IDatabaseObject;
import pl.mpak.orbada.plugins.IGadgetAccesibilities;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.ListRowChangeKeyListener;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.Configurable;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.Titleable;

/**
 *
 * @author  akaluza
 */
public class QuickSearchObject extends javax.swing.JPanel implements Titleable, Closeable, Configurable {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private IGadgetAccesibilities accesibilities;
  
  /** Creates new form QuickSearchObject */
  public QuickSearchObject(IGadgetAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    initComponents();
    init();
  }

  private void init() {
    listFound.setModel(new DefaultListModel());
    listFound.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list,	Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof IDatabaseObject) {
          IDatabaseObject ido = (IDatabaseObject)value;
          setIcon(ido.getObjectIcon());
          if (accesibilities.getDatabase().getUserProperties().getProperty("schema-name", accesibilities.getDatabase().getUserName().toUpperCase()).equals(ido.getSchemaName())) {
            setText(ido.getObjectName());
          }
          else {
            setText(ido.getSqlObjectName());
          }
        }
        return this;
      }
    });
    ((JTextField)textSearch.getEditor().getEditorComponent()).addKeyListener(new ListRowChangeKeyListener(listFound));
    SwingUtil.addAction((JTextField)textSearch.getEditor().getEditorComponent(), cmFind);
    SwingUtil.addAction((JTextField)textSearch.getEditor().getEditorComponent(), cmFreezeObject);
    SwingUtil.addAction(listFound, cmFreezeObject);

    ISettings settings = accesibilities.getApplication().getSettings(accesibilities.getDatabase().getUserProperties().getProperty("schemaId"), "oracle-quick-search-object");
    checkAllSchemas.setSelected(settings.getValue("all-schemas", checkAllSchemas.isSelected()));
    checkJavaObjects.setSelected(settings.getValue("java-objects", checkJavaObjects.isSelected()));
    checkDataObjects.setSelected(settings.getValue("data-objects", checkDataObjects.isSelected()));
    checkPlSqlObjects.setSelected(settings.getValue("plsql-objects", checkPlSqlObjects.isSelected()));
    checkOtherObjects.setSelected(settings.getValue("other-objects", checkOtherObjects.isSelected()));
    
    DragSource dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(listFound, DnDConstants.ACTION_COPY_OR_MOVE, new DragSourceOracleObject(listFound));
  }

  private void addTextToList(String text) {
    DefaultComboBoxModel model = (DefaultComboBoxModel)textSearch.getModel();
    int index = model.getIndexOf(text);
    if (index != -1) {
      model.removeElementAt(index);
    }
    model.insertElementAt(text, 0);
    if (model.getSize() > 50) {
      model.removeElementAt(model.getSize() -1);
    }
  }

  private void findObject() {
    Query query = accesibilities.getDatabase().createQuery();
    try {
      String find = textSearch.getText();
      addTextToList(find);
      boolean filterSelected = false;
      String filter = "";
      if (!checkJavaObjects.isSelected() || !checkDataObjects.isSelected() || !checkPlSqlObjects.isSelected() || !checkOtherObjects.isSelected()) {
        if (checkJavaObjects.isSelected()) {
          filter = filter +"(OBJECT_TYPE like 'JAVA%')";
          filterSelected = true;
        }
        if (checkDataObjects.isSelected()) {
          if (filterSelected) {
            filter = filter +" or ";
          }
          filter = filter +"(OBJECT_TYPE in ('TABLE', 'VIEW', 'MATERIALIZED VIEW'))";
          filterSelected = true;
        }
        if (checkPlSqlObjects.isSelected()) {
          if (filterSelected) {
            filter = filter +" or ";
          }
          filter = filter +"(OBJECT_TYPE in ('PACKAGE', 'FUNCTION', 'PROCEDURE', 'TYPE', 'TRIGGER'))";
          filterSelected = true;
        }
        if (checkOtherObjects.isSelected()) {
          if (filterSelected) {
            filter = filter +" or ";
          }
          filter = filter +"(OBJECT_TYPE in ('SEQUENCE', 'DIRECTORY', 'INDEX', 'DATABASE LINK', 'SYNONYM', 'LIBRARY', 'INDEXTYPE', 'EVALUATION CONTEXT', 'CONSUMER GROUP', 'OPERATOR'))";
          filterSelected = true;
        }
      }
      query.setSqlText(Sql.getObjectsSearch(filterSelected ? "(" +filter +")" : null));
      if (!checkAllSchemas.isSelected()) {
        query.paramByName("schema_name").setString(accesibilities.getDatabase().getUserProperties().getProperty("schema-name", accesibilities.getDatabase().getUserName().toUpperCase()));
      }
      query.paramByName("object_name").setString(find);
      query.open();
      DefaultListModel model = (DefaultListModel)listFound.getModel();
      model.clear();
      while (!query.eof()) {
        try {
          model.addElement(new OracleObject(query));
        }
        catch (Exception ex) {
          ExceptionUtil.processException(ex);            
        }
        query.next();
      }
      textSearch.setSelectedItem(find);
    }
    catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
    finally {
      query.close();
    }
  }

  public String getTitle() {
    return String.format(stringManager.getString("QuickSearchObject-title"), new Object[] {accesibilities.getDatabase().getPublicName()});
  }

  public void close() throws IOException {
    ISettings settings = accesibilities.getApplication().getSettings(accesibilities.getDatabase().getUserProperties().getProperty("schemaId"), "oracle-quick-search-object");
    settings.setValue("all-schemas", checkAllSchemas.isSelected());
    settings.setValue("java-objects", checkJavaObjects.isSelected());
    settings.setValue("data-objects", checkDataObjects.isSelected());
    settings.setValue("plsql-objects", checkPlSqlObjects.isSelected());
    settings.setValue("other-objects", checkOtherObjects.isSelected());
    settings.store();
  }

  public boolean configure() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isConfig() {
    return false;
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    cmFind = new pl.mpak.sky.gui.swing.Action();
    cmFreezeObject = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    jPanel4 = new javax.swing.JPanel();
    jPanel5 = new javax.swing.JPanel();
    buttonFind = new pl.mpak.sky.gui.swing.comp.ToolButton();
    buttonFreeze = new pl.mpak.sky.gui.swing.comp.ToolButton();
    textSearch = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jPanel2 = new javax.swing.JPanel();
    checkAllSchemas = new javax.swing.JCheckBox();
    jPanel3 = new javax.swing.JPanel();
    checkPlSqlObjects = new javax.swing.JCheckBox();
    checkDataObjects = new javax.swing.JCheckBox();
    checkJavaObjects = new javax.swing.JCheckBox();
    checkOtherObjects = new javax.swing.JCheckBox();
    jScrollPane1 = new javax.swing.JScrollPane();
    listFound = new JList() {
      public String getToolTipText (MouseEvent e) {
        int index = locationToIndex (e.getPoint ());
        if (index > -1) {
          ListModel lm = (ListModel)getModel ();
          IDatabaseObject ido = (IDatabaseObject)lm.getElementAt (index);
          return "<html>" +ido.getObjectType() +" <b>" +ido.getSqlObjectName() +"</b> " +ido.getStatus();
        }
        else {
          return null;
        }
      }
    };

    cmFind.setActionCommandKey("cmFind");
    cmFind.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmFind.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_source.gif")); // NOI18N
    cmFind.setText(stringManager.getString("cmFind-text")); // NOI18N
    cmFind.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFindActionPerformed(evt);
      }
    });

    cmFreezeObject.setActionCommandKey("cmFreezeObject");
    cmFreezeObject.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
    cmFreezeObject.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/freeze.gif")); // NOI18N
    cmFreezeObject.setText(stringManager.getString("cmFreezeObject-text")); // NOI18N
    cmFreezeObject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmFreezeObjectActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout(0, 2));

    jPanel1.setLayout(new java.awt.BorderLayout(2, 2));

    jPanel4.setLayout(new java.awt.BorderLayout());

    jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 0));

    buttonFind.setAction(cmFind);
    buttonFind.setPreferredSize(new java.awt.Dimension(24, 24));
    jPanel5.add(buttonFind);

    buttonFreeze.setAction(cmFreezeObject);
    buttonFreeze.setPreferredSize(new java.awt.Dimension(24, 24));
    jPanel5.add(buttonFreeze);

    jPanel4.add(jPanel5, java.awt.BorderLayout.EAST);

    textSearch.setEditable(true);
    jPanel4.add(textSearch, java.awt.BorderLayout.CENTER);

    jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

    jPanel2.setLayout(new java.awt.GridBagLayout());

    checkAllSchemas.setText(stringManager.getString("in-all-schemas")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add(checkAllSchemas, gridBagConstraints);

    jPanel3.setLayout(new java.awt.GridLayout(2, 2));

    checkPlSqlObjects.setSelected(true);
    checkPlSqlObjects.setText(stringManager.getString("pl-sql-obejcts")); // NOI18N
    jPanel3.add(checkPlSqlObjects);

    checkDataObjects.setSelected(true);
    checkDataObjects.setText(stringManager.getString("objects-with-data")); // NOI18N
    jPanel3.add(checkDataObjects);

    checkJavaObjects.setSelected(true);
    checkJavaObjects.setText(stringManager.getString("java-objects")); // NOI18N
    jPanel3.add(checkJavaObjects);

    checkOtherObjects.setSelected(true);
    checkOtherObjects.setText(stringManager.getString("other-objects")); // NOI18N
    jPanel3.add(checkOtherObjects);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    jPanel2.add(jPanel3, gridBagConstraints);

    jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

    add(jPanel1, java.awt.BorderLayout.PAGE_START);

    listFound.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    listFound.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        listFoundMouseClicked(evt);
      }
    });
    jScrollPane1.setViewportView(listFound);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  textSearch.requestFocusInWindow();
}//GEN-LAST:event_formComponentShown

private void cmFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFindActionPerformed
  findObject();
}//GEN-LAST:event_cmFindActionPerformed

private void cmFreezeObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFreezeObjectActionPerformed
  if (listFound.getSelectedIndex() >= 0) {
    try {
      IDatabaseObject ido = (IDatabaseObject)listFound.getModel().getElementAt(listFound.getSelectedIndex());
      FreezeViewService service = new FreezeFactory().createInstance(ido.getObjectType(), ido.getSchemaName(), ido.getObjectName());
      if (service != null) {
        accesibilities.getPerspectiveAccesibilities().createView(service);
      }
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmFreezeObjectActionPerformed

private void listFoundMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listFoundMouseClicked
  if (listFound.getSelectedValue() != null && evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
    cmFreezeObject.performe();
  }
}//GEN-LAST:event_listFoundMouseClicked


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFind;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonFreeze;
  private javax.swing.JCheckBox checkAllSchemas;
  private javax.swing.JCheckBox checkDataObjects;
  private javax.swing.JCheckBox checkJavaObjects;
  private javax.swing.JCheckBox checkOtherObjects;
  private javax.swing.JCheckBox checkPlSqlObjects;
  private pl.mpak.sky.gui.swing.Action cmFind;
  private pl.mpak.sky.gui.swing.Action cmFreezeObject;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList listFound;
  private pl.mpak.sky.gui.swing.comp.ComboBox textSearch;
  // End of variables declaration//GEN-END:variables

}
