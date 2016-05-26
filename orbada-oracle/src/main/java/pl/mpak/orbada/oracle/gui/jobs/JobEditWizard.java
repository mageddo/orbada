package pl.mpak.orbada.oracle.gui.jobs;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTextField;

import pl.mpak.orbada.gui.comps.OrbadaSyntaxTextArea;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.script.SimpleSQLScript;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class JobEditWizard extends SqlCodeWizardPanel {

  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  private Database database;
  private Long job;
  private ArrayList<Interval> intervalList;
  
  private class Interval {

    public String title;
    public String sqlCode;

    public Interval(String title, String sqlCode) {
      this.title = title;
      this.sqlCode = sqlCode;
    }

    public String getSqlCode() {
      return sqlCode;
    }

    public String getTitle() {
      return title;
    }

    @Override
    public String toString() {
      return sqlCode;
    }
  }
  
    /** Creates new form JobEditWizard */
  public JobEditWizard(Database database, Long job) {
    this.database = database;
    this.job = job;
    intervalList = new ArrayList<Interval>();
    initComponents();
    init();
  }

  private void init() {
    intervalList.add(new Interval(stringManager.getString("immediately"), ""));
    intervalList.add(new Interval(stringManager.getString("per-minute"), "sysdate +1/60/24"));
    intervalList.add(new Interval(stringManager.getString("half-an-hour"), "sysdate +0.5/24"));
    intervalList.add(new Interval(stringManager.getString("per-hour"), "sysdate +1/24"));
    intervalList.add(new Interval(stringManager.getString("for-2-weeks"), "sysdate +2/24"));
    intervalList.add(new Interval(stringManager.getString("o-clock"), "trunc(sysdate +1/24, 'hh')"));
    intervalList.add(new Interval(stringManager.getString("15-minutes-after-the-hour"), "trunc(sysdate +1/24, 'hh') +15/60/24"));
    intervalList.add(new Interval(stringManager.getString("on-the-1st-night"), "trunc(sysdate) +1/24"));
    intervalList.add(new Interval(stringManager.getString("about-6-am"), "trunc(sysdate) +6/24"));
    intervalList.add(new Interval(stringManager.getString("the-next-day-at-3rd-10"), "trunc(sysdate)+1+3/24 +10/60/24"));
    intervalList.add(new Interval(stringManager.getString("1st-day-of-the-month-on-the-1st-night"), "add_months(trunc(sysdate,'MM'),1) +1/24"));
    DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Interval) {
          setText(((Interval)value).title);
        }
        return this;
      }
    };
    comboNextDate.setModel(new DefaultComboBoxModel(intervalList.toArray()));
    comboNextDate.setRenderer(renderer);
    comboInterval.setModel(new DefaultComboBoxModel(intervalList.toArray()));
    comboInterval.setRenderer(renderer);
  }
  
  public void wizardShow() {
    textWhat.setDatabase(database);
    if (job != null) {
      Query query = database.createQuery();
      try {
        if ("true".equalsIgnoreCase(database.getUserProperties().getProperty("dba-role"))) {
          query.setSqlText("select * from dba_jobs where job = :JOB");
        }
        else {
          query.setSqlText("select INTERVAL, WHAT, INSTANCE from user_jobs where job = :JOB");
        }
        query.paramByName("JOB").setLong(job);
        query.open();
        if (!query.fieldByName("INSTANCE").isNull()) {
          if (query.fieldByName("INSTANCE").getLong() != 0) {
            textInstance.setText(query.fieldByName("INSTANCE").getString());
          }
        }
        textWhat.setText(query.fieldByName("WHAT").getString());
        ((JTextField)comboNextDate.getEditor().getEditorComponent()).setText(query.fieldByName("INTERVAL").getString());
        ((JTextField)comboInterval.getEditor().getEditorComponent()).setText(query.fieldByName("INTERVAL").getString());
      }
      catch (Exception ex) {
        MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
      finally {
        query.close();
      }
    }
  }
  
  public String getDialogTitle() {
    return (job == null ? stringManager.getString("new-task") : stringManager.getString("edit-task"));
  }
  
  public String getTabTitle() {
    return stringManager.getString("JobEditWizard-title");
  }
  
  public String getSqlCode() {
    String nextDate = ((JTextField)comboNextDate.getEditor().getEditorComponent()).getText();
    String interval = StringUtil.replaceString(((JTextField)comboInterval.getEditor().getEditorComponent()).getText(), "'", "''");
    String what = StringUtil.replaceString(textWhat.getText(), "'", "''");
    StringBuffer sb = new StringBuffer();
    if (job == null) {
      sb.append("DECLARE\n");
      sb.append("  JOB NUMBER;\n");
      sb.append("BEGIN\n");
      sb.append("  DBMS_JOB.SUBMIT(\n");
      sb.append("    JOB => JOB,\n");
      sb.append("    WHAT => '" +what +"'");
      if (!"".equals(nextDate)) {
        sb.append(",\n    NEXT_DATE => " +nextDate);
      }
      if (!"".equals(interval)) {
        sb.append(",\n    INTERVAL => '" +interval +"'");
      }
      if (!"".equals(textInstance.getText())) {
        sb.append(",\n    INSTANCE => " +textInstance.getText());
        if (checkForce.isSelected()) {
          sb.append(",\n    FORCE => TRUE");
        }
      }
      sb.append("\n  );\n");
      sb.append("  DBMS_OUTPUT.PUT_LINE(JOB);\n");
      sb.append("END;\n/\n");
    }
    else {
      sb.append("BEGIN\n");
      sb.append("  DBMS_JOB.CHANGE(\n");
      sb.append("    JOB => " +job +",\n");
      sb.append("    WHAT => '" +what +"',\n");
      sb.append("    NEXT_DATE => " +("".equals(nextDate) ? "SYSDATE" : nextDate) +",\n");
      sb.append("    INTERVAL => '" +interval +"'");
      if (!"".equals(textInstance.getText())) {
        sb.append(",\n    INSTANCE => " +textInstance.getText());
        if (checkForce.isSelected()) {
          sb.append(",\n    FORCE => TRUE");
        }
      }
      sb.append("\n  );\n");
      sb.append("END;\n/\n");
    }
    if (checkCommit.isSelected()) {
      sb.append("COMMIT\n/\n");
    }
    return sb.toString();
  }
  
  public boolean execute() {
    SimpleSQLScript script = new SimpleSQLScript(database);
    if (!script.executeScript(getSqlCode())) {
      MessageBox.show(this, stringManager.getString("error"), script.getErrors(), ModalResult.OK, MessageBox.ERROR);
      return false;
    }
    return true;
  }
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmSheduler = new pl.mpak.sky.gui.swing.Action();
    jLabel1 = new javax.swing.JLabel();
    comboNextDate = new pl.mpak.sky.gui.swing.comp.ComboBox();
    buttonSzeduler = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jLabel2 = new javax.swing.JLabel();
    comboInterval = new pl.mpak.sky.gui.swing.comp.ComboBox();
    buttonInterval = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jLabel3 = new javax.swing.JLabel();
    checkCommit = new javax.swing.JCheckBox();
    textWhat = new OrbadaSyntaxTextArea();
    jLabel4 = new javax.swing.JLabel();
    textInstance = new pl.mpak.sky.gui.swing.comp.TextField();
    checkForce = new javax.swing.JCheckBox();

    cmSheduler.setActionCommandKey("cmSheduler");
    cmSheduler.setEnabled(false);
    cmSheduler.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/shedule.gif")); // NOI18N
    cmSheduler.setText(stringManager.getString("cmSheduler-text")); // NOI18N

    setPreferredSize(new java.awt.Dimension(400, 300));

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText(stringManager.getString("execution-time-dd")); // NOI18N

    comboNextDate.setEditable(true);

    buttonSzeduler.setAction(cmSheduler);
    buttonSzeduler.setPreferredSize(new java.awt.Dimension(24, 24));

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("interval-time-dd")); // NOI18N

    comboInterval.setEditable(true);

    buttonInterval.setAction(cmSheduler);
    buttonInterval.setPreferredSize(new java.awt.Dimension(24, 24));

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("pl-sql-command-dd")); // NOI18N

    checkCommit.setSelected(true);
    checkCommit.setText(stringManager.getString("commit-changes")); // NOI18N

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("instance-no-dd")); // NOI18N

    checkForce.setText(stringManager.getString("check-if-runing")); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(textWhat, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(comboInterval, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(comboNextDate, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonSzeduler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(checkCommit)
          .addGroup(layout.createSequentialGroup()
            .addComponent(textInstance, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(checkForce)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonSzeduler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel1)
          .addComponent(comboNextDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(comboInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel3)
          .addComponent(textWhat, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textInstance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkForce))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkCommit)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonInterval;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonSzeduler;
  private javax.swing.JCheckBox checkCommit;
  private javax.swing.JCheckBox checkForce;
  private pl.mpak.sky.gui.swing.Action cmSheduler;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboInterval;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboNextDate;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private pl.mpak.sky.gui.swing.comp.TextField textInstance;
  private OrbadaSyntaxTextArea textWhat;
  // End of variables declaration//GEN-END:variables

}
