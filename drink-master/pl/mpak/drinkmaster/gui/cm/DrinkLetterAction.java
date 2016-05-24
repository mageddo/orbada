/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.drinkmaster.gui.cm;

import java.awt.Color;
import java.awt.event.ActionEvent;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.comp.ToolButton;

/**
 *
 * @author akaluza
 */
public abstract class DrinkLetterAction extends Action {

  private String letter;
  private ToolButton button;
  
  public DrinkLetterAction(String letter, int count, ToolButton button) {
    super();
    setText(letter +" (" +count +")");
    this.letter = letter;
    this.button = button;
    button.setAction(this);
    button.setHideActionText(false);
    button.setPreferredSize(new java.awt.Dimension(50, 19));
    button.setMinimumSize(button.getPreferredSize());
    button.setMaximumSize(button.getPreferredSize());
  }
  
  @Override
  public abstract void actionPerformed(ActionEvent e);

  public String getLetter() {
    return letter;
  }

  public boolean isSelected() {
    return button.isSelected();
  }

  public void setSelected(boolean selected) {
    button.setSelected(selected);
  }

  public ToolButton getButton() {
    return button;
  }

}
