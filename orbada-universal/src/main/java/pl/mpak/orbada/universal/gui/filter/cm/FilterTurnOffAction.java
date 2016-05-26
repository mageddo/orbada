package pl.mpak.orbada.universal.gui.filter.cm;

import java.awt.event.ActionEvent;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.filter.SqlFilter;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class FilterTurnOffAction extends Action {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  private SqlFilter sqlFilter;
  
  public FilterTurnOffAction(SqlFilter sqlFilter) {
    super();
    this.sqlFilter = sqlFilter;
    setText(stringManager.getString("FilterTurnOffAction-text"));
    setTooltip(stringManager.getString("FilterTurnOffAction-hint"));
    setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/filter_off16.gif"));
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    sqlFilter.setSilentDialog(true);
    try {
      sqlFilter.setTurnedOn(false);
      sqlFilter.storeSettings();
      sqlFilter.getAction().actionPerformed(e);
    }
    finally {
      sqlFilter.setSilentDialog(false);
    }
  }
  
}
