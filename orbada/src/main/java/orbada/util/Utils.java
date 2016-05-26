/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.util;

import java.awt.Component;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.CRC32;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import orbada.gui.PerspectivePanel;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class Utils {

  private static FilenameFilter fileFilter = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return !(new File(dir.getAbsolutePath() +"/" +name).isDirectory());
    }
  };
  private static FilenameFilter dirFilter = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return (new File(dir.getAbsolutePath() +"/" +name).isDirectory());
    }
  };

  public static ViewProvider[] sortViewList(ViewProvider[] viewList) {
    Arrays.sort(viewList, new Comparator<ViewProvider>() {
      @Override
      public int compare(ViewProvider o1, ViewProvider o2) {
        if (o1.getGroupName() != null && o2.getGroupName() != null) {
          int result = o1.getGroupName().compareTo(o2.getGroupName());
          if (result == 0) {
            if (o1.getSubmenu() != null && o2.getSubmenu() == null) {
              result = 1;
            }
            else if (o1.getSubmenu() == null && o2.getSubmenu() != null) {
              result = -1;
            }
          }
          if (result == 0 && o1.getPublicName() != null && o2.getPublicName() != null) {
            result = o1.getPublicName().compareToIgnoreCase(o2.getPublicName());
          }
          return result;
        }
        else {
          return -1;
        }
      }
    });
    return viewList;
  }
  
  /**
   * <p>Zwraca unikalne crc w postaci 8 znaków szesnastkowych
   * @param component
   * @return
   */
  public static String getUniqueCompId(Component component) {
    StringBuilder sb = new StringBuilder();
    Object o = component;
    while (o != null) {
      if (sb.length() > 0) {
        sb.append("-");
      }
      if (o.getClass().getName().startsWith("javax.swing.")) {
        sb.append(o.getClass().getSimpleName());
      }
      else {
        sb.append(o.getClass().getName());
      }
      if (o instanceof Component) {
        if (((Component)o).getParent() instanceof JSplitPane) {
          if (((JSplitPane)((Component)o).getParent()).getLeftComponent() == o) {
            sb.append("-left");
          }
          else {
            sb.append("-right");
          }
        }
//        if (((Component)o).getParent() instanceof JTabbedPane) {
//          Component[] comps = SwingUtil.getTabbedPaneComponents(((Component)o).getClass(), (Component)o);
//          if (comps != null && comps.length > 1) {
//            for (int i=0; i<comps.length; i++) {
//              if (comps[i] == o) {
//                sb.append("-").append(i);
//                break;
//              }
//            }
//          }
//        }
        o = ((Component)o).getParent();
        if (o instanceof PerspectivePanel) {
          break;
        }
      }
    }
    CRC32 crc = new CRC32();
    crc.update(sb.toString().getBytes());
    return String.format("%08X", new Object[] {crc.getValue()});
  }
  
  private static void searchLibPath(ArrayList<File> fa, String path) {
    File[] files = new File(path).listFiles(fileFilter);
    for (File file : files) {
      fa.add(file);
    }
    files = new File(path).listFiles(dirFilter);
    for (File file : files) {
      searchLibPath(fa, file.getAbsolutePath());
    }
  }
  
  public static File[] getOrbadaFiles() {
    ArrayList<File> fa = new ArrayList<File>();
    searchLibPath(fa, ".");
    return fa.toArray(new File[fa.size()]);
  }
  
  public static String getComponentClasses(Component component) {
    StringBuilder sb = new StringBuilder();
    while (component != null) {
      if (component.getClass().getName().startsWith("pl.mpak.") && component.getClass().getName().indexOf("$") == -1) {
        sb.append(component.getClass().getName() +"\n");
        Class clazz = component.getClass().getSuperclass();
        while (clazz != null) {
          if (clazz.getName().startsWith("pl.mpak.") && clazz.getName().indexOf("$") == -1) {
            sb.append(clazz.getName() +"\n");
          }
          clazz = clazz.getSuperclass();
        }
      }
      component = component.getParent();
    }
    return sb.toString();
  }

  public static void gotoHelp() {
    gotoHelp(FocusManager.getCurrentManager().getFocusOwner());
  }

  public static void gotoHelp(Component component) {
    try {
      String classes = getComponentClasses(component).trim().replace('\n', ',');
      //Desktop.getDesktop().browse(Application.get().getWebAppAccessibilities().getWebPageUri("/www_orbada_doc.help?keys=" +classes));
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public static void moveTabLeft(JTabbedPane pane) {
    if (pane.getComponentCount() > 1) {
      int index = pane.getSelectedIndex();
      String title = pane.getTitleAt(index);
      String toolTip = pane.getToolTipTextAt(index);
      Icon icon = pane.getIconAt(index);
      Component tab = pane.getTabComponentAt(index);
      Component view = pane.getComponentAt(index);
      if (index == 0) {
        pane.remove(index);
        index = pane.getTabCount();
        pane.insertTab(title, icon, view, toolTip, index);
        pane.setTabComponentAt(index, tab);
        pane.setSelectedIndex(index);
      } else {
        pane.remove(index);
        pane.insertTab(title, icon, view, toolTip, index - 1);
        pane.setTabComponentAt(index - 1, tab);
        pane.setSelectedIndex(index - 1);
      }
    }
  }
  
  public static void moveTabRight(JTabbedPane pane) {
    if (pane.getComponentCount() > 1) {
      int index = pane.getSelectedIndex();
      String title = pane.getTitleAt(index);
      String toolTip = pane.getToolTipTextAt(index);
      Icon icon = pane.getIconAt(index);
      Component tab = pane.getTabComponentAt(index);
      Component view = pane.getComponentAt(index);
      if (index == pane.getTabCount() - 1) {
        pane.remove(index);
        pane.insertTab(title, icon, view, toolTip, 0);
        pane.setTabComponentAt(0, tab);
        pane.setSelectedIndex(0);
      } else {
        pane.remove(index);
        pane.insertTab(title, icon, view, toolTip, index + 1);
        pane.setTabComponentAt(index + 1, tab);
        pane.setSelectedIndex(index + 1);
      }
    }
  }
  
}
