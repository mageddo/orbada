package pl.mpak.startup;

import java.net.URL;
import java.net.URLClassLoader;

public class StartupClassLoader
  extends URLClassLoader
{
  public StartupClassLoader(ClassLoader paramClassLoader)
  {
    super(new URL[0], paramClassLoader);
  }
  
  public StartupClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
  {
    super(paramArrayOfURL, paramClassLoader);
  }
  
  public StartupClassLoader(URL[] paramArrayOfURL)
  {
    super(paramArrayOfURL);
  }
  
  public void addURL(URL paramURL)
  {
    super.addURL(paramURL);
  }
}
