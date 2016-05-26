package pl.mpak.orbada.gui.cm;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.gui.IRootTabObjectInfo;
import pl.mpak.orbada.gui.ITabObjectInfo;
import pl.mpak.orbada.plugins.ComponentAction;
import pl.mpak.orbada.plugins.providers.ComponentActionProvider;
import pl.mpak.orbada.plugins.providers.ComponentActionsProvider;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.SearchPanel;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class ComponentActionsAction extends Action {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private Database database;
  private JComponent component;
  private JPopupMenu menuActions;
  private JToolBar toolBar;
  private JButton buttonActions;
  private String actionType;
  
  public ComponentActionsAction(Database database, JComponent component, JButton buttonActions, JPopupMenu menuActions, String actionType) {
    super();
    this.database = database;
    this.component = component;
    this.buttonActions = buttonActions;
    this.menuActions = menuActions;
    this.actionType = actionType;
    this.toolBar = (JToolBar)SwingUtil.getOwnerComponent(JToolBar.class, buttonActions);
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        init();
      }
    });
  }
  
  private void init() {
    addActionListener(createActionListener());
    setActionCommandKey("cmActions");
    setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/down10.gif"));
    setText(stringManager.getString("ComponentActionsAction-text"));
    this.buttonActions.addMouseListener(new MouseListener() {
      private String lastTooltip;
      private boolean entered;
      @Override
      public void mouseClicked(MouseEvent e) {
      }
      @Override
      public void mousePressed(MouseEvent e) {
      }
      @Override
      public void mouseReleased(MouseEvent e) {
      }
      @Override
      public void mouseEntered(MouseEvent e) {
        if (e.isControlDown()) {
          lastTooltip = getTooltip();
          ITabObjectInfo toi = (ITabObjectInfo)SwingUtil.getOwnerComponent(ITabObjectInfo.class, ComponentActionsAction.this.component);
          IRootTabObjectInfo rtoi = (IRootTabObjectInfo)SwingUtil.getOwnerComponent(IRootTabObjectInfo.class, ComponentActionsAction.this.component);
          setTooltip(String.format(
            stringManager.getString("ComponentActionsAction-programmers-hint"),
            new Object[] {
              ComponentActionsAction.this.component.getClass().getName(),
              ComponentActionsAction.this.actionType,
              (toi != null ? stringManager.getString("ComponentActionsAction-implemented") : stringManager.getString("ComponentActionsAction-not-implemented")),
              (rtoi != null ? stringManager.getString("ComponentActionsAction-implemented") : stringManager.getString("ComponentActionsAction-not-implemented"))
          }));
          entered = true;
        }
      }
      @Override
      public void mouseExited(MouseEvent e) {
        if (entered) {
          setTooltip(lastTooltip);
          entered = false;
        }
      }
    });
    this.buttonActions.setAction(this);
    this.buttonActions.setHideActionText(false);
    this.buttonActions.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    this.buttonActions.setMaximumSize(new java.awt.Dimension(60, 26));
    this.buttonActions.setPreferredSize(new java.awt.Dimension(60, 26));
    this.buttonActions.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

    SwingUtil.addAction(this.component, this);
    java.awt.EventQueue.invokeLater(new Runnable() {
      Database database = ComponentActionsAction.this.database;
      JComponent component = ComponentActionsAction.this.component;
      JPopupMenu menuActions = ComponentActionsAction.this.menuActions;
      JToolBar toolBar = ComponentActionsAction.this.toolBar;
      JButton buttonActions = ComponentActionsAction.this.buttonActions;
      String actionType = ComponentActionsAction.this.actionType;
      @Override
      public void run() {
        boolean first = true;
        ComponentActionProvider[] capa = Application.get().getServiceArray(ComponentActionProvider.class);
        if (capa != null && capa.length > 0) {
          for (ComponentActionProvider cap : capa) {
            if (cap.isForComponent(database, actionType)) {
              if ("-".equals(cap.getText())) {
                menuActions.addSeparator();
                if (cap.isToolButton() && toolBar != null) {
                  SwingUtil.addBefore(toolBar, component, new JToolBar.Separator());
                }
              }
              else {
                cap.setComponent(component);
                cap.setDatabase(database);
                if (cap.getShortCut() != null) {
                  SwingUtil.addAction(component, cap);
                }
                if (first && menuActions.getComponentCount() > 0) {
                  menuActions.addSeparator();
                }
                first = false;
                menuActions.add(cap);
                if (cap.isToolButton() && toolBar != null) {
                  SwingUtil.addBefore(toolBar, buttonActions, new ToolButton(cap));
                }
              }
            }
          }
        }
        ComponentActionsProvider[] caspa = Application.get().getServiceArray(ComponentActionsProvider.class);
        if (caspa != null && caspa.length > 0) {
          for (ComponentActionsProvider cap : caspa) {
            ComponentAction[] actions = cap.getForComponent(database, actionType);
            if (actions != null && actions.length > 0) {
              for (ComponentAction action : actions) {
                if ("-".equals(action.getText())) {
                  menuActions.addSeparator();
                  if (action.isToolButton() && toolBar != null) {
                    SwingUtil.addBefore(toolBar, component, new JToolBar.Separator());
                  }
                }
                else {
                  action.setComponent(component);
                  action.setDatabase(database);
                  if (action.getShortCut() != null) {
                    SwingUtil.addAction(component, action);
                  }
                  if (first && menuActions.getComponentCount() > 0) {
                    menuActions.addSeparator();
                  }
                  first = false;
                  menuActions.add(action);
                  if (action.isToolButton() && toolBar != null) {
                    SwingUtil.addBefore(toolBar, buttonActions, new ToolButton(action));
                  }
                }
              }
            }
          }
        }
        ArrayList<SearchPanel> spa = new ArrayList<SearchPanel>();
        for (int i=0; i<toolBar.getComponentCount(); i++) {
          Component comp = toolBar.getComponent(i);
          if (comp instanceof SearchPanel) {
            spa.add((SearchPanel)comp);
          }
        }
        first = true;
        for (int i=0; i<toolBar.getComponentCount(); i++) {
          Component comp = toolBar.getComponent(i);
          javax.swing.Action action = null;
          if (comp instanceof ToolButton) {
            action = ((ToolButton)comp).getAction();
          }
          if (comp != buttonActions && comp instanceof ToolButton &&
              action != null && !(action instanceof ComponentActionProvider) &&
              !actionExists(action)) {
            if (first && menuActions.getComponentCount() > 0) {
              menuActions.addSeparator();
            }
            first = false;
            final JMenuItem mi = menuActions.add(action);
            mi.setVisible(comp.isVisible());
            comp.addPropertyChangeListener("visible", new PropertyChangeListener() {
              @Override
              public void propertyChange(PropertyChangeEvent evt) {
                mi.setVisible((Boolean)evt.getNewValue());
              }
            });
            if (action.getValue(ACCELERATOR_KEY) != null) {
              SwingUtil.addAction(component, action);
            }
          }
          else if (comp instanceof JSeparator && menuActions.getComponentCount() > 0 && !(menuActions.getComponent(menuActions.getComponentCount() -1) instanceof JSeparator)) {
            menuActions.addSeparator();
          }
          if (spa.size() > 0 && action != null && action.getValue(ACCELERATOR_KEY) != null) {
            for (SearchPanel sp : spa) {
              SwingUtil.addAction(sp.getTextSearch(), action);
            }
          }
        }
        if (menuActions.getComponentCount() > 0 && menuActions.getComponent(menuActions.getComponentCount() -1) instanceof JSeparator) {
          menuActions.remove(menuActions.getComponentCount() -1);
        }
        setEnabled(menuActions.getComponentCount() > 0);
      }
    });
  }

  private boolean actionExists(javax.swing.Action action) {
    if (action != null) {
      for (int i=0; i<this.menuActions.getComponentCount(); i++) {
        Component item = this.menuActions.getComponent(i);
        if (item instanceof JMenuItem && ((JMenuItem)item).getAction() != null && ((JMenuItem)item).getAction() == action) {
          return true;
        }
      }
    }
    return false;
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        menuActions.show(buttonActions, 0, buttonActions.getHeight());
      }
    };
  }
  
}
