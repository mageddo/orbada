/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.gui;

import java.awt.Component;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTabbedPane;
import pl.mpak.sky.gui.swing.TabCloseComponent;
import pl.mpak.usedb.util.SQLUtil;
import pl.mpak.util.CloseAbilitable;
import pl.mpak.util.Titleable;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OrbadaTabbedPane extends JTabbedPane implements ITabObjectInfo, ITabObjectUserData {
  
  private String objectType;
  private String catalogName;
  private String schemaName;
  private String objectName;
  
  public OrbadaTabbedPane(String objectType, final Component[] components) {
    super();
    this.objectType = objectType;
    setFocusable(false);
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (components != null) {
          for (Component component : components) {
            addInfoPanel(component);
          }
        }
      }
    });
  }
  
  public OrbadaTabbedPane(Component[] components) {
    this(null, components);
  }
  
  /**
   * <p>Odœwie¿a widoczny panel, natychmiast
   */
  public void refreshImmediate() {
    for (int i=0; i<getTabCount(); i++) {
      Component c = getComponentAt(i);
      if (c instanceof ITabObjectInfo && c.isVisible()) {
        ((ITabObjectInfo)c).refresh();
      }
    }
    revalidate();
    repaint();
  }
  
  @Override
  public void userData(HashMap<String, Variant> values) {
    for (int i=0; i<getTabCount(); i++) {
      Component c = getComponentAt(i);
      if (c instanceof ITabObjectUserData) {
        ((ITabObjectUserData)c).userData(values);
      }
    }
  }

  @Override
  public void refresh() {
    for (int i=0; i<getTabCount(); i++) {
      Component c = getComponentAt(i);
      if (c instanceof ITabObjectInfo) {
        ((ITabObjectInfo)c).refresh(catalogName, schemaName, objectName);
      }
    }
    revalidate();
    repaint();
  }

  @Override
  public void refresh(String catalogName, String schemaName, String objectName) {
    this.catalogName = catalogName;
    this.schemaName = schemaName;
    this.objectName = objectName;
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        refresh();
      }
    });
  }
  
  @Override
  public String getTitle() {
    return SQLUtil.createSqlName(catalogName, schemaName, objectName);
  }

  protected void addInfoPanel(Component panel) {
    if (panel != null) {
      addTab(((Titleable)panel).getTitle(), panel);
      setTabComponentAt(indexOfComponent(panel), new TabCloseComponent(((Titleable)panel).getTitle()));
    }
  }
  
  @Override
  public void close() throws IOException {
    int i = 0;
    while (i<getTabCount()) {
      Component c = getComponentAt(i);
      if (c instanceof Closeable) {
        try {
          ((Closeable)c).close();
        } catch (IOException ex) {
        }
      } else {
        i++;
      }
      remove(c);
    }
  }

  public String getObjectName() {
    return objectName;
  }

  public String getObjectType() {
    return objectType;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public Component getComponent(Class<? extends Component> clazz) {
    for (int i=0; i<getComponentCount(); i++) {
      if (clazz.isInstance(getComponent(i))) {
        return getComponent(i);
      }
    }
    return null;
  }

  public Component[] getComponents(Class<?> clazz) {
    ArrayList<Component> list = new ArrayList<Component>();
    for (int i=0; i<getComponentCount(); i++) {
      if (clazz.isInstance(getComponent(i))) {
        list.add(getComponent(i));
      }
    }
    return list.toArray(new Component[list.size()]);
  }

  @Override
  public boolean canClose() {
    for (int i=0; i<getComponentCount(); i++) {
      if (getComponent(i) instanceof CloseAbilitable) {
        if (!((CloseAbilitable)getComponent(i)).canClose()) {
          return false;
        }
      }
    }
    return true;
  }

}
