package pl.mpak.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassUtil {
  
  public static String changeClassNameToFileName(String name) {
    if (name == null) {
      return null;
    }
    return name.replace('.', '/').concat(".class");
  }

  public static String changeFileNameToClassName(String name) {
    if (name == null) {
      return null;
    }
    String className = null;
    if (name.endsWith(".class")) {
      className = name.replace('/', '.');
      className = className.replace('\\', '.');
      className = className.substring(0, className.length() - 6);
    }
    return className;
  }

  /**
   * Zwraca pe³n¹ nazwê klasa/interfejs "." methodName
   * @param method
   * @return
   */
  public static String getQualifiedMethodName(Method method) {
    Assert.notNull(method, "Method must not be empty");
    return method.getDeclaringClass().getName() + "." + method.getName();
  }
  
  /**
   * Wyci¹ga nazwê klasy z ci¹gu "klasa.member"
   * @param fullMemberName
   * @return
   */
  public static String extractClass(String fullMemberName) {
    StringBuilder sb = new StringBuilder(fullMemberName);
    int li = sb.lastIndexOf(".");
    if (li >= 0) {
      return sb.substring(0, li);
    }
    return null;
  }
  
  /**
   * Wyci¹ga nazwê pola/metody z ci¹gu "klasa.member"
   * @param fullMemberName
   * @return
   */
  public static String extractMember(String fullMemberName) {
    StringBuilder sb = new StringBuilder(fullMemberName);
    int li = sb.lastIndexOf(".");
    if (li >= 0) {
      return sb.substring(li +1, fullMemberName.length());
    }
    return null;
  }
  
  /**
   * Testuje czy klasa ma metodê o podanej nazwie i parametrach podanego typu 
   * @param clazz
   * @param methodName
   * @param args
   * @return
   */
  public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>[] args) {
    try {
      clazz.getMethod(methodName, args);
      return true;
    }
    catch (NoSuchMethodException ex) {
      return false;
    }
  }

  public static int getMethodCountForName(Class<?> clazz, String methodName) {
    int count = 0;
    do {
      for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
        Method method = clazz.getDeclaredMethods()[i];
        if (methodName.equals(method.getName())) {
          count++;
        }
      }
      clazz = clazz.getSuperclass();
   }
   while (clazz != null)
     ;
   return count;
  }
  
  public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>[] args) {
    try {
      Method method = clazz.getDeclaredMethod(methodName, args);
      if ((method.getModifiers() & Modifier.STATIC) != 0) {
        return method;
      }
    } 
    catch (NoSuchMethodException ex) {
    }
    return null;
  }
  
  public static Field getStaticField(Class<?> clazz, String fieldName) {
    try {
      Field field = clazz.getDeclaredField(fieldName);
      if ((field.getModifiers() & Modifier.STATIC) != 0) {
        return field;
      }
    } 
    catch (SecurityException e) {
    }
    catch (NoSuchFieldException e) {
    }
    return null;
  }
  
  public static int getStaticFieldInteger(String fullFieldAccess) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    String className = ClassUtil.extractClass(fullFieldAccess);
    String memberName = ClassUtil.extractMember(fullFieldAccess);
    if (className == null || memberName == null) {
      throw new ClassNotFoundException();
    }
    Class<?> type;
    type = Class.forName(className);
    Field field = ClassUtil.getStaticField(type, memberName);
    return field.getInt(null);
  }
  
}
