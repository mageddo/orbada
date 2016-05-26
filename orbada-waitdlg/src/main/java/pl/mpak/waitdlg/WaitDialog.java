package pl.mpak.waitdlg;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import pl.mpak.sky.gui.swing.SwingUtil;

public class WaitDialog extends JWindow {
  private static final long serialVersionUID = 1315881488084855280L;

  private JLabel labelWaitText = null;
  private JPanel panelProgress = null;
  private JProgressBar barProgress = null;
  
  private static volatile WaitDialog waitDialog = null;
  private static int waitDialogCount = 0;
  private static Object mutex = new Object();
  
  public static void showWaitDialog(final String waitText, final int maximum) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (mutex) {
          if (waitDialog == null) {
            waitDialog = new WaitDialog();
            waitDialog.setVisible(true);
          }
          waitDialog.labelWaitText.setText(waitText);
          waitDialogCount++;
          waitDialog.panelProgress.setVisible(true);
          waitDialog.barProgress.setMaximum(maximum);
          waitDialog.barProgress.setMinimum(0);
          waitDialog.barProgress.setValue(0);
          waitDialog.repaint();
        }
      }
    });
  }
  
  public static void showWaitDialog(final String waitText) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (mutex) {
          if (waitDialog == null) {
            waitDialog = new WaitDialog();
            waitDialog.setVisible(true);
          }
          waitDialog.labelWaitText.setText(waitText);
          waitDialogCount++;
          waitDialog.repaint();
        }
      }
    });
  }
  
  public static void continueWaitDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (mutex) {
          if (waitDialog != null) {
            waitDialog.barProgress.setValue(waitDialog.barProgress.getValue() +1);
          }
        }
      }
    });
  }
  
  public static void continueWaitDialog(final int value) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (mutex) {
          if (waitDialog != null) {
            waitDialog.barProgress.setValue(value);
          }
        }
      }
    });
  }
  
  public static void continueWaitDialog(final String text) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (mutex) {
          if (waitDialog != null) {
            waitDialog.labelWaitText.setText(text);
            waitDialog.repaint();
          }
        }
      }
    });
  }
  
  public static void hideWaitDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (mutex) {
          if (waitDialog != null) {
            if (--waitDialogCount == 0) {
              waitDialog.dispose();
              waitDialog = null;
            }
          }
        }
      }
    });
  }
  
  public static synchronized boolean isShowed() {
    synchronized (mutex) {
      return waitDialog != null;
    }
  }
  
  private WaitDialog() {
    super(SwingUtil.getRootFrame());
    init();
  }

//  private WaitDialog(Window owner) {
//    super(owner);
//    init();
//  }
//
//  private WaitDialog(JFrame owner) {
//    super(owner);
//    init();
//  }

  private void init() {
    setAlwaysOnTop(true);
    setContentPane(getPanelContent());
    setSize(400, 150);
    SwingUtil.centerWithinScreen(this);
  }
  
  private JPanel getPanelContent() {
    return new JPanel(new BorderLayout()) {
      private static final long serialVersionUID = 1L;
      {
        setBorder(BorderFactory.createTitledBorder(""));
        add(getPanelProgress(), BorderLayout.SOUTH);
        add(labelWaitText = new JLabel("", JLabel.CENTER), BorderLayout.CENTER);
        labelWaitText.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        labelWaitText.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        labelWaitText.setMinimumSize(new Dimension(100, 50));
      }
    };
  }
  
  private JPanel getPanelProgress() {
    return panelProgress = new JPanel(new BorderLayout()) {
      private static final long serialVersionUID = 1L;
      {
        add(barProgress = new JProgressBar(), BorderLayout.CENTER);
        setVisible(false);
      }
    }; 
  }
}
