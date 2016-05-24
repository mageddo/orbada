/*
 * ViewAsStringPanel.java
 *
 * Created on 26 listopad 2007, 18:53
 */

package pl.mpak.orbada.gui.comps.table.view;

import java.awt.Image;
import java.io.Closeable;
import java.io.IOException;
import javax.swing.ImageIcon;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantType;

/**
 *
 * @author  akaluza
 */
public class ViewAsPicturePanel extends javax.swing.JPanel implements Closeable {
  
  private Variant value;
  
  /** Creates new form ViewAsStringPanel 
   * @param value 
   */
  public ViewAsPicturePanel(Variant value) {
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
    try {
      if (((Variant)value).getValueType() == VariantType.varBinary) {
        byte[] buffer = value.getBinary();
        panelIcon.setIcon(new ImageIcon(buffer));
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  public void close() throws IOException {
    
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    panelIcon = new pl.mpak.sky.gui.swing.comp.IconPanel();

    setLayout(new java.awt.BorderLayout());

    jScrollPane1.setViewportView(panelIcon);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private pl.mpak.sky.gui.swing.comp.IconPanel panelIcon;
  // End of variables declaration//GEN-END:variables
  
}
