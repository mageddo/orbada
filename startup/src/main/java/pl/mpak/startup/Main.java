/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author akaluza
 */
public class Main {

  private static File startupJar;
  private static StartupClassLoader classLoader;

  private static String[] fileToDelete = {
    "hsqldb.jar",
    "pl.mpak.parser-1.0.0.jar",
    "pl.mpak.plugins-1.0.0.jar",
    "pl.mpak.g2-1.0.0.jar",
    "pl.mpak.id-1.0.0.jar",
    "pl.mpak.task-1.0.1.jar",
    "pl.mpak.util-1.0.0.jar",
    "pl.mpak.sky-2.0.0.jar",
    "pl.mpak.usedb-1.2.0.jar",
    "pl.mpak.datatext-1.0.0.jar",
    "pl.mpak.doscharset-1.0.0.jar",
    "pl.mpak.waitdlg-1.0.0.jar",
    "pl.mpak-1.0.0.jar",
    "swing-layout-1.0.3.jar"
  };
  
  private static void searchLibPath(StartupClassLoader classLoader, String path) {
    File[] files = new File(path).listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.toUpperCase().endsWith(".JAR");
      }
    });
    if (files != null) {
      try {
        for (File file : files) {
          boolean deleted = false;
          for (String f : fileToDelete) {
            if (f.equalsIgnoreCase(file.getName())) {
              file.delete();
              System.out.println("Deleted " +file);
              deleted = true;
              break;
            }
          }
          if (!deleted) {
            classLoader.addURL(file.toURI().toURL());
            System.out.println("Added " +file);
          }
        }
      } catch (MalformedURLException ex) {
        ex.printStackTrace();
        System.exit(-1);
      }
    }
    files = new File(path).listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return new File(dir.getAbsolutePath() +"/" +name).isDirectory();
      }
    });
    if (files != null) {
      for (File file : files) {
        searchLibPath(classLoader, file.getAbsolutePath());
      }
    }
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (ClassLoader.getSystemClassLoader() instanceof StartupClassLoader) {
      classLoader = (StartupClassLoader)ClassLoader.getSystemClassLoader();
    }
    else {
      System.out.println("Can not start application, invalid class loader!");
      System.exit(-1);
    }
    
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream("startup.ini"));
    } catch (IOException ex) {
      ex.printStackTrace();
      System.exit(-1);
    }

    File files[];
    if (properties.getProperty("Startup-Jar") == null) {
      files = new File(".").listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.toUpperCase().endsWith(".JAR");
        }
      });
      for (File file : files) {
        if (!file.getName().equalsIgnoreCase("startup.jar")) {
          startupJar = file;
          break;
        }
      }
    }
    else {
      startupJar = new File(properties.getProperty("Startup-Jar"));
    }
    if (startupJar == null) {
      System.out.println("Can not start application, startup jar not found!");
      System.exit(-1);
    }
    try {
      System.out.println("Startup with " +startupJar);
      classLoader.addURL(startupJar.toURI().toURL());
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      System.exit(-1);
    }
    
    StringTokenizer stLib = new StringTokenizer(properties.getProperty("Lib-Path", "lib;plugins"), ";");
    while (stLib.hasMoreTokens()) {
      searchLibPath(classLoader, stLib.nextToken());
    }
//    files = new File(properties.getProperty("Lib-Path", "lib")).listFiles(new FilenameFilter() {
//      public boolean accept(File dir, String name) {
//        return name.toUpperCase().endsWith(".JAR");
//      }
//    });
//    try {
//      for (File file : files) {
//        System.out.println("Adding " +file);
//        classLoader.addURL(file.toURI().toURL());
//      }
//    } catch (MalformedURLException ex) {
//      ex.printStackTrace();
//      System.exit(-1);
//    }
    
    System.out.println("Start Main invocation");
    try {
      Class clazz = classLoader.loadClass(properties.getProperty("Startup-Main"));
      Method method = clazz.getDeclaredMethod("main", new Class[] {String[].class});
      method.invoke(clazz, new Object[] {args});
      System.out.println("End Main invocation");
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(-1);
    }

  }
  
}
