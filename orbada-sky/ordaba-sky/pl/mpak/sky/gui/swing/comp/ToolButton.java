package pl.mpak.sky.gui.swing.comp;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import pl.mpak.sky.gui.swing.SwingUtil;

public class ToolButton extends JButton {
  private static final long serialVersionUID = -2008217162015344774L;
  
  public static int SIZE = 26;

  public ToolButton() {
    this(null, null);
  }
  
  public ToolButton(Action a) {
    this(null, null);
    setAction(a);
  }
  
  public ToolButton(String text, Icon icon) {
    super(text, icon);
    setPreferredSize(new java.awt.Dimension(SIZE, SIZE));
    setMinimumSize(getPreferredSize());
    setMaximumSize(getPreferredSize());
    setMargin(new java.awt.Insets(1, 1, 1, 1));
    putClientProperty("hideActionText", Boolean.TRUE);
    setFocusable(false);
  }

  public ToolButton(String text) {
    this(text, null);
  }
  
  public void setText(String text) {
    super.setText(SwingUtil.setButtonText(this, text));
  }

}
