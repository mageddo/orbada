package pl.mpak.util.files;

import java.io.File;

/**
 * Klasa pozwala ograniczyæ listê do katalogów wg podanego wzorca nazwy
 * 
 * @author Andrzej Ka³u¿a
 *
 */
public class PatternFolderFilter extends PatternFilenameFilter {

  public PatternFolderFilter(String regex) {
    super(regex);
  }

  public boolean accept(File dir, String name) {
    if (new File(dir.getAbsolutePath() +"/" +name).isDirectory()) {
      return super.accept(dir, name);
    }
    return false;
  }

}
