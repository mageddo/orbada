package pl.mpak.util;

public class ErrorMessages {
  private Languages languages;
  
  private String prefix;

  public ErrorMessages() {
    this(ErrorMessages.class, "UTL");
  }
  
  public ErrorMessages(String prefix) {
    this(ErrorMessages.class, prefix);
  }
  
  public ErrorMessages(Class<?> clazz, String prefix) {
    super();
    languages = new Languages(clazz);
    this.prefix = prefix;
  }
  
  public ErrorMessages(String resName, String prefix) {
    super();
    languages = new Languages(resName);
    this.prefix = prefix;
  }
  
  public String getKey(int code) {
    return prefix +"-" +String.format("%05d", new Object[] {code});
  }
  
  private String postMessages(String msg) {
    return StringUtil.evl(msg, "Can't find message for code.");
  }
  
  public String getString(String code) {
    return code +": " +postMessages(languages.getString(code));
  }
  
  public String getString(String code, Object[] argCode) {
    System.out.println(languages.getKeys().nextElement());
    return code +": " +postMessages(languages.getString(code, argCode));
  }
  
  public String getPrefix() {
    return prefix;
  }
  
}
