package pl.mpak.orbada.plugins.providers;

import java.awt.Component;
import pl.mpak.orbada.plugins.providers.abs.ActionProvider;
import pl.mpak.usedb.core.Database;

/**
 * <p>Akcja zostanie dodana do odpowiedniego przycisku Akcje zwi¹zanego z komponentem table, edytora, etc.
 * @author akaluza
 * @see pl.mpak.orbada.gui.cm.ComponentActionAction
 */
public abstract class ComponentActionProvider extends ActionProvider {
  
  protected Component component;
  protected Database database;

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
   * <p>Testuje czy akcja jest przeznaczona dla konkretnej bazy danych i konkretnego typu akcji
   * @param database
   * @param actionType
   * @return
   */
  public abstract boolean isForComponent(Database database, String actionType);
  
  /**
   * <p>Powinna zwróciæ informacje czy akcja ma siê pojawiæ jako przycisk toolbar'a
   * @return
   */
  public boolean isToolButton() {
    return false;
  }
  
}
