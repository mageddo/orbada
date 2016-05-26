package pl.mpak.util;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import pl.mpak.util.files.FileExtensionFilter;

public class FileUtil {

  private final static String FILE_UTIL_PATHS = "file-util-paths";

  private static Properties properties = SettingsUtil.get(FILE_UTIL_PATHS);

  public static File selectFileToSave(Component parent, FileExtensionFilter[] filters) {
    return selectFileToSave(parent, null, null, null, filters);
  }

  public static File selectFileToSave(Component parent, String title, FileExtensionFilter[] filters) {
    return selectFileToSave(parent, title, null, null, filters);
  }

  public static File selectFileToSave(Component parent, File selectedFile, FileExtensionFilter[] filters) {
    return selectFileToSave(parent, null, null, selectedFile, filters);
  }

  public static File selectFileToSave(Component parent, String title, String currDir, File selectedFile, FileExtensionFilter[] filters) {
    if (currDir == null) {
      currDir = properties.getProperty(parent == null ? "default" : parent.getClass().getName());
    }
    JFileChooser fileChoose = new JFileChooser(currDir == null ? "." : currDir);
    if (filters != null) {
      for (FileExtensionFilter filter : filters) {
        fileChoose.addChoosableFileFilter(filter);
      }
    }
    if (title != null) {
      fileChoose.setDialogTitle(title);
    }
    if (selectedFile != null) {
      fileChoose.setSelectedFile(selectedFile);
    }
    int returnVal = fileChoose.showSaveDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      FileFilter fcf = fileChoose.getFileFilter();
      File file = fileChoose.getSelectedFile().getAbsoluteFile();
      if (fcf instanceof FileExtensionFilter) {
        FileExtensionFilter ff = (FileExtensionFilter)fcf;
        file = ff.addExtenstion(file);
      }
      properties.setProperty(parent == null ? "default" : parent.getClass().getName(), fileChoose.getCurrentDirectory().toString());
      SettingsUtil.store(FILE_UTIL_PATHS);
      return file;
    }
    return null;
  }

  public static File selectFileToOpen(Component parent,
      FileExtensionFilter[] filters) {
    return selectFileToOpen(parent, null, null, null, filters);
  }

  public static File selectFileToOpen(Component parent, String title,
      String currDir, FileExtensionFilter[] filters) {
    return selectFileToOpen(parent, title, currDir, null, filters);
  }

  public static File selectFileToOpen(Component parent, File selectedFile,
      FileExtensionFilter[] filters) {
    return selectFileToOpen(parent, null, null, selectedFile, filters);
  }

  public static File selectFileToOpen(Component parent, String title,
      File selectedFile, FileExtensionFilter[] filters) {
    return selectFileToOpen(parent, title, null, selectedFile, filters);
  }

  public static File selectFileToOpen(Component parent, String title,
      String currDir, File selectedFile, FileExtensionFilter[] filters) {
    if (currDir == null) {
      currDir = properties.getProperty(parent == null ? "default" : parent
          .getClass().getName());
    }
    JFileChooser fileChoose = new JFileChooser(currDir == null ? "." : currDir);
    if (filters != null) {
      for (FileExtensionFilter filter : filters) {
        fileChoose.addChoosableFileFilter(filter);
      }
    }
    if (title != null) {
      fileChoose.setDialogTitle(title);
    }
    if (selectedFile != null) {
      fileChoose.setSelectedFile(selectedFile);
    }
    int returnVal = fileChoose.showOpenDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      properties.setProperty(parent == null ? "default" : parent.getClass().getName(), fileChoose.getCurrentDirectory().toString());
      SettingsUtil.store(FILE_UTIL_PATHS);
      return fileChoose.getSelectedFile().getAbsoluteFile();
    }
    return null;
  }

  public static File[] selectFilesToOpen(Component parent, String title,
      String currDir, FileExtensionFilter[] filters) {
    JFileChooser fileChoose = new JFileChooser(currDir);
    if (filters != null) {
      for (FileExtensionFilter filter : filters) {
        fileChoose.addChoosableFileFilter(filter);
      }
    }
    if (title != null) {
      fileChoose.setDialogTitle(title);
    }
    fileChoose.setMultiSelectionEnabled(true);
    int returnVal = fileChoose.showOpenDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return fileChoose.getSelectedFiles();
    }
    return null;
  }

  public static String prepareName(String text) {
    if (text == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      if (Character.isDigit(ch) || Character.isUnicodeIdentifierPart(ch)) {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  public static void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null) {
        source.close();
      }
      if (destination != null) {
        destination.close();
      }
    }
    destFile.setLastModified(sourceFile.lastModified());
  }

  /**
   * <p>Pozwala usun¹æ katalog oraz podkatalogi i pliki
   * @param dir
   * @return
   */
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    // The directory is now empty so delete it
    return dir.delete();
  }
  
  public static String fileExtension(String fileName) {
    String extension = "";

    int i = fileName.lastIndexOf('.');
    int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

    if (i > p) {
      extension = fileName.substring(i+1);
    }
    
    return extension;
  }

}
