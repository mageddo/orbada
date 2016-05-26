package pl.mpak.util.patt;

public interface ResolvableModel {

  /**
   * <p>Wór bez nawiasów np "user_home".
   * <p>Wielkoœæ znaków nie ma znaczenia.
   * @return
   */
  public String getModel();
  
  public String getResolve();
  
}
