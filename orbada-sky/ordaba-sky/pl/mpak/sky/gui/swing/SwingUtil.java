/*
 * Created on 2005-07-13
 *
 */
package pl.mpak.sky.gui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

import pl.mpak.util.ExceptionUtil;

/**
 * @author Andrzej Ka³u¿a
 * 
 */
public class SwingUtil {

  public interface Color {
    public final static java.awt.Color GREEN = new java.awt.Color(0, 0x80, 0);
    public final static java.awt.Color NAVY = new java.awt.Color(0, 0, 128);
    public final static java.awt.Color DARKORANGE = new java.awt.Color(205, 150, 0);
    public final static java.awt.Color FUCHSIA = new java.awt.Color(255, 0, 255);
  }

  /**
   * Ustawia Mnemonic obiektu AbstractButton, JMenuItem, JMenu, JButton, etc
   * 
   * @param button
   * @param text
   * @return ci¹g znaków jaki trzeba przekazaæ do super.setText()
   */
  public static String setButtonText(AbstractButton button, String text) {
    if (text == null || text.equals("")) {
      return null;
    }

    String stmp = text;
    int itmp = stmp.indexOf('&');

    if (itmp >= 0 && stmp.toUpperCase().indexOf("&AMP;") < 0) {
      button.setMnemonic(stmp.charAt(itmp + 1));
      if (itmp > 0) {
        text = stmp.substring(0, itmp);
      } else {
        text = "";
      }
      text = text + stmp.substring(itmp + 1, stmp.length());
    }
    return text;
  }

  public static String setButtonText(JLabel label, String text) {
    if (text == null || text.equals("")) {
      return null;
    }

    String stmp = text;
    int itmp = stmp.indexOf('&');

    if (itmp >= 0 && stmp.toUpperCase().indexOf("&AMP;") < 0) {
      label.setDisplayedMnemonic(stmp.charAt(itmp + 1));
      if (itmp > 0) {
        text = stmp.substring(0, itmp);
      } else {
        text = "";
      }
      text = text + stmp.substring(itmp + 1, stmp.length());
    }
    return text;
  }

  public static String setButtonText(AbstractAction action, String text) {
    if (text == null || text.equals("")) {
      return null;
    }

    String stmp = text;
    int itmp = stmp.indexOf('&');

    if (itmp >= 0 && stmp.toUpperCase().indexOf("&AMP;") < 0) {
      action.putValue(AbstractAction.MNEMONIC_KEY, Integer.valueOf(stmp.charAt(itmp + 1)));
      if (itmp > 0) {
        text = stmp.substring(0, itmp);
      } else {
        text = "";
      }
      text = text + stmp.substring(itmp + 1, stmp.length());
    }
    return text;
  }

  public static JMenu updateMenuText(JMenu menu) {
    if (menu == null) {
      return null;
    }

    String stmp = menu.getText();
    int itmp = stmp.indexOf('&');

    if (itmp >= 0 && stmp.toUpperCase().indexOf("&AMP;") < 0) {
      menu.setMnemonic(stmp.charAt(itmp + 1));
      String text = stmp;
      if (itmp > 0) {
        text = stmp.substring(0, itmp);
      } else {
        text = "";
      }
      text = text + stmp.substring(itmp + 1, stmp.length());
      menu.setText(text);
    }
    return menu;
  }

  public static void centerWithinScreen(Window wind) {
    if (wind == null) {
      throw new IllegalArgumentException("null Window passed");
    }
    final Toolkit toolKit = Toolkit.getDefaultToolkit();
    final Rectangle rcScreen = new Rectangle(toolKit.getScreenSize());
    final Dimension windSize = wind.getSize();
    final Dimension parentSize = new Dimension(rcScreen.width, rcScreen.height);
    if (windSize.height > parentSize.height) {
      windSize.height = parentSize.height;
    }
    if (windSize.width > parentSize.width) {
      windSize.width = parentSize.width;
    }
    center(wind, rcScreen);
  }

  private static void center(Component wind, Rectangle rect) {
    if (wind == null || rect == null) {
      throw new IllegalArgumentException("null Window or Rectangle passed");
    }
    Dimension windSize = wind.getSize();
    int x = ((rect.width - windSize.width) / 2) + rect.x;
    int y = ((rect.height - windSize.height) / 2) + rect.y;
    if (y < rect.y) {
      y = rect.y;
    }
    wind.setLocation(x, y);
  }

  static public Point computeLocation(Component rel, Component popup) {
    return computeLocation(0, 0, rel, popup);
  }

  static public Point computeLocation(MouseEvent event, Component rel,
      Component popup) {
    return computeLocation(event.getX(), event.getY(), rel, popup);
  }

  static public Point computeLocation(int x, int y, Component rel,
      Component popup) {

    Dimension psz = popup.getSize();
    Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
    Point gLoc = rel.getLocationOnScreen();
    Point result = new Point(x, y);

    gLoc.x += x;
    gLoc.y += y;

    if (psz.width == 0 || psz.height == 0) {
      if (popup instanceof JPopupMenu) {
        int items = ((JPopupMenu) popup).getSubElements().length;
        psz.height = (items * 22);
      } else {
        psz.height = popup.getPreferredSize().height;
      }
      psz.width = 100;
    }

    psz.height += 5;

    if ((gLoc.x + psz.width) > ssz.width) {
      result.x -= ((gLoc.x + psz.width) - ssz.width);
      if ((gLoc.x + result.x) < 0)
        result.x = -(gLoc.x + x);
    }

    if ((gLoc.y + psz.height) > ssz.height) {
      result.y -= ((gLoc.y + psz.height) - ssz.height);
      if ((gLoc.y + result.y) < 0)
        result.y = -gLoc.y;
    }

    return result;
  }

  /**
   * Ustala punkt od którego mo¿na rozpocz¹æ pokazywaæ obiekt popup
   * 
   * @param x
   *          pozycja wzglêdem pozycji rel na ekranie
   * @param y
   * @param rel
   *          komponent wzglêdem którego bêdzie wyliczyna pozycja popup
   * @param popup
   * @return
   */
  static public Point computeLocationOnScreen(int x, int y, Component rel,
      Component popup) {

    Dimension psz = popup.getSize();
    Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
    Point gLoc = rel.getLocationOnScreen();
    Point result = new Point(gLoc.x + x, gLoc.y + y);

    gLoc.x += x;
    gLoc.y += y;

    if (psz.width == 0 || psz.height == 0) {
      if (popup instanceof JPopupMenu) {
        int items = ((JPopupMenu) popup).getSubElements().length;
        psz.height = (items * 22);
      } else {
        psz.height = popup.getPreferredSize().height;
      }
      psz.width = 100;
    }

    psz.height += 5;

    if ((gLoc.x + psz.width) > ssz.width) {
      result.x -= ((gLoc.x + psz.width) - ssz.width);
      if ((gLoc.x + result.x) < 0)
        result.x = -(gLoc.x + x);
    }

    if ((gLoc.y + psz.height) > ssz.height) {
      result.y -= ((gLoc.y + psz.height) - ssz.height);
      if ((gLoc.y + result.y) < 0)
        result.y = -gLoc.y;
    }

    return result;
  }

  /**
   * Pozwala uzyskaæ indeks komponentu le¿¹cego na innym komponencie
   * 
   * @param owner
   * @param item
   * @return
   */
  public static int getIndexOf(JComponent owner, Component item) {
    synchronized (owner.getTreeLock()) {
      for (int i = 0; i < owner.getComponentCount(); i++) {
        if (owner.getComponent(i) == item) {
          return i;
        }
      }
      return -1;
    }
  }

  /**
   * Dodaje za komponentem comp komponent newItem
   * 
   * @param owner
   * @param comp
   * @param newItem
   */
  public static void addBefore(JComponent owner, Component comp,
      Component newItem) {
    synchronized (owner.getTreeLock()) {
      int index = getIndexOf(owner, comp);
      if (index > 0) {
        owner.add(newItem, index);
      } else if (index == 0) {
        owner.add(newItem, 0);
      } else if (owner.getComponentCount() == 0) {
        owner.add(newItem);
      }
    }
  }

  /**
   * Dodaje przed komponentem comp komponent newItem
   * 
   * @param owner
   * @param comp
   * @param newItem
   */
  public static void addAfter(JComponent owner, Component comp,
      Component newItem) {
    synchronized (owner.getTreeLock()) {
      int index = getIndexOf(owner, comp);
      if (index == owner.getComponentCount() - 1
          || owner.getComponentCount() == 0) {
        owner.add(newItem);
      } else {
        owner.add(newItem, index + 1);
      }
    }
  }

  /**
   * <p>
   * £aduje obrazek do pamiêci.
   * 
   * @param image
   * @throws InterruptedException
   * @throws IllegalArgumentException
   */
  public static void loadImage(Image image) throws InterruptedException,
      IllegalArgumentException {
    Component dummy = new Component() {
      private static final long serialVersionUID = 1L;
    };
    MediaTracker tracker = new MediaTracker(dummy);
    tracker.addImage(image, 0);
    tracker.waitForID(0);
    if (tracker.isErrorID(0))
      throw new IllegalArgumentException();
  }

  /**
   * <p>
   * Zwraca component, który jest oknem, root-em dla component
   * 
   * @param component
   * @return
   */
  public static Window getWindowComponent(Component component) {
    Component c = component.getParent();
    while (c != null) {
      if (c instanceof Window) {
        return (Window) c;
      }
      c = c.getParent();
    }
    return null;
  }

  /**
   * <p>
   * Zwraca component, który jest oknem, root-em dla component
   * 
   * @param component
   * @return
   */
  public static Frame getFrameComponent(Component component) {
    Component c = component.getParent();
    while (c != null) {
      if (c instanceof Frame) {
        return (Frame) c;
      }
      c = c.getParent();
    }
    return null;
  }

  /**
   * <p>
   * Sprawdza czy component le¿y na componentOn
   * 
   * @param component
   * @return
   */
  public static boolean isComponentOn(Component component, Component componentOn) {
    Component c = componentOn;
    while (c != null) {
      if (c.equals(component)) {
        return true;
      }
      c = c.getParent();
    }
    return false;
  }

  /**
   * <p>
   * Zwraca component, który jest oknem, root-em dla component
   * 
   * @param component
   * @return
   */
  public static Component getOwnerComponent(Class<?> clazz, Component component) {
    if (component != null) {
      Component c = component.getParent();
      while (c != null) {
        if (clazz.isInstance(c)) {
          return c;
        }
        c = c.getParent();
      }
    }
    return null;
  }

  /**
   * <p>
   * Zwraca component klasy clazz, który le¿y na JTabbedPane, który z kolei jest
   * rodzicem component
   * 
   * @param component
   * @return
   */
  public static Component getTabbedPaneComponent(
      Class<? extends Component> clazz, Component component) {
    Component c = getOwnerComponent(JTabbedPane.class, component);
    if (c != null) {
      JTabbedPane tp = (JTabbedPane) c;
      for (int i = 0; i < tp.getComponentCount(); i++) {
        if (clazz.isInstance(tp.getComponent(i))) {
          return tp.getComponent(i);
        }
      }
    }
    return null;
  }

  /**
   * <p>
   * Zwraca listê komponentów klasy clazz, które le¿¹ na JTabbedPane, który z kolei jest
   * rodzicem component
   * Lista komponentów jest w kolejnoœci ich wystêpowania na liœcie JTabbedPane
   * 
   * @param component
   * @return
   */
  public static Component[] getTabbedPaneComponents(
      Class<? extends Component> clazz, Component component) {
    ArrayList<Component> list = new ArrayList<Component>();
    Component c = getOwnerComponent(JTabbedPane.class, component);
    if (c != null) {
      JTabbedPane tp = (JTabbedPane) c;
      for (int i = 0; i < tp.getComponentCount(); i++) {
        if (clazz.isInstance(tp.getComponent(i))) {
          list.add(tp.getComponent(i));
        }
      }
    }
    if (!list.isEmpty()) {
      return list.toArray(new Component[list.size()]);
    }
    return null;
  }

  /**
   * <p>
   * Zwraca czy komponent lub któryœ komponent na którym on le¿y jest widoczny
   * lub zminimalizowany
   * 
   * @param component
   * @return
   */
  public static boolean isVisible(Component component) {
    if (!component.isVisible()) {
      return false;
    }
    Component c = component.getParent();
    while (c != null) {
      if (!c.isVisible()) {
        return false;
      } else if (c instanceof Frame) {
        if (((Frame) c).getState() == Frame.ICONIFIED) {
          return false;
        }
      }
      c = c.getParent();
    }
    return true;
  }

  public static void addAction(JComponent component, Action a) {
    String actionCommand = (String) a.getValue(Action.ACTION_COMMAND_KEY);
    if (actionCommand == null) {
      actionCommand = a.getClass().getCanonicalName();
    }
    component.getInputMap(JComponent.WHEN_FOCUSED).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), actionCommand);
    if (a instanceof BaseAction) {
      component.getInputMap(JComponent.WHEN_FOCUSED).put(((BaseAction)a).getAltShortCut(), actionCommand);
    }
    component.getActionMap().put(actionCommand, a);
  }

  public static void addAction(JComponent component, String key, Action a) {
    component.getInputMap(JComponent.WHEN_FOCUSED).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), key);
    if (a instanceof BaseAction) {
      component.getInputMap(JComponent.WHEN_FOCUSED).put(((BaseAction)a).getAltShortCut(), key);
    }
    component.getActionMap().put(key, a);
  }

  public static void addAction(JDialog component, String key, Action a) {
    component.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), key);
    if (a instanceof BaseAction) {
      component.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(((BaseAction)a).getAltShortCut(), key);
    }
    component.getRootPane().getActionMap().put(key, a);
  }

  public static void addAction(JPanel component, String key, Action a) {
    component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), key);
    if (a instanceof BaseAction) {
      component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(((BaseAction)a).getAltShortCut(), key);
    }
    component.getActionMap().put(key, a);
  }

  public static void addAction(JFrame component, String key, Action a) {
    component.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), key);
    if (a instanceof BaseAction) {
      component.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(((BaseAction)a).getAltShortCut(), key);
    }
    component.getRootPane().getActionMap().put(key, a);
  }

  public static Frame getRootFrame() {
    Frame[] frames = javax.swing.JFrame.getFrames();
    if (frames != null && frames.length > 0) {
      for (Frame frame : frames) {
        if (frame.isVisible()) {
          return frame;
        }
      }
    }
    return null;
  }

  public static java.awt.Color addColor(java.awt.Color color, int r, int g, int b) {
    return new java.awt.Color(Math.max(Math.min(color.getRed() + r, 255), 0),
        Math.max(Math.min(color.getGreen() + g, 255), 0), Math.max(Math.min(color.getBlue() + b, 255), 0));
  }

  public static java.awt.Color computeColor(java.awt.Color color1, java.awt.Color color2) {
    return new java.awt.Color((color1.getRed() +color2.getRed()) / 2, (color1.getGreen() +color2.getGreen()) / 2, (color1.getGreen() +color2.getGreen()) / 2);
  }

  /**
   * <p>
   * Zwraca ci¹g znaków z reprezentacj¹ skrótu klawiszowego
   * 
   * @param code
   * @param modifiers
   * @return
   */
  public static String shortcutText(int code, int modifiers) {
    if (code == 0 && modifiers == 0) {
      return "";
    }
    String text = KeyEvent.getKeyModifiersText(modifiers);
    if (text.length() > 0) {
      if (code != KeyEvent.VK_SHIFT && code != KeyEvent.VK_ALT && code != KeyEvent.VK_CONTROL) {
        text = text + "+" + KeyEvent.getKeyText(code);
      }
    } else {
      text = KeyEvent.getKeyText(code);
    }
    return text;
  }
  
  public static int setButtonSizesTheSame(AbstractButton[] btns) {
    return setButtonSizesTheSame(btns, 9999);
  }

  public static int setButtonSizesTheSame(AbstractButton[] btns, int maxHeight) {
    if (btns == null) {
      throw new IllegalArgumentException("null Button[] passed");
    }

    // Get the largest width and height
    final Dimension maxSize = new Dimension(0, 0);
    for (AbstractButton btn : btns) {
      final FontMetrics fm = btn.getFontMetrics(btn.getFont());
      final Insets insets = btn.getInsets();
      Rectangle2D bounds = fm.getStringBounds(btn.getText(), btn.getGraphics());
      final Icon icon = btn.getIcon();
      final Dimension prefSize = btn.getPreferredSize();
      int boundsHeight = Math.max(prefSize.height, Math.max((int)bounds.getHeight(), (icon != null ? icon.getIconHeight() : 0)) +insets.top + insets.bottom);
      int boundsWidth = Math.max(prefSize.width, (int)bounds.getWidth() +(icon != null ? icon.getIconWidth() +btn.getIconTextGap() : 0) +insets.left + insets.right);
      maxSize.width = boundsWidth > maxSize.width ? boundsWidth : maxSize.width;
      maxSize.height = boundsHeight > maxSize.height ? boundsHeight : maxSize.height;
    }

    maxSize.height = Math.min(maxHeight, maxSize.height);
    for (AbstractButton btn : btns) {
      btn.setPreferredSize(maxSize);
    }
    return maxSize.height;
  }
  
  public static int getPreviousWord(JTextComponent comp, int offset) {
    String text = comp.getText();
    if (offset > 0) {
      char ch = text.charAt(offset -1);
      boolean isLetter = Character.isLetter(ch) || Character.isJavaIdentifierPart(ch);
      if (isLetter) {
        while (offset > 0) {
          offset--;
          ch = text.charAt(offset);
          if (!Character.isLetter(ch) && !Character.isJavaIdentifierPart(ch)) {
            offset++;
            break;
          }
        }
      }
      else {
        while (offset > 0) {
          offset--;
          ch = text.charAt(offset);
          if (Character.isLetter(ch) || Character.isJavaIdentifierPart(ch)) {
            offset++;
            break;
          }
        }
      }
    }
    return offset;
  }

  public static int getNextWord(JTextComponent comp, int offset) {
    String text = comp.getText();
    if (offset < text.length()) {
      char ch = text.charAt(offset);
      boolean isLetter = Character.isLetter(ch) || Character.isJavaIdentifierPart(ch);
      if (isLetter) {
        while (offset < text.length()) {
          ch = text.charAt(offset);
          if (!Character.isLetter(ch) && !Character.isJavaIdentifierPart(ch)) {
            break;
          }
          offset++;
        }
      }
      else {
        while (offset < text.length()) {
          ch = text.charAt(offset);
          if (Character.isLetter(ch) || Character.isJavaIdentifierPart(ch)) {
            break;
          }
          offset++;
        }
      }
    }
    return offset;
  }
  
  public static <V> V invokeAndWait(Callable<V> callable) {
    try {
      FutureTask<V> task = new FutureTask<V>(callable);
      if (!java.awt.EventQueue.isDispatchThread()) {
        java.awt.EventQueue.invokeAndWait(task);
      } else {
        task.run();
      }
      return task.get();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public static void invokeLater(Callable<?> callable) {
    try {
      FutureTask task = new FutureTask(callable);
      if (!java.awt.EventQueue.isDispatchThread()) {
        java.awt.EventQueue.invokeLater(task);
      } else {
        task.run();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }
  
  @SuppressWarnings("unchecked")
  public void doInBackground(final Callable<?> callable) {
    SwingWorker worker = new SwingWorker() {
      protected Object doInBackground() throws Exception {
        try {
          return callable.call();
        } catch (Exception e) {
          ExceptionUtil.processException(e);
          return null;
        }
      }
    };
    worker.execute();
  }

}
