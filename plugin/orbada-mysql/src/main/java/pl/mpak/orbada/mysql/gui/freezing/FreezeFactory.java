package pl.mpak.orbada.mysql.gui.freezing;

import java.util.HashMap;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class FreezeFactory {
  
  private HashMap<String, Class<? extends FreezeViewService>> freezeMap;
  private IViewAccesibilities accesibilities;
  
  public FreezeFactory() {
    freezeMap = new HashMap<String, Class<? extends FreezeViewService>>();
    freezeMap.put("TABLE", TableFreezeViewService.class);
    freezeMap.put("VIEW", ViewFreezeViewService.class);
    freezeMap.put("PROCEDURE", ProcedureFreezeViewService.class);
    freezeMap.put("FUNCTION", FunctionFreezeViewService.class);
    freezeMap.put("TRIGGER", TriggerFreezeViewService.class);
  }
  
  public FreezeFactory(IViewAccesibilities accesibilities) {
    this();
    this.accesibilities = accesibilities;
  }
  
  public boolean canCreate(String objectType) {
    return freezeMap.get(objectType) != null;
  }
  
  public FreezeViewService createInstance(String objectType, String databaseName, String objectName) {
    Class<? extends FreezeViewService> clazz = freezeMap.get(objectType);
    if (clazz != null) {
      try {
        FreezeViewService service = clazz.newInstance();
        service.setDatabaseName(databaseName);
        service.setObjectName(objectName);
        return service;
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    return null;
  }

}
