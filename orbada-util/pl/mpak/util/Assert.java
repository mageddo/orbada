package pl.mpak.util;

/**
 * @author Andrzej Ka³u¿a
 * 
 * <p>
 * Wszystkie funkcje zwracaj¹ wyj¹tek <code>IllegalArgumentException</code>
 * jeœli <code>function is true</code>
 * 
 */
public class Assert {

  public static void notTrue(boolean expression, String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notTrue(boolean expression) {
    notTrue(expression, "[Assertion failed] - this expression must be true");
  }

  public static void notNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notNull(Object object) {
    notNull(object, "[Assertion failed] - this argument is required; it cannot be null");
  }

  /**
   * <p>Sprawdzenie czy test jest pusty lub null
   * @param text
   * @param message
   */
  public static void notEmpty(String text, String message) {
    if (StringUtil.isEmpty(text)) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <p>Sprawdzenie czy test jest pusty lub null
   * @param text
   */
  public static void notEmpty(String text) {
    notEmpty(text, "[Assertion failed] - this String argument must have length; it cannot be <code>null</code> or empty");
  }
  
  public static void hasText(String text, String message) {
    if (!StringUtil.hasText(text)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void hasText(String text) {
    hasText(text, "[Assertion failed] - this String argument must have text; it cannot be <code>null</code>, empty, or blank");
  }

}
