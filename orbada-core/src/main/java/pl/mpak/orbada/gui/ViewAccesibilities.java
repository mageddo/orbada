package pl.mpak.orbada.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.IPerspectiveAccesibilities;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.TabCloseComponent;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.usedb.core.Database;

/**
 * <p>Klasa dostêpu widoku do obiektów programu<br>
 * Przekazywana jest do widoku.
 * @author akaluza
 */
public class ViewAccesibilities implements IViewAccesibilities, ComponentListener {
  
  private TabCloseComponent tabClose;
  private Database database;
  private Component viewComponent;
  private ArrayList<JMenu> menuList;
  private JToolBar toolBarView;
  private ViewProvider viewProvider;
  private PerspectivePanel perspective;
  private boolean hideTitle;
  private boolean hideIcon;
  private String orgTabTooltip;
  private String tabExtTooltip;
//  private Component focusedObject;
  
  public ViewAccesibilities(Database database, TabCloseComponent tabClose, PerspectivePanel perspective, ViewProvider viewProvider) {
    this.tabClose = tabClose;
    this.database = database;
    this.perspective = perspective;
    this.viewProvider = viewProvider;
    init();
  }
  
  private void init() {
  }
  
  public void close() {
    Application.get().getMainFrame().setTitle(null);
    if (menuList != null) {
      for (int i=0; i<menuList.size(); i++) {
        Application.get().getMainFrame().removeMenu(menuList.get(i));
      }
      menuList.clear();
    }
    if (toolBarView != null) {
      Application.get().getMainFrame().removeToolBar(toolBarView);
      toolBarView = null;
    }
    viewProvider.viewClose();
    setViewComponent(null);
    database = null;
    perspective = null;
    viewProvider = null;
    tabClose = null;
  }
  
  public Database getDatabase() {
    return database;
  }
  
  public IApplication getApplication() {
    return Application.get();
  }
  
  public IPerspectiveAccesibilities getPerspectiveAccesibilities() {
    return perspective.perspectiveAccesibilities;
  }
  
  public Component getViewComponent() {
    return viewComponent;
  }
  
  public void setViewComponent(Component view) {
    if (this.viewComponent != null) {
      this.viewComponent.removeComponentListener(this);
    }
    this.viewComponent = view;
    if (this.viewComponent != null) {
      this.viewComponent.addComponentListener(this);
    }
  }

  public ViewProvider getViewProvider() {
    return viewProvider;
  }

  public boolean isHideIcon() {
    return hideIcon;
  }

  public void setHideIcon(boolean hideIcon) {
    this.hideIcon = hideIcon;
    tabClose.getIconLabel().setVisible(!hideIcon && !Application.get().getSettings().getValue(Consts.noViewTabPictures, false));
  }

  public boolean isHideTitle() {
    return hideTitle;
  }

  public void setHideTitle(boolean hideTitle) {
    this.hideTitle = hideTitle;
    if (!viewComponent.isVisible()) {
      tabClose.getLabel().setVisible(
        !hideTitle && 
        !Application.get().getSettings().getValue(Consts.noViewTabTitles, false) ||
        tabClose.getIconLabel().getIcon() == null);
    }
    else {
      tabClose.getLabel().setVisible(true);
    }
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
      if (toolBarView == null) {
        toolBarView = new JToolBar();
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            Application.get().getMainFrame().addToolBar(toolBarView);
          }
        });
      }
      ToolButton tb = new ToolButton(action);
      tb.setPreferredSize(new Dimension(28, 28));
      toolBarView.add(tb);
    }
  }
  
  public Component getViewComponent(ViewProvider view) {
    return perspective.getViewComponent(view);
  }

  public Component[] getViewComponentList(ViewProvider view) {
    return perspective.getViewComponentList(view);
  }
  
  public Component createView(ViewProvider view) {
    return perspective.createView(view, false, false);
  }

  public void componentShow() {
    if (menuList != null) {
      for (int i=0; i<menuList.size(); i++) {
        menuList.get(i).setVisible(true);
      }
    }
    if (toolBarView != null) {
      toolBarView.setVisible(true);
    }
    tabClose.getButton().setVisible(true);
    tabClose.getLabel().setVisible(true);
    viewProvider.viewShow();
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        Application.get().getMainFrame().setTitle(tabClose.getTitle());
      }
    });
  }

  public void componentHide() {
    Application.get().getMainFrame().setTitle(null);
    tabClose.getButton().setVisible(false);
    tabClose.getLabel().setVisible(
      !hideTitle && 
      !Application.get().getSettings().getValue(Consts.noViewTabTitles, false) ||
      tabClose.getIconLabel().getIcon() == null);
    if (menuList != null) {
      for (int i=0; i<menuList.size(); i++) {
        menuList.get(i).setVisible(false);
      }
    }
    if (toolBarView != null) {
      toolBarView.setVisible(false);
    }
    viewProvider.viewHide();
  }
  
  public void componentResized(ComponentEvent e) {
  }

  public void componentMoved(ComponentEvent e) {
  }

  public void componentShown(ComponentEvent e) {
    componentShow();
//    java.awt.EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        if (focusedObject != null) {
//          focusedObject.requestFocusInWindow();
//          System.out.println("Shown:" +focusedObject);
//        }
//      }
//    });
  }

  public void componentHidden(ComponentEvent e) {
    componentHide();
  }

//  void setFocusedObject(Component focusedObject) {
//    this.focusedObject = focusedObject;
//    System.out.println("Focused on " +getTabTitle() +":" +this.focusedObject);
//  }

  public JLabel getTabLabel() {
    return tabClose.getLabel();
  }

  public Action getCloseAction() {
    return tabClose.getAction();
  }

  public void setTabTitle(String title) {
    tabClose.getLabel().setText(title);
  }
  
  public String getTabTitle() {
    return tabClose.getLabel().getText();
  }

  @Override
  public void setTabExtTooltip(String tooltip) {
    int index = perspective.getTabbedViews().indexOfComponent(viewComponent);
    if (orgTabTooltip == null) {
      orgTabTooltip = perspective.getTabbedViews().getToolTipTextAt(index);
    }
    tabExtTooltip = tooltip;
    perspective.getTabbedViews().setToolTipTextAt(index, orgTabTooltip +(tabExtTooltip != null ? "<hr>" +tabExtTooltip : ""));
  }

  @Override
  public String getTabExtTooltip() {
    return tabExtTooltip;
  }

  public void setCloseEnabled(boolean enabled) {
    tabClose.getAction().setEnabled(enabled);
  }
  
  public boolean isCloseEnabled() {
    return tabClose.getAction().isEnabled();
  }

}
