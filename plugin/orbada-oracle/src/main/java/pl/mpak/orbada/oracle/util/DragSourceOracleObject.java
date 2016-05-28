/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.util;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import javax.swing.JList;

/**
 *
 * @author akaluza
 */
public class DragSourceOracleObject implements DragSourceListener, DragGestureListener {

  private JList list;
  
  public DragSourceOracleObject(JList list) {
    this.list = list;
  }
  
  public void dragEnter(DragSourceDragEvent dsde) {
  }

  public void dragOver(DragSourceDragEvent dsde) {
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
  }

  public void dragExit(DragSourceEvent dse) {
  }

  public void dragDropEnd(DragSourceDropEvent dsde) {
  }

  public void dragGestureRecognized(DragGestureEvent dge) {
    if (list.getSelectedIndex() >= 0) {
      Object o = list.getModel().getElementAt(list.getSelectedIndex());
      if (o instanceof OracleObject) {
        dge.startDrag(null, (OracleObject)o, this);
      }
    }
  }

}
