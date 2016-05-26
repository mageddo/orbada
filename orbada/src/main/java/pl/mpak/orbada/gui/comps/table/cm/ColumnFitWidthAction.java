/*
 * ColumnFitWidthAction.java
 *
 * Created on 2007-10-08, 20:05:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui.comps.table.cm;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import pl.mpak.orbada.Consts;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class ColumnFitWidthAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private JTable table;
  
  public ColumnFitWidthAction(JTable table) {
    super();
    this.table = table;
    setText(stringManager.getString("ColumnFitWidth-text"));
    setShortCut(KeyEvent.VK_I, KeyEvent.CTRL_MASK);
    setActionCommandKey("cmColumnFitWidth");
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Variant v = new Variant();
        if (table.isShowing()) {
          TableModel tm = table.getModel();
          Graphics g = table.getGraphics();
          FontMetrics fm = g.getFontMetrics();

          for (int c=0; c<table.getColumnCount(); c++) {
            TableColumn tc = table.getColumnModel().getColumn(c);
            int width = fm.stringWidth(((String)tc.getHeaderValue()).toString());
            for (int r=Math.max(table.getSelectedRow(), 0); r<Math.min(tm.getRowCount(), Math.max(table.getSelectedRow(), 0) +50); r++) {
              Object value = tm.getValueAt(r, tc.getModelIndex());
              if (value instanceof Icon || value instanceof Image || value == null) {
                continue;
              }
              if (c >= table.getColumnCount()) {
                break;
              }
              v.setValue(value);
              Component comp = table.getCellRenderer(r, c).getTableCellRendererComponent(table, v.toString(), false, false, r, c);
              int twidth = comp.getWidth();
              if (comp instanceof JLabel) {
                twidth = fm.stringWidth(((JLabel)comp).getText());
              }
              else if (comp instanceof JCheckBox) {
                twidth = fm.stringWidth(((JCheckBox)comp).getText()) +30;
              }
              if (twidth > width) {
                width = twidth;
              }
              if (width > 1000) {
                width = 1000;
              }
            }
            if (c >= table.getColumnCount()) {
              break;
            }
            table.getColumnModel().getColumn(c).setPreferredWidth(width +10);
            table.getColumnModel().getColumn(c).setWidth(width +10);
          }
          table.revalidate();
          table.repaint();
        }
      }
    };
  }
  
}
