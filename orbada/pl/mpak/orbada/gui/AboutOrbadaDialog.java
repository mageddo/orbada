/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AboutOrbadaDialog.java
 *
 * Created on 2009-05-12, 18:19:55
 */

package pl.mpak.orbada.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import pl.mpak.g2.G2Util;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class AboutOrbadaDialog extends javax.swing.JDialog {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public final static String[] orbadaTranlateHelp = {
    "Piotr R�nicki"};
  public final static String[] orbadaProgrammingSupport = {
    "Piotr R�nicki"};
  public final static String[] orbadaTestAndSuggestion = {
    "Piotr R�nicki",
    "Tomasz Michalski",
    "Micha� Kurczabi�ski",
    stringManager.getString("about-dialog-test-andothers")};
  public final static String[] orbadaSplashScreens = {
    "Robert Proksa"};

  private BufferedImage bufferImage;

  /** Creates new form AboutOrbadaDialog */
  public AboutOrbadaDialog() {
    super(SwingUtil.getRootFrame(), true);
    initComponents();
    init();
  }

  public static void showDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        AboutOrbadaDialog dialog = new AboutOrbadaDialog();
        dialog.setVisible(true);
      }
    });
  }

  private void init() {
    bufferImage = G2Util.createImage(getClass().getResource("/res/splashscreen.jpg"));
    Graphics2D g2 = (Graphics2D)bufferImage.getGraphics();
    Application.drawSplashInfo(g2, new Rectangle(bufferImage.getWidth(), bufferImage.getHeight()));
    labelSplash.setIcon(new ImageIcon(bufferImage));
    labelSplash.setPreferredSize(new Dimension(bufferImage.getWidth(), bufferImage.getHeight()));

    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append(String.format(stringManager.getString("AboutOrbadaDialog-version"), new Object[] {Consts.orbadaSubname, Consts.orbadaVersion}));
    sb.append(String.format(stringManager.getString("AboutOrbadaDialog-author"), new Object[] {Consts.orbadaAutor, Consts.orbadaYears}));
    sb.append(stringManager.getString("AboutOrbadaDialog-project"));
    sb.append("<hr>");
    sb.append(String.format(stringManager.getString("AboutOrbadaDialog-orbada-id"), new Object[] {Application.get().getOrbadaString("unique-id")}));
    sb.append(String.format(stringManager.getString("AboutOrbadaDialog-update"), new Object[] {Consts.orbadaReleaseDate}));
    labelInfo.setText(sb.toString());

    labelTransHelps.setText("<html>" +StringUtil.toString(orbadaTranlateHelp, "<br>"));
    labelProgramSupport.setText("<html>" +StringUtil.toString(orbadaProgrammingSupport, "<br>"));
    labelTestSugestion.setText("<html>" +StringUtil.toString(orbadaTestAndSuggestion, "<br>"));
    labelSplashScreens.setText("<html>" +StringUtil.toString(orbadaSplashScreens, "<br>"));
    
    pack();
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmClose);
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonCancel});
    SwingUtil.centerWithinScreen(this);
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmClose = new pl.mpak.sky.gui.swing.Action();
        jPanel1 = new javax.swing.JPanel();
        tabInfo = new javax.swing.JTabbedPane();
        labelSplash = new javax.swing.JLabel();
        panelThanks = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labelTransHelps = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        labelProgramSupport = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTestSugestion = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelSplashScreens = new javax.swing.JLabel();
        labelInfo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        buttonCancel = new javax.swing.JButton();

        cmClose.setActionCommandKey("cmCancel");
        cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        cmClose.setText(stringManager.getString("cmClose-text")); // NOI18N
        cmClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCloseActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(stringManager.getString("AboutOrbadaDialog-title")); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout());

        jPanel1.setLayout(new pl.mpak.sky.gui.swing.VerticalFlowLayout());

        tabInfo.setFocusable(false);

        labelSplash.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSplash.setPreferredSize(new java.awt.Dimension(500, 300));
        tabInfo.addTab(stringManager.getString("tab-orbada"), labelSplash); // NOI18N

        jScrollPane1.setBorder(null);

        jPanel3.setBackground(javax.swing.UIManager.getDefaults().getColor("window"));

        jLabel1.setText(stringManager.getString("about-dialog-translate-help-h-dd")); // NOI18N

        labelTransHelps.setText(" ");

        jLabel2.setText(stringManager.getString("about-dialog-programmer-support-h-dd")); // NOI18N

        labelProgramSupport.setText(" ");

        jLabel3.setText(stringManager.getString("about-dialog-tests-sugestion-h-dd")); // NOI18N

        labelTestSugestion.setText(" ");

        jLabel4.setText(stringManager.getString("about-dialog-splashscreen-h-dd")); // NOI18N

        labelSplashScreens.setText(" ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTransHelps)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelProgramSupport)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTestSugestion)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSplashScreens))
                .addContainerGap(239, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTransHelps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelProgramSupport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTestSugestion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSplashScreens)
                .addContainerGap(123, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel3);

        javax.swing.GroupLayout panelThanksLayout = new javax.swing.GroupLayout(panelThanks);
        panelThanks.setLayout(panelThanksLayout);
        panelThanksLayout.setHorizontalGroup(
            panelThanksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        panelThanksLayout.setVerticalGroup(
            panelThanksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        tabInfo.addTab(stringManager.getString("tab-thanks"), panelThanks); // NOI18N

        jPanel1.add(tabInfo);

        labelInfo.setText("jLabel1");
        jPanel1.add(labelInfo);

        buttonCancel.setAction(cmClose);
        buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));
        jPanel2.add(buttonCancel);

        jPanel1.add(jPanel2);

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
      dispose();
}//GEN-LAST:event_cmCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private pl.mpak.sky.gui.swing.Action cmClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelProgramSupport;
    private javax.swing.JLabel labelSplash;
    private javax.swing.JLabel labelSplashScreens;
    private javax.swing.JLabel labelTestSugestion;
    private javax.swing.JLabel labelTransHelps;
    private javax.swing.JPanel panelThanks;
    private javax.swing.JTabbedPane tabInfo;
    // End of variables declaration//GEN-END:variables

}
