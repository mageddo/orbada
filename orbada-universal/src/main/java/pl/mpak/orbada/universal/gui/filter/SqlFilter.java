/*
 * SqlFilter.java
 *
 * Created on 2007-11-04, 19:48:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.universal.gui.filter;

import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.universal.OrbadaUniversalPlugin;
import pl.mpak.orbada.universal.gui.filter.cm.FilterDefAction;
import pl.mpak.orbada.universal.gui.filter.cm.FilterTurnOffAction;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class SqlFilter {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager("universal");

  public static Icon filterOnIcon = null;
  public static Icon filterOffIcon = null;
  
  private ISettings settings;
  private SqlFilterDef definition;
  private Action action;
  private AbstractButton button;
  private boolean turnedOn;
  private SqlFilterComponent[] filterComponent;
  private ArrayList<DefinedFilterComponent> filterComponentList;
  private JPopupMenu menu;
  
  private boolean silentDialog;
  private FilterTurnOffAction cmFilterTurnOff;
  
  public SqlFilter(ISettings settings, Action action, AbstractButton button, SqlFilterDef definition) {
    filterComponentList = new ArrayList<DefinedFilterComponent>();
    filterComponent = new SqlFilterComponent[5];
    for (int i=0; i<filterComponent.length; i++) {
      filterComponent[i] = new SqlFilterComponent();
    }
    this.button = button;
    this.action = action;
    this.settings = settings;
    this.definition = definition;
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        SqlFilter.this.action.putValue(Action.NAME, stringManager.getString("filter-definition-3d"));
        cmFilterTurnOff = new FilterTurnOffAction(SqlFilter.this);
        loadSettings();
        updateAction();
        prepareMenu();
      }
    });
  }
  
  public SqlFilter(ISettings settings, Action action, SqlFilterDef definition) {
    this(settings, action, null, definition);
  }
  
  private void loadSettings() {
    turnedOn = settings.getValue("turned-on", turnedOn);
    for (int i=0; i<filterComponent.length; i++) {
      filterComponent[i].setTurnedOn(settings.getValue("turned-on-" +i, filterComponent[i].isTurnedOn()));
      filterComponent[i].setOperator(settings.getValue("operator-" +i, filterComponent[i].getOperator()));
      filterComponent[i].setColumnName(settings.getValue("column-name-" +i, filterComponent[i].getColumnName()));
      filterComponent[i].setCondition(settings.getValue("condition-" +i, filterComponent[i].getCondition()));
      filterComponent[i].setValue(settings.getValue("value-" +i, filterComponent[i].getValue()));
    }
    filterComponentList.add(new DefinedFilterComponent(filterComponent));
    long fdc = settings.getValue("filter-def-count", 0L) +1;
    for (int d=1; d<fdc; d++) {
      DefinedFilterComponent def = new DefinedFilterComponent(settings.getValue(d +"-name", "<undefined>"));
      for (int i=0; i<def.getFilterComponent().length; i++) {
        def.getFilterComponent()[i].setTurnedOn(settings.getValue(d +"-turned-on-" +i, def.getFilterComponent()[i].isTurnedOn()));
        def.getFilterComponent()[i].setOperator(settings.getValue(d +"-operator-" +i, def.getFilterComponent()[i].getOperator()));
        def.getFilterComponent()[i].setColumnName(settings.getValue(d +"-column-name-" +i, def.getFilterComponent()[i].getColumnName()));
        def.getFilterComponent()[i].setCondition(settings.getValue(d +"-condition-" +i, def.getFilterComponent()[i].getCondition()));
        def.getFilterComponent()[i].setValue(settings.getValue(d +"-value-" +i, def.getFilterComponent()[i].getValue()));
      }
      filterComponentList.add(def);
    }
  }
  
  public void storeSettings() {
    settings.setValue("turned-on", turnedOn);
    for (int i=0; i<filterComponent.length; i++) {
      settings.setValue("turned-on-" +i, filterComponent[i].isTurnedOn());
      settings.setValue("operator-" +i, filterComponent[i].getOperator());
      settings.setValue("column-name-" +i, filterComponent[i].getColumnName());
      settings.setValue("condition-" +i, filterComponent[i].getCondition());
      settings.setValue("value-" +i, filterComponent[i].getValue());
    }
    settings.store();
  }
  
  public void storeSettings(DefinedFilterComponent def, long index) {
    settings.setValue("filter-def-count", (long)filterComponentList.size() -1);
    if (def == null) {
      return;
    }
    settings.setValue(index +"-name", def.getName());
    for (int i=0; i<def.getFilterComponent().length; i++) {
      settings.setValue(index +"-turned-on-" +i, def.getFilterComponent()[i].isTurnedOn());
      settings.setValue(index +"-operator-" +i, def.getFilterComponent()[i].getOperator());
      settings.setValue(index +"-column-name-" +i, def.getFilterComponent()[i].getColumnName());
      settings.setValue(index +"-condition-" +i, def.getFilterComponent()[i].getCondition());
      settings.setValue(index +"-value-" +i, def.getFilterComponent()[i].getValue());
    }
    settings.store();
  }
  
  public void setDefComponents(DefinedFilterComponent def) {
    for (int i=0; i<filterComponent.length; i++) {
      filterComponent[i].setTurnedOn(def.getFilterComponent()[i].isTurnedOn());
      filterComponent[i].setColumnName(def.getFilterComponent()[i].getColumnName());
      filterComponent[i].setCondition(def.getFilterComponent()[i].getCondition());
      filterComponent[i].setOperator(def.getFilterComponent()[i].getOperator());
      filterComponent[i].setValue(def.getFilterComponent()[i].getValue());
    }
  }
  
  private void updateAction() {
    if (filterOnIcon == null || filterOffIcon == null) {
      filterOnIcon = pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/filter16.gif");
      filterOffIcon = pl.mpak.sky.gui.swing.ImageManager.getImage("/res/icons/filter_off16.gif");
    }
    if (action != null) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          if (action instanceof pl.mpak.sky.gui.swing.BaseAction) {
            if (turnedOn) {
              ((pl.mpak.sky.gui.swing.BaseAction)action).setSmallIcon(filterOnIcon);
            }
            else {
              ((pl.mpak.sky.gui.swing.BaseAction)action).setSmallIcon(filterOffIcon);
            }
            ((pl.mpak.sky.gui.swing.BaseAction)action).setTooltip(getTooltip());
          }
          else {
            if (turnedOn) {
              action.putValue(Action.SMALL_ICON, filterOnIcon);
            }
            else {
              action.putValue(Action.SMALL_ICON, filterOffIcon);
            }
            action.putValue(Action.SHORT_DESCRIPTION, getTooltip());
          }
          cmFilterTurnOff.setEnabled(turnedOn);
        }
      });
    }
  }
  
  private void prepareMenu() {
    if (button != null && menu == null) {
      menu = new JPopupMenu("Filtr");
      menu.addPopupMenuListener(getPopupMenuListener());
      button.setComponentPopupMenu(menu);
    }
  }
  
  private PopupMenuListener getPopupMenuListener() {
    return new PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        menu.removeAll();
        menu.add(action);
        if (filterComponentList.size() > 1) {
          menu.addSeparator();
          for (int d=1; d<filterComponentList.size(); d++) {
            menu.add(new FilterDefAction(SqlFilter.this, filterComponentList.get(d)));
          }
        }
        menu.addSeparator();
        menu.add(cmFilterTurnOff);
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    };
  }

  public SqlFilterDef getDefinition() {
    return definition;
  }
  
  public String getTooltip() {
    if (!turnedOn) {
      return stringManager.getString("SqlFilter-hint-off");
    }
    boolean resultFilterOn = false;
    String result = "<html>" +stringManager.getString("SqlFilter-hint-on") +"<br><hr>";
    for (int i=0; i<filterComponent.length; i++) {
      if (filterComponent[i].isTurnedOn()) {
        resultFilterOn = true;
        if (i > 0) {
          result = result +"<br>";
          if ("AND".equalsIgnoreCase(filterComponent[i].getOperator())) {
            result = result +stringManager.getString("and") +" ";
          }
          else if ("OR".equalsIgnoreCase(filterComponent[i].getOperator())) {
            result = result +stringManager.getString("or") +" ";
          }
        }
        result = result +getDefinition().getByColumn(filterComponent[i].getColumnName()).getColumnPublicName();
        if (!"USER".equalsIgnoreCase(filterComponent[i].getCondition())) {
          String value = "";
          if (!"IS NULL".equalsIgnoreCase(filterComponent[i].getCondition()) && !"IS NOT NULL".equalsIgnoreCase(filterComponent[i].getCondition())) {
            value = filterComponent[i].getValue();
          }
          result = result +" " +filterComponent[i].getCondition() +" " +value;
        }
      }
    }
    return resultFilterOn ? result : stringManager.getString("SqlFilter-hint-on-offed");
  }
  
  public String getSqlText() {
    if (!turnedOn) {
      return null;
    }
    boolean resultFilterOn = false;
    String result = "(";
    for (int i=0; i<filterComponent.length; i++) {
      if (filterComponent[i].isTurnedOn()) {
        resultFilterOn = true;
        if (i > 0) {
          result = result +" " +filterComponent[i].getOperator() +" ";
        }
        result = result +filterComponent[i].getColumnName();
        if (!"USER".equalsIgnoreCase(filterComponent[i].getCondition())) {
          String cond = filterComponent[i].getCondition();
          String value = "";
          if (!"IS NULL".equalsIgnoreCase(filterComponent[i].getCondition()) && !"IS NOT NULL".equalsIgnoreCase(filterComponent[i].getCondition())) {
            value = filterComponent[i].getValue();
          }
          if (("LIKE".equalsIgnoreCase(cond) || "NOT LIKE".equalsIgnoreCase(cond)) && value != null && value.trim().length() > 0 && value.trim().charAt(0) != '\'') {
            value = "'" +value +"'";
          }
          else if (("IN".equalsIgnoreCase(cond) || "NOT IN".equalsIgnoreCase(cond)) && value != null && value.trim().length() > 0 && value.trim().charAt(0) != '(') {
            value = "(" +value +")";
          }
          result = result +" " +cond +" " +value;
        }
      }
    }
    return resultFilterOn ? result +")" : null;
  }

  public boolean isTurnedOn() {
    return turnedOn;
  }

  public void setTurnedOn(boolean turnedOn) {
    this.turnedOn = turnedOn;
    updateAction();
  }

  public boolean isSilentDialog() {
    return silentDialog;
  }

  public void setSilentDialog(boolean silentDialog) {
    this.silentDialog = silentDialog;
  }

  public SqlFilterComponent[] getFilterComponent() {
    return filterComponent;
  }

  public Action getAction() {
    return action;
  }

  public ArrayList<DefinedFilterComponent> getFilterComponentList() {
    return filterComponentList;
  }
  
}
