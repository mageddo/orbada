package orbada.gui;

public class LoginInfo {

  private String userName;
  private String password;

  public LoginInfo() {
    userName = null;
    password = null;
  }
  
  public LoginInfo(String userName, String password) {
    super();
    this.userName = userName;
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public String getUserName() {
    return userName;
  }
}
