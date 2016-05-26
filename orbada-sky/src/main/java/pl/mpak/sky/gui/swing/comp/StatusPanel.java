package pl.mpak.sky.gui.swing.comp;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.Border;

import pl.mpak.util.StringUtil;

/**
 * @author Andrzej Ka³u¿a
 * Jest to czêœæ sk³adowa klasy StatusBar
 *
 */
public class StatusPanel extends JLabel {
  private static final long serialVersionUID = -639556133309106085L;

  private Border oldBorder;
  private boolean displayActivation = false;
  
  public StatusPanel(String name, String text, Icon icon) {
    super(text, icon, LEADING);
    setName(name);
    init();
  }

  public StatusPanel(String name, String text) {
    super(text);
    setName(name);
    init();
  }

  public StatusPanel(String name) {
    super();
    setName(name);
    init();
  }

  public StatusPanel() {
    super();
    init();
  }

  private void init() {
    setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent arg0) {
      }
      public void mouseEntered(MouseEvent arg0) {
        if (displayActivation) {
          oldBorder = getBorder();
          setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }
      }
      public void mouseExited(MouseEvent arg0) {
        if (displayActivation) {
          setBorder(oldBorder);
        }
      }
      public void mousePressed(MouseEvent arg0) {
      }
      public void mouseReleased(MouseEvent arg0) {
      }
    });
  }

  public boolean isDisplayActivation() {
    return displayActivation;
  }

  public void setDisplayActivation(boolean displayActivation) {
    this.displayActivation = displayActivation;
  }
  
  public void setText(String text) {
    if (StringUtil.isEmpty(text)) {
      super.setText(" ");
    }
    else {
      if (!text.startsWith(" ")) {
        text = " " +text;
      }
      if (!text.endsWith(" ")) {
        text = text +" ";
      }
      super.setText(text);
    }
  }
  
}
