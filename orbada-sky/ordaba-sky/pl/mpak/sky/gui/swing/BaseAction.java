package pl.mpak.sky.gui.swing;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import pl.mpak.sky.SkySetting;
import pl.mpak.util.SystemUtil;

public abstract class BaseAction extends AbstractAction {
  private static final long serialVersionUID = 1L;

  private static boolean macos = SystemUtil.isMacOs();
  private static boolean macoscommandkey = SkySetting.getBoolean(SkySetting.Action_MacOsCommandKey, SkySetting.Default_Action_MacOsCommandKey);
  private String oryginalTooltip;
  private KeyStroke altKey;
  
  protected BaseAction() {
    super();
    init();
  }

  protected BaseAction(String title) {
    super();
    setText(title);
    init();
  }

  protected BaseAction(String title, Icon icon) {
    super(null, icon);
    setText(title);
    init();
  }
  
  private void init() {
  }
  
  public void putValue(String key, Object newValue) {
    if (macos && macoscommandkey && ACCELERATOR_KEY.equals(key) && newValue instanceof KeyStroke) {
      KeyStroke ks = (KeyStroke)newValue;
      if ((ks.getModifiers() & KeyEvent.VK_CONTROL) == KeyEvent.VK_CONTROL) {
        int modifiers = ks.getModifiers();
        modifiers = (modifiers & ~KeyEvent.VK_CONTROL) | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        newValue = KeyStroke.getKeyStroke(ks.getKeyCode(), modifiers);
      }
    }
    super.putValue(key, newValue);
  }

  public void setSmallIcon(Icon icon) {
    putValue(SMALL_ICON, icon);
  }

  public Icon getSmallIcon() {
    return (Icon)getValue(SMALL_ICON);
  }

  public void setLargeIcon(Icon icon) {
    putValue(LARGE_ICON_KEY, icon);
  }

  public Icon getLargeIcon() {
    return (Icon)getValue(LARGE_ICON_KEY);
  }

  public void setText(String name) {
    if (name.length() != 0) {
      String text = SwingUtil.setButtonText(this, name);
      if (getTooltip() == null || "".equals(getTooltip()) || text.equals(getText())) {
        setTooltip(text);
      }
      putValue(NAME, text);
    }
  }

  public String getText() {
    return (String) getValue(NAME);
  }
  
  public void setActionCommandKey(String key) {
    putValue(ACTION_COMMAND_KEY, key);
  }
  
  public String getActionCommandKey() {
    return (String) getValue(ACTION_COMMAND_KEY);
  }
  
  private String shortCut2Text() {
    String result = "";
    if (getShortCut().getModifiers() != 0) {
      result = KeyEvent.getKeyModifiersText(getShortCut().getModifiers()) +"+";
    }
    return result +KeyEvent.getKeyText(getShortCut().getKeyCode());
  }
  
  public void setTooltip(String tooltip) {
    if (tooltip != null && tooltip.length() != 0) {
      oryginalTooltip = tooltip;
      if (oryginalTooltip != null && getShortCut() != null) {
        tooltip = tooltip +" (" +shortCut2Text() +")";
      }
    }
    putValue(SHORT_DESCRIPTION, tooltip);
  }

  public String getTooltip() {
    return oryginalTooltip == null ? (String)getValue(SHORT_DESCRIPTION) : oryginalTooltip;
  }

  public void setHelp(String help) {
    putValue(LONG_DESCRIPTION, help);
  }

  public String getHelp() {
    return (String)getValue(LONG_DESCRIPTION);
  }

  public void setShortCut(KeyStroke key) {
    putValue(ACCELERATOR_KEY, key);
    if (oryginalTooltip != null && key != null) {
      String tooltip = oryginalTooltip +" (" +shortCut2Text() +")";
      putValue(SHORT_DESCRIPTION, tooltip);
    }
  }

  public KeyStroke getShortCut() {
    return (KeyStroke) getValue(ACCELERATOR_KEY);
  }

  public void setShortCut(int keyCode, int modifiers) {
    setShortCut(KeyStroke.getKeyStroke(keyCode, modifiers));
  }

  /**
   * <p>Alternatywny przycisk trzeba obs³u¿yæ samodzielnie lub u¿yæ SwingUtil.addAction()
   * @return
   * @see SwingUtil.addAction()
   */
  public KeyStroke getAltShortCut() {
    return altKey;
  }

  /**
   * <p>Alternatywny przycisk trzeba obs³u¿yæ samodzielnie lub u¿yæ SwingUtil.addAction()
   * @param keyCode
   * @param modifiers
   * @see SwingUtil.addAction()
   */
  public void setAltShortCut(int keyCode, int modifiers) {
    altKey = KeyStroke.getKeyStroke(keyCode, modifiers);
  }

  public void setMnemonic(Integer mnemonic) {
    putValue(MNEMONIC_KEY, mnemonic);
  }

  public Integer getMnemonic() {
    return (Integer)getValue(MNEMONIC_KEY);
  }
  
  /**
   * <p>
   * Return the <TT>Component</TT> of clazz associated to evt.getSource()
   * 
   * @param evt
   *          <TT>ActionEvent</TT> to find frame for.
   * @return <TT>Component</TT> or <TT>null</TT> if none found.
   */
  protected Component getParent(ActionEvent evt, Class<?> clazz) {
    Component parent = null;
    if (evt != null) {
      final Object src = evt.getSource();
      if (src instanceof Component) {
        Component comp = (Component)src;
        while (comp != null && parent == null) {
          if (clazz.isAssignableFrom(comp.getClass())) {
            parent = comp;
          }
          else if (comp instanceof JPopupMenu) {
            comp = ((JPopupMenu)comp).getInvoker();
          }
          else {
            comp = comp.getParent();
          }
        }
      }
    }
    return parent;
  }
}
