/*
 * FilterDialog.java
 *
 * Created on 4 listopad 2007, 18:37
 */

package pl.mpak.orbada.universal.gui.filter;

import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.filter.SqlFilterDefComponent;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class SqlFilterDialog extends javax.swing.JDialog {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  private int modalResult = ModalResult.NONE;
  private SqlFilter sqlFilter;
  private boolean disableUpdatedControls;
  
  /** Creates new form FilterDialog 
   * @param sqlFilter 
   */
  public SqlFilterDialog(SqlFilter sqlFilter) {
    super(SwingUtil.getRootFrame());
    this.sqlFilter = sqlFilter;
    initComponents();
    init();
  }
  
  public static boolean show(SqlFilter sqlFilter) {
    if (sqlFilter.isSilentDialog()) {
      return true;
    }
    SqlFilterDialog dialog = new SqlFilterDialog(sqlFilter);
    dialog.setVisible(true);
    return dialog.modalResult == ModalResult.OK;
  }
  
  private void init() {
    SwingUtil.centerWithinScreen(this);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmCancel);
    getRootPane().setDefaultButton(buttonOk);
    
    ArrayList<SqlFilterDefComponent> defList = new ArrayList<SqlFilterDefComponent>();
    for (int i=0; i<sqlFilter.getDefinition().getCount(); i++) {
      SqlFilterDefComponent def = sqlFilter.getDefinition().get(i);
      defList.add(def);
    }
    comboColumn1.setModel(new DefaultComboBoxModel(defList.toArray()));
    comboColumn2.setModel(new DefaultComboBoxModel(defList.toArray()));
    comboColumn3.setModel(new DefaultComboBoxModel(defList.toArray()));
    comboColumn4.setModel(new DefaultComboBoxModel(defList.toArray()));
    comboColumn5.setModel(new DefaultComboBoxModel(defList.toArray()));
    
    comboDefs.setModel(new DefaultComboBoxModel(sqlFilter.getFilterComponentList().toArray()));
    
    checkFilterOn.setSelected(sqlFilter.isTurnedOn());
    filterToDialog(sqlFilter.getFilterComponent());
    
    updateEnableControls();
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        comboColumn1.requestFocusInWindow();
      }
    });
  }
  
  private void filterToDialog(SqlFilterComponent[] filterComponent) {
    SqlFilterComponent sfc = filterComponent[0];
    comboColumn1.setSelectedItem(sqlFilter.getDefinition().getByColumn(sfc.getColumnName()));
    comboCond1.setSelectedItem(sfc.getCondition());
    comboValue1.setSelectedItem(sfc.getValue());
    
    sfc = filterComponent[1];
    comboOp1.setSelectedItem(sfc.getOperator());
    comboColumn2.setSelectedItem(sqlFilter.getDefinition().getByColumn(sfc.getColumnName()));
    comboCond2.setSelectedItem(sfc.getCondition());
    comboValue2.setSelectedItem(sfc.getValue());
    
    sfc = filterComponent[2];
    comboOp2.setSelectedItem(sfc.getOperator());
    comboColumn3.setSelectedItem(sqlFilter.getDefinition().getByColumn(sfc.getColumnName()));
    comboCond3.setSelectedItem(sfc.getCondition());
    comboValue3.setSelectedItem(sfc.getValue());
    
    sfc = filterComponent[3];
    comboOp3.setSelectedItem(sfc.getOperator());
    comboColumn4.setSelectedItem(sqlFilter.getDefinition().getByColumn(sfc.getColumnName()));
    comboCond4.setSelectedItem(sfc.getCondition());
    comboValue4.setSelectedItem(sfc.getValue());
    
    sfc = filterComponent[4];
    comboOp4.setSelectedItem(sfc.getOperator());
    comboColumn5.setSelectedItem(sqlFilter.getDefinition().getByColumn(sfc.getColumnName()));
    comboCond5.setSelectedItem(sfc.getCondition());
    comboValue5.setSelectedItem(sfc.getValue());
  }
  
  private void dialogToFilter(SqlFilterComponent[] filterComponent) {
    sqlFilter.setTurnedOn(checkFilterOn.getSelectedObjects() != null);
    
    SqlFilterComponent sfc = filterComponent[0];
    sfc.setColumnName(((SqlFilterDefComponent)comboColumn1.getSelectedItem()).getColumnSqlName());
    sfc.setCondition(StringUtil.nvl(comboCond1.getSelectedItem(), "").toString());
    sfc.setValue(StringUtil.nvl(comboValue1.getSelectedItem(), "").toString());
    sfc.setTurnedOn(((SqlFilterDefComponent)comboColumn1.getSelectedItem()).getColumnSqlName() != null);
    
    sfc = filterComponent[1];
    sfc.setOperator(comboOp1.getSelectedItem().toString());
    sfc.setColumnName(((SqlFilterDefComponent)comboColumn2.getSelectedItem()).getColumnSqlName());
    sfc.setCondition(StringUtil.nvl(comboCond2.getSelectedItem(), "").toString());
    sfc.setValue(StringUtil.nvl(comboValue2.getSelectedItem(), "").toString());
    sfc.setTurnedOn(!StringUtil.isEmpty(comboOp1.getSelectedItem().toString()) && ((SqlFilterDefComponent)comboColumn2.getSelectedItem()).getColumnSqlName() != null);
    
    sfc = filterComponent[2];
    sfc.setOperator(comboOp2.getSelectedItem().toString());
    sfc.setColumnName(((SqlFilterDefComponent)comboColumn3.getSelectedItem()).getColumnSqlName());
    sfc.setCondition(StringUtil.nvl(comboCond3.getSelectedItem(), "").toString());
    sfc.setValue(StringUtil.nvl(comboValue3.getSelectedItem(), "").toString());
    sfc.setTurnedOn(!StringUtil.isEmpty(comboOp2.getSelectedItem().toString()) && ((SqlFilterDefComponent)comboColumn3.getSelectedItem()).getColumnSqlName() != null);
    
    sfc = filterComponent[3];
    sfc.setOperator(comboOp3.getSelectedItem().toString());
    sfc.setColumnName(((SqlFilterDefComponent)comboColumn4.getSelectedItem()).getColumnSqlName());
    sfc.setCondition(StringUtil.nvl(comboCond4.getSelectedItem(), "").toString());
    sfc.setValue(StringUtil.nvl(comboValue4.getSelectedItem(), "").toString());
    sfc.setTurnedOn(!StringUtil.isEmpty(comboOp3.getSelectedItem().toString()) && ((SqlFilterDefComponent)comboColumn4.getSelectedItem()).getColumnSqlName() != null);
    
    sfc = filterComponent[4];
    sfc.setOperator(comboOp4.getSelectedItem().toString());
    sfc.setColumnName(((SqlFilterDefComponent)comboColumn5.getSelectedItem()).getColumnSqlName());
    sfc.setCondition(StringUtil.nvl(comboCond5.getSelectedItem(), "").toString());
    sfc.setValue(StringUtil.nvl(comboValue5.getSelectedItem(), "").toString());
    sfc.setTurnedOn(!StringUtil.isEmpty(comboOp4.getSelectedItem().toString()) && ((SqlFilterDefComponent)comboColumn5.getSelectedItem()).getColumnSqlName() != null);
  }
  
  private void updateEnableControls() {
    if (disableUpdatedControls) {
      return;
    }
    comboCond1.setEnabled(!"USER".equals(comboCond1.getSelectedItem()));
    comboValue1.setEnabled(!StringUtil.equalAnyOfString((String)comboCond1.getSelectedItem(), new String[] {"USER", "IS NULL", "IS NOT NULL"}));

    comboOp1.setEnabled(!StringUtil.isEmpty(comboColumn1.getSelectedItem().toString()));
    comboColumn2.setEnabled(!StringUtil.isEmpty(comboOp1.getSelectedItem().toString()));
    comboCond2.setEnabled(comboColumn2.isEnabled() && !"USER".equals(comboCond2.getSelectedItem()));
    comboValue2.setEnabled(comboColumn2.isEnabled() && !StringUtil.equalAnyOfString((String)comboCond2.getSelectedItem(), new String[] {"USER", "IS NULL", "IS NOT NULL"}));
    
    comboOp2.setEnabled(comboColumn2.isEnabled() && !StringUtil.isEmpty(comboColumn2.getSelectedItem().toString()));
    comboColumn3.setEnabled(!StringUtil.isEmpty(comboOp2.getSelectedItem().toString()));
    comboCond3.setEnabled(comboColumn3.isEnabled() && !"USER".equals(comboCond3.getSelectedItem()));
    comboValue3.setEnabled(comboColumn3.isEnabled() && !StringUtil.equalAnyOfString((String)comboCond3.getSelectedItem(), new String[] {"USER", "IS NULL", "IS NOT NULL"}));
    
    comboOp3.setEnabled(comboColumn3.isEnabled() && !StringUtil.isEmpty(comboColumn3.getSelectedItem().toString()));
    comboColumn4.setEnabled(!StringUtil.isEmpty(comboOp3.getSelectedItem().toString()));
    comboCond4.setEnabled(comboColumn4.isEnabled() && !"USER".equals(comboCond4.getSelectedItem()));
    comboValue4.setEnabled(comboColumn4.isEnabled() && !StringUtil.equalAnyOfString((String)comboCond4.getSelectedItem(), new String[] {"USER", "IS NULL", "IS NOT NULL"}));
    
    comboOp4.setEnabled(comboColumn4.isEnabled() && !StringUtil.isEmpty(comboColumn4.getSelectedItem().toString()));
    comboColumn5.setEnabled(!StringUtil.isEmpty(comboOp4.getSelectedItem().toString()));
    comboCond5.setEnabled(comboColumn5.isEnabled() && !"USER".equals(comboCond5.getSelectedItem()));
    comboValue5.setEnabled(comboColumn5.isEnabled() && !StringUtil.equalAnyOfString((String)comboCond5.getSelectedItem(), new String[] {"USER", "IS NULL", "IS NOT NULL"}));

    cmDeleteDef.setEnabled(comboDefs.getSelectedIndex() > 0);
    cmUpdateDef.setEnabled(comboDefs.getSelectedIndex() > 0);
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmCancel = new pl.mpak.sky.gui.swing.Action();
        cmOk = new pl.mpak.sky.gui.swing.Action();
        cmNewDef = new pl.mpak.sky.gui.swing.Action();
        cmDeleteDef = new pl.mpak.sky.gui.swing.Action();
        cmClearDefs = new pl.mpak.sky.gui.swing.Action();
        cmUpdateDef = new pl.mpak.sky.gui.swing.Action();
        checkFilterOn = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        comboColumn1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        comboCond1 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        comboValue1 = new javax.swing.JComboBox();
        comboOp1 = new javax.swing.JComboBox();
        comboColumn2 = new javax.swing.JComboBox();
        comboCond2 = new javax.swing.JComboBox();
        comboValue2 = new javax.swing.JComboBox();
        comboOp2 = new javax.swing.JComboBox();
        comboColumn3 = new javax.swing.JComboBox();
        comboCond3 = new javax.swing.JComboBox();
        comboValue3 = new javax.swing.JComboBox();
        comboOp3 = new javax.swing.JComboBox();
        comboColumn4 = new javax.swing.JComboBox();
        comboCond4 = new javax.swing.JComboBox();
        comboValue4 = new javax.swing.JComboBox();
        comboOp4 = new javax.swing.JComboBox();
        comboColumn5 = new javax.swing.JComboBox();
        comboCond5 = new javax.swing.JComboBox();
        comboValue5 = new javax.swing.JComboBox();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        comboDefs = new pl.mpak.sky.gui.swing.comp.ComboBox();
        buttonDeleteDef = new javax.swing.JButton();
        buttonNewDef = new javax.swing.JButton();
        buttonUpdateDef = new javax.swing.JButton();
        buttonClear = new javax.swing.JButton();

        cmCancel.setActionCommandKey("cmCancel");
        cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        cmCancel.setText(stringManager.getString("cmCancel-text")); // NOI18N
        cmCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCancelActionPerformed(evt);
            }
        });

        cmOk.setActionCommandKey("cmOk");
        cmOk.setText(stringManager.getString("cmOk-text")); // NOI18N
        cmOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOkActionPerformed(evt);
            }
        });

        cmNewDef.setActionCommandKey("cmNewDef");
        cmNewDef.setText(stringManager.getString("cmNewDef-text")); // NOI18N
        cmNewDef.setTooltip(stringManager.getString("cmNewDef-hint")); // NOI18N
        cmNewDef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmNewDefActionPerformed(evt);
            }
        });

        cmDeleteDef.setActionCommandKey("cmDeleteDef");
        cmDeleteDef.setText(stringManager.getString("cmDeleteDef-text")); // NOI18N
        cmDeleteDef.setTooltip(stringManager.getString("cmDeleteDef-hint")); // NOI18N
        cmDeleteDef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDeleteDefActionPerformed(evt);
            }
        });

        cmClearDefs.setActionCommandKey("cmClearDefs");
        cmClearDefs.setText(stringManager.getString("cmClearDefs-text")); // NOI18N
        cmClearDefs.setTooltip(stringManager.getString("cmClearDefs-hint")); // NOI18N
        cmClearDefs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmClearDefsActionPerformed(evt);
            }
        });

        cmUpdateDef.setActionCommandKey("cmUpdateDef");
        cmUpdateDef.setText(stringManager.getString("cmUpdateDef-text")); // NOI18N
        cmUpdateDef.setTooltip(stringManager.getString("cmUpdateDef-hint")); // NOI18N
        cmUpdateDef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmUpdateDefActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(stringManager.getString("SqlFilterDialog-title")); // NOI18N
        setModal(true);

        checkFilterOn.setText(stringManager.getString("filter-on")); // NOI18N
        checkFilterOn.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkFilterOn.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("column-filter"))); // NOI18N

        jLabel1.setText(stringManager.getString("comb-op-dd")); // NOI18N

        jLabel2.setText(stringManager.getString("column-dd")); // NOI18N

        comboColumn1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboColumn1ItemStateChanged(evt);
            }
        });

        jLabel3.setText(stringManager.getString("condition-dd")); // NOI18N

        comboCond1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboCond1ItemStateChange(evt);
            }
        });

        jLabel4.setText(stringManager.getString("value-dd")); // NOI18N

        comboValue1.setEditable(true);

        comboOp1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "AND", "OR" }));
        comboOp1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboOp1ItemStateChanged(evt);
            }
        });

        comboColumn2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboColumn2ItemStateChanged(evt);
            }
        });

        comboCond2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboCond2ItemStateChange(evt);
            }
        });

        comboValue2.setEditable(true);

        comboOp2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "AND", "OR" }));
        comboOp2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboOp2ItemStateChanged(evt);
            }
        });

        comboColumn3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboColumn3ItemStateChanged(evt);
            }
        });

        comboCond3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboCond3ItemStateChange(evt);
            }
        });

        comboValue3.setEditable(true);

        comboOp3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "AND", "OR" }));
        comboOp3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboOp3ItemStateChanged(evt);
            }
        });

        comboColumn4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboColumn4ItemStateChanged(evt);
            }
        });

        comboCond4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboCond4ItemStateChange(evt);
            }
        });

        comboValue4.setEditable(true);

        comboOp4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "AND", "OR" }));
        comboOp4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboOp4ItemStateChanged(evt);
            }
        });

        comboColumn5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboColumn5ItemStateChanged(evt);
            }
        });

        comboCond5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboCond5ItemStateChange(evt);
            }
        });

        comboValue5.setEditable(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(comboColumn1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboCond1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboValue1, 0, 236, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel2)
                        .addGap(103, 103, 103)
                        .addComponent(jLabel3)
                        .addGap(35, 35, 35)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(comboOp1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboColumn2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboCond2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboValue2, 0, 236, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(comboOp2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboColumn3, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboCond3, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboValue3, 0, 236, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(comboOp3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboColumn4, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboCond4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboValue4, 0, 236, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(comboOp4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboColumn5, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboCond5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboValue5, 0, 236, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboColumn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboCond1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboValue1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboOp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboColumn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboCond2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboValue2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboOp2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboColumn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboCond3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboValue3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboOp3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboColumn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboCond4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboValue4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboOp4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboColumn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboCond5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboValue5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonOk.setAction(cmOk);
        buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));

        buttonCancel.setAction(cmCancel);
        buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(stringManager.getString("saved-definitions"))); // NOI18N

        comboDefs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboDefsItemStateChanged(evt);
            }
        });

        buttonDeleteDef.setAction(cmDeleteDef);
        buttonDeleteDef.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonDeleteDef.setPreferredSize(new java.awt.Dimension(75, 23));

        buttonNewDef.setAction(cmNewDef);
        buttonNewDef.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonNewDef.setPreferredSize(new java.awt.Dimension(75, 23));

        buttonUpdateDef.setAction(cmUpdateDef);
        buttonUpdateDef.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonUpdateDef.setPreferredSize(new java.awt.Dimension(75, 23));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(comboDefs, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonNewDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonUpdateDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDeleteDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonDeleteDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonUpdateDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonNewDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboDefs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        buttonClear.setAction(cmClearDefs);
        buttonClear.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonClear.setPreferredSize(new java.awt.Dimension(75, 23));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkFilterOn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 441, Short.MAX_VALUE)
                        .addComponent(buttonClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkFilterOn)
                    .addComponent(buttonClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void comboOp4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboOp4ItemStateChanged
  updateEnableControls();
}//GEN-LAST:event_comboOp4ItemStateChanged

private void comboOp3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboOp3ItemStateChanged
  updateEnableControls();
}//GEN-LAST:event_comboOp3ItemStateChanged

private void comboOp2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboOp2ItemStateChanged
  updateEnableControls();
}//GEN-LAST:event_comboOp2ItemStateChanged

private void comboOp1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboOp1ItemStateChanged
  updateEnableControls();
}//GEN-LAST:event_comboOp1ItemStateChanged

private void comboColumn5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboColumn5ItemStateChanged
  comboCond5.setModel(new DefaultComboBoxModel(SqlFilterConsts.getConditionList(((SqlFilterDefComponent)evt.getItem()).getConditions())));
  comboValue5.setModel(new DefaultComboBoxModel(((SqlFilterDefComponent)evt.getItem()).getValueList()));
  updateEnableControls();
}//GEN-LAST:event_comboColumn5ItemStateChanged

private void comboColumn4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboColumn4ItemStateChanged
  comboCond4.setModel(new DefaultComboBoxModel(SqlFilterConsts.getConditionList(((SqlFilterDefComponent)evt.getItem()).getConditions())));
  comboValue4.setModel(new DefaultComboBoxModel(((SqlFilterDefComponent)evt.getItem()).getValueList()));
  updateEnableControls();
}//GEN-LAST:event_comboColumn4ItemStateChanged

private void comboColumn3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboColumn3ItemStateChanged
  comboCond3.setModel(new DefaultComboBoxModel(SqlFilterConsts.getConditionList(((SqlFilterDefComponent)evt.getItem()).getConditions())));
  comboValue3.setModel(new DefaultComboBoxModel(((SqlFilterDefComponent)evt.getItem()).getValueList()));
  updateEnableControls();
}//GEN-LAST:event_comboColumn3ItemStateChanged

private void comboColumn2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboColumn2ItemStateChanged
  comboCond2.setModel(new DefaultComboBoxModel(SqlFilterConsts.getConditionList(((SqlFilterDefComponent)evt.getItem()).getConditions())));
  comboValue2.setModel(new DefaultComboBoxModel(((SqlFilterDefComponent)evt.getItem()).getValueList()));
  updateEnableControls();
}//GEN-LAST:event_comboColumn2ItemStateChanged

private void comboColumn1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboColumn1ItemStateChanged
  comboCond1.setModel(new DefaultComboBoxModel(SqlFilterConsts.getConditionList(((SqlFilterDefComponent)evt.getItem()).getConditions())));
  comboValue1.setModel(new DefaultComboBoxModel(((SqlFilterDefComponent)evt.getItem()).getValueList()));
  updateEnableControls();
}//GEN-LAST:event_comboColumn1ItemStateChanged

  private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOkActionPerformed
  modalResult = ModalResult.OK;
  dialogToFilter(sqlFilter.getFilterComponent());
  sqlFilter.storeSettings();
  dispose();
}//GEN-LAST:event_cmOkActionPerformed

private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCancelActionPerformed
  modalResult = ModalResult.CANCEL;
  dispose();
}//GEN-LAST:event_cmCancelActionPerformed

private void cmClearDefsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmClearDefsActionPerformed
  disableUpdatedControls = true;
  try {
    comboOp1.setSelectedItem("");
    comboColumn1.setSelectedItem(sqlFilter.getDefinition().getByColumn(null));
    comboCond1.setSelectedItem(null);
    comboValue1.setSelectedItem(null);
    comboOp2.setSelectedItem("");
    comboColumn2.setSelectedItem(sqlFilter.getDefinition().getByColumn(null));
    comboCond2.setSelectedItem(null);
    comboValue2.setSelectedItem(null);
    comboOp3.setSelectedItem("");
    comboColumn3.setSelectedItem(sqlFilter.getDefinition().getByColumn(null));
    comboCond3.setSelectedItem(null);
    comboValue3.setSelectedItem(null);
    comboOp4.setSelectedItem("");
    comboColumn4.setSelectedItem(sqlFilter.getDefinition().getByColumn(null));
    comboCond4.setSelectedItem(null);
    comboValue4.setSelectedItem(null);
    comboColumn5.setSelectedItem(sqlFilter.getDefinition().getByColumn(null));
    comboCond5.setSelectedItem(null);
    comboValue5.setSelectedItem(null);
  }
  finally {
    disableUpdatedControls = false;
    updateEnableControls();
  }
}//GEN-LAST:event_cmClearDefsActionPerformed

private void cmNewDefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewDefActionPerformed
  String name = JOptionPane.showInputDialog(this, stringManager.getString("definition-name-dd"));
  if (name != null) {
    DefinedFilterComponent def = new DefinedFilterComponent(name);
    filterToDialog(def.getFilterComponent());
    sqlFilter.getFilterComponentList().add(def);
    sqlFilter.storeSettings(def, sqlFilter.getFilterComponentList().size() -1);
    comboDefs.setModel(new DefaultComboBoxModel(sqlFilter.getFilterComponentList().toArray()));
    comboDefs.setSelectedIndex(sqlFilter.getFilterComponentList().size() -1);
  }
}//GEN-LAST:event_cmNewDefActionPerformed

private void cmDeleteDefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteDefActionPerformed
  if (MessageBox.show(this, stringManager.getString("deleting"), stringManager.getString("SqlFilterDialog-del-def-q"), ModalResult.YESNO) == ModalResult.YES) {
    sqlFilter.getFilterComponentList().remove(comboDefs.getSelectedIndex());
    comboDefs.setModel(new DefaultComboBoxModel(sqlFilter.getFilterComponentList().toArray()));
    comboDefs.setSelectedIndex(0);
    sqlFilter.storeSettings(null, -1);
  }
}//GEN-LAST:event_cmDeleteDefActionPerformed

private void comboDefsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboDefsItemStateChanged
  filterToDialog(((DefinedFilterComponent)comboDefs.getSelectedItem()).getFilterComponent());
  updateEnableControls();
}//GEN-LAST:event_comboDefsItemStateChanged

private void cmUpdateDefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUpdateDefActionPerformed
  DefinedFilterComponent def = (DefinedFilterComponent)comboDefs.getSelectedItem();
  dialogToFilter(def.getFilterComponent());
  sqlFilter.storeSettings(def, sqlFilter.getFilterComponentList().size() -1);
}//GEN-LAST:event_cmUpdateDefActionPerformed

  private void comboCond1ItemStateChange(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboCond1ItemStateChange
    updateEnableControls();
  }//GEN-LAST:event_comboCond1ItemStateChange

  private void comboCond2ItemStateChange(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboCond2ItemStateChange
    updateEnableControls();
  }//GEN-LAST:event_comboCond2ItemStateChange

  private void comboCond3ItemStateChange(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboCond3ItemStateChange
    updateEnableControls();
  }//GEN-LAST:event_comboCond3ItemStateChange

  private void comboCond4ItemStateChange(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboCond4ItemStateChange
    updateEnableControls();
  }//GEN-LAST:event_comboCond4ItemStateChange

  private void comboCond5ItemStateChange(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboCond5ItemStateChange
    updateEnableControls();
  }//GEN-LAST:event_comboCond5ItemStateChange
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonDeleteDef;
    private javax.swing.JButton buttonNewDef;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonUpdateDef;
    private javax.swing.JCheckBox checkFilterOn;
    private pl.mpak.sky.gui.swing.Action cmCancel;
    private pl.mpak.sky.gui.swing.Action cmClearDefs;
    private pl.mpak.sky.gui.swing.Action cmDeleteDef;
    private pl.mpak.sky.gui.swing.Action cmNewDef;
    private pl.mpak.sky.gui.swing.Action cmOk;
    private pl.mpak.sky.gui.swing.Action cmUpdateDef;
    private javax.swing.JComboBox comboColumn1;
    private javax.swing.JComboBox comboColumn2;
    private javax.swing.JComboBox comboColumn3;
    private javax.swing.JComboBox comboColumn4;
    private javax.swing.JComboBox comboColumn5;
    private javax.swing.JComboBox comboCond1;
    private javax.swing.JComboBox comboCond2;
    private javax.swing.JComboBox comboCond3;
    private javax.swing.JComboBox comboCond4;
    private javax.swing.JComboBox comboCond5;
    private pl.mpak.sky.gui.swing.comp.ComboBox comboDefs;
    private javax.swing.JComboBox comboOp1;
    private javax.swing.JComboBox comboOp2;
    private javax.swing.JComboBox comboOp3;
    private javax.swing.JComboBox comboOp4;
    private javax.swing.JComboBox comboValue1;
    private javax.swing.JComboBox comboValue2;
    private javax.swing.JComboBox comboValue3;
    private javax.swing.JComboBox comboValue4;
    private javax.swing.JComboBox comboValue5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
  
}
