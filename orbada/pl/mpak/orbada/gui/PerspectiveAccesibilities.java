package pl.mpak.orbada.gui;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class PerspectiveAccesibilities implements IPerspectiveAccesibilities, ComponentListener {
  
  private ArrayList<JMenu> menuList;
  private JToolBar toolBar;
  private ArrayList<JComponent> statusList;
  private PerspectivePanel perspective;
  private ArrayList<PerspectiveProvider> serviceList;
  private ArrayList<JToolBar> toolBarList;

  public PerspectiveAccesibilities(PerspectivePanel perspective) {
    this.perspective = perspective;
    init();
  }
  
  private void init() {
    serviceList = new ArrayList<PerspectiveProvider>();
  }
  
  public void close() {
    if (menuList != null) {
      for (JMenu menu : menuList) {
        Application.get().getMainFrame().removeMenu(menu);
      }
      menuList.clear();
    }
    if (toolBar != null) {
      Application.get().getMainFrame().removeToolBar(toolBar);
      toolBar = null;
    }
    if (statusList != null) {
      for (JComponent statusBar : statusList) {
        perspective.removeStatusBar(statusBar);
      }
      statusList.clear();
    }
    if (toolBarList != null) {
      for (JToolBar tb : toolBarList) {
        Application.get().getMainFrame().removeToolBar(tb);
      }
      toolBarList.clear();
    }
    for (PerspectiveProvider pp : serviceList) {
      pp.perspectiveClose();
    }
    serviceList.clear();
    perspective = null;
  }
  
  public void addSerivce(PerspectiveProvider serivce) {
    serviceList.add(serivce);
  }
  
  public Database getDatabase() {
    return perspective.getDatabase();
  }
  
  public IApplication getApplication() {
    return Application.get();
  }
  
  public void addMenu(final JMenu menu) {
    if (menu != null) {
      if (menuList == null) {
        menuList = new ArrayList<JMenu>();
      }
      menuList.add(menu);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          Application.get().getMainFrame().addMenu(menu);
        }
      });
    }
  }

  public void addAction(Action action) {
    if (action != null) {
      if (toolBar == null) {
        toolBar = new JToolBar();
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            Application.get().getMainFrame().addToolBar(toolBar);
          }
        });
      }
      toolBar.add(new ToolButton(action));
    }
  }
  
  public void addStatusBar(JComponent statusBar) {
    if (statusBar != null) {
      if (statusList == null) {
        statusList = new ArrayList<JComponent>();
      }
      statusList.add(statusBar);
      perspective.addStatusBar(statusBar);
    }
  }
  
  public void addToolBar(final JToolBar toolBar) {
    if (toolBar != null) {
      if (toolBarList == null) {
        toolBarList = new ArrayList<JToolBar>();
      }
      toolBarList.add(toolBar);
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          Application.get().getMainFrame().addToolBar(toolBar);
        }
      });
    }
  }

  public Component getViewComponent(ViewProvider view) {
    return perspective.getViewComponent(view);
  }
  
  public Component createView(ViewProvider view) {
    return perspective.createView(view, false, false);
  }
  
  public void closeView(Component component) {
    perspective.closeView(component);
  }
  
  public void setSelectedView(Component c) {
    perspective.setSelectedView(c);
  }

  public void componentShow() {
    if (menuList != null) {
      for (JMenu menu : menuList) {
        menu.setVisible(true);
      }
    }
    if (toolBar != null) {
      toolBar.setVisible(true);
    }
    if (toolBarList != null) {
      for (JToolBar tb : toolBarList) {
        tb.setVisible(true);
      }
    }
//    if (statusList != null) {
//      for (JComponent statusBar : statusList) {
//        statusBar.setVisible(true);
//      }
//    }
    for (PerspectiveProvider pp : serviceList) {
      pp.perspectiveShow();
    }
  }

  public void componentHide() {
    if (menuList != null) {
      for (JMenu menu : menuList) {
        menu.setVisible(false);
      }
    }
    if (toolBar != null) {
      toolBar.setVisible(false);
    }
    if (toolBarList != null) {
      for (JToolBar tb : toolBarList) {
        tb.setVisible(false);
      }
    }
//    if (statusList != null) {
//      for (JComponent statusBar : statusList) {
//        statusBar.setVisible(false);
//      }
//    }
    for (PerspectiveProvider pp : serviceList) {
      pp.perspectiveHide();
    }
  }
  
  public void componentResized(ComponentEvent e) {
  }

  public void componentMoved(ComponentEvent e) {
  }

  public void componentShown(ComponentEvent e) {
    componentShow();
  }

  public void componentHidden(ComponentEvent e) {
    componentHide();
  }

  public String getPerspectiveId() {
    return perspective.getPerspective().getPpsId();
  }

}
