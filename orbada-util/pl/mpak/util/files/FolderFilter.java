package pl.mpak.util.files;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Klasa ogranicza listê tylko do katalogów
 * 
 * @author Andrzej Ka³u¿a
 *
 */
public class FolderFilter implements FilenameFilter {

  public FolderFilter() {
    super();
  }

  public boolean accept(File dir, String name) {
    return new File(dir.getName() +"/" +name).isDirectory();
  }

}
