package pl.mpak.usedb.gui.linkreq;

public interface IFieldRequires {

  public boolean accept(Object value);
  
  public String getMessage(Object value);
  
}
