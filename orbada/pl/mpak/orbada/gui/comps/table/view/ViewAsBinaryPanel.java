/*
 * ViewAsStringPanel.java
 *
 * Created on 26 listopad 2007, 18:53
 */

package pl.mpak.orbada.gui.comps.table.view;

import java.io.Closeable;
import java.io.IOException;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author  akaluza
 */
public class ViewAsBinaryPanel extends javax.swing.JPanel implements Closeable {
  
  private Variant value;
  
  /** Creates new form ViewAsStringPanel 
   * @param value 
   */
  public ViewAsBinaryPanel(Variant value) {
    this.value = value;
    initComponents();
    init();
  }
  
  private void init() {
    if (value != null) {
      convert();
    }
  }
  
  private void convert() {
    StringBuilder sb = new StringBuilder();
    try {
      byte[] buffer = null;
      if (((Variant)value).getValueType() == VariantType.varBinary) {
        buffer = value.getBinary();
      }
      else {
        buffer = value.getString().getBytes();
      }
      for (int i=0; i<buffer.length; i++) {
        if (buffer[i] < 32) {
          sb.append((char)0);
        }
        else {
          sb.append((char)buffer[i]);
        }
        if ((i +1) % 80 == 0) {
          sb.append('\n');
        }
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    textView.setText(sb.toString());
    textView.setCaretPosition(0);
  }
  
  public void close() throws IOException {
    
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    textView = new javax.swing.JTextArea();

    setLayout(new java.awt.BorderLayout());

    textView.setColumns(20);
    textView.setEditable(false);
    textView.setFont(new java.awt.Font("Courier New", 0, 12));
    textView.setRows(5);
    jScrollPane1.setViewportView(textView);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTextArea textView;
  // End of variables declaration//GEN-END:variables
  
}
