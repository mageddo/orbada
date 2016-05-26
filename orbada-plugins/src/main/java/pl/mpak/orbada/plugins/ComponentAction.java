/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import java.awt.Component;
import javax.swing.Icon;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.usedb.core.Database;

/**
 *
 * @author akaluza
 */
public class ComponentAction extends Action {

  protected Component component;
  protected Database database;

  public final static ComponentAction Separator = new ComponentAction("-");

  public ComponentAction(String title, Icon icon) {
    super(title, icon);
  }

  public ComponentAction(String title) {
    super(title);
  }

  public ComponentAction() {
    super();
  }

  public void setComponent(Component component) {
    this.component = component;
  }
  
  public void setDatabase(Database database) {
    this.database = database;
  }
  
  /**
   * <p>Zwraca zwi¹zany z akcj¹ komponent.<br>
   * Mo¿e to byæ np JTable, QueryTable, SyntaxEditor, etc.<br>
   * Aby sprawdziæ w programie z jakim komponentem oraz jaki jest typ akcji jest 
   * zwi¹zany przycisk Akcje nale¿y najechaæ na przycisk
   * z wciœniêtym przyciskiem Ctrl i poczekaæ na pojawienie siê tooltip-a
   * @return
   * @see pl.mpak.orbada.gui.cm.ComponentActionAction
   */
  public Component getComponent() {
    return component;
  }
  
  public Database getDatabase() {
    return database;
  }
  
  /**
   * <p>Powinna zwróciæ informacje czy akcja ma siê pojawiæ jako przycisk toolbar'a
   * @return
   */
  public boolean isToolButton() {
    return false;
  }

}
