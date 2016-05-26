package pl.mpak.util.files;

import java.io.File;

/**
 * Klasa pozwala ograniczyæ listê do plików wg podanego wzorca nazwy
 * 
 * @author Andrzej Ka³u¿a
 *
 */
public class PatternFileFilter extends PatternFilenameFilter {

  public PatternFileFilter(String regex) {
    super(regex);
  }

  public boolean accept(File dir, String name) {
    if (new File(dir.getAbsolutePath() +"/" +name).isFile()) {
      return super.accept(dir, name);
    }
    return false;
  }

}
