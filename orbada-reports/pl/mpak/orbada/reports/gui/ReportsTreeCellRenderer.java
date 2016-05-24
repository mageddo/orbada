/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.reports.gui;

import pl.mpak.orbada.reports.gui.nodes.RootGroupTreeNodeInfo;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import pl.mpak.orbada.reports.gui.nodes.ReportGroupTreeNodeInfo;
import pl.mpak.orbada.reports.gui.nodes.ReportTreeNodeInfo;

/**
 *
 * @author akaluza
 */
public class ReportsTreeCellRenderer extends DefaultTreeCellRenderer {

  private Icon rootIcon = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/root_folder.gif");
  private Icon goupIcon = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/report_folder.gif");
  private Icon reportIcon = pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/reports.gif");
  
  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
    if (node.getUserObject() instanceof RootGroupTreeNodeInfo) {
      setIcon(rootIcon);
    } 
    else if (node.getUserObject() instanceof ReportGroupTreeNodeInfo) {
      setIcon(goupIcon);
      setToolTipText(((ReportGroupTreeNodeInfo)node.getUserObject()).getTooltip());
    } 
    else if (node.getUserObject() instanceof ReportTreeNodeInfo) {
      setIcon(reportIcon);
      setToolTipText(((ReportTreeNodeInfo)node.getUserObject()).getTooltip());
    } 
    else {
      setToolTipText(null); //no tool tip
    } 
    return this;
  }

}
