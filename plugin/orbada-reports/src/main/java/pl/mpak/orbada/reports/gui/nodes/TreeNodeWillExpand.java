/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.gui.nodes;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author akaluza
 */
public class TreeNodeWillExpand extends DefaultMutableTreeNode {

  public TreeNodeWillExpand() {
    super(java.util.ResourceBundle.getBundle("pl/mpak/orbada/orbada-reports").getString("waiting_for_data"));
  }

}
