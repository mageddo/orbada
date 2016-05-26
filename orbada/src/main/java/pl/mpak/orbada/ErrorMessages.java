package orbada;

public class ErrorMessages extends pl.mpak.util.ErrorMessages {
  
  public final static int ORBADA_01001_NO_DRIVER_FOUND = 1001;
  public final static int ORBADA_01002_LOGIN_CANCELED  = 1002;
  public final static int ORBADA_01003_BAD_USER_PASSWD = 1003;
  public final static int ORBADA_01004_NO_RIGHTS       = 1004;
  public final static int ORBADA_01005_NO_URL          = 1005;

  public ErrorMessages() {
    this("ORBADA");
  }

  public ErrorMessages(String prefix) {
    super(ErrorMessages.class, prefix);
  }

}
