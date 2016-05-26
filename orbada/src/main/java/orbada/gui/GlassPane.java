/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orbada.gui;

import orbada.Consts;
import orbada.core.Application;
import orbada.services.DefaultPleaseWaitRenderer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JComponent;

import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.providers.PleaseWaitRendererProvider;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.timer.TimerManager;

/**
 *
 * @author akaluza
 */
public class GlassPane extends JComponent {

  private int visibleCount;
  private final ArrayList<PleaseWait> waitList;
  private Timer waitTimer;
  private PleaseWaitRendererProvider[] pwrpa;
  private String waitRendererId;
  private boolean waitRendererOn = true;
  private PleaseWaitRendererProvider waitRenderer;
  private PleaseWait[] waitArray;
  private boolean repainting;

  public GlassPane() {
    setIgnoreRepaint(true);
    setBackground(Color.WHITE);
    setDoubleBuffered(true);
    setFont(new Font("Default", Font.BOLD, 11));
    waitList = new ArrayList<PleaseWait>();
    pwrpa = Application.get().getServiceArray(PleaseWaitRendererProvider.class);
    if (pwrpa != null && pwrpa.length > 0) {
      for (PleaseWaitRendererProvider pwrp : pwrpa) {
        pwrp.setComponent(this);
      }
    }
    waitTimer = new Timer(100) {
      {
        setEnabled(false);
      }
      @Override
      public void run() {
        if (waitRendererOn) {
          if (waitRenderer != null) {
            waitRenderer.control();
          }
          if (!repainting) {
            repainting = true;
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                try {
                  if (waitRenderer != null) {
                    Rectangle rect = waitRenderer.getRenderBounds();
                    if (rect != null) {
                      repaint(rect);
                    }
                    else {
                      repaint();
                    }
                  }
                  else {
                    repaint();
                  }
                } finally {
                  repainting = false;
                }
              }
            });
          }
        }
      }
    };
    TimerManager.getGlobal().add(waitTimer);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    Toolkit.getDefaultToolkit().sync();
    synchronized (waitList) {
      if (waitRendererOn) {
        boolean showWait = false;
        for (PleaseWait wait : waitList) {
          if (wait.isShowTime()) {
            showWait = true;
            break;
          }
        }

        if (waitList.size() > 0 && showWait) {
          if (waitRenderer != null) {
            waitRenderer.render(g2, waitArray);
          }
        }
      }
    }
    super.paintComponent(g);
  }

  @Override
  public void setVisible(boolean aFlag) {
    if (aFlag) {
      if (visibleCount == 0) {
        super.setVisible(true);
      }
      visibleCount++;
    } else {
      if (visibleCount > 0) {
        visibleCount--;
        if (visibleCount == 0) {
          super.setVisible(false);
        }
      }
    }
  }

  public void addPleaseWait(PleaseWait wait) {
    addPleaseWait(wait, null);
  }

  public void addPleaseWait(PleaseWait wait, String waitRendererId) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        setVisible(true);
      }
    });
    synchronized (waitList) {
      if (waitList.indexOf(wait) == -1) {
        if (waitList.size() == 0) {
          if (waitRendererId != null) {
            this.waitRendererId = waitRendererId;
            this.waitRendererOn = true;
          }
          else {
            this.waitRendererId = Application.get().getSettings().getValue(Consts.pleaseWaitRendererSetting, DefaultPleaseWaitRenderer.uniqueId);
            this.waitRendererOn = Application.get().getSettings().getValue(Consts.pleaseWaitRendererOnSetting, true);
          }
          if (pwrpa != null && pwrpa.length > 0) {
            waitRenderer = null;
            for (PleaseWaitRendererProvider pwrp : pwrpa) {
              if (this.waitRendererId.equalsIgnoreCase(pwrp.getRendererId())) {
                waitRenderer = pwrp;
              }
            }
            if (waitRenderer == null) {
              for (PleaseWaitRendererProvider pwrp : pwrpa) {
                if (DefaultPleaseWaitRenderer.uniqueId.equalsIgnoreCase(pwrp.getRendererId())) {
                  waitRenderer = pwrp;
                }
              }
            }
          }
          if (waitRenderer != null) {
            waitRenderer.beginProcess();
          }
        }
        waitList.add(wait);
        waitArray = waitList.toArray(new PleaseWait[waitList.size()]);
      }
      waitTimer.setEnabled(waitList.size() > 0 && this.waitRendererOn);
    }
  }

  public void removePleaseWait(PleaseWait wait) {
    synchronized (waitList) {
      waitList.remove(wait);
      try {
        if (waitList.size() > 0) {
          waitArray = waitList.toArray(new PleaseWait[waitList.size()]);
        }
        else {
          waitArray = null;
        }
        if (waitList.size() == 0 && waitRenderer != null) {
          waitRenderer.endProcess();
        }
      }
      finally {
        waitTimer.setEnabled(waitList.size() > 0 && this.waitRendererOn);
        if (waitList.size() == 0) {
          repaint();
          java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
              setVisible(false);
            }
          });
        }
      }
    }
  }

  /**
   * If someone adds a mouseListener to the GlassPane or set a new cursor
   * we expect that he knows what he is doing
   * and return the super.contains(x, y)
   * otherwise we return false to respect the cursors
   * for the underneath components
   */
  @Override
  public boolean contains(int x, int y) {
    if (getMouseListeners().length == 0 && getMouseMotionListeners().length == 0 && getMouseWheelListeners().length == 0 && getCursor() == Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) {
      return false;
    }
    return super.contains(x, y);
  }
}
