package pl.mpak.util.files;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilter 
  extends javax.swing.filechooser.FileFilter
  implements java.io.FileFilter, FilenameFilter {

  private String description;
  private String[] extensions;
  
  public FileExtensionFilter(String description, String extension) {
    this(description, new String[] {extension});
  }

  public FileExtensionFilter(String description, String[] extensions) {
    super();
    this.extensions = extensions;
    StringBuilder buf = new StringBuilder(description);
    buf.append(" (");
    for (int i = 0; i < this.extensions.length; ++i) {
      buf.append("*").append(this.extensions[i]);
      if (i != (this.extensions.length - 1)) {
        buf.append(", ");
      }
    }
    buf.append(")");
    this.description = buf.toString();
  }

  public boolean accept(File dir, String name) {
    return checkFileName(name.toLowerCase());
  }

  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    return checkFileName(file.getName().toLowerCase());
  }

  public String getDescription() {
    return description;
  }

  private boolean checkFileName(String name) {
    for (String extension : extensions) {
      if (name.endsWith(extension)) {
        return true;
      }
    }
    return false;
  }

  public String[] getExtensions() {
    return extensions;
  }
  
  public File addExtenstion(File file) {
    return new File(addExtenstion(file.getAbsolutePath()));
  }
  
  public String addExtenstion(String fileName) {
    if (!checkFileName(fileName)) {
      for (String extension : extensions) {
        if (extension.indexOf('*') == -1) {
          return fileName +extension;
        }
      }
    }
    return fileName;
  }
  
}