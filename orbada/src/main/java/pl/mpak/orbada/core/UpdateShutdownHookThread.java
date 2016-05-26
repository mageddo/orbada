/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package orbada.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 *
 * @author akaluza
 */
public class UpdateShutdownHookThread extends Thread {
  
  private ArrayList<UpdateFile> fileList;
  
  public UpdateShutdownHookThread(ArrayList<UpdateFile> fileList) {
    super();
    this.fileList = fileList;
  }
  
  @Override
  public void run() {
    for (UpdateFile file : fileList) {
      file.process();
    }
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

  public static class UpdateFile {
    private File sourceFile;
    private File destPath;
    private File destFile;

    public UpdateFile(File sourceFile, File destPath, File destFile) {
      this.sourceFile = sourceFile;
      this.destPath = destPath;
      this.destFile = destFile;
    }
    
    public void process() {
      destPath.mkdirs();
      try {
        copyFile(sourceFile, destFile);
      } catch (IOException ex) {
      } finally {
        sourceFile.delete();
      }
    }
    
  }

}
