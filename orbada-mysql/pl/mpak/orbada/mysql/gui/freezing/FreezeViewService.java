package pl.mpak.orbada.mysql.gui.freezing;

import pl.mpak.orbada.plugins.IViewAccesibilities;

/**
 *
 * @author akaluza
 */
public abstract class FreezeViewService extends MySQLViewService {

  protected IViewAccesibilities accesibilities;
  protected String databaseName;
  protected String objectName;
  
  public FreezeViewService() {
  }
  
  public FreezeViewService(IViewAccesibilities accesibilities, String databaseName, String objectName) {
    this.accesibilities = accesibilities;
    this.databaseName = databaseName;
    this.objectName = objectName;
  }

  public void setAccesibilities(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  @Override
  public String getPublicName() {
    return objectName;
  }

}
