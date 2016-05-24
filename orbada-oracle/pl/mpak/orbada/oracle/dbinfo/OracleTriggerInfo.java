package pl.mpak.orbada.oracle.dbinfo;

import pl.mpak.orbada.plugins.dbinfo.DbObjectIdentified;
import pl.mpak.orbada.plugins.dbinfo.DbObjectInfo;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleTriggerInfo extends DbObjectIdentified implements DbObjectInfo {
  
  private boolean onTable;
  private String tableName;
  private String enabled;
  private String state;
  private String event;
  private String type;
  private String when;
  
  public OracleTriggerInfo(String name, OracleTriggerListInfo owner) {
    super(name, owner);
    DbObjectInfo info = getObjectOwner();
    onTable = info != null && StringUtil.equalAnyOfString(info.getObjectType(), new String[] {"TABLE", "VIEW", "MATERIALIZED VIEW"}, true);
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getEnabled() {
    return enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = enabled;
  }

  public String getWhen() {
    return when;
  }

  public void setWhen(String when) {
    this.when = when;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String[] getMemberNames() {
    if (onTable) {
      return new String[] {"Stan", "W³¹czony", "Zda¿enie", "Typ", "Kiedy"};
    }
    else {
      return new String[] {"Nazwa tabeli", "Stan", "W³¹czony", "Zda¿enie", "Typ", "Kiedy"};
    }
  }

  public Variant[] getMemberValues() {
    if (onTable) {
      return new Variant[] {
        new Variant(getState()),
        new Variant(getEnabled()),
        new Variant(getEvent()),
        new Variant(getType()),
        new Variant(getWhen())
      };
    }
    else {
      return new Variant[] {
        new Variant(getTableName()),
        new Variant(getState()),
        new Variant(getEnabled()),
        new Variant(getEvent()),
        new Variant(getType()),
        new Variant(getWhen())
      };
    }
  }

  public void refresh() throws Exception {
  }

  public String getObjectType() {
    return "TRIGGER";
  }
  
}
