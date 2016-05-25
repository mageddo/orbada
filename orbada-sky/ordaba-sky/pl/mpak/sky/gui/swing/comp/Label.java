package pl.mpak.sky.gui.swing.comp;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;

import pl.mpak.sky.gui.swing.SwingUtil;

public class Label extends JLabel {

  private static final long serialVersionUID = -1819424892322178709L;

  public Label() {
    super();
  }

  public Label(String text) {
    super(text);
  }

  public Label(String text, Component labelFor) {
    super(text);
    setLabelFor(labelFor);
  }

  public Label(Icon image) {
    super(image);
  }

  public Label(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
  }

  public Label(Icon image, int horizontalAlignment) {
    super(image, horizontalAlignment);
  }

  public Label(String text, Icon icon, int horizontalAlignment) {
    super(text, icon, horizontalAlignment);
  }

  public void setText(String text) {
    super.setText(SwingUtil.setButtonText(this, text));
  }

}
