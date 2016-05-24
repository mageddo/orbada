package pl.mpak.orbada.firebird.gui.freezing;

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
    freezeMap.put("PROCEDURE", ProcedureFreezeViewService.class);
    freezeMap.put("TABLE", TableFreezeViewService.class);
    freezeMap.put("VIEW", ViewFreezeViewService.class);
    freezeMap.put("TRIGGER", TriggerFreezeViewService.class);
    freezeMap.put("FUNCTION", FunctionFreezeViewService.class);
  }
  
  public FreezeFactory(IViewAccesibilities accesibilities) {
    this();
    this.accesibilities = accesibilities;
  }
  
  public boolean canCreate(String objectType) {
    return freezeMap.get(objectType) != null;
  }
  
  public FreezeViewService createInstance(String objectType, String schemaName, String objectName) {
    Class<? extends FreezeViewService> clazz = freezeMap.get(objectType);
    if (clazz != null) {
      try {
        FreezeViewService service = clazz.newInstance();
        //service.setAccesibilities(accesibilities);
        service.setSchemaName(schemaName);
        service.setObjectName(objectName);
        return service;
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    return null;
  }

}
