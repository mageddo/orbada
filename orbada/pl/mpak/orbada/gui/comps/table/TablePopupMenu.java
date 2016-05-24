/*
 * TablePopupMenu.java
 * 
 * Created on 2007-10-09, 20:27:16
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pl.mpak.orbada.gui.comps.table;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.gui.comps.table.cm.ColumnDecWidthAction;
import pl.mpak.orbada.gui.comps.table.cm.ColumnFitWidthAction;
import pl.mpak.orbada.gui.comps.table.cm.ColumnIncWidthAction;
import pl.mpak.orbada.gui.comps.table.cm.ColumnMoveToCurrentAction;
import pl.mpak.orbada.gui.comps.table.cm.ColumnMoveToEndAction;
import pl.mpak.orbada.gui.comps.table.cm.CopyCellToClipboardAction;
import pl.mpak.orbada.gui.comps.table.cm.RefreshAction;
import pl.mpak.orbada.gui.comps.table.cm.RowDecHeightAction;
import pl.mpak.orbada.gui.comps.table.cm.RowIncHeightAction;
import pl.mpak.orbada.gui.comps.table.cm.ViewQueryFieldAction;
import pl.mpak.orbada.gui.comps.table.cm.ViewValueAction;
import pl.mpak.orbada.plugins.providers.ExportTableActionProvider;
import pl.mpak.orbada.plugins.providers.TableActionProvider;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.PopupMenu;
import pl.mpak.usedb.gui.swing.QueryTable;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class TablePopupMenu extends PopupMenu {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  public TablePopupMenu(JTable popupComponent) {
    super(popupComponent);
    if (popupComponent instanceof QueryTable) {
      add(new RefreshAction((QueryTable)popupComponent));
      addSeparator();
    }
    add(new ColumnFitWidthAction(popupComponent));
    addSeparator();
    add(new ColumnIncWidthAction(popupComponent));
    add(new ColumnDecWidthAction(popupComponent));
    add(new RowIncHeightAction(popupComponent));
    add(new RowDecHeightAction(popupComponent));
    addSeparator();
    add(new ColumnMoveToEndAction(popupComponent));
    add(new ColumnMoveToCurrentAction(popupComponent));
    addSeparator();
    add(new CopyCellToClipboardAction(popupComponent));
    if (popupComponent instanceof QueryTable) {
      add(new ViewValueAction((QueryTable)popupComponent));
      add(new ViewQueryFieldAction((QueryTable)popupComponent));
    }
    if (pl.mpak.orbada.core.Application.get() != null) {
      JMenu exportMenu = new JMenu(stringManager.getString("SubMenuExport"));
      exportMenu.addMenuListener(getMenuListener());
      ExportTableActionProvider[] exportList = pl.mpak.orbada.core.Application.get().getServiceArray(ExportTableActionProvider.class);
      for (ExportTableActionProvider eta : exportList) {
        eta.setTable(popupComponent);
        exportMenu.add(eta);
        SwingUtil.addAction(popupComponent, eta);
      }
      add(exportMenu);
      TableActionProvider[] list = pl.mpak.orbada.core.Application.get().getServiceArray(TableActionProvider.class);
      if (list.length > 0) {
        addSeparator();
        for (TableActionProvider ta : list) {
          ta.setTable(popupComponent);
          add(ta);
        }
      }
    }
    addPopupMenuListener(getPopupMenuListener());
  }

  private MenuListener getMenuListener() {
    return new MenuListener() {
      public void menuSelected(MenuEvent e) {
        checkEnable((JComponent)e.getSource());
      }
      public void menuDeselected(MenuEvent e) {
      }
      public void menuCanceled(MenuEvent e) {
      }
    };
  }

  private PopupMenuListener getPopupMenuListener() {
    return new PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        checkEnable((JComponent)e.getSource());
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    };
  }
  
  private void checkEnable(JComponent menu) {
    for (int i=0; i<menu.getComponentCount(); i++) {
      Component c = menu.getComponent(i);
      if (c instanceof JMenuItem && ((JMenuItem)c).getAction() != null) {
        if (((JMenuItem)c).getAction() instanceof ExportTableActionProvider) {
          ((ExportTableActionProvider)((JMenuItem)c).getAction()).checkEnable();
        }
        else if (((JMenuItem)c).getAction() instanceof TableActionProvider) {
          ((TableActionProvider)((JMenuItem)c).getAction()).checkEnable();
        }
      }
    }
  }
  
}
