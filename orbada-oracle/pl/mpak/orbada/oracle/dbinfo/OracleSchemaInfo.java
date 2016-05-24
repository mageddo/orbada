package pl.mpak.orbada.oracle.dbinfo;

import java.sql.Timestamp;
import pl.mpak.orbada.oracle.services.OracleDbInfoProvider;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class OracleSchemaInfo extends DbObjectContainer<DbObjectContainer> {
  
  private Long id;
  private Timestamp created;
  
  public OracleSchemaInfo(String name, OracleSchemaListInfo owner) {
    super(name, owner);
  }

  public String[] getColumnNames() {
    return new String[] {};
  }

  public String[] getMemberNames() {
    return new String[] {"Id", "Utworzony"};
  }

  public Variant[] getMemberValues() {
    return new Variant[] {
      new Variant(getId()), 
      new Variant(getCreated())
    };
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void refresh() throws Exception {
    if (isRefreshing()) {
      return;
    }
    setRefreshing(true);
    try {
      clear();
      put(new OracleTableListInfo(this));
      put(new OracleIndexListInfo(this));
      put(new OracleConstraintListInfo(this));
      put(new OracleTriggerListInfo(this));
      put(new OracleSequenceListInfo(this));
      if ("TRUE".equalsIgnoreCase(getDatabase().getUserProperties().getProperty("dba-role", "false")) &&
          OracleDbInfoProvider.instance.getMajorVersion(getDatabase()) >= 10) {
        put(new OracleRecyclebinListInfo(this));
      }
      put(new OracleSynonymListInfo(this));
      put(new OracleObjectListInfo(this));
      put(new OracleDbLinkListInfo(this));
      put(new OracleViewListInfo(this));
      put(new OracleTypeListInfo(this));
      put(new OraclePackageListInfo(this));
      put(new OracleMethodListInfo(this));
      put(new OracleFunctionListInfo(this));
      put(new OracleProcedureListInfo(this));
      put(new OracleMViewListInfo(this));
      put(new OracleAllTableListInfo(this));
      put(new OracleJavaSourceListInfo(this));
      put(new OracleJavaClassListInfo(this));
      put(new OracleJavaResourceListInfo(this));
    }
    finally {
      setRefreshed(true);
    }
  }
  
}
