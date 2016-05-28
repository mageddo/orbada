package pl.mpak.orbada.universal.gui.filter.cm;

import java.awt.event.ActionEvent;
import pl.mpak.orbada.universal.gui.filter.DefinedFilterComponent;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.sky.gui.swing.Action;

/**
 *
 * @author akaluza
 */
public class FilterDefAction extends Action {
  
  private SqlFilter sqlFilter;
  private DefinedFilterComponent def;
  
  public FilterDefAction(SqlFilter sqlFilter, DefinedFilterComponent def) {
    super();
    this.sqlFilter = sqlFilter;
    this.def = def;
    setText(def.getName());
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    sqlFilter.setSilentDialog(true);
    try {
      sqlFilter.setDefComponents(def);
      sqlFilter.setTurnedOn(true);
      sqlFilter.storeSettings();
      sqlFilter.getAction().actionPerformed(e);
    }
    finally {
      sqlFilter.setSilentDialog(false);
    }
  }
  
}
