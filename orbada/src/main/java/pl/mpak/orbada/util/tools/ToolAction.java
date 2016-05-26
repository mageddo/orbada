/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.util.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.Tool;
import pl.mpak.sky.gui.swing.Action;

/**
 *
 * @author akaluza
 */
public class ToolAction extends Action {

  private Tool tool;
  private Object[] args;
  
  public ToolAction(Tool tool) {
    super();
    this.tool = tool;
    setText(tool.getTitle());
    setTooltip(tool.getTitle());
    setSmallIcon(tool.getIcon());
    addActionListener(createActionListener());
  }
  
  public ToolAction(Tool tool, Object[] args) {
    this(tool);
    this.args = args;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        tool.exec(Application.get().getMainFrame().getActiveDatabase(), args);
      }
    };
  }

}
