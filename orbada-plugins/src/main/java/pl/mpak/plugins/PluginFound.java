package pl.mpak.plugins;


public class PluginFound {

  private String fileName;
  private String className;
  private ClassLoader classLoader;
  private Class<?> clazz;
  private boolean enabled = true;
  
  public PluginFound(String fileName, String className, ClassLoader classLoader) {
    this.fileName = fileName;
    this.className = className;
    this.classLoader = classLoader;
  }

  public PluginFound(String fileName, Class<?> clazz, ClassLoader classLoader) {
    this.fileName = fileName;
    this.className = clazz.getName();
    this.classLoader = classLoader;
    this.clazz = clazz;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  ClassLoader getClassLoader() {
    return classLoader;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
