package pl.mpak.sky.gui.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.KeyStroke;

import pl.mpak.util.StringUtil;

public class ActionListCellRenderer extends DefaultListCellRenderer {
  private static final long serialVersionUID = 1L;

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (value instanceof Action) {
      Action a = (Action)value;
      setIcon((Icon)a.getValue(Action.SMALL_ICON));
      String text = (String)a.getValue(Action.NAME);
      if (StringUtil.isEmpty(text)) {
        text = (String)a.getValue(Action.ACTION_COMMAND_KEY);
      }
      if (a.getValue(Action.ACCELERATOR_KEY) != null) {
        KeyStroke ks = (KeyStroke)a.getValue(Action.ACCELERATOR_KEY);
        String sk = SwingUtil.shortcutText(ks.getKeyCode(), ks.getModifiers());
        if (!StringUtil.isEmpty(sk)) {
          text = text +" (" +sk +")";
        }
      }
      setText(text);
      setToolTipText((String)a.getValue(Action.SHORT_DESCRIPTION));
    }
    return this;
  }

}
