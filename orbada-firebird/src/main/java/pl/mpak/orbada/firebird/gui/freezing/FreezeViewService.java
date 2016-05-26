package pl.mpak.orbada.firebird.gui.freezing;

import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.usedb.util.SQLUtil;

/**
 *
 * @author akaluza
 */
public abstract class FreezeViewService extends FirebirdViewService {

  protected IViewAccesibilities accesibilities;
  protected String schemaName;
  protected String objectName;
  
  public FreezeViewService() {
  }
  
  public FreezeViewService(IViewAccesibilities accesibilities, String schemaName, String objectName) {
    this.accesibilities = accesibilities;
    this.schemaName = schemaName;
    this.objectName = objectName;
  }

  public void setAccesibilities(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  @Override
  public String getPublicName() {
    return SQLUtil.createSqlName(objectName);
  }

}
