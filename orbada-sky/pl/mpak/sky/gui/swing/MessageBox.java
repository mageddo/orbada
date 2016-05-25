package pl.mpak.sky.gui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.mr.ModalResult;

/**
 * @author akaluza
 *         <p>
 *         MessageBox pozwala wyœwietliæ na ekranie okienko dialogowe z
 *         komunikatem.
 *         <p>
 *         przyk³ad:
 *         <p>
 *         MessageBox.show(dialog, "Tytu³", "komunikat", new int[]
 *         {ModalResult.OK});
 *         <p>
 *         Funkcja zwraca wartoœæ ModalResult przekazanego przycisku.
 *         <p>
 *         MessageBox domyœlnie stara siê zinterpretowaæ komunikat jako kod
 *         html.
 *         <p>
 *         Funkcja show() jest odporna na w¹tek, zawsze jest wykonywana w java.awt.EventQueue.
 */
public class MessageBox {
  
  public final static int INFORMATION = JOptionPane.INFORMATION_MESSAGE;
  public final static int QUESTION = JOptionPane.QUESTION_MESSAGE;
  public final static int WARNING = JOptionPane.WARNING_MESSAGE;
  public final static int ERROR = JOptionPane.ERROR_MESSAGE;
  
  private static String[] createButtons(int[] buttons) {
    String[] result = new String[buttons.length];
    for (int i=0; i<buttons.length; i++) {
      switch (buttons[i]) {
        case ModalResult.OK: result[i] = Messages.getString("MessageBox.ok"); break; //$NON-NLS-1$
        case ModalResult.CANCEL: result[i] = Messages.getString("MessageBox.cancel"); break; //$NON-NLS-1$
        case ModalResult.YES: result[i] = Messages.getString("MessageBox.yes"); break; //$NON-NLS-1$
        case ModalResult.NO: result[i] = Messages.getString("MessageBox.no"); break; //$NON-NLS-1$
        case ModalResult.IGNORE: result[i] = Messages.getString("MessageBox.ignore"); break; //$NON-NLS-1$
        case ModalResult.RETRY: result[i] = Messages.getString("MessageBox.retry"); break; //$NON-NLS-1$
      }
    }
    return result;
  }
  
  private static AbstractButton[] createComponents(String[] buttons) {
    AbstractButton[] comps = new AbstractButton[buttons.length];
    for (int i = 0; i < buttons.length; i++) {
      final JButton button = new JButton();
      button.setText(SwingUtil.setButtonText(button, buttons[i]));
      button.setMargin(new Insets(2, 4, 2, 4));
      button.setPreferredSize(new Dimension(55, 24));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          JOptionPane op = (JOptionPane)SwingUtil.getOwnerComponent(JOptionPane.class, button);
          if (op != null) {
            op.setValue(button);
          }
          Window w = (Window)SwingUtil.getOwnerComponent(Window.class, button);
          if (w != null) {
            w.dispose();
          }
        }
      });
      comps[i] = button;
    }
    SwingUtil.setButtonSizesTheSame(comps);
    return comps;
  }

  public static int show(Component owner, String title, String message, int[] buttons) {
    return show(owner, title, message, buttons, INFORMATION);
  }

  public static int show(final Component owner, final String title, final String message, final int[] buttons, final int messageType) {
    return SwingUtil.invokeAndWait(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        String[] options = createButtons(buttons);
        AbstractButton[] comps = createComponents(options);
        int result = JOptionPane.showOptionDialog(owner, message, title, JOptionPane.DEFAULT_OPTION, messageType, null, comps, comps[0]);
        if (result == -1) {
          return ModalResult.NONE;
        }
        return buttons[result];
      }
    });
  }

  public static int show(final Component owner, final String title, final String message, final String[] buttons, final int messageType) {
    return SwingUtil.invokeAndWait(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        AbstractButton[] comps = createComponents(buttons);
        return JOptionPane.showOptionDialog(owner, message, title, JOptionPane.DEFAULT_OPTION, messageType, null, comps, comps[0]);
      }
    });
  }

  public static int show(String message) {
    return show(null, Messages.getString("MessageBox.information"), message, new int[] { ModalResult.OK }); //$NON-NLS-1$
  }

  public static int show(String title, String message) {
    return show(null, title, message, new int[] { ModalResult.OK });
  }

  public static int show(String title, String message, int button) {
    return show(null, title, message, new int[] { button });
  }

  public static int show(Component owner, String title, String message, int button) {
    return show(owner, title, message, button, INFORMATION);
  }

  public static int show(Component owner, String title, String message, int button, int messageType) {
    return show(owner, title, message, new int[] { button }, messageType);
  }

  public static int show(Component owner, String title, String message) {
    return show(owner, title, message, ModalResult.OK, INFORMATION);
  }
  
}
