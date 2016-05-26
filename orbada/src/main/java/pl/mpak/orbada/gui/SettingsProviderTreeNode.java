/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.gui;

import java.awt.Component;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import pl.mpak.orbada.plugins.ISettingsComponent;
import pl.mpak.orbada.plugins.providers.PerspectiveSettingsProvider;
import pl.mpak.orbada.plugins.providers.SettingsProvider;
import pl.mpak.orbada.plugins.providers.abs.AbstractSettingsProvider;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.usedb.core.Database;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class SettingsProviderTreeNode extends DefaultMutableTreeNode implements ISettingsComponent {
  
  private Database database;
  private AbstractSettingsProvider service;
  private Component component;
  
  public SettingsProviderTreeNode(AbstractSettingsProvider serivce, Database database, String name) {
    super(name);
    this.service = serivce;
    this.database = database;
  }

  public SettingsProviderTreeNode(String name) {
    this(null, null, name);
  }
  
  public boolean isShowed() {
    return component != null;
  }

  public Component getComponent() {
    if (component == null) {
      if (service instanceof SettingsProvider) {
        component = ((SettingsProvider)service).getSettingsComponent();
      }
      else if (service instanceof PerspectiveSettingsProvider) {
        component = ((PerspectiveSettingsProvider)service).getSettingsComponent(database);
      }
    }
    return component;
  }

  public Icon getIcon() {
    if (service != null) {
      return service.getIcon();
    }
    return null;
  }

  public String getDescription() {
    return service.getDescription();
  }

  public IOrbadaPluginProvider getSerivce() {
    return service;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }

  public void setService(AbstractSettingsProvider service) {
    this.service = service;
  }
  
  public void restoreSettings() {
    if (component instanceof ISettingsComponent) {
      ((ISettingsComponent)component).restoreSettings();
    }
  }

  public void applySettings() {
    if (component instanceof ISettingsComponent) {
      ((ISettingsComponent)component).applySettings();
    }
  }

  public void cancelSettings() {
    if (component instanceof ISettingsComponent) {
      ((ISettingsComponent)component).cancelSettings();
    }
  }
  
  public void close() {
    if (component instanceof Closeable) {
      try {
        ((Closeable) component).close();
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

}
