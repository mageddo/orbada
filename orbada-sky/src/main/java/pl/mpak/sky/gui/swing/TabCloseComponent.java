package pl.mpak.sky.gui.swing;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import pl.mpak.sky.gui.swing.comp.ToolButton;

public class TabCloseComponent extends Box {
  private static final long serialVersionUID = 5802918401241083041L;

  private String title;
  private Action action;
  private Icon tabIcon;
  private ToolButton button;
  private JLabel label;
  private JLabel iconLabel;
  private Component buttonBox;
  
  public TabCloseComponent(String title) {
    this(title, null, null);
  }
  
  public TabCloseComponent(String title, Action action) {
    this(title, action, null);
  }
  
  public TabCloseComponent(String title, Icon tabIcon) {
    this(title, new Action(), tabIcon);
  }
  
  public TabCloseComponent(String title, Action action, Icon tabIcon) {
    super(BoxLayout.X_AXIS);
    this.title = title;
    this.action = action;
    this.tabIcon = tabIcon;
    init();
  }
  
  private void init() {
    buttonBox = Box.createHorizontalStrut(14);
    button = new ToolButton() {
      private static final long serialVersionUID = 1L;
      public void setVisible(boolean visible) {
        buttonBox.setVisible(!visible);
        super.setVisible(visible);
      }
    };
    button.setMaximumSize(new Dimension(15, 15));
    button.setMinimumSize(new Dimension(15, 15));
    button.setPreferredSize(new Dimension(15, 15));
    button.setForeground(Color.RED);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setBorderPainted(false);
    button.setOpaque(false);
    setAction(action);
    
    label = new JLabel(title);
    iconLabel = new JLabel(tabIcon); 
    add(iconLabel);
    add(Box.createHorizontalStrut(2));
    add(label);
    add(Box.createHorizontalStrut(6));
    add(button);
    add(buttonBox);
    
    setFocusable(false);
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
    button.setAction(action);
    button.setVisible(action != null);
    if (action == null) {
      buttonBox.setVisible(false);
    }
    if (button.getIcon() == null) {
      button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/close10.gif")));
    }
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
    label.setText(title);
  }
  
  public JLabel getLabel() {
    return label;
  }
  
  public JLabel getIconLabel() {
    return iconLabel;
  }
  
  public ToolButton getButton() {
    return button;
  }

  public String getToolTipText() {
    return label.getToolTipText();
  }

  public void setToolTipText(String toolTipText) {
    label.setToolTipText(toolTipText);
  }
  
  public void setCloseable(boolean closeable) {
    if (action != null) {
      action.setEnabled(closeable);
    }
  }
  
  public boolean isCloseable() {
    if (action != null) {
      return action.isEnabled();
    }
    return false;
  }

  public void dispose() {
    action = null;
    tabIcon = null;
    button.setAction(null);
    button = null;
    label = null;
  }
  
}
