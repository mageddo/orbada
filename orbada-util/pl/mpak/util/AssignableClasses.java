package pl.mpak.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author Andrzej Ka³u¿a
 * 
 * Wyszukuje klasy odpowiedniego typu w pliku jar/zip
 * Do funkcji getAssignableClasses() przekazuje siê typ klasy,
 * a jako jej rezultat otrzymuje siê listê klas zgodnych z typem
 *
 */
public class AssignableClasses extends URLClassLoader {
  //private Map<String, Class<?>> _classes = new HashMap<String, Class<?>>(1000);

  ArrayList<ClassLoaderListener> listeners = new ArrayList<ClassLoaderListener>();

  public AssignableClasses(String fileName) throws IOException {
    this(new File(fileName).toURI().toURL());
  }

  public AssignableClasses(URL url) {
    this(new URL[] { url });
  }

  public AssignableClasses(URL[] urls) {
    super(urls, ClassLoader.getSystemClassLoader());
  }

  public AssignableClasses(URL[] urls, ClassLoader classLoader) {
    super(urls, classLoader);
  }

  public void addClassLoaderListener(ClassLoaderListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  private void notifyListenersLoadedZipFile(String filename) {
    Iterator<ClassLoaderListener> i = listeners.iterator();
    while (i.hasNext()) {
      ClassLoaderListener listener = i.next();
      listener.loadedZipFile(filename);
    }
  }

  private void notifyListenersFinished() {
    Iterator<ClassLoaderListener> i = listeners.iterator();
    while (i.hasNext()) {
      ClassLoaderListener listener = i.next();
      listener.finishedLoadingZipFiles();
    }
  }

  public void removeClassLoaderListener(ClassLoaderListener listener) {
    listeners.remove(listener);
  }

  /**
   * <p>Pozwala pobraæ listê klas zgodnych z typem podanym jako parametr.
   * Aby sprawdziæ czy zwrócona klasa nie jest czasem abstrakcyjna nale¿y siê
   * pos³u¿yæ: Modifier.isAbstract()
   * @param type
   * @return listê klas zgodnych z podanym typem
   * @throws ZipException
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public Class[] getAssignableClasses(Class type) throws ZipException, IOException {
    Properties settings = SettingsUtil.get("assignable-classes");
    List classes = new ArrayList(100);
    for (URL url : getURLs()) {
      try {
        File file = new File(url.toURI());
        if (!file.isDirectory() && file.exists() && file.canRead()) {
          String properties = settings.getProperty(file.getAbsolutePath());
          long size = 0;
          long time = 0;
          int count = 0;
          if (properties != null) {
            StringTokenizer st = new StringTokenizer(properties, ",");
            if (st.hasMoreTokens()) {
              size = Long.valueOf(st.nextToken());
              if (st.hasMoreTokens()) {
                time = Long.valueOf(st.nextToken());
                if (st.hasMoreTokens()) {
                  count = Integer.valueOf(st.nextToken());
                }
              }
            }
          }
          if (properties != null && size == file.length() && time == file.lastModified() && count > 0) {
            for (int i=0; i<count; i++) {
              String className = settings.getProperty(file.getAbsolutePath() +":" +i);
              try {
                classes.add(loadClass(className));
              }
              catch (Throwable ex) {
              }
            }
          }
          else {
            count = 0;
            Class cls;
            String className;
            ZipFile zipFile = new ZipFile(file);
            notifyListenersLoadedZipFile(file.getName());
            for (Enumeration<? extends ZipEntry> en = zipFile.entries(); en.hasMoreElements();) {
              className = ClassUtil.changeFileNameToClassName(en.nextElement().getName());
              if (className != null) {
                try {
                  cls = loadClass(className);
                  if (cls != null) {
                    if (type.isAssignableFrom(cls)) {
                      classes.add(cls);
                      settings.put(file.getAbsolutePath() +":" +count, className);
                      count++;
                    }
                  }
                }
                catch (Throwable ex) {
                  //ExceptionUtil.processException(ex);
                }
              }
            }
            settings.put(file.getAbsolutePath(), file.length() +"," +file.lastModified() +"," +count);
          }
        }
      } catch (URISyntaxException e) {
        ExceptionUtil.processException(e);
      }
    }
    SettingsUtil.store("assignable-classes");
    notifyListenersFinished();
    return (Class[])classes.toArray(new Class[classes.size()]);
  }

//  protected synchronized Class<?> findClass(String className) throws ClassNotFoundException {
//    Class<?> cls = (Class<?>)_classes.get(className);
//    if (cls == null) {
//      cls = super.findClass(className);
//      _classes.put(className, cls);
//    }
//    return cls;
//  }

  protected void classHasBeenLoaded(Class<?> cls) {
    // Empty
  }
}
