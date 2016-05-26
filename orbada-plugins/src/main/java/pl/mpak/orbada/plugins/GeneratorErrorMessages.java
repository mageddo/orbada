package pl.mpak.orbada.plugins;

public class GeneratorErrorMessages extends pl.mpak.util.ErrorMessages {
  
  public final static int OGEN_01001_INIT           = 1001;
  public final static int OGEN_01002_NEXT_VALUE     = 1002;
  public final static int OGEN_01003_NO_CURR_VALUE  = 1003;
  public final static int OGEN_01004_MIN_MAX_VALUE  = 1004;

  public GeneratorErrorMessages() {
    this("OGEN");
  }

  public GeneratorErrorMessages(String prefix) {
    super(GeneratorErrorMessages.class, prefix);
  }

}
