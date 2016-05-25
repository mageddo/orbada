package pl.mpak.util;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;

public class DriverAssignableClasses extends AssignableClasses {

  public DriverAssignableClasses(URL[] urls) {
    super(urls);
  }

  public DriverAssignableClasses(URL url) {
    super(url);
  }

  @SuppressWarnings("unchecked")
  public Class[] getDriverClasses() throws ZipException, IOException {
    final Class[] classes = getAssignableClasses(Driver.class);
    final List list = new ArrayList();
    for (int i = 0; i < classes.length; ++i) {
      Class clazz = classes[i];
      if (!Modifier.isAbstract(clazz.getModifiers())) {
        list.add(clazz);
      }
    }
    return (Class[]) list.toArray(new Class[list.size()]);
  }
}
