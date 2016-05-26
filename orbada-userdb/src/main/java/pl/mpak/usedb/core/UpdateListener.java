package pl.mpak.usedb.core;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author akaluza
 * <p>Metody wywo³ywane przy zmianie rekordu.
 * <p>Dla zda¿eñ Query, metody beforeCancel i afterCancel nie bêd¹ wywo³ane.
 * @see Query, RecordUpdate 
 */
public interface UpdateListener extends EventListener {

  public enum Event {
    BEFORE_UPDATE,
    AFTER_UPDATE,
    BEFORE_APPEND,
    AFTER_APPEND,
    BEFORE_DELETE,
    AFTER_DELETE,
    BEFORE_CANCEL,
    AFTER_CANCEL
  }

  public void beforeInsert(EventObject e);
  
  public void afterInsert(EventObject e);
  
  public void beforeUpdate(EventObject e);
  
  public void afterUpdate(EventObject e);
  
  public void beforeDelete(EventObject e);
  
  public void afterDelete(EventObject e);
  
  public void beforeCancel(EventObject e);
  
  public void afterCancel(EventObject e);
  
}
