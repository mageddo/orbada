/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.gui.packages;

import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import orbada.gui.OrbadaTabbedPane;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.swing.vtab.VTextIcon;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.Titleable;

/**
 *
 * @author akaluza
 */
public class PackageTabbedPane extends OrbadaTabbedPane {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOraclePlugin.class);

  public PackageTabbedPane(IViewAccesibilities accesibilities) {
    super("PACKAGE",
      new Component[] {
        new PackagePartTabbedPane(accesibilities, stringManager.getString("PackageTabbedPane-general")),
        new PackagePartTabbedPane(accesibilities, "PACKAGE", stringManager.getString("PackageTabbedPane-header")),
        new PackagePartTabbedPane(accesibilities, "PACKAGE BODY", stringManager.getString("PackageTabbedPane-body"))
    });
    setTabPlacement(JTabbedPane.LEFT);
  }
  
  @Override
  public void addInfoPanel(Component panel) {
    if (UIManager.getLookAndFeel().getName() != null && UIManager.getLookAndFeel().getName().startsWith("Substance")) {
      addTab(((Titleable)panel).getTitle(), null, panel);
    }
    else {
      String title = ((Titleable)panel).getTitle();
      VTextIcon titleIcon = new VTextIcon(panel, title, VTextIcon.ROTATE_LEFT);
      addTab(null, titleIcon, panel);
    }
  }

}
