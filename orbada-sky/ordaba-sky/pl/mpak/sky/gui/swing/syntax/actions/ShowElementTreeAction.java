package pl.mpak.sky.gui.swing.syntax.actions;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.MissingResourceException;

import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.syntax.ElementTreePanel;

public class ShowElementTreeAction extends Action {
  private static final long serialVersionUID = 1L;

  private JFrame elementTreeFrame;
  private ElementTreePanel elementTreePanel;
  private JTextComponent textComponent;

  public ShowElementTreeAction(JTextComponent textComponent) {
    super("showElementTree"); //$NON-NLS-1$
    this.textComponent = textComponent;
    setShortCut(KeyEvent.VK_F11, KeyEvent.ALT_MASK);
    setActionCommandKey("cmShowElementTree"); //$NON-NLS-1$
    addActionListener(createActionListener());
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (elementTreeFrame == null) {
          // Create a frame containing an instance of
          // ElementTreePanel.
          try {
            String title = Messages.getString("ShowElementTreeAction.title"); //$NON-NLS-1$
            elementTreeFrame = new JFrame(title);
          } catch (MissingResourceException mre) {
            elementTreeFrame = new JFrame();
          }
    
          elementTreeFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent weeee) {
              elementTreeFrame.dispose();
              elementTreeFrame = null;
            }
          });
          Container fContentPane = elementTreeFrame.getContentPane();
    
          fContentPane.setLayout(new BorderLayout());
          elementTreePanel = new ElementTreePanel(textComponent);
          fContentPane.add(elementTreePanel);
          elementTreeFrame.pack();
        }
        elementTreeFrame.setVisible(true);
      }
    };
  }
}
