package pl.mpak.sky.gui.swing.comp;

import java.awt.Color;

import javax.swing.JLabel;

public class EventLabel extends JLabel {

  /**
   * @author Pawe³ Rutkowski
   */
  private static final long serialVersionUID = 3488553178092663400L;
  
  public String title = null;
  public String description = null;

  /**
   * Creates a <code>LabelListElement</code> instance with the specified
   * text, description and background color.
   *
   * @param title  The text to be displayed by the label.
   * @param desc  The description displayed as a tooltip when mouse is over the label.
   * @param color  Background color of the label.
   */
  public EventLabel(String title, String desc, Color color) {
    super();
    // TODO Auto-generated constructor stub
    setOpaque(true);
    setBackground(color);
    setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 2));
    setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
    setText(title);
    setToolTipText(desc);
    this.title = title;
    description = desc;
  }
  
}
