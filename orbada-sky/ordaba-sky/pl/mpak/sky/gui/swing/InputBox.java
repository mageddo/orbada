package pl.mpak.sky.gui.swing;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.mr.ModalResult;


/**
 * @author akaluza
 * <p>Wycofana na rzecz JOptionPane.showInputDialog
 */
@Deprecated
public class InputBox extends javax.swing.JDialog {
  private static final long serialVersionUID = -7147570058827394427L;

  private int modalResult = ModalResult.NONE;
  private String title;
  private String input;

  public InputBox(java.awt.Frame parent, String title) {
    this(parent, title, ""); //$NON-NLS-1$
  }

  public InputBox(java.awt.Frame parent, String title, String input) {
    super(parent);
    this.title = title;
    this.input = input;
    initComponents();
    init();
  }

  public static String show(java.awt.Frame parent, String title) {
    return show(parent, title, ""); //$NON-NLS-1$
  }

  public static String show(java.awt.Frame parent, String title, String input) {
    InputBox dialog = new InputBox(parent, title, input);
    dialog.setVisible(true);
    if (dialog.modalResult == ModalResult.OK) {
      return dialog.textInput.getText();
    } else {
      return null;
    }
  }

  private void init() {
    labelTitle.setText(title);
    textInput.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        textChanged();
      }
      public void removeUpdate(DocumentEvent e) {
        textChanged();
      }
      public void changedUpdate(DocumentEvent e) {
      }
    });
    textInput.setText(input);

    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmCancel.getShortCut(), cmCancel); //$NON-NLS-1$
    getRootPane().getActionMap().put(cmCancel, cmCancel); //$NON-NLS-1$

    pack();
    pl.mpak.sky.gui.swing.SwingUtil.centerWithinScreen(this);
  }

  private void textChanged() {
    try {
      cmOk.setEnabled(textInput.getText().length() > 0);
    } catch (NumberFormatException ex) {
      cmOk.setEnabled(false);
    }
  }

  private void initComponents() {

    cmOk = new pl.mpak.sky.gui.swing.Action();
    cmCancel = new pl.mpak.sky.gui.swing.Action();
    jPanel1 = new javax.swing.JPanel();
    labelTitle = new javax.swing.JLabel();
    textInput = new pl.mpak.sky.gui.swing.comp.TextField();
    jPanel2 = new javax.swing.JPanel();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();

    cmOk.setEnabled(false);
    cmOk.setText(Messages.getString("InputBox.ok")); //$NON-NLS-1$
    cmOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmOkActionPerformed(evt);
      }
    });

    cmCancel.setShortCut(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmCancel.setText(Messages.getString("InputBox.cancel")); //$NON-NLS-1$
    cmCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCancelActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(Messages.getString("InputBox.go-to-line")); //$NON-NLS-1$
    setModal(true);

    labelTitle.setText(Messages.getString("InputBox.input")); //$NON-NLS-1$
    jPanel1.add(labelTitle);

    textInput.setPreferredSize(new java.awt.Dimension(150, 22));
    jPanel1.add(textInput);

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    buttonOk.setAction(cmOk);
    buttonOk.setPreferredSize(new java.awt.Dimension(75, 23));
    jPanel2.add(buttonOk);

    buttonCancel.setAction(cmCancel);
    buttonCancel.setPreferredSize(new java.awt.Dimension(75, 23));
    jPanel2.add(buttonCancel);

    getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

  }

  private void cmCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmCancelActionPerformed
    modalResult = ModalResult.CANCEL;
    dispose();
  }

  private void cmOkActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmOkActionPerformed
    modalResult = ModalResult.OK;
    dispose();
  }

  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOk;
  private pl.mpak.sky.gui.swing.Action cmCancel;
  private pl.mpak.sky.gui.swing.Action cmOk;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JLabel labelTitle;
  private pl.mpak.sky.gui.swing.comp.TextField textInput;

}
